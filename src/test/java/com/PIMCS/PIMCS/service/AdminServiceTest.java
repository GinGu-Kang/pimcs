package com.PIMCS.PIMCS.service;
import com.PIMCS.PIMCS.Utils.CompanyServiceUtils;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import java.util.*;
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
        int inputSize = 10;
        Pageable pageable= PageRequest.of(0,inputSize,Sort.by("createdAt").descending());
        User user =  userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
        Company company = user.getCompany();
        List<Question> questionList = new ArrayList<>();

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
        questionList = adminService
                .findAdminQnaListService(keyword,selectOption,pageable)
                .getContent();

        //then
        assertThat(questionList.size()).isEqualTo(inputSize);

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
    @DisplayName("회사 이름으로 검색")
    void findCompanyListByCompanyNameTest() {
        //given
        String keyword = "test-company";
        String selectOption = "회사 이름";
        int inputSize = 100;
        Pageable pageable= PageRequest.of(0,inputSize,Sort.by("createdAt").descending());
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();
        List<Company> companyList = new ArrayList<>();

        for(int i = 0; i < inputSize; i++){
            Company company = Company.builder()
                    .companyName("test-company"+(inputSize+1))
                    .companyCode(companyServiceUtils.UUIDgeneration().substring(0,30))
                    .companyAddress("제주시 테스트마을 1동 101호")
                    .contactPhone("07028421062")
                    .ceoEmail("rkdwlsrn212@gmail.com")
                    .ceoName("강진구")
                    .build();
            companyList.add(company);
        }

        companyRepository.saveAll(companyList);

        //then
        companyList = adminService
                .findCompanyListService(keyword,selectOption,pageable)
                .getContent();

        assertThat(companyList.size()).isEqualTo(inputSize);
    }
    @Test
    @DisplayName("회사 코드로 검색")
    void findCompanyListByCompanyCodeTest() {
        //given
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();
        String companyCode= companyServiceUtils.UUIDgeneration().substring(0,30);
        String keyword =companyCode;
        String selectOption = "회사 코드";
        int inputSize = 1000;
        List<Company> companyList;
        Pageable pageable= PageRequest.of(0,inputSize,Sort.by("createdAt").descending());
        Company company = Company.builder()
                .companyName("test-company"+(inputSize+1))
                .companyCode(companyCode)
                .companyAddress("제주시 테스트마을 1동 101호")
                .contactPhone("07028421062")
                .ceoEmail("rkdwlsrn212@gmail.com")
                .ceoName("강진구")
                .build();

        companyRepository.save(company);


        //when
        companyList = adminService
                .findCompanyListService(keyword,selectOption,pageable)
                .getContent();


        //then
        Company result = companyRepository.findByCompanyCode(companyCode).get();
        assertThat(companyList.get(0)).isEqualTo(result);
    }

    @Test
    @DisplayName("소유 기기 시리얼 검색")
    void findCompanyListByOwnDeviceTest() {
        //given
        String serialNumber = "test-1";
        String keyword = serialNumber;
        String selectOption = "기기 시리얼";
        List<Company> companyList;
        Pageable pageable= PageRequest.of(0,10,Sort.by("createdAt").descending());
        Company company = Company.builder()
                .companyName("test-company")
                .companyCode("test1234")
                .companyAddress("제주시 테스트마을 1동 101호")
                .contactPhone("07028421062")
                .ceoEmail("rkdwlsrn212@gmail.com")
                .ceoName("강진구")
                .build();
        OwnDevice ownDevice = OwnDevice.builder()
                .serialNumber(serialNumber)
                .company(company)
                .build();

        companyRepository.save(company);
        ownDeviceRepository.save(ownDevice);

        //when
        companyList=adminService
                .findCompanyListService(keyword,selectOption,pageable)
                .getContent();

        //then
        assertThat(companyList.get(0)).isEqualTo(company);
    }

    @Test
    void detailsCompanyService() {
        //given
        List<OwnDevice> ownDeviceList = new ArrayList<>();
        int saveId =0;
        Company company = Company.builder()
                .companyName("test-company")
                .companyCode("test1234")
                .companyAddress("제주시 테스트마을 1동 101호")
                .contactPhone("07028421062")
                .ceoEmail("rkdwlsrn212@gmail.com")
                .ceoName("강진구")
                .build();

        saveId = companyRepository.save(company).getId();
        for(int i =0;i<10;i++) {
            OwnDevice ownDevice = OwnDevice.builder()
                    .serialNumber("test"+(i+1))
                    .company(company)
                    .build();
            ownDeviceList.add(ownDevice);
        }

        ownDeviceList = ownDeviceRepository.saveAll(ownDeviceList);
        System.out.println(ownDeviceList.get(0));

        //when
        Company detailCompany=adminService.detailsCompanyService(saveId);

        //then
        List<OwnDevice> resultOwnDevice=ownDeviceRepository.findByCompany(company);
        assertThat(detailCompany).isEqualTo(company);
        assertThat(ownDeviceList).isEqualTo(resultOwnDevice);
    }



    @Test
    void deleteOwnDeviceListService() {
        //given
        List<OwnDevice> ownDeviceList = new ArrayList<>();
        Company company = Company.builder()
                .companyName("test-company")
                .companyCode("test1234")
                .companyAddress("제주시 테스트마을 1동 101호")
                .contactPhone("07028421062")
                .ceoEmail("rkdwlsrn212@gmail.com")
                .ceoName("강진구")
                .build();
        for (int i=0;i<10;i++){
            OwnDevice ownDevice = OwnDevice.builder()
                .serialNumber("test"+(i+1))
                .company(company)
                .build();
            ownDeviceList.add(ownDevice);
        }


        companyRepository.save(company);
        List<Integer> ownDeviceIdList =
                ownDeviceRepository.saveAll(ownDeviceList)
                .stream()
                        .map(OwnDevice::getId)
                        .collect(Collectors.toList());
        //when
        adminService.deleteOwnDeviceListService(ownDeviceIdList);

        //then
        ownDeviceList = ownDeviceRepository.findAllById(ownDeviceIdList);
        int result = 0;
        assertThat(ownDeviceList.size()).isEqualTo(result);
    }


    @Test
    void detailsAdminQnaService() {
        //given
        User user = userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
        Company company = user.getCompany();
        Question question = Question.builder()
                .user(user)
                .company(company)
                .isSecret(true)
                .title("테스트 제목")
                .content("테스트가 잘진행되고 있나요?")
                .build();

        int saveId=questionRepository.save(question).getId();

        //when
        adminService.detailsAdminQnaService(saveId);

        //then
        Question result=questionRepository.findById(saveId).get();
        assertThat(question).isEqualTo(result);
    }



    @Test
    void createOrderMailFrameService() {
        //given
        String greeting="테스트입니다.";
        OrderMailFrame orderMailFrame = OrderMailFrame.builder()
                .greeting(greeting)
                .managerInfo("(주)스마트쇼핑 PIMCS\n" +
                "메일 : support@pimcs.kr ( 영업담당자 메일 주소)\n" +
                "전화 : *****                 ( 영업담당자 회사 전화 )\n" +
                "접수 시간：평일 9:00〜18:00\n" +
                "※본 메일은 송신 전용 주소로부터 보내 드리고 있습니다.").build();

        //when
        orderMailFrame=adminService.createOrderMailFrameService(orderMailFrame);

        //then
        OrderMailFrame result = orderMailFrameRepository.findByGreeting(greeting).get();
        assertThat(orderMailFrame).isEqualTo(result);
    }


    @Test
    void findOrderListService() {
        //given
        User user = userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
        Company company = user.getCompany();
        List<MatOrder> matOrderList = new ArrayList<>();
        String keyword = "aldsf";
        Integer totalPriceStart=0;
        Integer totalPriceEnd=1200001;
        Pageable pageable=PageRequest.of(0,10,Sort.by("createdAt").descending());

        for (int i=0;i<10;i++) {
            MatOrder matOrder = MatOrder.builder()
                    .user(user)
                    .company(company)
                    .totalCnt(30)
                    .totalPrice(1200000)
                    .deliveryAddress("제주시 테스트동")
                    .postCode("29284")
                    .depositStatus(0)
                    .depositerName("강진구")
                    .deliveryStatus(0).build();
            matOrderList.add(matOrder);
        }
        matOrderRepository.saveAll(matOrderList);

        List<Integer> saveIdList=matOrderList.stream()
                .map(MatOrder::getId)
                .collect(Collectors.toList());

        //when
        matOrderList=adminService.findOrderListService(
                keyword,totalPriceStart,totalPriceEnd,pageable).getContent();


        //then
        List<MatOrder> result=matOrderRepository.findAllById(saveIdList);
        assertThat(matOrderList).isEqualTo(result);
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
