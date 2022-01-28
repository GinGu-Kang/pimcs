package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/management/company")
public class CompanyManagementController {
    private final CompanyManagementService companyManagementService;

    public CompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }

    //사원 관리 현재 get으로 회사코드를 받아옴. 회사코드를 알면 사원정보를 알수있기 때문에 나중에 post방식으로 변경
    @GetMapping("worker")
    public String companyWorkerManagement(@RequestParam(name="companyCode")String companyCode, Model model, HttpServletRequest httpServletRequest){
        companyCode="code123";
        List<User> companyWorker=companyManagementService.findMyCompanyWorker(companyCode);
        model.addAttribute("companyWorker",companyWorker);
        return "company/workerManagement";
    }



}
