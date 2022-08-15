package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Answer;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Transactional
@SpringBootTest
public class AdminServiceTest {

    @Autowired
    AdminService adminService;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    MatCategoryRepository matCategoryRepository;
    @Autowired
    MatCategoryOrderRepository matCategoryOrderRepository;
    @Autowired
    MatOrderRepository matOrderRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    OrderMailFrameRepository orderMailFrameRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    OwnDeviceRepository ownDeviceRepository;
    @Autowired
    SendHistoryRepository sendHistoryRepository;

    @Test
    public void createMatCategoryService(){
        //given
        String matCategoryName = "t6";
        String mappingSerialCode = "t6";
        int matPrice = 150000;
        String matInformation = "(240*240)";
        int maxWeight = 5000;


        MatCategory matCategory = MatCategory.builder()
                .matCategoryName(matCategoryName)
                .mappingSerialCode(mappingSerialCode)
                .matPrice(matPrice)
                .matInformation(matInformation)
                .maxWeight(maxWeight)
                .build();

        //when
        adminService.createMatCategoryService(matCategory);

        //then
        MatCategory result = matCategoryRepository.findByMatCategoryName("t6").get();
        assertThat(matCategory).isEqualTo(result);
    }


    @Test
    void updateMatCategoryService() {
        //given
        String matCategoryName = "t6";
        String updateMatCategoryName = "t7";
        String mappingSerialCode = "t6";
        int matPrice = 150000;
        String matInformation = "(240*240)";
        int maxWeight = 5000;

        MatCategory matCategory = MatCategory.builder()
                .matCategoryName(matCategoryName)
                .mappingSerialCode(mappingSerialCode)
                .matPrice(matPrice)
                .matInformation(matInformation)
                .maxWeight(maxWeight)
                .build();

        int savedId = adminService.createMatCategoryService(matCategory).getId();

        //when
        matCategory.setMatCategoryName(updateMatCategoryName);
        adminService.updateMatCategoryService(matCategory);

        //then
        String result = matCategoryRepository.findById(savedId).get().getMatCategoryName();
        assertThat(updateMatCategoryName).isEqualTo(result);
    }

    @Test
    void findMatCategoryListService() {
        //given
        IntStream.range(1,11).forEach(System.out::println);


    }

    @Test
    void deleteMatCategoryService() {
    }

    @Test
    void findMatCategoryList() {
    }

    @Test
    void createAnswerService() {
    }

    @Test
    void detailsCompanyService() {
    }

    @Test
    void findCompanyListService() {
    }

    @Test
    void deleteOwnDeviceListService() {
    }

    @Test
    void findAllQuestion() {
    }

    @Test
    void detailsAdminQnaService() {
    }

    @Test
    void findAdminQnaListService() {
    }

    @Test
    void createOrderMailFrameService() {
    }

    @Test
    void createOrderMailFrameFormService() {
    }

    @Test
    void findOrderListService() {
    }

    @Test
    void testFindOrderListService() {
    }

    @Test
    void detailsOrderService() {
    }

    @Test
    void updateOrderDepositService() {
    }

    @Test
    void createOwnDeviceAndSendHistoryService() {
    }

    @Test
    void createOwnDeviceService() {
    }
}
