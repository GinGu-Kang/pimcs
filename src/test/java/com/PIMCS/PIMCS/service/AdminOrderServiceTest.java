package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class AdminOrderServiceTest {

    AdminOrderService adminOrderService;

    @Autowired
    public AdminOrderServiceTest(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

//    @Test
//    @Commit
//    public void addMatCategoryTest(){
//        MatCategory matCategory = MatCategory.builder()
//                .matCategory("A2")
//                .matInformation("(420 × 594)")
//                .maxWeight(15)
//                .matPrice(250000)
//                .build();
//        adminOrderService.addMatCategory(matCategory);
//    }
}
