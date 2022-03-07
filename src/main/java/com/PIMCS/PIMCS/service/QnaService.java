package com.PIMCS.PIMCS.service;



import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Question;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


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

        };

        return searchQuestions;
    }
}
