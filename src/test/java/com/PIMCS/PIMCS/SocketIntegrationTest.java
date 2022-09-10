package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.noSqlDomain.DynamoMat;
import com.PIMCS.PIMCS.noSqlDomain.DynamoProduct;
import com.PIMCS.PIMCS.utils.NonSslSocket;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class SocketIntegrationTest {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private List<DynamoMat> dynamoMatList;
    private List<DynamoProduct> dynamoProducts;

    private int companyId;

    private int maxSize = 2;

    private int weightLength = 5;



    @BeforeEach
    public void setup(){
        companyId = 0;
        dynamoMatList = new ArrayList<>();
        dynamoProducts = new ArrayList<>();

        for(int i=0; i<maxSize; i++){

            dynamoMatList.add(createDynamoMat(i));
            dynamoProducts.add(createDynamoProduct(i));
            dynamoDBMapper.save(dynamoMatList.get(i));
            dynamoDBMapper.save(dynamoProducts.get(i));
        }

        /*dynamoDBMapper.batchSave(dynamoProducts);*/
    }



    @Test
    @DisplayName("매트 무게 변경 테스트")
    public void changeWeightTest(){
//        NonSslSocket socket = new NonSslSocket();
//        socket.send("ab,WS01E210001,00001");
        List<Integer> weightList = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i< maxSize; i++){
            DynamoMat dynamoMat = dynamoMatList.get(i);
            DynamoProduct dynamoProduct = dynamoProducts.get(i);
            int weight = random.nextInt((100 - 50) + 1) + 50;

            System.out.println(dynamoMat.getMatSerialNumber()+":"+weight);

            weightList.add(weight);
            NonSslSocket socket = new NonSslSocket();
            socket.send("CH,"+dynamoMat.getMatSerialNumber()+","+setPadding(weight+""));
        }

        try {
            Thread.sleep(maxSize * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    @Test
    @DisplayName("매트 오류 체크")
    public void checkError(){
//        NonSslSocket socket = new NonSslSocket();
//        socket.send("ab,WS01E210001,00001");
        List<Integer> weightList = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i< maxSize; i++){
            DynamoMat dynamoMat = dynamoMatList.get(i);
            DynamoProduct dynamoProduct = dynamoProducts.get(i);
            int weight = random.nextInt((100 - 50) + 1) + 50;

            System.out.println(dynamoMat.getMatSerialNumber()+":"+weight);

            weightList.add(weight);
            NonSslSocket socket = new NonSslSocket();
            socket.send("IN,"+dynamoMat.getMatSerialNumber()+","+setPadding(weight+""));
        }

        try {
            Thread.sleep(maxSize * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("스케쥴러 테스트")
    public void schedule(){
        List<Integer> weightList = new ArrayList<>();
        Random random = new Random();

        DynamoMat dynamoMat = dynamoMatList.get(0);
        DynamoProduct dynamoProduct = dynamoProducts.get(0);
        int weight = random.nextInt((100 - 50) + 1) + 50;

        System.out.println(dynamoMat.getMatSerialNumber()+":"+weight);

        weightList.add(weight);
        NonSslSocket socket = new NonSslSocket();
        socket.send("IN,"+dynamoMat.getMatSerialNumber()+","+setPadding(weight+""));

        try {
            Thread.sleep(maxSize * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void delete(){
        System.out.println("after all");
        dynamoDBMapper.batchDelete(dynamoMatList);
        dynamoDBMapper.batchDelete(dynamoProducts);
    }

    public String setPadding(String weight){
        StringBuilder result = new StringBuilder();
        for(int i=0; i<5-weight.length(); i++){
            result.append("0");
        }
        result.append(weight);
        return result.toString();
    }

    public DynamoMat createDynamoMat(int productId){
        String serialNumber = UUID.randomUUID().toString().substring(0,11);
        DynamoMat dynamoMat = new DynamoMat();
        dynamoMat.setCompanyId(companyId);
        dynamoMat.setRangekey(serialNumber);
        dynamoMat.setMatSerialNumber(serialNumber);
        dynamoMat.setThreshold(2);
        dynamoMat.setProductId(productId);
        dynamoMat.setCalcMethod(1);
        dynamoMat.setMatLocation("test");
        dynamoMat.setBoxWeight(0);
        dynamoMat.setProductOrderCnt(10);
        dynamoMat.setCreatedAt(LocalDateTime.now());
        dynamoMat.setUpdatedAt(LocalDateTime.now());
        return dynamoMat;
    }

    public DynamoProduct createDynamoProduct(int productId){
        DynamoProduct dynamoProduct = new DynamoProduct();
        dynamoProduct.setCompanyId(companyId);
        dynamoProduct.setRangekey(productId);
        dynamoProduct.setProductId(productId);
        dynamoProduct.setProductWeight(2);
        dynamoProduct.setProductName(UUID.randomUUID().toString().substring(0,12));
        dynamoProduct.setProductCode(UUID.randomUUID().toString().substring(0,12));
        dynamoProduct.setCreatedAt(LocalDateTime.now());
        dynamoProduct.setUpdatedAt(LocalDateTime.now());
        return dynamoProduct;

    }


}
