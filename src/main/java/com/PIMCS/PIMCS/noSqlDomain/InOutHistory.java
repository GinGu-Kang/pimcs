package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.config.AWSConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
    private Integer updateWeight;

    @DynamoDBAttribute
    private Integer updateCurrentInventory;

    @DynamoDBAttribute
    private Integer updateCnt;

    @DynamoDBAttribute
    private Integer boxWeight;

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

    public static InOutHistory findByProductCode(String productCode, List<InOutHistory> inOutHistories){

        for(InOutHistory inOutHistory : inOutHistories){
            if(productCode.equals(inOutHistory.getProductCode())){
                return inOutHistory;
            }
        }
        return null;
    }

}
