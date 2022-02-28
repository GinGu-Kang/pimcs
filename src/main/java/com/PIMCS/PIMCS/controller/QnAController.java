package com.PIMCS.PIMCS.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qna")
public class QnAController {

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
    public String question(){

        return "/qna/question";
    }
}
