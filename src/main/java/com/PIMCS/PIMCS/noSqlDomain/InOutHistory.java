package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.form.InOutHistorySearchForm;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "InOutHistory")
public class InOutHistory {

    @DynamoDBHashKey(attributeName = "matSerialNumber")
    private String matSerialNumber;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byConpanyId")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "byProductId")
    private Integer companyId;

    @DynamoDBAttribute
    private String matLocation;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byProductId")
    private int productId;

    @DynamoDBAttribute
    private String productCode;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private Integer productWeight;

    @DynamoDBAttribute
    private Integer currentWeight;



    @DynamoDBAttribute
    private Integer updateCurrentInventory; // 매트에서 받은 현재 무게

    @DynamoDBAttribute
    private Integer updateCnt; // 수정 갯수, 계산방식 갯수일떄

    @DynamoDBAttribute
    private Integer boxWeight;

    @DynamoDBAttribute
    private int threshold;

    @DynamoDBAttribute
    private Integer isSendEmail;

    @DynamoDBAttribute
    private Integer calcMethod; //계산방식(0이면 무게, 1이면 갯수)

    @DynamoDBAttribute
    private String inOutStatus;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "byConpanyId")
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public InOutHistory(){}

    /**
     *날짜에 해당하는 입출고내역 찾기
     */
    public static InOutHistory findByLocalDate(List<InOutHistory> inOutHistories,LocalDate date, String serialNumber){
        InOutHistory result = null;

        for(InOutHistory inOutHistory : inOutHistories){
            LocalDate inDate = LocalDate.of(
                    inOutHistory.getCreatedAt().getYear(),
                    inOutHistory.getCreatedAt().getMonth(),
                    inOutHistory.getCreatedAt().getDayOfMonth()
            );

            if(inOutHistory.getMatSerialNumber().equals(serialNumber) &&
                    (date.isAfter(inDate) || date.isEqual(inDate))) {
                result = inOutHistory;
            }else{
                break;
            }
        }
        return result;
    }
    /**
     *날짜와 시간에 해당하는 입출고내역 찾기
     */
    public static InOutHistory findByLocalDateTime(List<InOutHistory> inOutHistories,LocalDateTime date, String serialNumber){
        InOutHistory result = null;
        date = date.plusMinutes(1);
        for(InOutHistory inOutHistory : inOutHistories){
            System.out.println(inOutHistory.getCreatedAt());
            if(inOutHistory.getMatSerialNumber().equals(serialNumber) && date.isAfter(inOutHistory.getCreatedAt())) {
                result = inOutHistory;
            }else{
                break;
            }
        }
        return result;
    }

    public static DynamoDBQueryExpression<InOutHistory> queryExpression(Company company, boolean scanIndexForward){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        return new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withKeyConditionExpression("companyId = :companyId")
                .withConsistentRead(false)
                .withScanIndexForward(scanIndexForward)
                .withExpressionAttributeValues(eav);
    }


    public static  DynamoDBQueryExpression<InOutHistory> searchQueryExpression(Company company, InOutHistorySearchForm searchForm){

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

        return new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withKeyConditionExpression(query)
                .withFilterExpression("contains (matSerialNumber, :query)")
                .withScanIndexForward(false)
                .withConsistentRead(false)
                .withExpressionAttributeValues(eav);
    }

}
