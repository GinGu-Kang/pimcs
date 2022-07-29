package com.PIMCS.PIMCS.noSqlDomain;


import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.Utils.Search;
import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.form.DynamoQueryForm;
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
@DynamoDBTable(tableName = "DynamoProduct")
public class DynamoProduct {

    @DynamoDBHashKey(attributeName = "companyId")
    private int companyId;

    @DynamoDBRangeKey
    private int rangekey;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byProductId")
    private int productId;


    @DynamoDBAttribute
    private int productWeight;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private String productCode;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "byProductId")
    private LocalDateTime createdAt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;

    public DynamoProduct(){}

    public static DynamoProduct save(DynamoDBMapper dynamoDBMapper, Product product){
        DynamoProduct dynamoProduct = DynamoProduct.builder()
                .rangekey(product.getId())
                .productId(product.getId())
                .companyId(product.getCompany().getId())
                .productWeight(product.getProductWeight())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        dynamoDBMapper.save(dynamoProduct);
        return dynamoProduct;
    }

    public static  void update(DynamoDBMapper dynamoDBMapper, DynamoQuery dynamoQuery, List<Product> products, Company company){
        List<Integer> productIdList = new ArrayList<>();
        products.forEach(o -> productIdList.add(o.getId()));

        DynamoQueryForm dynamoQueryForm = dynamoQuery.getOrQuery("productId", productIdList);
        Map<String, AttributeValue> eav = dynamoQueryForm.getEav();
        String query = dynamoQueryForm.getQuery();
        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<DynamoProduct> queryExpression = new DynamoDBQueryExpression<DynamoProduct>()
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav);

        List dynamoProducts = dynamoQuery.exeQuery(DynamoProduct.class, queryExpression);

        if(dynamoProducts.size() != products.size()){
            throw new IllegalStateException("Synchronization error");
        }

        List<String> dynamoProductIdList = new ArrayList<>();
        dynamoProducts.forEach(o-> {
            DynamoProduct dynamoProduct = (DynamoProduct)  o;
            dynamoProductIdList.add(String.valueOf(dynamoProduct.getProductId()));
        });


        List<DynamoProduct> save = new ArrayList<>();
        for(Product product : products){
            int index = Search.binarySearch(String.valueOf(product.getId()), dynamoProductIdList);
            DynamoProduct dynamoProduct = (DynamoProduct) dynamoProducts.get(index);

            dynamoProduct.setProductCode(product.getProductCode());
            dynamoProduct.setProductWeight(product.getProductWeight());
            dynamoProduct.setProductName(product.getProductName());
            dynamoProduct.setUpdatedAt(LocalDateTime.now());
            save.add(dynamoProduct);
        }

        dynamoDBMapper.batchSave(save);


    }

    public static void delete(DynamoDBMapper dynamoDBMapper, DynamoQuery dynamoQuery, List<Product> products, Company company){
        List<Integer> productIdList = new ArrayList<>();
        products.forEach(o -> productIdList.add(o.getId()));

        DynamoQueryForm dynamoQueryForm = dynamoQuery.getOrQuery("productId", productIdList);
        Map<String, AttributeValue> eav = dynamoQueryForm.getEav();
        String query = dynamoQueryForm.getQuery();
        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<DynamoProduct> queryExpression = new DynamoDBQueryExpression<DynamoProduct>()
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav);

        List dynamoProducts = dynamoQuery.exeQuery(DynamoProduct.class, queryExpression);

        dynamoDBMapper.batchDelete(dynamoProducts);

    }
}
