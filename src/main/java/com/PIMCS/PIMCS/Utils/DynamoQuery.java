package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.form.DynamoQueryForm;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.noSqlDomain.OrderHistory;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DynamoQuery {
    private final DynamoDBMapper dynamoDBMapper;
    private final int NUMBER_ITEMS_PER_PAGE = 50;

    @Autowired
    public DynamoQuery(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    /**
     * dynamodb page query 실행
     */
    public DynamoResultPage exePageQuery(Class clazz, DynamoDBQueryExpression queryExpression, DynamoDBQueryExpression countQueryExpression, Pageable pageable){
        int pageNumebr = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        queryExpression.setLimit(pageSize);

        List orderHistories = exeQueryPageCount(clazz, queryExpression, pageNumebr);

        DynamoResultPage dynamoResultPage = DynamoResultPage.builder()
                .page(pageNumebr)
                .size(pageSize)
                .totalPage(getTotalPage(dynamoDBMapper.count(clazz, countQueryExpression),pageable))
                .data(orderHistories)
                .build();

        return dynamoResultPage;
    }

    /**
     * 특정 페이지에 해당하는 items 가져오기
     */
    public List exeQueryPageCount(Class clazz, DynamoDBQueryExpression queryExpression, int page){
        QueryResultPage queryPage = null;

        for(int i=0; i < page; i++){
            queryPage = dynamoDBMapper.queryPage(clazz, queryExpression);
            queryExpression.setExclusiveStartKey(queryPage.getLastEvaluatedKey());

            if(queryExpression.getExclusiveStartKey() == null) {
                break;
            }
            
        }
        return queryPage.getResults();
    }

    public List exeQuery(Class clazz,  DynamoDBQueryExpression queryExpression){
        queryExpression.setLimit(NUMBER_ITEMS_PER_PAGE);

        return getAllItems(clazz, queryExpression);
    }

    public List getAllItems(Class clazz, DynamoDBQueryExpression queryExpression){
        List result = new ArrayList<>();
        do {
            QueryResultPage queryPage = dynamoDBMapper.queryPage(clazz, queryExpression);
            queryPage.getResults().forEach(o -> result.add(o));
            queryExpression.setExclusiveStartKey(queryPage.getLastEvaluatedKey());

        } while (queryExpression.getExclusiveStartKey() != null);
        return result;
    }


    private int getTotalPage(int size,Pageable pageable){
        return (int)Math.ceil((double)size / pageable.getPageSize());
    }

    public Map<Class<?>, List<KeyPair>> keyPairForTable(Class clazz, List<String> hashKeyList){
        List<KeyPair> keyPairList = new ArrayList<>();

        for(String hashKey : hashKeyList){
            KeyPair keyPair = new KeyPair();
            keyPair.withHashKey(hashKey);
            keyPairList.add(keyPair);
        }
        System.out.println(keyPairList.toString());
        Map<Class<?>, List<KeyPair>> keyPairMap = new HashMap<>();
        keyPairMap.put(clazz, keyPairList);
        return keyPairMap;
    }

    public DynamoQueryForm getOrQuery(String column, List values){
        String query = "( ";
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        System.out.println(values);
        for(int i=0; i<values.size(); i++){
            if(isNumeric(String.valueOf(values.get(i)))){
                eav.put(":"+column+""+i,new AttributeValue().withN(String.valueOf(values.get(i))));
            }else{
                eav.put(":"+column+""+i,new AttributeValue().withS(String.valueOf(values.get(i))));
            }

            query +=  (column +" = :"+column+""+i);
            if(i != values.size()-1) query += " or ";
        }
        query += " )";
        DynamoQueryForm form = new DynamoQueryForm();
        form.setEav(eav);
        form.setQuery(query);
        return form;
    }

    public  boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}


