package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBUtils {

    private DynamoDBMapper dynamoDBMapper;


    public DynamoDBUtils(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    /**
     * 회사 id로 입출고데이터 로드
     */
    public DynamoResultPage loadByCompany(Company company, Pageable pageable){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :v1")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);

        int end = pageable.getPageNumber() * pageable.getPageSize();
        List<InOutHistory> pageInOutHistories;
        try{
            pageInOutHistories = inOutHistories.subList(
                    end - pageable.getPageSize() ,
                    end
            );
        }catch (IndexOutOfBoundsException e){
            pageInOutHistories = inOutHistories.subList(
                    end - pageable.getPageSize() ,
                    inOutHistories.size()
            );
        }

        int totalPageSize = (int)Math.ceil((double)inOutHistories.size() / pageable.getPageSize());


        DynamoResultPage dynamoResultPage = DynamoResultPage.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(totalPageSize)
                .inOutHistories(pageInOutHistories)
                .build();

        return dynamoResultPage;
    }

    public List<InOutHistory> loadByCompany(Company company){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :v1")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
        return null;
    }

    /**
     * 날짜로 입출고내역 가져오기
     */
    public List<InOutHistory> loadByCompanyAndSerialNumberAndDate(Company company,List<String> serialNumberList, LocalDate startDate, LocalDate endDate){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        String filter = "companyId=:companyId AND begins_with(matSerialNumber";
        for(int i=0; i<serialNumberList.size(); i++){
            filter += ",";
            eav.put(":"+i,new AttributeValue().withS(serialNumberList.get(i)));
            filter += ":"+i;

        }
        filter += ")";

        System.out.println("=======");
        System.out.println(filter);
        System.out.println("======");

        DynamoDBScanExpression queryExpression = new DynamoDBScanExpression()
                .withIndexName("byConpanyId")
                .withFilterExpression(filter)
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.scan(InOutHistory.class, queryExpression);
        return inOutHistories;
    }

    /**
     * 채워넣을 마지막 값 찾기
     */
    public InOutHistory findLastInOutHistory(String serialNumber, LocalDate startDate){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":serialNumber",new AttributeValue().withS(serialNumber));
        eav.put(":start",new AttributeValue().withS(startDate.toString()));

        DynamoDBScanExpression queryExpression = new DynamoDBScanExpression()
                .withIndexName("bySerialNumber")
                .withFilterExpression("matSerialNumber=:serialNumber AND createdAt < :start")
                .withExpressionAttributeValues(eav);
        List<InOutHistory> inOutHistories = dynamoDBMapper.scan(InOutHistory.class, queryExpression);

        if(!inOutHistories.isEmpty()) return inOutHistories.get(inOutHistories.size()-1);
        else return null;
    }

}
