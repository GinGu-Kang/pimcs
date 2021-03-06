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
            deviceAmount=deviceAmount.concat(matCategoryOrder.getMatCategory().getMatCategoryName()+" ????????????: "+matCategoryOrder.getOrderCnt()+" ???\n" +
                    matCategoryOrder.getMatCategory().getMatCategoryName()+" ????????????: "+matCategory.getMatPrice()+"(???)\n\n");
        }
        System.out.println(deviceAmount);
        matOrder.setMatCategoryOrderList(matCategoryOrderList);
        matOrder.setTotalCnt(totalCnt);
        matOrder.setTotalPrice(totalPrice);
        matOrderRepository.save(matOrder);

        //????????? ??????
        String orderMail=
                "< pimcs ??? ??? ???> "+matOrder.getCreatedAt().toString().substring(0,4)+"???"+matOrder.getCreatedAt().toString().substring(5,7)+"???"+matOrder.getCreatedAt().toString().substring(8,10)+"???"+matOrder.getCreatedAt().toString().substring(10,16)+"???\n" +
                orderMailFrame.getGreeting()+"\n"+
                "\n????????? ???????????? ?????????\n" +
                deviceAmount+
                "??? ?????? ??????: " +totalPrice+"\n"+
                "\n??????????????? ????????????\n" +
                "?????? ??????: " +matOrder.getPostCode()+"\n"+
                "??????: " +matOrder.getDeliveryAddress()+"\n"+
                "?????? ?????????: " +matOrder.getHopeDeliveryDate()+"\n"+
                "????????? ??????: " +matOrder.getDepositerName()+"\n"+
                "\n???????????? ?????????" +"\n"+
                "??????: " +user.getName()+"\n"+
                "?????? ??????: " +user.getPhone()+"\n"+
                "?????????: " +user.getEmail()+"\n"+
                "?????? ??????: " +user.getCompany().getCompanyName()+"\n"+
                "?????????: " +user.getDepartment()+"\n"+"\n"+
                orderMailFrame.getManagerInfo();
        log.info(orderMail);
        emailUtilImpl.sendEmail(
                emailSednList
                , "PIMCS ?????? ????????? ?????????."
                , orderMail
                ,false
        );
    }

    public List<MatOrder> findOrderList(Company company){
        return matOrderRepository.findByCompany(company);
    }




    /*
    * ????????? ???
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
     * ????????? ?????? ??????
     * @param companyId
     * @return ?????? ????????????, ???????????? ??? ????????????, ????????? ??????
     */
    public List<MatCategoryOrder> findOrderList(Integer companyId){
        return matCategoryOrderRepository.findByCompanyId(companyId);
    }

    public void deleteMatOrder(int id){
        matOrderRepository.deleteById(id);
    }

}
