package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.Utils.MatServiceUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.form.MatCsvForm;
import com.PIMCS.PIMCS.form.MatForm;
import com.PIMCS.PIMCS.form.MatFormList;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatService {
    private final MatRepository matRepository;
    private final ProductRepository productRepository;
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public MatService(MatRepository matRepository, ProductRepository productRepository, DynamoDBMapper dynamoDBMapper) {
        this.matRepository = matRepository;
        this.productRepository = productRepository;
        this.dynamoDBMapper = dynamoDBMapper;
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
    public Mat createMat(MatForm matForm, Company company){
        Mat mat = matForm.getMat();
        //유효성 검사
        HashMap<String,Object> resultMap = checkMatSerialNumberService(mat.getSerialNumber());
        if(!(boolean)resultMap.get("result")){ //등록할수 없는 매트일때
            throw new IllegalStateException(resultMap.get("message").toString());
        }
        //prudct와 회사 객체를 mat entity에 추가
        Optional<Product> productOpt = productRepository.findByIdAndCompany(matForm.getProductId(), company);
        if(productOpt.isPresent()){
            Product product = productOpt.get();
            mat.setProduct(product);
            mat.setCompany(company);
            matRepository.save(mat);

            DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
            dynamoDBUtils.putOrderMailRecipients(mat, matForm.getMailRecipients());

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
    public HashMap<String, String> updateMat(Company company, MatFormList matFormList){
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


        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        dynamoDBUtils.updateMat(saveMats);

        HashMap<String, String> hashMap =new HashMap<>();
        hashMap.put("message","수정 완료했습니다.");
        return hashMap;
    }
    public HashMap<String,String> updateMatEmailService(List<OrderMailRecipients> orderMailRecipients){
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        dynamoDBUtils.updateOrderMailRecipients(orderMailRecipients);;
        HashMap<String, String> hashMap =new HashMap<>();
        hashMap.put("message","수정 완료했습니다.");
        return hashMap;
    }

    /**
     * 매트삭제 서비스
     */
    public HashMap<String, Object> deleteMat(Company company, MatFormList matFormList){
        List<Mat> findMats = matRepository.findByCompany(company);
        List<Mat> saveMats = new ArrayList<>();
        MatServiceUtils matServiceUtils = new MatServiceUtils();
        for(MatForm matForm : matFormList.getMatForms()){
            Mat findMat = matServiceUtils.findMat(findMats, matForm.getMat().getId());
            if(findMat != null)
                saveMats.add(matForm.getMat());
            else
                throw new IllegalStateException("Mat does not exist.");
        }
        matRepository.deleteAllInBatch(saveMats);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("success",true);
        hashMap.put("message","삭제 완료하였습니다.");
        return hashMap;
    }

    /**
     * 매트 serial num 체크 서버스
     * @return hashMap
     *      hashMap.get('result'): true면 serialNumber 사용가능, false면 serialNumber 사용불가능
     */
    public HashMap checkMatSerialNumberService(String serialNumber){
        HashMap hashMap = new HashMap<>();
        Optional<Mat> optMat = matRepository.findBySerialNumber(serialNumber);

        if(optMat.isPresent()){ //null 값이아니면
            hashMap.put("result", false);
            hashMap.put("message","매트가 이미 등록되어 있습니다.");
        }else{
            hashMap.put("result", true);
            hashMap.put("message","등록할 수 있는 시리얼번호 입니다.");
        }
        System.out.println("========");
        System.out.println(hashMap.get("message"));
        System.out.println("========");
        return hashMap;
    }



    public void downloadMatCsvService(Company company, MatCsvForm matCsvForm, Writer writer) throws IOException{
        List<String> columns = matCsvForm.getCheckedColumnNames();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        csvPrinter.printRecord(columns);

        List<Mat> findMats = null;
        if(matCsvForm.getCheckedMatId() == null){
            findMats = matRepository.findByCompany(company);
        }else{
            findMats = matRepository.findByIdIn(matCsvForm.getCheckedMatId());
        }

        for(Mat mat : findMats){
            List<String> record = new ArrayList<>();
            if(columns.contains("serialNumber")) record.add(mat.getSerialNumber());
            if(columns.contains("matVersion")) record.add("A3");
            if(columns.contains("productCode")) record.add(mat.getProduct().getProductCode());
            if(columns.contains("productName")) record.add(mat.getProduct().getProductName());
            if(columns.contains("productImage")) record.add(mat.getProduct().getProductImage());
            if(columns.contains("inventoryWeight")) record.add(String.valueOf(mat.getInventoryWeight()));
            if(columns.contains("calcMethod")) record.add((mat.getCalcMethod() == 0) ? "무게(g)" : "갯수(개)");
            if(columns.contains("threshold")) record.add(String.valueOf(mat.getThreshold()));
            if(columns.contains("inventoryCnt")) record.add(String.valueOf(mat.getInventoryWeight()));
            if(columns.contains("matLocation")) record.add(mat.getMatLocation());
            if(columns.contains("productOrderCnt")) record.add(String.valueOf(mat.getProductOrderCnt()));
            if(columns.contains("boxWeight")) record.add(String.valueOf(mat.getBoxWeight()));
            csvPrinter.printRecord(record);
        }


    }

}
