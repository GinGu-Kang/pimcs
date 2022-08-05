package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

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


    public void createMatCategoryService(MatCategory matCategory){
        matCategoryRepository.save(matCategory);
    }

    public HashMap<String,String> updateMatCategoryService(MatCategory matCategory){
        Optional<MatCategory> isMatCategory=matCategoryRepository.findById(matCategory.getId());
        HashMap<String,String> resultMap= new HashMap<>();
        String msg="";

        if(isMatCategory.isPresent()){

            matCategoryRepository.save(matCategory);

            msg="변경되었습니다.";
            resultMap.put("result","T");
            resultMap.put("msg",msg);

        }else {
            msg="존재하지 않는 카테고리 입니다.";
            resultMap.put("result","F");
            resultMap.put("msg",msg);
        }
        return resultMap;


    }
    public List<MatCategory> findMatCategoryListService(){
        return matCategoryRepository.findAll();
    }

    public HashMap<String,String> deleteMatCategoryService(Integer id){
        Optional<MatCategory> matCategory=matCategoryRepository.findById(id);
        HashMap<String,String> resultMap= new HashMap<>();
        String msg="";

        if(matCategory.isPresent()){
            matCategoryRepository.delete(matCategory.get());
            msg="삭제되었습니다.";
            resultMap.put("result","T");
            resultMap.put("msg",msg);
        }else {
            msg="존재하지 않는 카테고리 입니다.";
            resultMap.put("result","F");
            resultMap.put("msg",msg);
        }

        return resultMap;
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
    public Optional<Company> findCompany(Integer companyId){
        return companyRepository.findById(companyId);
    }
    //회사 검색
    public Page<Company> filterCompany(String keyword,String selectOption,Pageable pageable){

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
    public void removeOwndevice(List<Integer> ownDeviceList) {
        ownDeviceRepository.deleteAllByIdIn(ownDeviceList);
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
    public Page<Question> filterQuestion(String keyword,String selectOption,Pageable pageable){

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
