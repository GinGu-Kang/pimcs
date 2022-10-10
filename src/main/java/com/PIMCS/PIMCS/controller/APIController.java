package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductCategoryJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductPageJsonAdapter;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.Device;
import com.PIMCS.PIMCS.form.OperationForm;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class APIController {
    private final APIService apiService;
    private final CompanyRepository companyRepository;

    @Autowired
    public APIController(APIService apiService, CompanyRepository companyRepository) {
        this.apiService = apiService;
        this.companyRepository = companyRepository;
    }

    /**
     * 회사에 등록된 제품들을 json format 응답
     */
    @GetMapping("/api/products")
    @ResponseBody
    public List<ProductJsonAdapter> findProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm){
        return apiService.findProductsService(secUserCustomForm.getCompany());
    }
    /**
     * 회사에 등록된 제품 page형태로 응답
     */
    @GetMapping("/api/page/products")
    @ResponseBody
    public ProductPageJsonAdapter findPageProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                                   @PageableDefault(size=10) Pageable pageable){
        ProductPageJsonAdapter productPageJsonAdapter = apiService.findPageProductsService(secUserCustomForm.getCompany(), pageable);
        return productPageJsonAdapter;
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


    @GetMapping("/api/mats")
    @ResponseBody
    public MatPageAdapter findMatList(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                      OperationForm operationForm,
                                      SearchForm searchForm,
                                      Pageable pageable){

        String operationValue = operationForm.getLte();
        if(searchForm.isExist()){ //매트 검색
            return apiService.findMatListByAllService(searchForm, secUserCustomForm.getCompany(), pageable);
        }else if(operationValue != null){
            return apiService.findMatListByThresholdLte(secUserCustomForm.getCompany(),pageable);
        }else{
            return apiService.findMatListService(secUserCustomForm.getCompany(), pageable);
        }

    }


    @GetMapping("/api/mats/{serialNumber}/validations")
    @ResponseBody
    public ValidationForm validateSerialNumber(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PathVariable String serialNumber){

        return apiService.validateSerialNumberService(secUserCustomForm.getCompany(), serialNumber);
    }

    /**
     *  제품카테고리 api
     */
    @GetMapping("/api/companies/product/categories")
    @ResponseBody
    public List<ProductCategoryJsonAdapter> findProductsCategory(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm){
        return apiService.findProductsCategoryService(secUserCustomForm.getCompany());
    }

    /**
     *  주문이메일 api
     */
    @PostMapping("/api/mats/email")
    @ResponseBody
    public List<OrderMailRecipients> findMatsEmail(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, @RequestBody Map<String,List<String>> params){

        return apiService.findMatsEmailService(params.get("serialNumbers"), secUserCustomForm.getCompany());
    }


    @GetMapping("/api/devices/{serialNumber}")
    @ResponseBody
    public Device findDeviceListBySerialNumber(@PathVariable String serialNumber){
        Device device = apiService.findDeviceListBySerialNumberService(serialNumber);
        System.out.println(device);
        return device;
    }

    @GetMapping("/api/companies/validation")
    @ResponseBody
    public ValidationForm findCompaniesValidation(@RequestParam String type, @RequestParam String value){
        ValidationForm validationForm = null;
        
        if(type.equals("companyCode")){
           Company company = companyRepository.findByCompanyCode(value).orElse(null);

           if(company == null){
               validationForm = new ValidationForm(false, "존재하지 않는 회사코드 입니다.");
           }else{
               validationForm = new ValidationForm(true, "");   
           }
            
        }

        return validationForm;

    }


}
