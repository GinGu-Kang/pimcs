package com.PIMCS.PIMCS.service;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    @Autowired
    UserRepository userRepository;

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

        int savedId = matCategoryRepository.save(matCategory).getId();

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
        List<MatCategory> matCategoryList = new ArrayList<>();
        int inputSize = 10;
        String matCategoryName = "t";
        String mappingSerialCode = "t6";
        int matPrice = 150000;
        String matInformation = "(240*240)";
        int maxWeight = 5000;

        for(int i = 0; i < inputSize; i++){
            MatCategory matCategory = MatCategory.builder()
                    .matCategoryName(matCategoryName+(i+1))
                    .mappingSerialCode(mappingSerialCode)
                    .matPrice(matPrice)
                    .matInformation(matInformation)
                    .maxWeight(maxWeight)
                    .build();
            matCategoryList.add(matCategory);
        }

        matCategoryRepository.saveAll(matCategoryList);

        //when
        matCategoryList=adminService.findMatCategoryListService();

        //then
        assertThat(matCategoryList.size()).isEqualTo(inputSize);
    }

    @Test
    void deleteMatCategoryService() {
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


        int saveId = matCategoryRepository.save(matCategory).getId();

        //when
        adminService.deleteMatCategoryService(saveId);

        //then
        Optional<MatCategory> optMatCategory = matCategoryRepository.findById(saveId);
        Boolean result = true;
        assertThat(optMatCategory.isEmpty()).isEqualTo(result);
    }

    @Test
    void findAdminQnaListService() {
        //given
        String keyword = "테스트 제목";
        String selectOption = "제목";
        Pageable pageable= PageRequest.of(1,10,Sort.Direction.DESC);
        User user =  userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
        Company company = user.getCompany();
        List<Question> questionList = new ArrayList<>();
        int inputSize = 10;

        for (int i=0; i<inputSize; i++){
            Question question =Question.builder()
                    .isSecret(true)
                    .title("테스트 제목"+(i+1))
                    .content("테스트가 잘 진행되고 있나요?")
                    .user(user)
                    .company(company)
                    .build();
            questionList.add(question);
        }

        questionRepository.saveAll(questionList);

        //when
        questionList = adminService.findAdminQnaListService(keyword,selectOption);

    }

    @Test
    void createAnswerService() {
        //given
        User user =  userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
        Company company = user.getCompany();
        Question question =Question.builder()
                .isSecret(true)
                .title("테스트 제목")
                .content("테스트가 잘 진행되고 있나요?")
                .user(user)
                .company(company)
                .build();
        questionRepository.save(question);

        Answer answer = Answer.builder()
                .question(question)
                .comment("넵 잘진행됩니다!")
                .build();

        //when
        Integer saveId=adminService.createAnswerService(answer).getId();

        //then
        Answer result = answerRepository.findById(saveId).get();
        assertThat(answer).isEqualTo(result);
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
