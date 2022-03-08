package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Answer;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.MatCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class AdminOrderServiceTest {

    AdminOrderService adminOrderService;


    @Autowired
    public AdminOrderServiceTest(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }


    @Test
    public void findTest(){
        List<Integer> number = List.of(10,11);
        System.out.println(adminOrderService.findMatCategoryList(number).get(0).getMatCategoryName());
    }

    @Test
    @Commit
    void addAnswer() {
        Answer answer=Answer.builder().comment("야임마 그걸내가 어떻게알앙!!!").build();
        adminOrderService.addAnswer(1,answer);
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
