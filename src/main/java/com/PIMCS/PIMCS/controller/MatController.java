package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.Utils.MatControllerUtils;
import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.MatService;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;

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
    public String readMat(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                             @PageableDefault(page = 1,size = 10) Pageable pageable,
                             Model model){

        Page<Mat> mats = matService.readMatService(secUserCustomForm.getCompany(),pageable);
        model.addAttribute("mats",mats);
        System.out.println(mats);
        return "mat/readMat";
    }

    /**
     *  ajax로 매트데이터 로드하기
     *  Mat entity객체 사용하지않고 별도의 객체를 만들어서 response
     */
    @GetMapping("/api/mats")
    @ResponseBody
    public MatPageAdapter ajaxLoadMatData(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                           Pageable pageable){
        Page<Mat> pageMats = matService.readMatService(secUserCustomForm.getCompany(),pageable);
        System.out.println("=========");
        System.out.println(pageMats);
        System.out.println(pageMats.getNumber());
        for(Mat mat : pageMats){
            System.out.println(mat.getId());
            System.out.println(mat.getSerialNumber());
            System.out.println(mat.getBattery());
            break;
        }

        System.out.println("=========");
        return  new MatControllerUtils().createMatPageAdapter(pageMats,secUserCustomForm.getCompany());
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
