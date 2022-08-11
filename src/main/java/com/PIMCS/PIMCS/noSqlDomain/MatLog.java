package com.PIMCS.PIMCS.noSqlDomain;


import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.InOutHistorySearchForm;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "MatLog")
public class MatLog {

    @DynamoDBHashKey
    private int companyId;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byMatSerialNumber")
    private String matSerialNumber;

    @DynamoDBAttribute
    private String matLocation;

    @DynamoDBAttribute
    private String userName;

    @DynamoDBAttribute
    private String userEmail;

    @DynamoDBAttribute
    private String action;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "byMatSerialNumber")
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public MatLog(){}

    public static void save(Mat mat, User user,String action,DynamoDBMapper dynamoDBMapper){
        MatLog matLog = MatLog.builder()
                .companyId(user.getCompany().getId())
                .matSerialNumber(mat.getSerialNumber())
                .matLocation(mat.getMatLocation())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .action(action)
                .createdAt(LocalDateTime.now())
                .build();
        dynamoDBMapper.save(matLog);

    }

    public static void batchSave(List<Mat> mats, User user,String action,DynamoDBMapper dynamoDBMapper){
        List<MatLog> save = new ArrayList<>();
        for(Mat mat : mats){
            MatLog matLog = MatLog.builder()
                    .companyId(user.getCompany().getId())
                    .matSerialNumber(mat.getSerialNumber())
                    .matLocation(mat.getMatLocation())
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .action(action)
                    .createdAt(LocalDateTime.now())
                    .build();
            save.add(matLog);
        }
        dynamoDBMapper.batchSave(save);
    }


    public static DynamoDBQueryExpression<MatLog> queryExpression(Company company, boolean scanIndexForward){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        return new DynamoDBQueryExpression<MatLog>()
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(scanIndexForward)
                .withExpressionAttributeValues(eav);
    }

    public static  DynamoDBQueryExpression<MatLog> searchQueryExpression(Company company, InOutHistorySearchForm searchForm){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":query", new AttributeValue().withS(searchForm.getQuery()));
        String query = "companyId = :companyId";

        if(searchForm.getStartDate() != null && searchForm.getEndDate() != null){
            LocalDate endDate = searchForm.getEndDate().plusDays(1);
            eav.put(":startDate", new AttributeValue().withS(searchForm.getStartDate().toString()));
            eav.put(":endDate", new AttributeValue().withS(endDate.toString()));
            query = "companyId = :companyId AND createdAt BETWEEN :startDate AND :endDate";
        }

        System.out.println(eav);
        return new DynamoDBQueryExpression<MatLog>()
                .withKeyConditionExpression(query)
                .withFilterExpression("contains (matSerialNumber, :query)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
    }




}
