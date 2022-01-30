package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.CompanyRegistrationFrom;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    //회사 가입
    @GetMapping("registration")
    public String companyRegistrationFrom( ){
        return "company/companyRegistration";
    }
    @PostMapping("registration")
    public String companyRegistration(CompanyRegistrationFrom companyRegistrationFrom){
        User user= new User();


        return "user/auth/login";
    }

    //사원 관리 현재 get으로 회사코드를 받아옴. 회사코드를 알면 사원정보를 알수있기 때문에 나중에 post방식으로 변경
    @GetMapping("worker")
    public String companyWorkerManagement(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        List<User> companyWorker=companyManagementService.findMyCompanyWorker(user.getCompanyCode());
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }


    @GetMapping("worker/approve")
    public String companyWorkerApprove(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        List<User> companyWorker=companyManagementService.findApproveWaitWorker(user.getCompanyCode());
        model.addAttribute("companyWorker",companyWorker);
        return "company/worker/workerManagement";
    }





}
