package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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


    @Autowired
    public AdminService(AnswerRepository answerRepository, MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, QuestionRepository questionRepository, OrderMailFrameRepository orderMailFrameRepository) {
        this.answerRepository = answerRepository;
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.questionRepository = questionRepository;
        this.orderMailFrameRepository = orderMailFrameRepository;
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

    //댓글 추가 getOne 방식을 사용하면 select + insert 가 아닌 insert 쿼리만 나간다.
    public void addAnswer(Integer questionId, Answer answer){
        Question question=questionRepository.getOne(questionId);

        answer.setQuestion(question);
        answerRepository.save(answer);
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


}
