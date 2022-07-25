package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.form.InOutHistorySearchForm;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "ProductLog")
public class ProductLog {
    @DynamoDBHashKey
    private int companyId;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private String productCode;

    @DynamoDBAttribute
    private String userName;

    @DynamoDBAttribute
    private String userEmail;

    @DynamoDBAttribute
    private String action;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public ProductLog(){}

    public static DynamoDBQueryExpression<ProductLog> queryExpression(Company company, boolean scanIndexForward){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        return new DynamoDBQueryExpression<ProductLog>()
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(scanIndexForward)
                .withExpressionAttributeValues(eav);
    }

    public static  DynamoDBQueryExpression<ProductLog> searchQueryExpression(Company company, InOutHistorySearchForm searchForm){

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
        return new DynamoDBQueryExpression<ProductLog>()
                .withKeyConditionExpression(query)
                .withFilterExpression("contains (productName, :query)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
    }
}
