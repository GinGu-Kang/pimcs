package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.service.MatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class MatController {

    private final MatService matService;

    @Autowired
    public MatController(MatService matService) {
        this.matService = matService;
    }

    /**
     * 매트 조회
     */
    @GetMapping("/")
    public String inquiryMat(){
        return "mat/inquiryMat";
    }

    /**
     * 매트생성
     */
    @GetMapping("/mat/create")
    public String createMatForm(){

        return "mat/createMat.html";
    }
    @PostMapping("/mat/create")
    public String createMat(Mat mat){


        return null;
    }

    /**
     * 매드읽기
     */
    @GetMapping("/mat/read")
    public String readMat(Model model){
        return "mat/readMat.html";
    }


    /**
     * 매트수정
     */
    @GetMapping("/mat/update")
    public String updateMatForm(Mat mat,Model model){

        return "mat/updateMat.html";
    }
    @PostMapping("/mat/update")
    public String updateMat(Mat mat,Model model){

        matService.updateMat(mat);
        return null;
    }

    /**
     * 매트삭제
     */
    @GetMapping("/mat/delete")
    public String deleteMatForm(Model model){
        return null;
    }
    @PostMapping("/mat/delete")
    public String deleteMat(Model model){
        return null;
    }

    /**
     * 매트 serial num 체크
     */
    @GetMapping("/mat/check/serialNum")
    @ResponseBody
    public HashMap<String,Boolean> checkMatSerialNum(@RequestParam("serialNum") String serialNum){

        return null;
    }

    /**
     * 검색(시리얼번호,상품위치,제품코드,기기버전)
     */
    @PostMapping("/mat/search")
    public String searchMat(SearchForm searchForm, Model model){

        matService.searchMat(searchForm);
        return null;
    }


}
