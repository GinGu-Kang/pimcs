package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.OrderMailForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        User user=userRepository.findByEmail(secUserCustomForm.getUsername()).get();
        String[] emailSednList=new String[]{"user.getUsername()","wisp212@gmail.com"};
        String deviceAmount="";
        OrderMailFrame orderMailFrame=orderMailFrameRepository.findById(1).get();

        matOrder.setCompany(user.getCompany());
        matOrder.setUser(user);
//        matOrderRepository.save(matOrder);

        for(int i=0;i<matCategoryOrderList.size();i++){
            System.out.println(matCategoryOrderList.get(i).getMatCategory());
//            deviceAmount=deviceAmount.concat(matCategoryOrderList.get(i).getMatCategory().getMatCategoryName()+" 주문갯수:"+matCategoryOrderList.get(i).getOrderCnt()+"대\n");
            matCategoryOrderList.get(i).setMatOrder(matOrder);
            matCategoryOrderList.get(i).setMatCategory(matCategoryList.get(i));
        }

//        matCategoryOrderRepository.saveAll(matCategoryOrderList);

        //이메일 전송
        String orderMail=
                "< pimcs 주 문 서> "+matOrder.getCreatedAt().toString().substring(0,4)+"년"+matOrder.getCreatedAt().toString().substring(5,7)+"월"+matOrder.getCreatedAt().toString().substring(8,10)+"일"+matOrder.getCreatedAt().toString().substring(10,16)+"분\n" +
                orderMailFrame.getGreeting()+"\n"+
                "【주문 디바이스 정보】\n" +
                deviceAmount+"\n"+
                "【디바이스 배송지】\n" +
                "우편 번호: " +matOrder.getDeliveryCode()+"\n"+
                "주소: " +matOrder.getDeliveryAddress()+"\n"+
                "배송 예정일: " +matOrder.getHopeDeliveryDate()+"\n"+
                "【주문자 정보】" +"\n"+
                "이름: " +user.getName()+"\n"+
                "전화 번호: " +user.getPhone()+"\n"+
                "이메일: " +user.getEmail()+"\n"+
                "회사 이름: " +user.getCompany().getCompanyName()+"\n"+
                "부서명: " +user.getDepartment()+"\n"+
                orderMailFrame.getManagerInfo();
        System.out.println(orderMail);
//        emailUtilImpl.sendEmail(
//                emailSednList
//                , "주문 메일입니다라디라라"
//                , orderMail
//        );
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
        ;
        return orderMailForm;
    }

    public void insertorderMailFrame(OrderMailFrame orderMailFrame){
        orderMailFrameRepository.save(orderMailFrame);
    }

    public OrderMailFrame updateorderMailFrame(String greeting, String managerInfo){
        OrderMailFrame orderMailForm=orderMailFrameRepository.getOne(1);
        orderMailForm.setGreeting(greeting);
        orderMailForm.setManagerInfo(managerInfo);
        orderMailFrameRepository.save(orderMailForm);
        return orderMailForm;
    }

    /**
     * 회사별 주문 조회
     * @param companyId
     * @return 매트 카테고리, 카테고리 별 주문갯수, 주문자 정보
     */
    public List<MatCategoryOrder> findOrderList(Integer companyId){
        return matCategoryOrderRepository.findByCompanyId(companyId);
    }

}
