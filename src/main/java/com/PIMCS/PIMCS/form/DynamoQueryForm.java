package com.PIMCS.PIMCS.form;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.Data;

import java.util.Map;

@Data
public class DynamoQueryForm {
    private Map<String, AttributeValue> eav;
    private String query;
}
