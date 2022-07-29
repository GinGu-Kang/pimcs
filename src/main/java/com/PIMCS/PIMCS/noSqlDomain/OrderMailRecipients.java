package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.Utils.Search;
import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.DynamoQueryForm;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "OrderMailRecipients")
public class OrderMailRecipients {

    @DynamoDBAttribute
    private String matSerialNumber;

    @DynamoDBHashKey(attributeName = "companyId")
    private int companyId;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    private String rangekey;

    @DynamoDBAttribute
    private List<String> mailRecipients;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public OrderMailRecipients(){}

    /**
     * 시러얼번호 리스트로 조회
     */
    public static List<OrderMailRecipients> findBySerialNumbers(DynamoQuery dynamoQuery,List<String> serialNumbers, Company company){
        DynamoQueryForm dynamoQueryForm = dynamoQuery.getOrQuery("matSerialNumber", serialNumbers);

        Map<String, AttributeValue> eav = dynamoQueryForm.getEav();
        String query = dynamoQueryForm.getQuery();
        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));

        System.out.println("=================");

        System.out.println(query);
        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav);


        return dynamoQuery.exeQuery(OrderMailRecipients.class, queryExpression);

    }

    /**
     * 시리얼번호로 조회
     */
    public static  List<OrderMailRecipients> findBySerialNumber(DynamoQuery dynamoQuery,Company company, String serialNumber){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":serialNumber", new AttributeValue().withS(serialNumber));
        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));
        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression("companyId=:companyId AND rangekey=:serialNumber")
                .withExpressionAttributeValues(eav);

        return dynamoQuery.exeQuery(OrderMailRecipients.class, queryExpression);
    }

    public static OrderMailRecipients save(DynamoDBMapper dynamoDBMapper,Mat mat, List<String> mailRecipients){
        OrderMailRecipients orderMailRecipients = OrderMailRecipients.builder()
                .rangekey(mat.getSerialNumber())
                .matSerialNumber(mat.getSerialNumber())
                .mailRecipients(mailRecipients)
                .companyId(mat.getCompany().getId())
                .createdAt(LocalDateTime.now())
                .build();
        dynamoDBMapper.save(orderMailRecipients);
        return orderMailRecipients;

    }

    public static void update(
            DynamoDBMapper dynamoDBMapper,
            DynamoQuery dynamoQuery,
            List<OrderMailRecipients> orderMailRecipientsList,
            Company company){

        List<String> serialNumbers = new ArrayList<>();
        orderMailRecipientsList.forEach(o -> serialNumbers.add(o.getMatSerialNumber()));

        DynamoQueryForm dynamoQueryForm = dynamoQuery.getOrQuery("matSerialNumber", serialNumbers);
        Map<String, AttributeValue> eav = dynamoQueryForm.getEav();
        String query = dynamoQueryForm.getQuery();

        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav);

        List findOrderMails = dynamoQuery.exeQuery(OrderMailRecipients.class, queryExpression);
        if(orderMailRecipientsList.size() != findOrderMails.size()){
            throw new IllegalStateException("Synchronization error");
        }

        List<String> findSerialNumbers = new ArrayList<>();
        findOrderMails.forEach(o -> {
            OrderMailRecipients orderMail = (OrderMailRecipients) o;
            findSerialNumbers.add(orderMail.getMatSerialNumber());
        });

        List<OrderMailRecipients> save = new ArrayList<>();

        for(OrderMailRecipients o : orderMailRecipientsList){
            int index = Search.binarySearch(o.getMatSerialNumber(), findSerialNumbers);
            if(index == -1) continue;
            OrderMailRecipients find = (OrderMailRecipients) findOrderMails.get(index);
            find.setMailRecipients(o.getMailRecipients());
            save.add(find);
        }

        dynamoDBMapper.batchSave(save);

    }

    public static void delete(DynamoDBMapper dynamoDBMapper,
                              DynamoQuery dynamoQuery,
                              List<Mat> mats,
                              Company company){

        List<String> serialNumbers = new ArrayList<>();
        mats.forEach(o -> serialNumbers.add(o.getSerialNumber()));

        DynamoQueryForm dynamoQueryForm = dynamoQuery.getOrQuery("matSerialNumber", serialNumbers);
        Map<String, AttributeValue> eav = dynamoQueryForm.getEav();
        String query = dynamoQueryForm.getQuery();

        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav);

        List findOrderMails = dynamoQuery.exeQuery(OrderMailRecipients.class, queryExpression);

        dynamoDBMapper.batchDelete(findOrderMails);

    }



}
