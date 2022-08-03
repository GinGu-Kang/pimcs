package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.noSqlDomain.OrderHistory;
import com.PIMCS.PIMCS.repository.*;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {
    private final MatCategoryRepository matCategoryRepository;
    private final MatCategoryOrderRepository matCategoryOrderRepository;
    private final MatOrderRepository matOrderRepository;
    private final UserRepository userRepository;
    private final OrderMailFrameRepository orderMailFrameRepository;
    private final EmailUtilImpl emailUtilImpl;
    private final DynamoQuery dynamoQuery;





    public OrderService(MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, UserRepository userRepository, OrderMailFrameRepository orderMailFrameRepository, EmailUtilImpl emailUtilImpl, DynamoQuery dynamoQuery) {
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.userRepository = userRepository;
        this.orderMailFrameRepository = orderMailFrameRepository;
        this.emailUtilImpl = emailUtilImpl;

        this.dynamoQuery = dynamoQuery;
    }
    @Transactional
    public void saveOrder(MatOrder matOrder, SecUserCustomForm secUserCustomForm, MatCategoryAndOrderForm matCategoryAndOrderForm){
        List<MatCategoryOrder> matCategoryOrderList= matCategoryAndOrderForm.getMatCategoryOrderList();
        List<Integer> matCategoryId= matCategoryAndOrderForm.getMatCategoryIdList();
        List<MatCategory> matCategoryList=matCategoryRepository.findAllById(matCategoryId);
        Integer totalPrice=0;
        Integer totalCnt=0;
        User user=userRepository.findByEmail(secUserCustomForm.getUsername()).get();
        String[] emailSednList=new String[]{secUserCustomForm.getUsername(),"wisp212@gmail.com"};
        String deviceAmount="";
        OrderMailFrame orderMailFrame=orderMailFrameRepository.findById(1).get();


        matOrder.setCompany(user.getCompany());
        matOrder.setUser(user);

        for(int i=0;i<matCategoryOrderList.size();i++){
            MatCategoryOrder matCategoryOrder =matCategoryOrderList.get(i);
            MatCategory matCategory=matCategoryList.get(i);

            matCategoryOrder.setMatOrder(matOrder);
            matCategoryOrder.setMatCategory(matCategory);

            totalCnt+=matCategoryOrder.getOrderCnt();
            totalPrice+=matCategoryOrder.getOrderCnt()*matCategory.getMatPrice();
            deviceAmount=deviceAmount.concat(matCategoryOrder.getMatCategory().getMatCategoryName()+" 주문갯수: "+matCategoryOrder.getOrderCnt()+" 대\n" +
                    matCategoryOrder.getMatCategory().getMatCategoryName()+" 대당가격: "+matCategory.getMatPrice()+"(원)\n\n");
        }
        System.out.println(deviceAmount);
        matOrder.setMatCategoryOrderList(matCategoryOrderList);
        matOrder.setTotalCnt(totalCnt);
        matOrder.setTotalPrice(totalPrice);
        matOrderRepository.save(matOrder);

        //이메일 전송
        String orderMail=
                "< pimcs 주 문 서> "+matOrder.getCreatedAt().toString().substring(0,4)+"년"+matOrder.getCreatedAt().toString().substring(5,7)+"월"+matOrder.getCreatedAt().toString().substring(8,10)+"일"+matOrder.getCreatedAt().toString().substring(10,16)+"분\n" +
                orderMailFrame.getGreeting()+"\n"+
                "\n【주문 디바이스 정보】\n" +
                deviceAmount+
                "총 주문 가격: " +totalPrice+"\n"+
                "\n【디바이스 배송지】\n" +
                "우편 번호: " +matOrder.getPostCode()+"\n"+
                "주소: " +matOrder.getDeliveryAddress()+"\n"+
                "배송 예정일: " +matOrder.getHopeDeliveryDate()+"\n"+
                "입금자 이름: " +matOrder.getDepositerName()+"\n"+
                "\n【주문자 정보】" +"\n"+
                "이름: " +user.getName()+"\n"+
                "전화 번호: " +user.getPhone()+"\n"+
                "이메일: " +user.getEmail()+"\n"+
                "회사 이름: " +user.getCompany().getCompanyName()+"\n"+
                "부서명: " +user.getDepartment()+"\n"+"\n"+
                orderMailFrame.getManagerInfo();
        log.info(orderMail);
        emailUtilImpl.sendEmail(
                emailSednList
                , "PIMCS 기기 주문서 입니다."
                , orderMail
                ,false
        );
    }

    public List<MatOrder> findOrderList(Company company){
        return matOrderRepository.findByCompany(company);
    }




    /*
    * 이메일 폼
     */
    public OrderMailForm selectOrderMailForm(){
        OrderMailForm orderMailForm = com.PIMCS.PIMCS.form.OrderMailForm.builder()
                .orderMailFrame(orderMailFrameRepository.findById(1).get())
                .matOrder(matOrderRepository.findById(1).get())
                .matCategoryOrderList(matCategoryOrderRepository.findByCompanyId(1)).build();
        return orderMailForm;
    }

    public void insertOrderMailFrame(OrderMailFrame orderMailFrame){
        orderMailFrameRepository.save(orderMailFrame);
    }



    /**
     * 회사별 주문 조회
     * @param companyId
     * @return 매트 카테고리, 카테고리 별 주문갯수, 주문자 정보
     */
    public List<MatCategoryOrder> findOrderList(Integer companyId){
        return matCategoryOrderRepository.findByCompanyId(companyId);
    }

    public void deleteMatOrder(int id){
        matOrderRepository.deleteById(id);
    }


    /**
     * 발주내역
     */
    public DynamoResultPage orderHistoryService(Company company, Pageable pageable){
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":companyId",new AttributeValue().withN(String.valueOf(company.getId())));

        DynamoDBQueryExpression<OrderHistory> queryExpression = OrderHistory.queryExpression(company, false);
        DynamoDBQueryExpression<OrderHistory> countQueryExpression = OrderHistory.queryExpression(company, true);

        return dynamoQuery.exePageQuery(OrderHistory.class, queryExpression, countQueryExpression, pageable);
    }

    /**
     * 발주내역 검색
     */
    public DynamoResultPage orderHistorySearchService(Company company, InOutHistorySearchForm orderSearchForm, Pageable pageable){

        DynamoDBQueryExpression<OrderHistory> queryExpression = OrderHistory.searchQueryExpression(company,orderSearchForm);
        DynamoDBQueryExpression<OrderHistory> countQueryExpression = OrderHistory.searchQueryExpression(company, orderSearchForm);

        return dynamoQuery.exePageQuery(OrderHistory.class, queryExpression, countQueryExpression, pageable);
    }


    /**
     * 발주내역 csv 다운로드
     */
    public void downloadOrderHistoryCsvService(Company company, InOutHistorySearchForm searchForm, Writer writer) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        String[] columns = {"시리얼 번호", "장소", "주문 이메일", "상품명", "상품코드", "현재재고", "임계값", "상품주문갯수", "발주 시간"};
        csvPrinter.printRecord(columns);

        List orderHistories;
        if(searchForm.getQuery() != "" || (searchForm.getStartDate() != null && searchForm.getEndDate() != null)){
            orderHistories = dynamoQuery.exeQuery(OrderHistory.class,  OrderHistory.searchQueryExpression(company, searchForm));

        }else { // 전제데이터 다운로드
            orderHistories = dynamoQuery.exeQuery(OrderHistory.class, OrderHistory.queryExpression(company, false));
        }

        for(Object o : orderHistories){
            OrderHistory orderHistory = (OrderHistory) o;
            List<String> record = new ArrayList<>();
            record.add(orderHistory.getMatSerialNumber());
            record.add(orderHistory.getLocation());
            record.add(orderHistory.getMailRecipients().toString());
            record.add(orderHistory.getProductName());
            record.add(orderHistory.getProductCode());
            record.add(String.valueOf(orderHistory.getInventoryCnt()));
            record.add(String.valueOf(orderHistory.getThreshold()));
            record.add(String.valueOf(orderHistory.getOrderCnt()));
            record.add(orderHistory.getCreatedAt().toString());
            csvPrinter.printRecord(record);
        }
    }

    public List<MatCategory> findMatCategoryListService(){
        return matCategoryRepository.findAll();
    }
}


