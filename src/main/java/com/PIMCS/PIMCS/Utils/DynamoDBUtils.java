package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.form.InOutHistorySearchForm;
import com.PIMCS.PIMCS.form.MatGraphForm;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBUtils {
    private final int SCAN_LIMIT_SIZE = 50;
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
    public List<InOutHistory> loadByCompanyAndSerialNumberAndDate(Company company, MatGraphForm matGraphForm, LocalDate startDate, LocalDate endDate){
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNames = matGraphForm.getProductNameList();

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        endDate = endDate.plusDays(1);

        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        eav.put(":start",new AttributeValue().withS(startDate.toString()));
        eav.put(":end", new AttributeValue().withS(endDate.toString()));

        String filter = "companyId=:companyId AND createdAt BETWEEN :start AND :end AND ";
        if(serialNumberList.size() > 1) filter += "(";
        for(int i=0; i<serialNumberList.size(); i++){

            filter += "(matSerialNumber=:"+i+" AND productName=:productName"+i+")";
            if(i != serialNumberList.size()-1) filter += " or ";
            eav.put(":"+i,new AttributeValue().withS(serialNumberList.get(i)));
            eav.put(":productName"+i, new AttributeValue().withS(productNames.get(i)));
        }
        if(serialNumberList.size() > 1) filter += ")";

        System.out.println("===========");
        System.out.println(filter);
        System.out.println("===========");

        DynamoDBScanExpression queryExpression2 = new DynamoDBScanExpression()
                .withIndexName("byConpanyId")
                .withFilterExpression(filter)
                .withExpressionAttributeValues(eav)
                .withLimit(SCAN_LIMIT_SIZE);

        List<InOutHistory> inOutHistories = mapperInOutHistoryScanPage(queryExpression2);
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


    public List<InOutHistory> mapperInOutHistoryScanPage(DynamoDBScanExpression scanExpression){
        List<InOutHistory> result = new ArrayList<>();

        do {
            ScanResultPage<InOutHistory> scanPage = dynamoDBMapper.scanPage(InOutHistory.class, scanExpression);
            scanPage.getResults().forEach(inOutHistory -> result.add(inOutHistory));
            scanExpression.setExclusiveStartKey(scanPage.getLastEvaluatedKey());

        } while (scanExpression.getExclusiveStartKey() != null);
        return result;
    }

    public List<InOutHistory> mapperQueryPage(DynamoDBQueryExpression queryExpression){
        List<InOutHistory> result = new ArrayList<>();
        do {
            QueryResultPage<InOutHistory> queryPage = dynamoDBMapper.queryPage(InOutHistory.class, queryExpression);
            queryPage.getResults().forEach(inOutHistory -> result.add(inOutHistory));
            queryExpression.setExclusiveStartKey(queryPage.getLastEvaluatedKey());

        } while (queryExpression.getExclusiveStartKey() != null);
        return result;

    }

    /**
     * rdbms 수정시
     * dynamodb 매트위치 및 박스무게 수정
     */
    public void updateMat(List<Mat> mats, Company company){
        //요청 쿼리 만들기
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId", new AttributeValue().withN(String.valueOf(company.getId())));
        String query = "";
        for(int i=0; i<mats.size(); i++){
            Mat mat = mats.get(i);
            eav.put(":serialNumber"+i,new AttributeValue().withS(mat.getSerialNumber()));
            query += "matSerialNumber = :serialNumber"+i;
            if(i != mats.size()-1) query += " or ";
        }

        //select 쿼리실행
        DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId = :companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav)
                .withLimit(SCAN_LIMIT_SIZE);


        List<InOutHistory> inOutHistories = mapperQueryPage(queryExpression);

        List<InOutHistory> result = new ArrayList<>();
        //dynamodb mat위치 및 박스무게 수정
        for(InOutHistory inOutHistory : inOutHistories){
            Mat mat = Mat.findBySerialNumber(mats, inOutHistory.getMatSerialNumber());
            inOutHistory.setMatLocation(mat.getMatLocation());
            inOutHistory.setBoxWeight(mat.getBoxWeight());
            inOutHistory.setCalcMethod(mat.getCalcMethod());
            inOutHistory.setThreshold(mat.getThreshold());

            result.add(inOutHistory);
        }
        //update 쿼리실행
        dynamoDBMapper.batchSave(result);
    }

    public void updateProduct(Company company, List<Product> products){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));
        String query = "( ";
        for(int i=0; i<products.size(); i++){
            Product product = products.get(i);
            eav.put(":productId"+i, new AttributeValue().withN(String.valueOf(product.getId())));
            query += "productId = :productId"+i;
            if(i != products.size()-1) query += " or ";
        }
        query += " )";



    DynamoDBQueryExpression<InOutHistory> queryExpression = new DynamoDBQueryExpression<InOutHistory>()
                .withIndexName("byConpanyId")
                .withConsistentRead(false)
                .withKeyConditionExpression("companyId=:companyId")
                .withFilterExpression(query)
                .withExpressionAttributeValues(eav)
                .withLimit(SCAN_LIMIT_SIZE);


        List<InOutHistory> inOutHistories = mapperQueryPage(queryExpression);

        List<InOutHistory> result = new ArrayList<>();

        for(InOutHistory inOutHistory : inOutHistories){
            Product product = Product.findByProductId(products, inOutHistory.getProductId());
            inOutHistory.setProductCode(product.getProductCode());
            inOutHistory.setProductName(product.getProductName());
            inOutHistory.setProductWeight(product.getProductWeight());

            result.add(inOutHistory);
        }

        dynamoDBMapper.batchSave(result);
    }


    /**
     * 주문메일을 dynamodb에 insert
     */
    public OrderMailRecipients putOrderMailRecipients(Mat mat, List<String> mailRecipients){
        OrderMailRecipients orderMailRecipients = new OrderMailRecipients();

        orderMailRecipients.setMatSerialNumber(mat.getSerialNumber());
        orderMailRecipients.setMailRecipients(mailRecipients);
        orderMailRecipients.setCreatedAt(LocalDateTime.of(LocalDate.now(), LocalTime.now()));

        dynamoDBMapper.save(orderMailRecipients);


        return orderMailRecipients;
    }

    public List<OrderMailRecipients> loadOrderMailRecipients(List<String> serialNumbers){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        String query = "(";
        for(int i=0; i<serialNumbers.size(); i++){
            String serialNumber = serialNumbers.get(i);
            eav.put(":serialNumber"+i,new AttributeValue().withS(serialNumber));
            query += "matSerialNumber = :serialNumber"+i;
            if(i != serialNumbers.size()-1) query += " or ";
        }
        query +=")";
        System.out.println(query);

        //select 쿼리실행
        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression(query)
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(OrderMailRecipients.class, queryExpression);

    }


    public void updateOrderMailRecipients(List<OrderMailRecipients> list){
        List<String> serialNumbers = OrderMailRecipients.getSerialNumberList(list);
        List<OrderMailRecipients> findMailRecipients = loadOrderMailRecipients(serialNumbers);
        List<OrderMailRecipients> save = new ArrayList<>();

        for(OrderMailRecipients o : findMailRecipients ){
            OrderMailRecipients find = OrderMailRecipients.findBySerialNumber(o.getMatSerialNumber(), list);
            o.setMailRecipients(find.getMailRecipients());
            save.add(o);
        }

        dynamoDBMapper.batchSave(save);
    }

    public List<OrderMailRecipients> readMailBySerialNumber(String serialNumber){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":serialNumber", new AttributeValue().withS(serialNumber));
        DynamoDBQueryExpression<OrderMailRecipients> queryExpression = new DynamoDBQueryExpression<OrderMailRecipients>()
                .withKeyConditionExpression("matSerialNumber=:serialNumber")
                .withExpressionAttributeValues(eav)
                .withScanIndexForward(false);
        return dynamoDBMapper.query(OrderMailRecipients.class, queryExpression);
    }


}
