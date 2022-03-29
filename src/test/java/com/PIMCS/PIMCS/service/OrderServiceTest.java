package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

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


}
