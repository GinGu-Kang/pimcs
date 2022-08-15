package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@Controller
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }


    @GetMapping("/products/category/create")
    public String createProductCategoryForm(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<ProductCategory> productCategories = productCategoryService.readProductCategoryService(secUserCustomForm.getCompany());
        model.addAttribute("productCategories", productCategories);
        return "productCategory/createProductCategory";
    }
    @PostMapping("/products/category")
    public String createProductCategory(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductCategory productCategory){
//        productCategory.setCompany(secUserCustomForm.getCompany());
        productCategoryService.createProductCategoryService(secUserCustomForm.getCompany(), productCategory);
        return "redirect:/products/category/create";
    }

    @DeleteMapping("/products/category")
    @ResponseBody
    public HashMap<String, String> deleteProductCategory(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductCategory productCategory){

        return productCategoryService.deleteProductCategoryService(secUserCustomForm.getCompany(), productCategory);
    }

}
