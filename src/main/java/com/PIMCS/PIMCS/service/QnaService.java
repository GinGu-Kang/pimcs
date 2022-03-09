package com.PIMCS.PIMCS.service;



import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.AnswerRepository;
import com.PIMCS.PIMCS.repository.QuestionRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class QnaService {

    @Autowired
    private final QuestionRepository questionRepository;


    public QnaService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    //질문 추가
    public void addQuestion(Question question, User user, Company company){
        Boolean isSecret=question.getRadioValue().equals("secret");

        question.setCompany(company);
        question.setUser(user);
        question.setSecret(isSecret);
        questionRepository.save(question);
    }

    public Page<Question> findAllQuestion(Pageable pageable){
        return questionRepository.findAll(pageable);

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
    public Question findQuestion(Integer questionId){
        Question question=questionRepository.findById(questionId).get();
        if (question.getAnswer()==null){
            Answer answer = Answer.builder().comment("답변 준비 중입니다.").build();
            question.setAnswer(answer);
        }


        return question;
    }



}
