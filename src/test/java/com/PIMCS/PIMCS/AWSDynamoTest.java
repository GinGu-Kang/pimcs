package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.service.InOutHistoryService;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.dynamodbv2.xspec.M;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
public class AWSDynamoTest {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private  MatRepository matRepository;
    @Autowired
    InOutHistoryService inOutHistoryService;
    @Test
    public void createTable(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(InOutHistory.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );

        //System.out.println(createTableRequest.toString());
        System.out.println(amazonDynamoDB);
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }

    @Test
    public void createOrderMainRecipients(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(OrderMailRecipients.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

//        createTableRequest.getGlobalSecondaryIndexes().forEach(
//                idx -> idx
//                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
//                        .withProjection(new Projection().withProjectionType("ALL"))
//        );

        //System.out.println(createTableRequest.toString());
        System.out.println(amazonDynamoDB);
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);

    }


    @Test
    public void putItemTest(){
        List<Mat> mats = matRepository.findAll();
        Mat mat=mats.get(mats.size()-1);

        InOutHistory inOutHistory = InOutHistory.builder()
                .companyId(mat.getCompany().getId())
                .matSerialNumber(mat.getSerialNumber())
                .matLocation(mat.getMatLocation())
                .productCode(mat.getProduct().getProductCode())
                .productName(mat.getProduct().getProductName())
//                .productName("전룡호")
//                .updateWeight(100)
                .updateCnt(10)
                .updateCurrentInventory(30)
//                .updateWeight(10)
                .inOutStatus(((int)(Math.random()*2) == 0) ? "IN" : "OUT")
                .createdAt(LocalDateTime.of(LocalDate.now(), LocalTime.of(15,00)))
//                .createdAt(LocalDateTime.of(LocalDate.of(2022,3,15), LocalTime.now()))
                .build();
        dynamoDBMapper.save(inOutHistory);
    /*    Assertions.assertThat(inOutHistory.getMatSerialNumber()).isNotEmpty();*/
    }

    @Test
    public void putEmailItem(){
        OrderMailRecipients orderMailRecipients = new OrderMailRecipients();
        List<String> list = new ArrayList<>();
        list.add("jdijo");
        list.add("jidoj");
        orderMailRecipients.setMatSerialNumber("jsiojoi");
        orderMailRecipients.setMailRecipients(list);
        orderMailRecipients.setCreatedAt(LocalDateTime.of(LocalDate.now(), LocalTime.now()));

        dynamoDBMapper.save(orderMailRecipients);
    }

    @Test
    public void kdopkpd(){
        List<Mat> mats = matRepository.findAll();
        Mat mat=mats.get(mats.size()-1);
//
//        for(int i=0; i<50; i++){
//            InOutHistory inOutHistory = InOutHistory.builder()
//                    .companyId(mat.getCompany().getId())
//                    .matSerialNumber(mat.getSerialNumber())
//                    .matLocation(mat.getMatLocation())
//                    .productCode(mat.getProduct().getProductCode())
//                    .productName(mat.getProduct().getProductName())
////                .productName("전룡호")
//                    .updateWeight(100)
//                    .updateCnt(10)
//                    .updateCurrentInventory(30)
//                    .inOutStatus(((int)(Math.random()*2) == 0) ? "IN" : "OUT")
//                    .createdAt(LocalDateTime.of(LocalDate.now(), LocalTime.now()))
//    //                .createdAt(LocalDateTime.of(LocalDate.of(2022,3,15), LocalTime.now()))
//                    .build();
//            dynamoDBMapper.save(inOutHistory);
 //       }
    }
    @Test
    public void updateItem(){
        Company company = companyRepository.findById(18).get();
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);

        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompany(company);
        for(InOutHistory inOutHistory : inOutHistories){
            inOutHistory.setInOutStatus("OUT");
        }
        dynamoDBMapper.batchSave(inOutHistories);




    }


    @Test
    public void abc(){
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        for(int i=0; i<100000; i++){
//            dynamoDBUtils.loadByCompany()
        }
    }


    @Test
    public void inOutHistoryServiceTest(){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withN("18"));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(true)
                .withKeyConditionExpression("companyId = :v1")
                .withExpressionAttributeValues(eav);


        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        for(InOutHistory inOutHistory : inOutHistories){
            System.out.println(inOutHistory.toString());
        }
//        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);

//        Assertions.assertThat(inOutHistories.size()).isEqualTo(1);
//        System.out.println(inOutHistories.size());
//        for(InOutHistory in : inOutHistories){
//            System.out.println("=====");
//            System.out.println(in.toString());
//            System.out.println("=======");
//        }

    }

    @Test
    public void graphServiceTest(){
        LocalDateTime start = LocalDateTime.of(LocalDate.of(2022,3,14), LocalTime.now());
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withS("s"));
        eav.put(":start",new AttributeValue().withS("2022-03-01"));
        eav.put(":end",new AttributeValue().withS("2022-03-28"));


        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withKeyConditionExpression("matSerialNumber = :v1 AND createdAt BETWEEN :start AND :end")
                .withExpressionAttributeValues(eav);


        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        for(InOutHistory inOutHistory : inOutHistories){
            System.out.println(inOutHistory.toString());
        }

    }

    @Test
    public void paginationTest(){

        int page = 1;
        int size = 1;

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withN("18"));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :v1")
                .withExpressionAttributeValues(eav);



    }

    @Test
    public void testSlice(){
        LocalDate curDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(curDate.getYear(),curDate.getMonth(),1);
        Company company = companyRepository.findById(18).get();
//        MatSerialNumberForm matSerialNumberForm = new MatSerialNumberForm();
//        List<String> arr = new ArrayList<>();
//        arr.add("s");
//        matSerialNumberForm.setSerialNumberList(arr);
//
//
//        inOutHistoryService.inOutHistoryDayGraphService(company,matSerialNumberForm);

    }

    @Test
    public void testDate(){
        LocalDate curDate = LocalDate.now();
//        int startMonth  = Integer.parseInt(curDate.getMonth().toString()) - 1;
//        LocalDate startDate = LocalDate.of(curDate.getYear(),curDate.getMonth(),1);
//        startDate = startDate.minusMonths(1);
//        System.out.println(startDate.toString());

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddThh:mm");
        System.out.println(dateTimeFormatter.format(localDateTime));
    }



    @Test
    public void stram(){
//        List<String> arr = new ArrayList<>();
//        arr.add("a");
//        arr.add("a");
//        arr.add("b");

        LocalDate localDate = LocalDate.of(2022,2,1);
        localDate = localDate.plusMonths(1);
        System.out.println("=======");
        System.out.println(localDate.toString());
        System.out.println(localDate.getMonthValue());
        System.out.println(localDate.lengthOfMonth());
        System.out.println("=======");
    }

    @Test
    public void like(){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf("18")));
        eav.put(":productName",new AttributeValue().withS("호"));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :companyId")
                .withFilterExpression("contains (productName, :productName)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        System.out.println("============");
        System.out.println(inOutHistories.size());
        for(InOutHistory in : inOutHistories){
            System.out.println(in.toString());
        }

    }

    @Test
    public void updateMatDynamodbTest(){
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        Mat mat = matRepository.findBySerialNumber("serial_skjoij").get();
        mat.setMatLocation("창고");
        List<Mat> mats = new ArrayList<>();
        mats.add(mat);

        dynamoDBUtils.updateMat(mats);
    }
}


