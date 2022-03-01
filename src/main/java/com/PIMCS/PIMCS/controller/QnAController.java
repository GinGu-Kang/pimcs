package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.Question;
import com.PIMCS.PIMCS.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qna")
public class QnAController {
    private final QnaService qnaService;

    public QnAController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("")
    public String QnAList(){

        return "/qna/qna";
    }

    @GetMapping("/faq")
    public String FaQList(){

        return "/qna/faq";
    }
    @GetMapping("/question")
    public String questionForm(){

        return "/qna/question";
    }
    @PostMapping("/question")
    public String questionSave(Question question){
//        qnaService.addQuestion(question);
        return "/qna/question";
    }

    @GetMapping("/list")
    public String qnaList(){

        return "/qna/qnaList";
    }
}
