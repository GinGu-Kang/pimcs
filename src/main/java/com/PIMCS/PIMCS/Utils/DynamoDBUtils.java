package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.form.InOutHistorySearchForm;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBUtils {

    private DynamoDBMapper dynamoDBMapper;


    public DynamoDBUtils(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    /**
     * 회사 id로 입출고데이터 로드 및 페이지네이션
     */
    public DynamoResultPage loadByCompany(Company company, Pageable pageable){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);

        return createPagination(inOutHistories,pageable);
    }

    /**
     * 회사 id로 입출고데이터 로드
     */
    public List<InOutHistory> loadByCompany(Company company){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        return inOutHistories;
    }


    /**
     * 입출고 내역 검색
     */
    public DynamoResultPage searchInOutHistory(
            Company company,
            InOutHistorySearchForm inOutHistorySearchForm,
            Pageable pageable){

        String dynamodbQuery = "";
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":query", new AttributeValue().withS(inOutHistorySearchForm.getQuery()));
        //startDate와 endDate null 아니면
        if(inOutHistorySearchForm.getStartDate() != null && inOutHistorySearchForm.getStartDate() != null){
            LocalDate endDate = inOutHistorySearchForm.getEndDate().plusDays(1);
            eav.put(":start", new AttributeValue().withS(inOutHistorySearchForm.getStartDate().toString()));
            eav.put(":end", new AttributeValue().withS(endDate.toString()));
            dynamodbQuery = "companyId = :companyId AND createdAt BETWEEN :start AND :end";
        }else{ // startDate와 endDate null이면 검색쿼리에 포함된 모든데이터 조회
            dynamodbQuery = "companyId = :companyId";
        }

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression(dynamodbQuery)
                .withFilterExpression("contains (productName, :query)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        System.out.println(inOutHistories.size());
        return createPagination(inOutHistories,pageable);
    }

    public List<InOutHistory> searchInOutHistory(
            Company company,
            InOutHistorySearchForm inOutHistorySearchForm){
        String dynamodbQuery = "";
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":query", new AttributeValue().withS(inOutHistorySearchForm.getQuery()));
        //startDate와 endDate null 아니면
        if(inOutHistorySearchForm.getStartDate() != null && inOutHistorySearchForm.getStartDate() != null){
            LocalDate endDate = inOutHistorySearchForm.getEndDate().plusDays(1);
            eav.put(":start", new AttributeValue().withS(inOutHistorySearchForm.getStartDate().toString()));
            eav.put(":end", new AttributeValue().withS(endDate.toString()));
            dynamodbQuery = "companyId = :companyId AND createdAt BETWEEN :start AND :end";
        }else{ // startDate와 endDate null이면 검색쿼리에 포함된 모든데이터 조회
            dynamodbQuery = "companyId = :companyId";
        }

        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression(dynamodbQuery)
                .withFilterExpression("contains (productName, :query)")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);
        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);
        return inOutHistories;
    }


    /**
     * InoutHistory List를 pagination 만들기
     */
    private DynamoResultPage createPagination(List<InOutHistory> inOutHistories, Pageable pageable){
        int pageNumebr = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();


        int end = pageNumebr * pageSize;
        List<InOutHistory> pageInOutHistories = null;
        System.out.println("========");
        System.out.println("end: "+ end);
        System.out.println(pageNumebr);
        System.out.println("pageSize: "+pageSize);
        try{
            pageInOutHistories = inOutHistories.subList(
                    end - pageSize ,
                    end
            );
        }catch (IndexOutOfBoundsException e){
            pageInOutHistories = inOutHistories.subList(
                    end - pageSize,
                    inOutHistories.size()
            );
        }

        int totalPageSize = (int)Math.ceil((double)inOutHistories.size() /pageSize);
        totalPageSize = (totalPageSize == 0) ? 1 : totalPageSize;

        DynamoResultPage dynamoResultPage = DynamoResultPage.builder()
                .page(pageNumebr)
                .size(pageSize)
                .totalPage(totalPageSize)
                .inOutHistories(pageInOutHistories)
                .build();
        return dynamoResultPage;
    }



    /**
     * 회사id,시리얼번호, 날짜범위로 입출고내역 가져오기
     */
    public List<InOutHistory> loadByCompanyAndSerialNumberAndDate(Company company,List<String> serialNumberList, LocalDate startDate, LocalDate endDate){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        endDate = endDate.plusDays(1);
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":start",new AttributeValue().withS(startDate.toString()));
        eav.put(":end", new AttributeValue().withS(endDate.toString()));

        String filter = "companyId=:companyId AND createdAt BETWEEN :start AND :end AND (";
        for(int i=0; i<serialNumberList.size(); i++){
            filter += "matSerialNumber=:"+i;
            if(i != serialNumberList.size()-1) filter += " or ";
            eav.put(":"+i,new AttributeValue().withS(serialNumberList.get(i)));
        }
        filter += ")";

        System.out.println("=========");
        System.out.println(endDate.toString());
        System.out.println(filter);
        System.out.println("=========");
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


        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withKeyConditionExpression("matSerialNumber = :serialNumber AND createdAt < :start")
                .withExpressionAttributeValues(eav);

        List<InOutHistory> inOutHistories = dynamoDBMapper.query(InOutHistory.class, queryExpression);

        if(!inOutHistories.isEmpty()) return inOutHistories.get(inOutHistories.size()-1);
        else return null;
    }




}
