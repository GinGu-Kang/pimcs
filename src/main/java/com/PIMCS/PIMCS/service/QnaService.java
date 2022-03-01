package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.Question;
import com.PIMCS.PIMCS.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnaService {

    @Autowired
    private final QuestionRepository questionRepository;

    public QnaService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    public void addQuestion(Question question){
        System.out.println(question.getRadioValue().equals("secret"));

//        questionRepository.save(question);
    }
}
