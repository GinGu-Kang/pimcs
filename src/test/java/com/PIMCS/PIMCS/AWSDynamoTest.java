package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.noSqlDomain.*;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.service.InOutHistoryService;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Random;

//@ContextConfiguration()
//@RunWith(SpringRunner.class)
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
    @Autowired
    private  DynamoQuery dynamoQuery;

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
    public void createOrderHistory(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(OrderHistory.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }

    @Test
    public void createDynamoMat(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(DynamoMat.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }


    @Test
    public void createDynamoProduct()
    {
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(DynamoProduct.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }

    @Test
    public void createMatLog(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(MatLog.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }

    @Test
    public void createProductLog(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(ProductLog.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

//        createTableRequest.getGlobalSecondaryIndexes().forEach(
//                idx -> idx
//                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
//                        .withProjection(new Projection().withProjectionType("ALL"))
//        );
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }


    @Test
    public void createInoutHistory(){
        DynamoMat dynamoMat = dynamoDBMapper.load(DynamoMat.class, 2, "WS01E210001");

        Random random = new Random();
        for(int i=1; i <= 32; i++){

            LocalDateTime createdAt;
            try{
                Thread.sleep(500);
            }catch (Exception e){

            }

            LocalDateTime new_date = LocalDateTime.now();
            if(i == 32){
              createdAt = LocalDateTime.of(2022,11, 1,new_date.getHour() , new_date.getMinute(),new_date.getSecond(), new_date.getNano());
            }else {
                createdAt = LocalDateTime.of(2022,10, i, new_date.getHour() , new_date.getMinute(),new_date.getSecond(), new_date.getNano());
            }

            int weight = random.nextInt(400 - 40 + 1) + 40;
            System.out.println(createdAt);

            InOutHistory inOutHistory = InOutHistory.builder()
                    .matSerialNumber(dynamoMat.getMatSerialNumber())
                    .currentWeight(weight)
                    .productId(dynamoMat.getProductId())
                    .updateCurrentInventory(weight)
                    .isSendEmail(0)
                    .threshold(50)
                    .productWeight(136)
                    .productName("츄파춥스")
                    .calcMethod(0)
                    .updateCnt(10)
                    .companyId(2)
                    .inOutStatus("IN")
                    .productCode("code_food")
                    .matLocation("창고3")
                    .boxWeight(0)
                    .createdAt(createdAt)

                    .build();
            dynamoDBMapper.save(inOutHistory);
        }

    }

}


