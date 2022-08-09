package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AnswerRepository answerRepository;
    private final MatCategoryRepository matCategoryRepository;
    private final MatCategoryOrderRepository matCategoryOrderRepository;
    private final MatOrderRepository matOrderRepository;
    private final QuestionRepository questionRepository;
    private final OrderMailFrameRepository orderMailFrameRepository;
    private final CompanyRepository companyRepository;
    private final OwnDeviceRepository ownDeviceRepository;
    private final SendHistoryRepository sendHistoryRepository;
    private static final Logger logger =LoggerFactory.getLogger(AdminService.class);


    @Autowired
    public AdminService(AnswerRepository answerRepository, MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, QuestionRepository questionRepository, OrderMailFrameRepository orderMailFrameRepository, CompanyRepository companyRepository, OwnDeviceRepository ownDeviceRepository, SendHistoryRepository sendHistoryRepository) {
        this.answerRepository = answerRepository;
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.questionRepository = questionRepository;
        this.orderMailFrameRepository = orderMailFrameRepository;
        this.companyRepository = companyRepository;
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

    //댓글 추가 getOne 방식을 사용하면 select + insert 가 아닌 insert 쿼리만 나간다.
    public void addAnswer(Integer questionId, Answer answer){
        Question question=questionRepository.getOne(questionId);

        answer.setQuestion(question);
        answerRepository.save(answer);
    }

    /*회사 조회*/
    public Page<Company> findAllCompany(Pageable pageable){
        return companyRepository.findAll(pageable);
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


    public Question findQuestion(Integer questionId){
        Question question=questionRepository.findById(questionId).get();

        if (question.getAnswer()==null){
            Answer answer = Answer.builder().comment("").build();
            question.setAnswer(answer);
        }

        return question;
    }

    //질문 필터링 검색
    public Page<Question> findAdminQuestionListService(String keyword,String selectOption,Pageable pageable){

        Page<Question> searchQuestions =  null ;

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

    public OrderMailFrame updateOrderMailFrame(OrderMailFrame orderMailFrame){
        orderMailFrame.setId(1);
        orderMailFrameRepository.save(orderMailFrame);
        return orderMailFrame;
    }

    public OrderMailFrame selectOrderMailFrame(){
        OrderMailFrame orderMailFrame = orderMailFrameRepository.findById(1).get();
        return orderMailFrame;
    }

    public Page<MatOrder> findAllOrder(Pageable pageable){
        return matOrderRepository.findAll(pageable);
    }

    //주문 필터링 검색
    public Page<MatOrder> filterOrder(String keyword,Integer totalPriceStart,Integer totalPriceEnd, Pageable pageable){

        Page<MatOrder> searchOrders =  null ;

        searchOrders =matOrderRepository.findByDepositerNameLikeAndTotalPriceBetween("%"+keyword+"%",totalPriceStart,totalPriceEnd,pageable);

        return searchOrders;
    }

    public MatOrder findOrder(Integer orderId){
        return matOrderRepository.findById(orderId).get();
    }

    public MatOrder modifyDeposit(Integer orderId,Boolean isDeposit){
        MatOrder matOrder=matOrderRepository.getOne(orderId);

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
    public HashMap<String,String> addOwnDeviceAndSendHistory(Integer orderId, List<String> deviceSerialList , Integer companyId){
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
    public HashMap<String,String> addOwnDevice(String deviceSerial , Integer companyId) {
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

}
