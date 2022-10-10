package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.service.MatService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class MatController {

    private final MatService matService;
    private final UserRepository userRepository;


    @Autowired
    public MatController(MatService matService, UserRepository userRepository) {
        this.matService = matService;

        this.userRepository = userRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }

    @GetMapping("/")
    public String index(){
        return "redirect:/mats";
    }



    @GetMapping("/mats")
    public String findMatList(){
        return "mat/readMat/readMat";
    }

    /**
     * 매트생성
     */
    @GetMapping("/mats/create")
    public String createMatsForm(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<Product> products = matService.createMatFormService(secUserCustomForm.getCompany());
        model.addAttribute("products",products);
        return "mat/createMat/createMat.html";
    }

    @PostMapping("/mats")
    public String createMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, @RequestBody  MatForm matForm, Model model){

        System.out.println(matForm);
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        Mat mat = matService.createMatsService(matForm,secUserCustomForm.getCompany(), user);

        String mailRecipientsStr = String.join(",",matForm.getMailRecipients());

        model.addAttribute("mat",mat);
        model.addAttribute("mailRecipientsStr", mailRecipientsStr);
        return "mat/createMat/fragment/registeredCardView.html";
    }

    /**
     * 매트수정
     */
    @PutMapping("/mats")
    @ResponseBody
    public HashMap<String,String> updateMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatFormList matFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return matService.updateMatsService(secUserCustomForm.getCompany(),matFormList, user);
    }

    /**
     * 주문이메일 변경
     */
    @PutMapping("/mats/email")
    @ResponseBody
    public HashMap<String, String> updateMatsEmail(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MailRecipientsForm mailRecipientsForm){
        System.out.println(mailRecipientsForm.toString());
        return matService.updateMatsEmailService(mailRecipientsForm.getMailRecipients(), secUserCustomForm.getCompany());
    }


    /**
     * 매트삭제
     */
    @DeleteMapping("/mats")
    @ResponseBody
    public HashMap<String,Object> deleteMats(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatFormList matFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return matService.deleteMatsService(secUserCustomForm.getCompany(), matFormList, user);
    }

    /**
     *  매트 csv다운로드
     */
    @PostMapping(value = "/mats/csv/download",  produces = "text/csv")
    public void downloadMatCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,MatCsvForm matCsvForm ,HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "mat-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        matService.downloadMatCsvService(secUserCustomForm.getCompany(), matCsvForm, response.getWriter());
    }

    @GetMapping("/mats/log")
    public String matLog(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            InOutHistorySearchForm searchForm,
            Model model){

        DynamoResultPage dynamoResultPage;
        if(searchForm.isExist()){

            dynamoResultPage = matService.searchMatLogService(secUserCustomForm.getCompany(), searchForm, pageable);
        }else{
           dynamoResultPage = matService.matLogService(secUserCustomForm.getCompany(), pageable);
        }


        model.addAttribute("dynamoResultPage", dynamoResultPage);
        model.addAttribute("searchForm",searchForm);
        return "mat/matLog.html";
    }


    /**
     * 매트관리 로그
     */
    @GetMapping(value = "/mats/log/csv",  produces = "text/csv")
    public void downloadMatsLogCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,InOutHistorySearchForm searchForm ,HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "mat-log-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);

        matService.downloadMatsLogCsvService(secUserCustomForm.getCompany(), searchForm, response.getWriter());
    }


    /**
     * 앙도하기
     */
    @PutMapping("/mats/to/{companyCode}")
    @ResponseBody
    public HashMap<String, String> matsToOtherCompany(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            MatFormList matFormList,
            @PathVariable  String companyCode){

        User user = userRepository.getOne(secUserCustomForm.getUsername());

        return matService.matsToOtherCompanyService(secUserCustomForm.getCompany(), matFormList, companyCode, user);

    }

}
