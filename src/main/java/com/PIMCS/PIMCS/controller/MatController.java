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

    /**
     * 매트 조회
     */
    @GetMapping("/")
    public String readMat(){
        return "mat/readMat/readMat";
    }

    /**
     * 매트생성
     */
    @GetMapping("/mat/create")
    public String createMatForm(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<Product> products = matService.createMatFormService(secUserCustomForm.getCompany());
        model.addAttribute("products",products);
        return "mat/createMat/createMat.html";
    }

    @PostMapping("/mat/create")
    public String createMat(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatForm matForm, Model model){

        User user = userRepository.getOne(secUserCustomForm.getUsername());
        Mat mat = matService.createMat(matForm,secUserCustomForm.getCompany(), user);

        String mailRecipientsStr = String.join(",",matForm.getMailRecipients());

        model.addAttribute("mat",mat);
        model.addAttribute("mailRecipientsStr", mailRecipientsStr);
        return "mat/createMat/fragment/registeredCardView.html";
    }

    /**
     * 매트수정
     */
    @PostMapping("/mat/update")
    @ResponseBody
    public HashMap<String,String> updateMat(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatFormList matFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return matService.updateMat(secUserCustomForm.getCompany(),matFormList, user);
    }

    /**
     * 주문이메일 변경
     */
    @PostMapping("/mat/update/email")
    @ResponseBody
    public HashMap<String, String> updateMatEmail(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MailRecipientsForm mailRecipientsForm){
        System.out.println(mailRecipientsForm.toString());
        return matService.updateMatEmailService(mailRecipientsForm.getMailRecipients(), secUserCustomForm.getCompany());
    }


    /**
     * 매트삭제
     */

    @PostMapping("/mat/delete")
    @ResponseBody
    public HashMap<String,Object> deleteMat(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatFormList matFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return matService.deleteMat(secUserCustomForm.getCompany(), matFormList, user);
    }

    /**
     * 매트 serial num 체크
     */
    @GetMapping("/mat/check/serialNum")
    @ResponseBody
    public HashMap<String,Object> checkMatSerialNum(@RequestParam("serialNum") String serialNum){
        return matService.checkMatSerialNumberService(serialNum);
    }

    /**
     *  매트 csv다운로드
     */
    @PostMapping(value = "/download/mat/csv",  produces = "text/csv")
    public void downloadMatCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,MatCsvForm matCsvForm ,HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "mat-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        matService.downloadMatCsvService(secUserCustomForm.getCompany(), matCsvForm, response.getWriter());
    }

    @GetMapping("/mat/log")
    public String matLog(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            Model model){

        DynamoResultPage dynamoResultPage = matService.matLogService(secUserCustomForm.getCompany(), pageable);

        model.addAttribute("dynamoResultPage", dynamoResultPage);
        model.addAttribute("searchForm", new InOutHistorySearchForm());
        return "mat/matLog.html";
    }

    @GetMapping("/mat/log/search")
    public String searchMatLog(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            InOutHistorySearchForm searchForm,
            Model model
    ){
        System.out.println(searchForm);

        DynamoResultPage dynamoResultPage = matService.searchMatLogService(secUserCustomForm.getCompany(), searchForm, pageable);
        model.addAttribute("dynamoResultPage", dynamoResultPage);
        model.addAttribute("searchForm", searchForm);
        return "mat/matLog.html";
    }

    /**
     * 앙도하기
     */
    @PostMapping("/mat/transfer")
    @ResponseBody
    public HashMap<String, String> transferMat(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            MatFormList matFormList,
            @RequestParam(value = "companyCode")String companyCode){

        User user = userRepository.getOne(secUserCustomForm.getUsername());

        return matService.transferMatService(secUserCustomForm.getCompany(), matFormList, companyCode, user);
    }

}
