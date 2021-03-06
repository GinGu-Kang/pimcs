package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public void addMatCategory(MatCategory matCategory){
        matCategoryRepository.save(matCategory);
    }

    public void modifyMatCategory(MatCategory matCategory){
        Optional<MatCategory> matCategorySource=matCategoryRepository.findByMatCategoryName(matCategory.getMatCategoryName());

        if(matCategorySource.isPresent()){
            matCategory.setId(matCategorySource.get().getId());
            matCategoryRepository.save(matCategory);
        }

    }
    public List<MatCategory> findMatCategory(){
        return matCategoryRepository.findAll();
    }

    public void removeMatCategory(Integer id){
        Optional<MatCategory> matCategory=matCategoryRepository.findById(id);
        if(matCategory.isPresent()){
            matCategoryRepository.delete(matCategory.get());
        }
    }

    public List<MatCategory> findMatCategoryList(List<Integer> matCategoryIdList){
        return matCategoryRepository.findAllById(matCategoryIdList);
    }

    //?????? ?????? getOne ????????? ???????????? select + insert ??? ?????? insert ????????? ?????????.
    public void addAnswer(Integer questionId, Answer answer){
        Question question=questionRepository.getOne(questionId);

        answer.setQuestion(question);
        answerRepository.save(answer);
    }

    /*?????? ??????*/
    public Page<Company> findAllCompany(Pageable pageable){
        return companyRepository.findAll(pageable);
    }
    //?????? ??????
    public Page<Company> filterCompany(String keyword,String selectOption,Pageable pageable){

        Page<Company> searchCompanys =  null ;

        switch (selectOption){
            case "?????? ??????":
                searchCompanys =companyRepository.findByCompanyCodeLike("%"+keyword+"%",pageable);
                break;
            default:
                searchCompanys =companyRepository.findByCompanyNameLike("%"+keyword+"%",pageable);
                break;
        };

        return searchCompanys;
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
    //?????? ????????? ??????
    public Page<Question> filterQuestion(String keyword,String selectOption,Pageable pageable){

        Page<Question> searchQuestions =  null ;

        switch (selectOption){
            case "??????":
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

    //?????? ????????? ??????
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


    /*?????? ?????? ??????*/
    //csv ????????? ?????? ??????
    //sendHistory ????????????
    @Transactional
    public HashMap<String,String> addOwnDeviceAndSendHistory(Integer orderId, List<String> deviceSerialList , Integer companyId){
        List<OwnDevice> ownDeviceList = new ArrayList<OwnDevice>();
        List<OwnDevice> ownDeviceDuplicationCheckList = new ArrayList<OwnDevice>();
        String ownDeviceListCsv ="";
        String message="";
        HashMap<String,String> resultMap = new HashMap<String,String>();
        MatOrder matOrder= matOrderRepository.getById(orderId);
        //?????? ?????? ??????
        ownDeviceDuplicationCheckList=ownDeviceRepository.findAllBySerialNumberIn(deviceSerialList);

        //sendHistory ?????? ??????
        if(matOrder.getSendHistory()!=null){

            message="?????? ????????? ?????????????????????.";

            resultMap.put("message",message);
            resultMap.put("isError","true");

        }else if (ownDeviceDuplicationCheckList.size()!=0){

            message="????????? ?????????????????????. \n????????? ??????: ";

            for (OwnDevice duplicateDevice:ownDeviceDuplicationCheckList
            ) {
                message+=duplicateDevice.getSerialNumber()+",";
            }

            resultMap.put("message",message);
            resultMap.put("isError","true");

        }else{
            message="????????? ?????? ???????????????.";
            ownDeviceListCsv=String.join(",",deviceSerialList);
            //????????? ?????? ??????
            SendHistory sendHistory = SendHistory
                    .builder()
                    .matOrder(matOrder)
                    .history(ownDeviceListCsv)
                    .build();

            //????????? ????????? ?????? ??????
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


}
