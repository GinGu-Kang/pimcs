package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.config.AWSConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "InOutHistory")
public class InOutHistory {
    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byConpanyId")
    private Integer companyId;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "bySerialNumber")
    private String matSerialNumber;

    @DynamoDBAttribute
    private String matLocation;

    @DynamoDBAttribute
    private String productCode;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private Integer updateWeight;

    @DynamoDBAttribute
    private Integer updateCnt;

    @DynamoDBAttribute
    private String inOutStatus;

    @DynamoDBAttribute
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "byConpanyId")
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;



    public InOutHistory(){}

    public static InOutHistory findByDay(List<InOutHistory> inOutHistories,LocalDate date, String serialNumber){
        InOutHistory result = null;

        for(InOutHistory inOutHistory : inOutHistories){
            LocalDate inDate = LocalDate.of(
                    inOutHistory.getCreatedAt().getYear(),
                    inOutHistory.getCreatedAt().getMonth(),
                    inOutHistory.getCreatedAt().getDayOfMonth()
            );
            System.out.println("시간");
            System.out.println(inDate);
            if(inOutHistory.getMatSerialNumber().equals(serialNumber) &&
                    (date.isAfter(inDate) || date.isEqual(inDate))) {
                System.out.println("hahahaha");
                result = inOutHistory;

            }else{
                break;
            }

        }
        return result;
    }
}
