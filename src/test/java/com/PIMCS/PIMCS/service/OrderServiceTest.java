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
        orderService.insertorderMailFrame(orderMailFrame);
    }
    @Test
    @Commit
    public void updateorderMailFrameTest(){
        orderService.updateorderMailFrame(
                "------------------------------------------------------------------------------------------------------------\n" +
                "(주)스마트쇼핑 테크니컬 서포트입니다.\n" +
                "이번에는 재고 관리 IoT 스마트 매트를 신청 해 주셔서 감사 합니다 .\n" +
                "디바이스 대수·배송처 주소·배송 예정일은 이하와 같습니다.",
                "(주)스마트쇼핑 테크니컬 서포트\n" +
                "메일 : support@smartmat.jp ( 영업담당자 메일 주소)\n" +
                "전화 : *****                 ( 영업담당자 회사 전화 )\n" +
                "접수 시간：평일 9:00〜18:00\n" +
                "※본 메일은 송신 전용 주소로부터 보내 드리고 있습니다.");
    }

}
