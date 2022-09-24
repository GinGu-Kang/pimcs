package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class AdminService {
    private final AnswerRepository answerRepository;
    private final MatCategoryRepository matCategoryRepository;
    private final MatOrderRepository matOrderRepository;
    private final QuestionRepository questionRepository;
    private final OrderMailFrameRepository orderMailFrameRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final OwnDeviceRepository ownDeviceRepository;
    private final SendHistoryRepository sendHistoryRepository;


    @Autowired
    public AdminService(AnswerRepository answerRepository, MatCategoryRepository matCategoryRepository, MatOrderRepository matOrderRepository, QuestionRepository questionRepository, OrderMailFrameRepository orderMailFrameRepository, CompanyRepository companyRepository, UserRepository userRepository, OwnDeviceRepository ownDeviceRepository, SendHistoryRepository sendHistoryRepository) {
        this.answerRepository = answerRepository;
        this.matCategoryRepository = matCategoryRepository;
        this.matOrderRepository = matOrderRepository;
        this.questionRepository = questionRepository;
        this.orderMailFrameRepository = orderMailFrameRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.ownDeviceRepository = ownDeviceRepository;
        this.sendHistoryRepository = sendHistoryRepository;
    }


    public MatCategory createMatCategoryService(MatCategory matCategory){
        try{
            return matCategoryRepository.save(matCategory);
        }catch (DataIntegrityViolationException e){
            throw new DuplicateKeyException("이름은 중복될수 없습니다.");
        }
    }

    public void updateMatCategoryService(MatCategory matCategory){
        Optional<MatCategory> optionalMatCategory=matCategoryRepository.findById(matCategory.getId());
        if(optionalMatCategory.isPresent()){
            try{
                matCategoryRepository.save(matCategory);
            }catch (DataIntegrityViolationException e){
                throw new DuplicateKeyException("이름은 중복될수 없습니다.");
            }
        }else {
            throw new IllegalStateException("존재하지 않는 카테고리 입니다.");
        }
    }

    public List<MatCategory> findMatCategoryListService(){
        return matCategoryRepository.findAll();
    }

    public void deleteMatCategoryService(Integer id){
        Optional<MatCategory> optionalMatCategory=matCategoryRepository.findById(id);
        if(optionalMatCategory.isPresent()){
            matCategoryRepository.delete(optionalMatCategory.get());
        }else {
            throw new IllegalStateException("존재하지 않는 카테고리 입니다.");
        }
    }

    public List<MatCategory> findMatCategoryList(List<Integer> matCategoryIdList){
        return matCategoryRepository.findAllById(matCategoryIdList);
    }

    //답변 추가
    public Answer createAnswerService(Answer answer){
        return answerRepository.save(answer);
    }


    /*회사 상세*/
    public Company detailsCompanyService(Integer companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            return companyRepository.findById(companyId).get();
        }else{
            throw new IllegalStateException("존재하지 않는 회사입니다.");
        }
    }

    //회사 검색
    public Page<Company> findCompanyListService(String keyword,String selectOption,Pageable pageable){

        Page<Company> searchCompanies =  null ;

        switch (selectOption){
            case "회사 코드":
                searchCompanies =companyRepository.findByCompanyCodeLikeOrderById("%"+keyword+"%",pageable);
                break;
            case "기기 시리얼":
                searchCompanies =companyRepository.findByOwnDeviceSerial(keyword,pageable);
                break;
            default:
                searchCompanies =companyRepository.findByCompanyNameLikeOrderById("%"+keyword+"%",pageable);
                break;
        };

        return searchCompanies;
    }

    /*매핑된 기기 삭제*/
    @Transactional
    public void deleteOwnDeviceListService(List<Integer> ownDeviceIdList) {
        ownDeviceIdList.remove(0);
        System.out.println(ownDeviceIdList.size());
        ownDeviceRepository.deleteAllByIdIn(ownDeviceIdList);
    }

    public Page<Question> findAllQuestion(Pageable pageable){
        return questionRepository.findAll(pageable);
    }


    public Question detailsAdminQnaService(Integer questionId){
        Question question=questionRepository.findById(questionId).get();

        if (question.getAnswer()==null){
            Answer answer = Answer.builder().comment("").build();
            question.setAnswer(answer);
        }

        return question;
    }

    //질문 필터링 검색
    public Page<Question> findAdminQnaListService(String keyword,String selectOption,Pageable pageable){

        Page<Question> searchQuestions =  null ;

        System.out.println(keyword+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+selectOption);
        switch (selectOption){
            case "제목":
                searchQuestions =questionRepository.findByTitleLike("%"+keyword+"%",pageable);
                break;
            default:
                searchQuestions =questionRepository.findByTitleLike("%"+keyword+"%",pageable);
                break;
        };

        return searchQuestions;
    }

    public OrderMailFrame createOrderMailFrameService(OrderMailFrame orderMailFrame){
        List<OrderMailFrame> optOrderMailFrame=orderMailFrameRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if(optOrderMailFrame.size()!=0){
            orderMailFrame.setId(optOrderMailFrame.get(0).getId());
            return orderMailFrameRepository.save(orderMailFrame);
        }else{
            return orderMailFrameRepository.save(orderMailFrame);
        }

    }

    public OrderMailFrame createOrderMailFrameFormService(){
        List<OrderMailFrame> optOrderMailFrame=orderMailFrameRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if(optOrderMailFrame.size()!=0){
            return optOrderMailFrame.get(0);
        }else {
            OrderMailFrame orderMailFrame = OrderMailFrame.builder().greeting("------------------------------------------------------------------------------------------------------------\n" +
                    "(주)스마트쇼핑 PIMCS 입니다.\n" +
                    "이번에는 재고 관리 IoT 스마트 매트를 신청 해 주셔서 감사 합니다 .\n" +
                    "디바이스 대수·배송처 주소·배송 예정일은 이하와 같습니다."
            ).managerInfo("(주)스마트쇼핑 PIMCS\n" +
                    "메일 : support@pimcs.kr ( 영업담당자 메일 주소)\n" +
                    "전화 : *****                 ( 영업담당자 회사 전화 )\n" +
                    "접수 시간：평일 9:00〜18:00\n" +
                    "※본 메일은 송신 전용 주소로부터 보내 드리고 있습니다.").build();
            return orderMailFrameRepository.save(orderMailFrame);
        }
    }

//    public Page<MatOrder> findOrderListService(Pageable pageable){
//        return matOrderRepository.findAll(pageable);
//    }

    //주문 필터링 검색
    public Page<MatOrder> findOrderListService(String keyword,Integer totalPriceStart,Integer totalPriceEnd, Pageable pageable){

        Page<MatOrder> searchOrders =  null ;

        searchOrders =matOrderRepository.findByDepositerNameLikeAndTotalPriceBetween("%"+keyword+"%",totalPriceStart,totalPriceEnd,pageable);

        return searchOrders;
    }

    public MatOrder detailsOrderService(Integer orderId){
        return matOrderRepository.findById(orderId).get();
    }

    public MatOrder updateOrderDepositService(Integer orderId,Boolean isDeposit){
        MatOrder matOrder=matOrderRepository.findById(orderId).get();

        if(isDeposit){
            matOrder.setDepositStatus(1);
        }else{
            matOrder.setDepositStatus(0);
        }

        return matOrderRepository.save(matOrder);
    }


    /*회사 기기 매핑*/
    //csv 형태로 그냥 저장
    //sendHistory 중복체크
    @Transactional
    public HashMap<String,String> createOwnDeviceAndSendHistoryService(Integer orderId, List<String> deviceSerialList , Integer companyId){
        List<OwnDevice> ownDeviceList = new ArrayList<OwnDevice>();
        List<OwnDevice> ownDeviceDuplicationCheckList = new ArrayList<OwnDevice>();
        String ownDeviceListCsv ="";
        String message="";
        HashMap<String,String> resultMap = new HashMap<String,String>();
        MatOrder matOrder= matOrderRepository.getById(orderId);
        //기기 중복 체크
        ownDeviceDuplicationCheckList=ownDeviceRepository.findAllBySerialNumberIn(deviceSerialList);

        //sendHistory 중복 체크
        if(matOrder.getSendHistory()!=null){

            message="이미 배송이 완료되었습니다.";

            resultMap.put("message",message);
            resultMap.put("isError","true");

        }else if (ownDeviceDuplicationCheckList.size()!=0){

            message="기기가 중복되었습니다. \n중복된 기기: ";

            for (OwnDevice duplicateDevice:ownDeviceDuplicationCheckList
            ) {
                message+=duplicateDevice.getSerialNumber()+",";
            }

            resultMap.put("message",message);
            resultMap.put("isError","true");

        }else{
            message="저장이 완료 되었습니다.";
            ownDeviceListCsv=String.join(",",deviceSerialList);
            //배송된 기기 내역
            SendHistory sendHistory = SendHistory
                    .builder()
                    .matOrder(matOrder)
                    .history(ownDeviceListCsv)
                    .build();

            //회사와 구매한 기기 매핑
            for (String deviceSerial:deviceSerialList
            ) {
                OwnDevice ownDevice = OwnDevice.builder()
                        .serialNumber(deviceSerial)
                        .company(companyRepository.getOne(companyId))
                        .build();
                ownDeviceList.add(ownDevice);
            }

            matOrder.setDeliveryStatus(1);
            matOrderRepository.save(matOrder);
            sendHistoryRepository.save(sendHistory);
            ownDeviceRepository.saveAll(ownDeviceList);
            resultMap.put("message",message);
            resultMap.put("isError","false");
        }




        return resultMap;
    }

    @Transactional
    public HashMap<String,String> createOwnDeviceService(String deviceSerial , Integer companyId) {
        String message = "";
        HashMap<String, String> resultMap = new HashMap<>();
        //기기 중복 체크
        Optional<OwnDevice> ownDeviceDuplicateCheck = ownDeviceRepository.findBySerialNumber(deviceSerial);


        if (ownDeviceDuplicateCheck.isPresent()) {

            message = "중복된 기기입니다.";

            resultMap.put("message", message);
            resultMap.put("isError", "true");

        } else {

            OwnDevice ownDevice = OwnDevice.builder()
                    .company(companyRepository.getOne(companyId))
                    .serialNumber(deviceSerial)
                    .build();
            ownDeviceRepository.save(ownDevice);
            message = "저장 되었습니다.";

            resultMap.put("message", message);
            resultMap.put("isError", "false");

        }
        return resultMap;
    }


    //관리자 회원 검색
    public Page<User> findUserListService(String keyword,String selectOption,Pageable pageable){

        Page<User> searchUsers =  null ;
        System.out.println(keyword);
        System.out.println(selectOption);

        switch (selectOption){
            case "이름":
                searchUsers =userRepository.findByNameLikeOrderByName("%"+keyword+"%",pageable);
                break;
            case "이메일":
                searchUsers =userRepository.findByEmailLikeOrderByName("%"+keyword+"%",pageable);
                break;
            case "핸드폰":
                searchUsers =userRepository.findByPhoneLikeOrderByName("%"+keyword+"%",pageable);
                break;
            default:
                searchUsers =userRepository.findByNameLikeOrderByName("%"+keyword+"%",pageable);
                break;
        };

        return searchUsers;
    }

}
