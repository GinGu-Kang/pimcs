package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.Question;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.QnaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
    public String questionSave(Question question, @AuthenticationPrincipal SecUserCustomForm secUserCustomForm, User user){
        qnaService.addQuestion(question,user,secUserCustomForm.getCompany());
        return "/qna/question";
    }

    @GetMapping("/list")
    public String qnaList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                          Model model){
        Page<Question> questionPage=qnaService.findAllQuestion(pageable);
        System.out.println(questionPage.getSize());
        System.out.println(questionPage.getNumber());
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());

        return "/qna/qnaList";
    }
}
