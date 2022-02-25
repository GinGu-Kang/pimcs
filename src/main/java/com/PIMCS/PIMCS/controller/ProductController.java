package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.form.ProductFormList;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/product")
public class ProductController {

    /**
     * 상품등록
     */
    @GetMapping("/create")
    public String createForm(){
        return "product/createProduct/createProduct";
    }
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductFormList productFormList){
        System.out.println("========");
        System.out.println(productFormList.getProductForms().get(0));
        System.out.println("========");
        return "redirect:/product/create";
    }

    @GetMapping("/create/cardview")
    public String loadProductCardView(@RequestParam String index, Model model){
        model.addAttribute("index",index);
        return "product/createProduct/fragment/cardView";
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
