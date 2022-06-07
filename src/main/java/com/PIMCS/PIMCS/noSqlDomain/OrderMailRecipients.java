package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.config.AWSConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@DynamoDBTable(tableName = "OrderMailRecipients")
public class OrderMailRecipients {
    @DynamoDBHashKey(attributeName = "matSerialNumber")
    private String matSerialNumber;

    @DynamoDBAttribute
    private List<String> mailRecipients;

    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    public static OrderMailRecipients findBySerialNumber(String serialNumber, List<OrderMailRecipients> orderMailRecipients){

        for(OrderMailRecipients o : orderMailRecipients){
            if(o.matSerialNumber.equals(serialNumber)){
                return  o;
            }
        }
        return null;
    }

    public static List<String> getSerialNumberList(List<OrderMailRecipients> list){
        List<String> result = new ArrayList<>();

        for(OrderMailRecipients o : list){
            result.add(o.getMatSerialNumber());
        }
        return result;
    }
}
