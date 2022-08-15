package com.PIMCS.PIMCS.noSqlDomain;

import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.Utils.Search;
import com.PIMCS.PIMCS.config.AWSConfig;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.DynamoQueryForm;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@DynamoDBTable(tableName = "DynamoMat")
public class DynamoMat implements Comparable{

    @DynamoDBHashKey(attributeName = "companyId")
    private int companyId;

    @DynamoDBRangeKey
    private String rangekey;

    @DynamoDBAttribute(attributeName = "matSerialNumber")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byMatSerialNumber")
    private String matSerialNumber;

    @DynamoDBAttribute
    private int threshold;

    @DynamoDBAttribute
    private int productId;

    @DynamoDBAttribute
    private int calcMethod;

    @DynamoDBAttribute
    private String matLocation;

    @DynamoDBAttribute
    private int inventoryWeight;

    @DynamoDBAttribute
    private int boxWeight;

    @DynamoDBAttribute
    private int isSendEmail;

    @DynamoDBAttribute
    private  int communicationStatus;

    @DynamoDBAttribute
    private int productOrderCnt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AWSConfig.LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;

    public DynamoMat(){}

    public static DynamoMat save(DynamoDBMapper dynamoDBMapper,Mat mat){
        DynamoMat dynamoMat = DynamoMat.builder()
                .rangekey(mat.getSerialNumber())
                .matSerialNumber(mat.getSerialNumber())
                .productId(mat.getProduct().getId())
                .matLocation(mat.getMatLocation())
                .threshold(mat.getThreshold())
                .calcMethod(mat.getCalcMethod())
                .matLocation(mat.getMatLocation())
                .boxWeight(mat.getBoxWeight())
                .companyId(mat.getCompany().getId())
                .isSendEmail(0)
                .communicationStatus(0)
                .productOrderCnt(mat.getProductOrderCnt())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        dynamoDBMapper.save(dynamoMat);
        return dynamoMat;
    }

    public static void update(DynamoDBMapper dynamoDBMapper,DynamoQuery dynamoQuery, List<Mat> mats, Company company){


        List<KeyPair> keyPairList = new ArrayList<>();


        mats.forEach(mat -> {
//            serialNumbers.add(mat.getSerialNumber());
            KeyPair keyPair = new KeyPair();
            keyPair.withHashKey(company.getId());
            keyPair.withRangeKey(mat.getSerialNumber());
            keyPairList.add(keyPair);
        });



        List dynamoMats = dynamoQuery.batchLoad(DynamoMat.class, keyPairList);

        if(dynamoMats.size() != mats.size()){
            throw new IllegalStateException("Synchronization error");
        }

        Collections.sort(dynamoMats);
        List<String> dynamoSerialNumbers = new ArrayList<>();
        dynamoMats.forEach(o-> {
            DynamoMat dynamoMat = (DynamoMat)  o;
            dynamoSerialNumbers.add(dynamoMat.getMatSerialNumber());
        });

        List<DynamoMat> savedMats = new ArrayList<>();
        for(Mat mat : mats){
            int index = Search.binarySearch(mat.getSerialNumber(), dynamoSerialNumbers);
            DynamoMat dynamoMat = (DynamoMat) dynamoMats.get(index);
            dynamoMat.setProductId((mat.getProduct() != null) ? mat.getProduct().getId() : -1);
            dynamoMat.setThreshold(mat.getThreshold());
            dynamoMat.setCalcMethod(mat.getCalcMethod());
            dynamoMat.setMatLocation(mat.getMatLocation());
            dynamoMat.setBoxWeight(mat.getBoxWeight());
            dynamoMat.setUpdatedAt(LocalDateTime.now());
            dynamoMat.setProductOrderCnt(mat.getProductOrderCnt());
            savedMats.add(dynamoMat);
        }
        dynamoDBMapper.batchSave(savedMats);
    }


    public static void delete(DynamoDBMapper dynamoDBMapper,DynamoQuery dynamoQuery, List<Mat> mats, Company company){
        List<KeyPair> keyPairList = new ArrayList<>();

        mats.forEach(mat -> {
            KeyPair keyPair = new KeyPair();
            keyPair.withHashKey(company.getId());
            keyPair.withRangeKey(mat.getSerialNumber());
            keyPairList.add(keyPair);
        });

        List dynamoMats = dynamoQuery.batchLoad(DynamoMat.class, keyPairList);
        dynamoDBMapper.batchDelete(dynamoMats);
    }


    @Override
    public int compareTo(Object o) {
        DynamoMat dynamoMat = (DynamoMat)  o;
        return this.getRangekey().compareTo(dynamoMat.getRangekey());
    }
}
