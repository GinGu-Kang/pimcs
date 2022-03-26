package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.MatSerialNumberForm;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.service.InOutHistoryService;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

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
        Assertions.assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isEqualTo(true);
    }

    @Test
    public void putItemTest(){
        InOutHistory inOutHistory = InOutHistory.builder()
                .id("24522c98-8f03-4a35-b594-d06c1b012e9a")
                .companyId(24)
                .matSerialNumber("ss")
                .matLocation("화상실")
                .productCode("code_1644298216412")
                .productName("컴퓨터1644298216412")
                .updateWeight(100)
                .updateCnt(10)
                .inOutStatus("IN")
                .createdAt(LocalDateTime.of(LocalDate.now(), LocalTime.now()))
                .build();
        dynamoDBMapper.save(inOutHistory);
        Assertions.assertThat(inOutHistory.getId()).isNotEmpty();
    }

    @Test
    public void loadData(){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withN("18"));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :v1")
                .withExpressionAttributeValues(eav);


        QueryResultPage<InOutHistory> inOutHistoryQueryResultPage = dynamoDBMapper.queryPage(InOutHistory.class, queryExpression);
        for(InOutHistory inOutHistory : inOutHistoryQueryResultPage.getResults()){
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
        MatSerialNumberForm matSerialNumberForm = new MatSerialNumberForm();
        List<String> arr = new ArrayList<>();
        arr.add("s");
        matSerialNumberForm.setSerialNumberList(arr);


        inOutHistoryService.inOutHistoryDayGraphService(company,matSerialNumberForm);

    }

    @Test
    public void testDate(){
        LocalDate curDate = LocalDate.now();
//        int startMonth  = Integer.parseInt(curDate.getMonth().toString()) - 1;
        LocalDate startDate = LocalDate.of(curDate.getYear(),curDate.getMonth(),1);
        startDate = startDate.minusMonths(1);
        System.out.println(startDate.toString());
    }



    @Test
    public void stram(){
//        List<String> arr = new ArrayList<>();
//        arr.add("a");
//        arr.add("a");
//        arr.add("b");

        AWSConfig.LocalDateTimeConverter converter = new AWSConfig.LocalDateTimeConverter();
        /*arr.stream().filter(o -> o.equals("a")).skip();;
        System.out.println(a.skip(a.count() - 1).findFirst().get());*/
        System.out.println("================");
        System.out.println(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
        System.out.println(converter.convert(LocalDateTime.of(LocalDate.now(), LocalTime.now())));
        System.out.println("===========");
    }
}


