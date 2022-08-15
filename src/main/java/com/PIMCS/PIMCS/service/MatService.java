package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.Utils.MatServiceUtils;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.noSqlDomain.*;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.repository.OwnDeviceRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatService {
    private final MatRepository matRepository;
    private final ProductRepository productRepository;
    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoQuery dynamoQuery;
    private final OwnDeviceRepository ownDeviceRepository;
    private final CompanyRepository companyRepository;
    private final APIService apiService;
    @Autowired
    public MatService(MatRepository matRepository, ProductRepository productRepository, DynamoDBMapper dynamoDBMapper, DynamoQuery dynamoQuery, OwnDeviceRepository ownDeviceRepository, CompanyRepository companyRepository, APIService apiService) {
        this.matRepository = matRepository;
        this.productRepository = productRepository;
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoQuery = dynamoQuery;
        this.ownDeviceRepository = ownDeviceRepository;
        this.companyRepository = companyRepository;
        this.apiService = apiService;
    }


    /**
     * 기기등록폼에 필요한 데이터를 db에서 조회
     * @param company
     * @return produts
     */
    public List<Product> createMatFormService(Company company){
        List<Product> products = productRepository.findByCompany(company);
        return products;
    }

    /**
     * 매트생성 서비스
     * @throws IllegalStateException 사용할수없는 시리얼번호인 경우 발생
     * @throws IllegalStateException product존재하지 않을때
     */
    public Mat createMatsService(MatForm matForm, Company company, User user){
        Mat mat = matForm.getMat();
        System.out.println("====");
        System.out.println(mat);
        //유효성 검사
        ValidationForm validation = apiService.findMatListBySerialNumberService(company,mat.getSerialNumber());
        if(!validation.isValid()){ //등록할수 없는 매트일때
            throw new IllegalStateException(validation.getMessage());
        }
        //prudct와 회사 객체를 mat entity에 추가
        Optional<Product> productOpt = productRepository.findByIdAndCompany(matForm.getProductId(), company);
        if(productOpt.isPresent()){
            Product product = productOpt.get();
            mat.setProduct(product);
            mat.setCompany(company);
//            mat.setCommunicationStatus(0);
            matRepository.save(mat);


            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(),mat.getSerialNumber());

            if(dynamoMat != null){ //dynamodb에 매트가 등록되어 있으면 삭제
                dynamoDBMapper.delete(dynamoMat);
            }
            DynamoMat.save(dynamoDBMapper, mat); // dynamoMat 저장
            // 주문이메일 dynamodb에 삽입
            OrderMailRecipients.save(dynamoDBMapper,mat, matForm.getMailRecipients());
            //로그 남기기
            MatLog.save(mat, user, "생성", dynamoDBMapper);
            return mat;
        }else{
            throw new IllegalStateException("Product does not exist.");
        }
    }

    /**
     * 매트읽기 서버스
     */
    public List<Mat> readMat(){
        List<Mat> mats = matRepository.findAll();
        return mats;
    }

    /**
     * 매트수정 서비스
     * @throws  IllegalStateException 회사에 등록되지 않은 product 또는 mat 일때 발생
     */
    public HashMap<String, String> updateMatsService(Company company, MatFormList matFormList, User user){
        //로그인한 유저 회사에 등록된 produts와 mats찾기
        List<Product> findProducts = productRepository.findByCompany(company);
        List<Mat> findMats = matRepository.findByCompany(company);

        MatServiceUtils matServiceUtils = new MatServiceUtils();
        //saveAll 하기위한 List
        List<Mat> saveMats = new ArrayList<>();
        for(MatForm matForm : matFormList.getMatForms()){
            Mat saveMat = matForm.getMat();
            // 회사에 등록된 product 또는 mat인지 찾기, null이면 등록되지 않음
            Product findProduct = matServiceUtils.findProduct(findProducts, matForm.getProductId());
            Mat findMat = matServiceUtils.findMat(findMats, saveMat.getId());

            if(findProduct != null && findMat != null){
                saveMat.setProduct(findProduct);
                saveMat.setCompany(company);
                saveMat.setCreatedAt(findMat.getCreatedAt());

                saveMats.add(saveMat);
            }else{
                throw new IllegalStateException("Product or Mat does not exist.");
            }
        }

        matRepository.saveAll(saveMats);

        System.out.println(saveMats);
        //dynamodb 동기화
        DynamoMat.update(dynamoDBMapper,dynamoQuery,saveMats,company);

        //로그 남기기
        MatLog.batchSave(saveMats, user, "수정", dynamoDBMapper);
        HashMap<String, String> hashMap =new HashMap<>();
        hashMap.put("message","수정 완료했습니다.");
        return hashMap;
    }
    public HashMap<String,String> updateMatsEmailService(List<OrderMailRecipients> orderMailRecipients, Company company){

        OrderMailRecipients.update(dynamoDBMapper, dynamoQuery, orderMailRecipients, company);
        HashMap<String, String> hashMap =new HashMap<>();
        hashMap.put("message","수정 완료했습니다.");
        return hashMap;
    }

    /**
     * 매트삭제 서비스
     */
    public HashMap<String, Object> deleteMatsService(Company company, MatFormList matFormList,  User user){
        List<Mat> findMats = matRepository.findByCompany(company);
        List<Mat> saveMats = new ArrayList<>();
        MatServiceUtils matServiceUtils = new MatServiceUtils();
        System.out.println("forms");
        System.out.println(matFormList);
        for(MatForm matForm : matFormList.getMatForms()){
            Mat findMat = matServiceUtils.findMat(findMats, matForm.getMat().getId());
            if(findMat != null) {
                saveMats.add(findMat);
            }
            else {
                throw new IllegalStateException("Mat does not exist.");
            }
        }
        matRepository.deleteAllInBatch(saveMats);

        DynamoMat.delete(dynamoDBMapper, dynamoQuery, saveMats, company); // dynamoMat 데이터 삭제
        OrderMailRecipients.delete(dynamoDBMapper, dynamoQuery, saveMats, company); // 주문이메일 삭제
        MatLog.batchSave(saveMats, user, "삭제", dynamoDBMapper); //로그남기기

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("success",true);
        hashMap.put("message","삭제 완료하였습니다.");
        return hashMap;
    }





    public void downloadMatCsvService(Company company, MatCsvForm matCsvForm, Writer writer) throws IOException{
        List<String> columns = matCsvForm.getCheckedColumnNames();
        System.out.println(columns.toString());

        for(int i=0; i<columns.size(); i++){
            if(columns.get(i).equals("productImage")) columns.remove(i);
        }

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        columns.remove("communicationStatus");

        csvPrinter.printRecord(columns);


        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);

        List<Mat> findMats = null;
        if(matCsvForm.getCheckedMatId() == null){
            findMats = matRepository.findByCompany(company);
        }else{
            findMats = matRepository.findByIdIn(matCsvForm.getCheckedMatId());
        }

        for(Mat mat : findMats){
            List<String> record = new ArrayList<>();
            Product product = mat.getProduct();
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, mat.getCompany().getId(), mat.getSerialNumber());
            mat.setInventoryWeight(dynamoMat.getInventoryWeight());

            if(columns.contains("serialNumber")) record.add(mat.getSerialNumber());
            if(columns.contains("matVersion")) record.add("A3");
            if(columns.contains("productCode")) record.add((product != null) ? product.getProductCode() : "N/A");
            if(columns.contains("productName")) record.add((product != null) ? product.getProductName() : "N/A");
            if(columns.contains("inventoryCnt")) record.add((product != null ) ? String.valueOf(mat.getCurrentInventory()) : "N/A");
            if(columns.contains("threshold")) record.add(String.valueOf(mat.getThreshold()));
            if(columns.contains("calcMethod")) record.add((mat.getCalcMethod() == 0) ? "무게(g)" : "갯수(개)");
            if(columns.contains("productWeight")) record.add((product != null) ? String.valueOf(product.getProductWeight()) : "N/A");
            if(columns.contains("inventoryWeight")) record.add(String.valueOf(mat.getInventoryWeight()));
            if(columns.contains("matLocation")) record.add(mat.getMatLocation());
            if(columns.contains("productOrderCnt")) record.add(String.valueOf(mat.getProductOrderCnt()));
            if(columns.contains("boxWeight")) record.add(String.valueOf(mat.getBoxWeight()));
            if(columns.contains("mailRecipients")){

                OrderMailRecipients o = dynamoDBMapper.load(OrderMailRecipients.class, mat.getCompany().getId(), mat.getSerialNumber());
                record.add(
                        (o != null) ? o.getMailRecipients().toString() : "N/A"
                );
            }

            csvPrinter.printRecord(record);
        }


    }


    public DynamoResultPage matLogService(Company company, Pageable pageable){
        DynamoDBQueryExpression<MatLog> queryExpression = MatLog.queryExpression(company, false);
        DynamoDBQueryExpression<MatLog> countQueryExpression = MatLog.queryExpression(company, true);
        return dynamoQuery.exePageQuery(MatLog.class, queryExpression, countQueryExpression, pageable);
    }

    public DynamoResultPage searchMatLogService(Company company, InOutHistorySearchForm searchForm, Pageable pageable){
        DynamoDBQueryExpression<MatLog> queryExpression = MatLog.searchQueryExpression(company, searchForm);
        DynamoDBQueryExpression<MatLog> countQueryExpression = MatLog.searchQueryExpression(company, searchForm);

        return dynamoQuery.exePageQuery(MatLog.class, queryExpression, countQueryExpression, pageable);
    }

    /**
     * 양도하기 서비스
     */
    public HashMap<String, String> matsToOtherCompanyService(Company company, MatFormList matFormList, String targetCompanyCode, User user){

        List<MatForm> matForms = matFormList.getMatForms();
        List<String> serialNumbers = matForms.stream().map(o -> o.getMat().getSerialNumber()).collect(Collectors.toList());
        Company targetCompany = companyRepository.findByCompanyCode(targetCompanyCode).orElse(null);

        if (targetCompany == null){
            throw  new IllegalStateException("Company does not exists");
        }


        //rdb mat삭제
        List<Mat> mats = matRepository.findByCompanyAndSerialNumberIn(company,serialNumbers);
        matRepository.deleteAllInBatch(mats);

        //매트와 회사 연결 테이블 업데이터
        List<OwnDevice> ownDevicies = ownDeviceRepository.findAllBySerialNumberIn(serialNumbers);

        List<OwnDevice> saveOwnDevicies = ownDevicies.stream().map(o -> {
            o.setCompany(targetCompany);
            return o;
        }).collect(Collectors.toList());
        ownDeviceRepository.saveAll(saveOwnDevicies);

        // dynamoMat 데이터 삭제
        DynamoMat.delete(dynamoDBMapper, dynamoQuery, mats, company);

        // 주문이메일 삭제
        OrderMailRecipients.delete(dynamoDBMapper, dynamoQuery, mats, company);

        //로그남기기
        MatLog.batchSave(mats, user, "양도", dynamoDBMapper);

        HashMap<String, String> hashMap =new HashMap<>();
        hashMap.put("message","양도 완료했습니다.");
        return hashMap;

    }

    /**
     * 매트관리 로그 csv 다운로드
     */
    public void downloadMatsLogCsvService(Company company, InOutHistorySearchForm searchForm, Writer writer) throws IOException{

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        String[] columns = {"시리얼 번호","위치","사용자 이름", "사용자 이메일", "action", "일시"};
        DynamoDBQueryExpression<MatLog> queryExpression = MatLog.queryExpression(company, true);


        List matLogList;

        if(searchForm.getQuery() != "" || (searchForm.getStartDate() != null && searchForm.getEndDate() != null)){
            matLogList = dynamoQuery.exeQuery(MatLog.class,  MatLog.searchQueryExpression(company, searchForm));

        }else { // 전제데이터 다운로드
            matLogList = dynamoQuery.exeQuery(MatLog.class, MatLog.queryExpression(company, false));
        }

        csvPrinter.printRecord(columns);
        for(Object o : matLogList){
            MatLog matLog = (MatLog) o;
            List<String> record = new ArrayList<>();
            record.add(matLog.getMatSerialNumber());
            record.add(matLog.getMatLocation());
            record.add(matLog.getUserName());
            record.add(matLog.getUserEmail());
            record.add(matLog.getAction());
            record.add(matLog.getCreatedAt().toString());
            csvPrinter.printRecord(record);
        }

    }

}
