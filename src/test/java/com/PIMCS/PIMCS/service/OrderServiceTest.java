package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.noSqlDomain.OrderHistory;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    DynamoQuery dynamoQuery;

    @Test
    public void companyTest(){
        Integer index=0;
        for (MatOrder matorder:orderService.findOrderList(companyRepository.findById(1).get())
             ) {
            System.out.println("몇개양");
            if (!matorder.getMatCategoryOrderList().isEmpty()){
                System.out.println(matorder.getMatCategoryOrderList().get(0).getMatCategory().getMatCategoryName());
            }
            index++;
        }
    }

    @Test
    public void findMatCategoryOrderTest(){
        System.out.println(orderService.findOrderList(1).get(0).getOrderCnt());
    }
    @Test
    @Commit
    public void insertorderMailFrameTest(){
        OrderMailFrame orderMailFrame = OrderMailFrame.builder().managerInfo("나는바보에요").greeting("안녕하십니까").build();
        orderService.insertOrderMailFrame(orderMailFrame);
    }

    @Test
    @Commit
    public void deleteTest(){
        orderService.deleteMatOrder(20);
    }



    @Test
    public void orderhistory(){
        Pageable pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return 1;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public Pageable withPage(int pageNumber) {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };

        Company company = companyRepository.findById(1).get();
        DynamoResultPage orderHistoryList = orderService.orderHistoryService(company, pageable);

        for(Object o : orderHistoryList.getData()){
            OrderHistory orderHistory = (OrderHistory) o;
            System.out.println(orderHistory);
        }
    }

    @Test
    public void abc(){
        Pageable pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return 1;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public Pageable withPage(int pageNumber) {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(1)));


        DynamoDBQueryExpression<OrderHistory> queryExpression = new DynamoDBQueryExpression<OrderHistory>()
                .withKeyConditionExpression("companyId = :companyId")
                .withScanIndexForward(false)
                .withExpressionAttributeValues(eav);

        DynamoDBQueryExpression<OrderHistory> countQueryExpression = new DynamoDBQueryExpression<OrderHistory>()
                .withKeyConditionExpression("companyId = :companyId")
                .withExpressionAttributeValues(eav);

         dynamoQuery.exePageQuery(OrderHistory.class, queryExpression, countQueryExpression, pageable);
    }
}
