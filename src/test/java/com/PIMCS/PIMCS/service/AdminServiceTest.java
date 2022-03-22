package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Answer;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class AdminServiceTest {

    AdminService adminService;


    @Autowired
    public AdminServiceTest(AdminService adminService) {
        this.adminService = adminService;
    }


    @Test
    public void findTest(){
        List<Integer> number = List.of(10,11);
        System.out.println(adminService.findMatCategoryList(number).get(0).getMatCategoryName());
    }

    @Test
    @Commit
    void addAnswer() {
        Answer answer=Answer.builder().comment("야임마 그걸내가 어떻게알앙!!!").build();
        adminService.addAnswer(1,answer);
    }
    @Test
    @Commit
    public void updateoOrderMailFrameTest(){
        OrderMailFrame orderMailFrame = OrderMailFrame.builder().greeting("------------------------------------------------------------------------------------------------------------\n" +
                        "(주)스마트쇼핑 테크니컬 서포트입니다.\n" +
                        "이번에는 재고 관리 IoT 스마트 매트를 신청 해 주셔서 감사 합니다 .\n" +
                        "디바이스 대수·배송처 주소·배송 예정일은 이하와 같습니다."
                ).managerInfo("(주)스마트쇼핑 테크니컬 서포트\n" +
                "메일 : support@smartmat.jp ( 영업담당자 메일 주소)\n" +
                "전화 : *****                 ( 영업담당자 회사 전화 )\n" +
                "접수 시간：평일 9:00〜18:00\n" +
                "※본 메일은 송신 전용 주소로부터 보내 드리고 있습니다.").build();
        adminService.updateOrderMailFrame(orderMailFrame);

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
//        adminService.addMatCategory(matCategory);
//    }
}
