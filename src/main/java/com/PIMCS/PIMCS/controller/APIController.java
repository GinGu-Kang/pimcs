package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductCategoryJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductPageJsonAdapter;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class APIController {
    private final APIService apiService;

    @Autowired
    public APIController(APIService apiService) {
        this.apiService = apiService;
    }

    /**
     * 회사에 등록된 제품들을 json format 응답
     */
    @GetMapping("/api/products")
    @ResponseBody
    public List<ProductJsonAdapter> loadProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm){
        return apiService.loadProductsService(secUserCustomForm.getCompany());
    }
    /**
     * 회사에 등록된 제품 page형태로 응답
     */
    @GetMapping("/api/page/products")
    @ResponseBody
    public ProductPageJsonAdapter loadPageProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                                   @PageableDefault(size=10) Pageable pageable){
        ProductPageJsonAdapter productPageJsonAdapter = apiService.loadPageProductsService(secUserCustomForm.getCompany(), pageable);
        System.out.println("=======");
        System.out.println(productPageJsonAdapter.getData().size());
        System.out.println("========");
        return productPageJsonAdapter;
    }

    /**
     *  매트 검색시 사용하는 api controller
     */
    @GetMapping("/api/search/mats")
    @ResponseBody
    public MatPageAdapter searchMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                     SearchForm searchForm,
                                     Pageable pageable){
        return apiService.searchMatsService(searchForm, secUserCustomForm.getCompany(), pageable);
    }

    /**
     *  product 검색 api
     */
    @GetMapping("/api/search/products")
    @ResponseBody
    public ProductPageJsonAdapter searchProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                                 SearchForm searchForm,
                                                 Pageable pageable){
        return apiService.searchProductsService(searchForm, secUserCustomForm.getCompany(), pageable);
    }


    /**
     *  page query로 select하기 위한 api controler
     */
    @GetMapping("/api/page/mats")
    @ResponseBody
    public MatPageAdapter loadPageMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                          Pageable pageable){
       return apiService.loadPageMatsService(secUserCustomForm.getCompany(), pageable);
    }

    /**
     * 임계값 미달 api controller
     */
    @GetMapping("/api/below/threshold/mats")
    @ResponseBody
    public MatPageAdapter belowThresholdMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                             Pageable pageable){
        return apiService.belowThresholdMats(secUserCustomForm.getCompany(),pageable);

    }

    /**
     *  제품카테고리 api
     */
    @GetMapping("/api/product/category")
    @ResponseBody
    public List<ProductCategoryJsonAdapter> productCategoryAPI(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm){
        return apiService.productCategoryAPIService(secUserCustomForm.getCompany());
    }

}
