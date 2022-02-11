package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    CompanyRepository companyRepository;

    @Test
    public void companyTest(){
        Integer index=0;
        for (MatOrder matorder:orderService.findOrder(companyRepository.findById(1).get())
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

}
