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
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "OrderHistory")
public class OrderHistory {
    @DynamoDBHashKey(attributeName = "companyId")
    private int companyId;

    @DynamoDBAttribute
    private String matSerialNumber;

    @DynamoDBAttribute
    private String location;

    @DynamoDBAttribute
    private List<String> mailRecipients;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private String productCode;

    @DynamoDBAttribute
    private int inventoryCnt;

    @DynamoDBAttribute
    private int threshold;

    @DynamoDBAttribute
    private int orderCnt;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public OrderHistory(){}

    public static DynamoDBQueryExpression<OrderHistory> queryExpression(Company company, boolean scanIndexForward){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        return new DynamoDBQueryExpression<OrderHistory>()
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(scanIndexForward)
                .withExpressionAttributeValues(eav);
    }

    public static  DynamoDBQueryExpression<OrderHistory> searchQueryExpression(Company company, InOutHistorySearchForm orderSearchForm){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":query", new AttributeValue().withS(orderSearchForm.getQuery()));
        String query = "companyId = :companyId";

        if(orderSearchForm.getStartDate() != null && orderSearchForm.getEndDate() != null){
            LocalDate endDate = orderSearchForm.getEndDate().plusDays(1);
            eav.put(":startDate", new AttributeValue().withS(orderSearchForm.getStartDate().toString()));
            eav.put(":endDate", new AttributeValue().withS(endDate.toString()));
            query = "companyId = :companyId AND createdAt BETWEEN :startDate AND :endDate";
        }
        return new DynamoDBQueryExpression<OrderHistory>()
                .withKeyConditionExpression(query)
                .withFilterExpression("contains (productName, :query)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
    }
}
