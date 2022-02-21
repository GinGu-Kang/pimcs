package com.PIMCS.PIMCS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/product/category")
public class ProductCategoryController {

    @GetMapping("/create")
    public String createProductCategory(){
        return "productCategory/createProductCategory";
    }

}
