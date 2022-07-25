package com.PIMCS.PIMCS.service;



import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.MatForm;
import com.PIMCS.PIMCS.noSqlDomain.DynamoMat;
import com.PIMCS.PIMCS.noSqlDomain.DynamoProduct;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
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

    private final int companyId =1;

    private List<String> checkedSerialNumber = new ArrayList<>();
    private List<Integer> checkedProductId = new ArrayList<>();
    private List<MatForm> matFormList = new ArrayList<>();


//    @Test
//    @org.junit.jupiter.api.Order(1)
//    public void save(){
//        String[] mails = new String[]{"ryongho1997@gmail.com"};
//        Company company = companyRepository.findById(companyId).get();
//
//        //db에저장 체크
//        Mat mat =createMat();
//        MatForm matForm = createMatForm(mat,5, Arrays.asList(mails));
//        matService.createMat(matForm, company);
//        checkedSerialNumber.add(mat.getSerialNumber());
//        checkedProductId.add(matForm.getProductId());
//        matFormList.add(matForm);
//        Assertions.assertEquals(mat, matRepository.findBySerialNumber(mat.getSerialNumber()).get());
//
//        //dynamodb 주문메일 저장체크
//        OrderMailRecipients orderMailRecipients = dynamoDBMapper.load(OrderMailRecipients.class, mat.getSerialNumber());
//        Assertions.assertEquals(orderMailRecipients.getMailRecipients(), Arrays.asList(mails));
//
//        //dynamoMat 정상저장 되었는지 체크
//        DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, mat.getSerialNumber());
//        Assertions.assertNotNull(dynamoMat);
//
//        //dynamoProduct 정상저장 되었는지 체크
//        DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class, mat.getProduct().getId());
//        Assertions.assertNotNull(dynamoProduct);
////        rollbackDynamo();
//    }


    @Test
    @org.junit.jupiter.api.Order(2)
    public void updateProductTest(){

    }


    @Test
    @org.junit.jupiter.api.Order(3)
    public void rollbackDynamo(){
        System.out.println("=====");
        System.out.println(checkedSerialNumber.size());
        for(String serialNumber : checkedSerialNumber){
            System.out.println("serialNumber: "+ serialNumber);
            DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, serialNumber);
            dynamoDBMapper.delete(dynamoMat);

            OrderMailRecipients mail = dynamoDBMapper.load(OrderMailRecipients.class, serialNumber);
            dynamoDBMapper.delete(mail);
        }

        for(int productId : checkedProductId){

            DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class, productId);
            dynamoDBMapper.delete(dynamoProduct);
        }
    }

    private Mat createMat(){
//        Mat mat = new Mat(dynamoDBMapper);
//        mat.setSerialNumber("test_"+UUID.randomUUID().toString());
//        mat.setMatLocation("창고");
//        mat.setCalcMethod(1);
//        mat.setThreshold(2);
//        mat.setProductOrderCnt(10);
//        mat.setBoxWeight(0);
        return null;
    }

    private MatForm createMatForm(Mat mat,int productId,List<String> mails){
        MatForm matForm = new MatForm();

        matForm.setMat(mat);
        matForm.setProductId(productId);
        matForm.setMailRecipients(mails);
        return matForm;
    }



}
