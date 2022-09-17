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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/qnas")
public class QnAController {
    private final QnaService qnaService;

    public QnAController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

//    @GetMapping("")
//    public String QnAList(){
//
//        return "/qna/qna";
//    }

    @GetMapping("/faq")
    public String FaQList(){

        return "/qna/faq";
    }
    @GetMapping("/create")
    public String createQuestionForm(){

        return "/qna/question";
    }
    @PostMapping("")
    public String questionSave(Question question, @AuthenticationPrincipal SecUserCustomForm secUserCustomForm, User user){
        qnaService.addQuestion(question,user,secUserCustomForm.getCompany());
        return "redirect:/qnas";
    }

    @GetMapping("")
    public String findQnaList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                          Model model){
        Page<Question> questionPage=qnaService.findQnaListService(pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());

        return "/qna/findQnaList";
    }


    //필터링 조회
    @GetMapping("/search")
    public String searchQuestion(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,String keyword,String selectOption,Model model){
        System.out.println(pageable.getPageSize());
        Page<Question> questionPage=qnaService.filterQuestion(keyword,selectOption,pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());
        return "/qna/qnaList";
    }

    /*
        질문보기
        비밀글 여부가 on이면 글쓴이와 일치하는지 검사하고 아니면 noneRole
        비밀글 여부가 off면 아무나 공개
     */
    @GetMapping("/view")
    public String detailQna(Model model,Integer questionId, @AuthenticationPrincipal SecUserCustomForm secUserCustomForm){

        Question question = qnaService.findQuestion(questionId);

        if (question.isSecret()){

            if(question.getUser().getEmail().equals(secUserCustomForm.getUsername())){
                model.addAttribute(question);
                return "/qna/qnaView";
            }else{
                return "noneRole";
            }
        }
        else {
            model.addAttribute(question);
            return "/qna/qnaView";
        }

    }

}
