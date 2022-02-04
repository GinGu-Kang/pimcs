package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@RequestMapping("company")
public class CompanyManagementController {
    private final CompanyManagementService companyManagementService;


    @Autowired
    public CompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }
    //회사 등록
    @GetMapping("registration")
    public String companyRegistrationFrom( ){
        return "company/companyRegistration";
    }


    @PostMapping("registration")
    public String companyRegistration(User ceo, Company company){
        ceo.setEmail(company.getCeoEmail());
        companyManagementService.companyRegistration(ceo,company);
        return "user/auth/login";
    }


    @GetMapping("worker")
    public String companyWorkerManagement(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        List<User> companyWorker=companyManagementService.findMyCompanyWorker(user.getCompanyCode());
        ArrayList<String> a = new ArrayList<>();
        a.add("ROLE_User");
//        System.out.println(companyWorker.get(0).getAuthorities().contains(2,"sadf"));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }

    @PostMapping("giveAuthority")
    @ResponseBody
    public Model giveAuthority(String email,String authority){

    }



//    @GetMapping("worker/approve")
//    public String companyWorkerApprove(Model model, @AuthenticationPrincipal SecUserCustomForm user){
//        List<User> companyWorker=companyManagementService.findApproveWaitWorker(user.getCompanyCode());
//        model.addAttribute("companyWorker",companyWorker);
//        return "company/worker/workerManagement";
//    }






}
