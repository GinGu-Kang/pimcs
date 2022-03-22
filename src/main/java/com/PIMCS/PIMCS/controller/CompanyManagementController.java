package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyManagementService companyManagementService;


    @Autowired
    public CompanyManagementController(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyManagementService companyManagementService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyManagementService = companyManagementService;
    }
    //회사 등록
    @GetMapping("registration")
    public String companyRegistrationFrom( ){
        return "company/companyRegistration";
    }

    //회사 등록
    @PostMapping("registration")
    public String companyRegistration(User ceo, Company company){
        company.setCeoName(ceo.getName());
        ceo.setEmail(company.getCeoEmail());
        companyManagementService.companyRegistration(ceo,company);
        return "user/auth/login";
    }
    @GetMapping("registration/verify")
    public String companyRegistrationVerify(@RequestParam("verifyKey") String verifyKey){
        System.out.println(verifyKey+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        companyManagementService.companyRegistrationVerify(verifyKey);
        return "redirect:/user/auth/login";
    }

    //회사원 전체 조회
    @GetMapping("worker")
    public String companyWorkerManagement(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        List<User> companyWorker=companyManagementService.findMyCompanyWorker(user.getCompany());
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }
    //필터링 조회
    @GetMapping("search")
    public String searchCompanyWorkerManagement(String keyword,String selectOption,Model model, @AuthenticationPrincipal SecUserCustomForm user){
        List<User> companyWorker=companyManagementService.filterMyCompanyWorker(keyword,selectOption,user.getCompany());
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }

    /**
     * 선택된 회사원 삭제
     * 선택된 이메일 받아오기
     */
    @PostMapping("worker/remove")
    @ResponseBody
    public void removeSelectWorker(@RequestParam(value="selectWorkersEmail[]") List<String> selectWorkersEmail,@AuthenticationPrincipal SecUserCustomForm workerManager){
        companyManagementService.companyWorkerDelete(selectWorkersEmail,workerManager.getCompany());
        System.out.println(selectWorkersEmail.get(0));
    }


    @PostMapping("give/authority")
    @ResponseBody
    public boolean giveAuthority(String email, String authority,@AuthenticationPrincipal SecUserCustomForm user){
        boolean isEqualCompany=companyManagementService.userRoleSave(email,authority,user.getCompany().getCompanyCode());
        return isEqualCompany;
    }

    @PostMapping("remove/authority")
    @ResponseBody
    public boolean removeAuthority(String email, String authority,@AuthenticationPrincipal SecUserCustomForm user){
        boolean isEqualCompany=companyManagementService.userRoleDelete(email,authority,user.getCompany().getCompanyCode());
        return isEqualCompany;
    }


    //회사 정보
    @GetMapping("/info")
    public String companyInfo(@AuthenticationPrincipal SecUserCustomForm user,Model model){
        Company company = user.getCompany();
        model.addAttribute(company);
        return "/company/companyInfoModify.html";
    }

    @PostMapping("/info/modify")
    public String companyInfoModify(Model model,@AuthenticationPrincipal SecUserCustomForm user,Company companyForm){
        Company userCompany =  user.getCompany();
        userCompany.setCompanyAddress(companyForm.getCompanyAddress());
        userCompany.setCompanyName(companyForm.getCompanyName());
        userCompany.setContactPhone(companyForm.getContactPhone());
        userCompany.setCeoName(companyForm.getCeoName());
        userCompany.setCeoEmail(companyForm.getCeoEmail());
        companyManagementService.updateCompany(userCompany);
        model.addAttribute("company",userCompany);
        return "/company/companyInfoModify.html";
    }


}
