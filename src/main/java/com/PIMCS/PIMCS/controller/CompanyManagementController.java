package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.form.response.ResponseForm;
import com.PIMCS.PIMCS.repository.BusinessCategoryRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitCeoRedisRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2022.01.27
 * @author: GinGu-Kang
 *     구현 page
 *     1.회사등록: companyRegistration
 *     2.사원조회: companyWorkerManagement
 *     3.사원삭제: companyWorkerDelete
 *     4.사원권한수정: companyWorkerRoleModify
 *     5.회사정보검색: companyInfoSearch
 *     6.회사정보: companyInfo
 *     7.회사정보수정: companyInfoModify
 *     8.사원승인(승인대기사원) companyWorkerApprove
 */

@Controller
@RequestMapping("companies")
public class CompanyManagementController {
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyManagementService companyManagementService;
    private final WaitCeoRedisRepository waitCeoRedisRepository;
    private final BusinessCategoryRepository businessCategoryRepository;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    public class UrlNotFoundException extends RuntimeException { }


    @Autowired
    public CompanyManagementController(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyManagementService companyManagementService, WaitCeoRedisRepository waitCeoRedisRepository, BusinessCategoryRepository businessCategoryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyManagementService = companyManagementService;
        this.waitCeoRedisRepository = waitCeoRedisRepository;
        this.businessCategoryRepository = businessCategoryRepository;
    }



    //회사 등록
    @GetMapping("create")
    public String createCompanyForm(Model model){
        List<BusinessCategory> businessCategories = businessCategoryRepository.findAll();
        model.addAttribute("businessCategories", businessCategories);
        return "company/companyRegistration";
    }


    //회사 등록
    @PostMapping("")
    public String createCompany(User ceo, Company company){
        BusinessCategory businessCategory = businessCategoryRepository.findById(company.getBusinessCategoryId().getId()).orElse(null);
        if(businessCategory == null){
            throw new IllegalStateException("Does not exist business category");
        }

        company.setBusinessCategoryId(businessCategory);
        company.setCeoName(ceo.getName());
        ceo.setEmail(company.getCeoEmail());
        companyManagementService.createCompanyService(ceo,company);
        return "redirect:/auth/login";
    }

    //회사정보 수정
    @PutMapping("")
    public String updateCompany(Model model,@AuthenticationPrincipal SecUserCustomForm user,Company companyForm){

        BusinessCategory businessCategory = businessCategoryRepository.findById(companyForm.getBusinessCategoryId().getId()).orElse(null);
        if(businessCategory == null){
            throw new IllegalStateException("Does not exist business category");
        }

        Company userCompany =  user.getCompany();
        userCompany.setCompanyAddress(companyForm.getCompanyAddress());
        userCompany.setCompanyName(companyForm.getCompanyName());
        userCompany.setContactPhone(companyForm.getContactPhone());
        userCompany.setCeoName(companyForm.getCeoName());
        userCompany.setCeoEmail(companyForm.getCeoEmail());
        userCompany.setBusinessCategoryId(businessCategory);
        companyManagementService.updateCompanyService(userCompany);
        model.addAttribute("company",userCompany);
        return "redirect:/companies/details";
    }

    //회사 정보
    @GetMapping("/details")
    public String companyDetails(@AuthenticationPrincipal SecUserCustomForm user,Model model){
        Company company = user.getCompany();

        List<BusinessCategory> businessCategories = businessCategoryRepository.findAll();
        model.addAttribute("businessCategories", businessCategories);
        model.addAttribute(company);
        return "/company/companyInfoModify.html";
    }

    //회사등록시 발급한 검증키 확인 및 회사와 대표등록
    @GetMapping("verification/{verifyKey}")
    public String confirmVerificationKey(@PathVariable String verifyKey){

        WaitCeo waitCeo = waitCeoRedisRepository.findById(verifyKey).orElse(null);

        if(waitCeo != null){ // 유효한 검증키일때
            companyManagementService.createCompanyAndCeoService(waitCeo.getCompany(), waitCeo.getUser());
            return "company/companyRegistrationSuccess.html";

        }else{// 유효하지 않은 검증키면 404발생시키기
            throw new UrlNotFoundException();
        }
    }

    /**
     * 회사원 전체 조회 또는 회사원 검색
     */
    @GetMapping("workers")
    public String findCompanyWorkers(Model model, @AuthenticationPrincipal SecUserCustomForm user, SearchForm searchForm){
        List<User> companyWorker;

        if(searchForm.isExist()) {// 검색일때
            companyWorker=companyManagementService.findCompanyWorkersByNameOrDepartmentService(searchForm,user.getCompany());
        }else{
            companyWorker = companyManagementService.findCompanyWorkersService(user.getCompany());
        }
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }

    /**
     * 선택된 회사원 삭제
     * 선택된 이메일 받아오기
     */
    @DeleteMapping("workers")
    @ResponseBody
    public ResponseForm deleteCompanyWorkers(@RequestParam(value="selectWorkersEmail[]") List<String> selectWorkersEmail, @AuthenticationPrincipal SecUserCustomForm workerManager){

        companyManagementService.deleteCompanyWorkersService(selectWorkersEmail,workerManager.getCompany());
        return new ResponseForm(true, "회원 삭제 되었습니다.", null);
    }

    /**
     * 선택한 이메일에 권한부여
     */
    @PostMapping("worker/authority")
    @ResponseBody
    public ResponseForm createWorkerAuthority(String email, String authority,@AuthenticationPrincipal SecUserCustomForm user){
        System.out.println("=====djiojdmio");
        System.out.println("email:"+ email);
        System.out.println("auth: "+authority);
        System.out.println(user.getCompany());
        System.out.println("===");
        return companyManagementService.createWorkerAuthorityService(email,authority,user.getCompany().getCompanyCode());
    }

    //선택한 이메일의 권한 회수
    @DeleteMapping("worker/authority")
    @ResponseBody
    public ResponseForm deleteWorkerAuthority(String email, String authority,@AuthenticationPrincipal SecUserCustomForm user){

        return companyManagementService.deleteWorkerAuthorityService(email,authority,user.getCompany().getCompanyCode());
    }



}
