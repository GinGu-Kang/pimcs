package com.PIMCS.PIMCS.service;



import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.MatForm;
import com.PIMCS.PIMCS.form.MatFormList;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.noSqlDomain.DynamoMat;
import com.PIMCS.PIMCS.noSqlDomain.DynamoProduct;
import com.PIMCS.PIMCS.noSqlDomain.MatLog;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.*;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class MatServiceIntegrationTest {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MatRepository matRepository;

    @Autowired
    private MatService matService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    OwnDeviceRepository ownDeviceRepository;

    @Autowired
    GenerateEntity generateEntity;

    @Autowired
    DynamoQuery dynamoQuery;

    private  Company company;
    private  User user;
    private  List<MatForm> matFormList;
    private  String[] serialNumberList;
    private  String notExistSerialNumber = UUID.randomUUID().toString();
    private List<Product> products = new ArrayList<>();
    private  String[] orderMails = new String[]{"ryongho1997@gmail.com"};
    private int productSelectedIndex=0;

    private int matMaxSize = 100;


    //private  Mat mat;
    @BeforeAll
    public void start(){

        String[] arrays = new String[matMaxSize];
        for(int i=0; i<matMaxSize; i++){

            arrays[i] =UUID.randomUUID().toString();
        }
        serialNumberList = arrays;

        user = generateEntity.createUser(null,true);
        company = user.getCompany();

        //회사와 mat 연결 테이블 insert
        for(String serialNumber : serialNumberList){
            OwnDevice ownDevice = new OwnDevice();
            ownDevice.setSerialNumber(serialNumber);
            ownDevice.setCompany(company);
            ownDeviceRepository.save(ownDevice);
        }

        //상품 생성
        for(int i=0; i<serialNumberList.length; i++){
            products.add(generateEntity.createProduct(company, true));
        }

    }




    @BeforeEach
    public void setup(){
        System.out.println("setup");
        matFormList = new ArrayList<>();
        for(String serialNumber : serialNumberList){
            Mat mat = new Mat();
            mat.setSerialNumber(serialNumber);
            mat.setMatLocation("창고");
            mat.setCalcMethod(1);
            mat.setThreshold(2);
            mat.setProductOrderCnt(10);
            mat.setBoxWeight(0);
            matFormList.add(createMatForm(mat, products.get(productSelectedIndex).getId(), Arrays.asList(orderMails)));
        }
    }

    /**
     *  회사 소유기기가 아닐때 validation 오류 발생하는지 체크
     */
    @Test
    public void haveNotDevice(){

        //given
        Mat mat = new Mat();
        mat.setSerialNumber(notExistSerialNumber);
        mat.setMatLocation("창고");
        mat.setCalcMethod(1);
        mat.setThreshold(2);
        mat.setProductOrderCnt(10);
        mat.setBoxWeight(0);
        MatForm matForm= createMatForm(mat,this.matFormList.get(0).getProductId(), this.matFormList.get(0).getMailRecipients());
        //when
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> matService.createMatsService(matForm, company, user));
        Assertions.assertEquals("소유 기기가 아니거나 존재하지않는 기기입니다.", e.getMessage());

    }

    /**
     * 매트 serialNumber 중복될때
     */
    @Test
    public void duplicateDeviceSerialNumber(){
        List<Mat> mats = matRepository.findAll();

        if(mats.size() > 0 && matFormList.size() > 0){

            MatForm matForm= createMatForm(mats.get(0),this.matFormList.get(0).getProductId(), this.matFormList.get(0).getMailRecipients());
            IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> matService.createMatsService(matForm, company, user));
            Assertions.assertEquals("매트가 이미 등록되어 있습니다.", e.getMessage());
        }
    }

    /**
     *  매트 등록
     */
    @Test

    public void create(){

        for(MatForm matForm : matFormList){
            matService.createMatsService(matForm, company, user);
            //rbd 저장되었는지
            Optional<Mat> findMat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber());
            Assertions.assertEquals(true, findMat.isPresent());

            //dynamoMat 저장 되었는지 체크
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);

            //dynamodb 주문메일 정상저장되었는지
            OrderMailRecipients orderMailRecipients = dynamoDBMapper.load(OrderMailRecipients.class,company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertEquals(orderMailRecipients.getMailRecipients(), matForm.getMailRecipients());

        }

    }


    /**
     * 상품변경
     */
    @Test
    public void updateProduct(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            int num = random.nextInt(products.size() - 1);
            matForm.setProductId(products.get(num).getId());
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getProductId(), mat.getProduct().getId());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getProductId(), dynamoMat.getProductId());
        }


    }

    /**
     * 계산방식 변경
     */
    @Test
    public void updateCalcMethod(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            Mat mat = matForm.getMat();
            mat.setCalcMethod(random.nextInt(1));
            matForm.setMat(mat);
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getMat().getCalcMethod(), mat.getCalcMethod());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getMat().getCalcMethod(), dynamoMat.getCalcMethod());
        }
    }


    /**
     * 임계값 변경
     */
    @Test
    public void updateThreshold(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            Mat mat = matForm.getMat();
            mat.setThreshold(random.nextInt(20));
            matForm.setMat(mat);
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getMat().getThreshold(), mat.getThreshold());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getMat().getThreshold(), dynamoMat.getThreshold());
        }
    }

    /**
     * 위치 변경
     */
    @Test
    public void updateLocation(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            Mat mat = matForm.getMat();
            mat.setMatLocation("창고");
            matForm.setMat(mat);
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getMat().getMatLocation(), mat.getMatLocation());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getMat().getMatLocation(), dynamoMat.getMatLocation());
        }
    }

    /**
     * 상품주문갯수 변경
     */
    @Test
    public void updateProductOrderCnt(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            Mat mat = matForm.getMat();
            mat.setProductOrderCnt(random.nextInt(20));
            matForm.setMat(mat);
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getMat().getProductOrderCnt(), mat.getProductOrderCnt());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getMat().getProductOrderCnt(), dynamoMat.getProductOrderCnt());
        }
    }

    /**
     * 박스무게 변경
     */
    @Test
    public void updateBoxWeight(){
        create();

        List<MatForm> arr = new ArrayList<>();
        Random random = new Random();
        for(MatForm matForm : matFormList){
            Mat mat = matForm.getMat();
            mat.setBoxWeight(random.nextInt(20));
            matForm.setMat(mat);
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        matService.updateMatsService(company, matForms ,user);
        //rdb에 업데이트 되었는지
        for(MatForm matForm : arr){
            Mat mat = matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).get();
            Assertions.assertEquals(matForm.getMat().getBoxWeight(), mat.getBoxWeight());
        }

        //dynamodb에 업데이트 되었는지
        for(MatForm matForm : arr){
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNotNull(dynamoMat);
            Assertions.assertEquals(matForm.getMat().getBoxWeight(), dynamoMat.getBoxWeight());
        }
    }
    /**
     * 주문이메일 변경
     */
    @Test
    public void updateOrderMail(){
        create();
        List<OrderMailRecipients> orderMailRecipients = new ArrayList<>();
        for(MatForm matForm : matFormList){
            OrderMailRecipients obj = new OrderMailRecipients();
            obj.setMatSerialNumber(matForm.getMat().getSerialNumber());
            obj.setMailRecipients(Arrays.asList(new String[]{"2543@qq.com"}));
            orderMailRecipients.add(obj);
        }

        matService.updateMatsEmailService(orderMailRecipients, company);

        for(OrderMailRecipients o : orderMailRecipients){
            OrderMailRecipients find = dynamoDBMapper.load(OrderMailRecipients.class, company.getId(), o.getMatSerialNumber());
            Assertions.assertEquals(o.getMailRecipients(), find.getMailRecipients());
        }

    }

    /**
     * 양도하기
     */
    @Test
    public void matsToCompany(){
        create();

        List<MatForm> arr = new ArrayList<>();

        for(MatForm matForm : matFormList){
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);

        Company toCompany = generateEntity.createCompany(null,true);

        matService.matsToOtherCompanyService(company,matForms, toCompany.getCompanyCode(), user);

//        Assertions.assertNull(matRepository.);

        for(MatForm matForm : arr){
            Assertions.assertNull(matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).orElse(null));
            Assertions.assertNull(dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber()));
            OwnDevice ownDevice = ownDeviceRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).orElse(null);
            Assertions.assertEquals(ownDevice.getCompany().getId(), toCompany.getId());
        }

    }


    /**
     * 매트 삭제
     */
    @Test
    public void delete(){
        create();
        List<MatForm> arr = new ArrayList<>();

        for(MatForm matForm : matFormList){
            arr.add(matForm);
        }
        MatFormList matForms = new MatFormList();
        matForms.setMatForms(arr);
        matService.deleteMatsService(company, matForms, user);

        for(MatForm matForm : arr){
            Assertions.assertNull(matRepository.findBySerialNumber(matForm.getMat().getSerialNumber()).orElse(null));
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNull(dynamoMat);

            OrderMailRecipients orderMailRecipients = dynamoDBMapper.load(OrderMailRecipients.class, company.getId(), matForm.getMat().getSerialNumber());
            Assertions.assertNull(orderMailRecipients);
        }

    }


//    @Test
//    public void batchLoadTest(){
//        create();
//        List<KeyPair> keyPairList = new ArrayList<>();
//
//        for(MatForm matForm : matFormList){
//            KeyPair keyPair = new KeyPair();
//            keyPair.withHashKey(company.getId());
//            keyPair.withRangeKey(matForm.getMat().getSerialNumber());
//            keyPairList.add(keyPair);
//        }
//
//        List<Object> results = dynamoQuery.batchLoad(DynamoMat.class, keyPairList);
//        System.out.println("=====");
//        System.out.println("result size: "+ results.size());
//        System.out.println("matForm size: "+ matFormList.size());
//
//        Assertions.assertEquals(matFormList.size(), results.size());
//
//    }

    /**
     *  테스트중 insert한 dynamodb 내용삭제
     */
    @AfterAll
    public void completeAllTest(){
        System.out.println("completeAllTest");

        int companyId = company.getId();

        List<OwnDevice> ownDeviceList = ownDeviceRepository.findAllBySerialNumberIn(Arrays.asList(serialNumberList));
        for(OwnDevice ownDevice : ownDeviceList){
            ownDeviceRepository.delete(ownDevice);
        }

        userRepository.delete(user);
        companyRepository.delete(company);

        for(String serialNumber : serialNumberList){

            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, companyId, serialNumber);
            if(dynamoMat != null)
            dynamoDBMapper.delete(dynamoMat);

            OrderMailRecipients mail = dynamoDBMapper.load(OrderMailRecipients.class, companyId, serialNumber);
            if(mail!=null)
            dynamoDBMapper.delete(mail);



        }
        DynamoDBQueryExpression<MatLog> queryExpression = MatLog.queryExpression(company, false);
        List<MatLog> matLogs = dynamoQuery.exeQuery(MatLog.class, queryExpression);

        if(matLogs.size() > 0){
            dynamoDBMapper.batchDelete(matLogs);
        }
    }

    private MatForm createMatForm(Mat mat,int productId,List<String> mails){
        MatForm matForm = new MatForm();

        matForm.setMat(mat);
        matForm.setProductId(productId);
        matForm.setMailRecipients(mails);
        return matForm;
    }



}
