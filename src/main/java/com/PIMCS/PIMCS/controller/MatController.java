package com.PIMCS.PIMCS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping(value = "/mat")
public class MatController {

    /**
     * 매트생성
     */
    @GetMapping("/create")
    public String createMatForm(){

        return "mat/createMat.html";
    }
    @PostMapping("/create")
    public String createMat(){
        return null;
    }

    /**
     * 매드읽기
     */
    @GetMapping("/read")
    public String readMat(Model model){
        return "mat/readMat.html";
    }


    /**
     * 매트수정
     */
    @GetMapping("/update")
    public String updateMatForm(Model model){

        return "mat/updateMat.html";
    }
    @PostMapping("/update")
    public String updateMat(Model model){

        return null;
    }

    /**
     * 매트삭제
     */
    @GetMapping("/delete")
    public String deleteMatForm(Model model){
        return null;
    }
    @PostMapping("/delete")
    public String deleteMat(Model model){
        return null;
    }

    /**
     * 매트 serial num 체크
     */
    @GetMapping("/check/serialNum")
    @ResponseBody
    public HashMap<String,Boolean> checkMatSerialNum(@RequestParam("serialNum") String serialNum){

        return null;
    }

}
