package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.OrderMailForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class OrderService {
    private final MatCategoryRepository matCategoryRepository;
    private final MatCategoryOrderRepository matCategoryOrderRepository;
    private final MatOrderRepository matOrderRepository;
    private final UserRepository userRepository;
    private final OrderMailFrameRepository orderMailFrameRepository;
    private final EmailUtilImpl emailUtilImpl;





    public OrderService(MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, UserRepository userRepository, OrderMailFrameRepository orderMailFrameRepository, EmailUtilImpl emailUtilImpl) {
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.userRepository = userRepository;
        this.orderMailFrameRepository = orderMailFrameRepository;
        this.emailUtilImpl = emailUtilImpl;

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

}
