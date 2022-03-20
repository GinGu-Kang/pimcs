package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.*;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final MatCategoryRepository matCategoryRepository;
    private final MatCategoryOrderRepository matCategoryOrderRepository;
    private final MatOrderRepository matOrderRepository;
    private final UserRepository userRepository;
    private final OrderMailRepository orderMailRepository;



    public OrderService(MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, UserRepository userRepository, OrderMailRepository orderMailRepository) {
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.userRepository = userRepository;
        this.orderMailRepository = orderMailRepository;
    }


    public void saveOrder(MatOrder matOrder, SecUserCustomForm user, MatCategoryAndOrderForm matCategoryAndOrderForm){
        List<MatCategoryOrder> matCategoryOrderList= matCategoryAndOrderForm.getMatCategoryOrderList();
        List<Integer> matCategoryId= matCategoryAndOrderForm.getMatCategoryIdList();
        List<MatCategory> matCategoryList=matCategoryRepository.findAllById(matCategoryId);

        matOrder.setCompany(user.getCompany());
        matOrder.setUser(userRepository.findByEmail(user.getUsername()).get());
        matOrderRepository.save(matOrder);

        for(int i=0;i<matCategoryOrderList.size();i++){
            matCategoryOrderList.get(i).setMatOrder(matOrder);
            matCategoryOrderList.get(i).setMatCategory(matCategoryList.get(i));
        }

        matCategoryOrderRepository.saveAll(matCategoryOrderList);
    }

    public List<MatOrder> findOrder(Company company){
        return matOrderRepository.findByCompany(company);
    }


    /*
    * 이메일 폼
     */
    public OrderMail selectOrderMailForm(){

        OrderMail orderMailForm=orderMailRepository.findById(1).get();

        return orderMailForm;
    }

    public void insertOrderMail(OrderMail orderMail){
        orderMailRepository.save(orderMail);
    }
    public OrderMail updateOrderMail(String greeting,String managerInfo){
        OrderMail orderMailForm=orderMailRepository.getOne(1);
        orderMailForm.setGreeting(greeting);
        orderMailForm.setManagerInfo(managerInfo);
        orderMailRepository.save(orderMailForm);
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
