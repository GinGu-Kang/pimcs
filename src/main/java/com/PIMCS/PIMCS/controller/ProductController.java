package com.PIMCS.PIMCS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/product")
public class ProductController {

    /**
     * 상품등록
     */
    @GetMapping("/create")
    public String createForm(){
        return "product/createProduct";
    }
    @PostMapping("/create")
    public String create(){
        return null;
    }

    /**
     * 상품읽기
     */
    @GetMapping("/read")
    public String readForm(){
        return null;
    }
    @PostMapping("/read")
    public String read(){
        return null;
    }

    /**
     * 상품수정
     */
    @GetMapping("/update")
    public String updateForm(Model model){
        return null;
    }
    @PostMapping("/update")
    public String update(){

        return null;
    }

    /**
     * 상품삭제
     */
    @PostMapping("/delete")
    public String delete(){
        return null;
    }




}
