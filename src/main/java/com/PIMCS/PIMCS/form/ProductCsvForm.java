package com.PIMCS.PIMCS.form;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.List;

@Data
public class ProductCsvForm {
    private List<Integer> productIdList;
}
