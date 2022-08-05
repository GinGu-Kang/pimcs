package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.service.AdminService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/matcategories/create")
    public String createMatCategoryForm(){
        return "admin/createMatCategory";
    }


    @ResponseBody
    @PostMapping("/matcategories")
    public MatCategory createMatCategory(@RequestBody MatCategory matCategory){
        MatCategory resultData=adminService.createMatCategoryService(matCategory);
        return resultData;
    }


    @GetMapping("/matcategories")
    public String findMatCategoryList(Model model){
        model.addAttribute(adminService.findMatCategoryListService());
        return "admin/findMatCategoryList.html";
    }

    @ResponseBody
    @PutMapping("/matcategories")
    public HashMap<String,String> updateMatCategory(MatCategory matCategory){
        HashMap<String,String> resultMap = new HashMap<>();
        try{
             resultMap= adminService.updateMatCategoryService(matCategory);
        }
        catch (DataIntegrityViolationException e){
            System.out.println("잡았다 요놈");
            resultMap.put("msg","error");
        }

        return resultMap;
    }

    @ResponseBody
    @DeleteMapping("/matcategories")
    public void deleteMatCategory(Integer Id){
        adminService.deleteMatCategoryService(Id);
    }


    //회사 조회
    @GetMapping("companies")
    public String companySearch(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                @RequestParam(value = "selectOption", defaultValue = "") String selectOption,
                                Model model){
        Page<Company> companyPage=adminService.filterCompany(keyword,selectOption,pageable);
        model.addAttribute("companyPage",companyPage);
        model.addAttribute("companyList",companyPage.getContent());
        return "admin/companyList";
    }

    /*회사 상세 보기*/
    @GetMapping("companies/{companyId}")
    public String companyDetail(Model model,@PathVariable Integer companyId){
        Optional<Company> company = adminService.findCompany(companyId);
        if (company.isPresent()){
            model.addAttribute("company",company.get());
            return "admin/companyView";
        }else{
            return "noneRole";
        }
    }
    /*매핑된 기기 삭제*/
    @ResponseBody
    @DeleteMapping (value = "owndevices")
    public Boolean deleteOwnDevices(@RequestParam(value = "ownDeviceList[]") List<Integer> ownDeviceList) {
        adminService.removeOwndevice(ownDeviceList);
        return true;
    }



    @GetMapping("qna/list")
    public String adminQnaList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model){
        Page<Question> questionPage=adminService.findAllQuestion(pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());

        return "admin/adminQnaList";
    }
    //필터링 조회
    @GetMapping("qna/search")
    public String adminSearchQuestion(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,String keyword,String selectOption,Model model){
        System.out.println(keyword);
        Page<Question> questionPage=adminService.filterQuestion(keyword,selectOption,pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());
        return "admin/adminQnaList";
    }

    @GetMapping("qna/view")
    public String detailAdminQna(Model model,Integer questionId){
        Question question = adminService.findQuestion(questionId);
        model.addAttribute(question);
        return "admin/adminQnaView";
    }

    @PostMapping("qna/view")
    public String answerAdd(Answer answer,Integer questionId){
        adminService.addAnswer(questionId,answer);
        return "redirect:/admin/qna/list";
    }

    @GetMapping("email/frame/modify")
    public String emailFrameModifyForm(Model model){
        model.addAttribute("OrderMailFrame",adminService.selectOrderMailFrame());
        return "admin/emailFrameModify";
    }
    @PostMapping("email/frame/modify")
    public String emailFrameModify(OrderMailFrame orderMailFrame){
        System.out.println(orderMailFrame.getGreeting());
        adminService.updateOrderMailFrame(orderMailFrame);
        return "redirect:/admin/email/frame/modify";
    }

    /*
    *주문 목록
     */
    @GetMapping("/order/list")
    public String orderList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page<MatOrder> matOrderPage=adminService.findAllOrder(pageable);
        model.addAttribute("matOrderPage",matOrderPage);
        model.addAttribute("matOrderList",matOrderPage.getContent());

        return "admin/orderList";
    }

    @GetMapping("order/search")
    public String adminOrderQuestion(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,String keyword,Integer totalPriceStart,Integer totalPriceEnd,Model model){
        System.out.println( "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(keyword);
        System.out.println(totalPriceEnd);
        System.out.println(totalPriceStart);

        System.out.println(keyword);
        Page<MatOrder> matOrderPage=adminService.filterOrder(keyword,totalPriceStart,totalPriceEnd,pageable);
        model.addAttribute("matOrderPage",matOrderPage);
        model.addAttribute("matOrderList",matOrderPage.getContent());
        return "admin/orderList";
    }

    @GetMapping("order/view/{orderId}")
    public String detailOrder(Model model,@PathVariable Integer orderId){
        MatOrder matOrder = adminService.findOrder(orderId);
        User orderer=matOrder.getUser();
        Company orderCompany=matOrder.getCompany();
        model.addAttribute(matOrder);
        model.addAttribute(orderer);
        model.addAttribute(orderCompany);
        return "admin/orderView";
    }
    @ResponseBody
    @PutMapping("order/deposit/{orderId}")
    public String depositModify(@PathVariable Integer orderId,Boolean isDeposit){
        MatOrder matOrder = adminService.modifyDeposit(orderId,isDeposit);
        return "admin/orderView";
    }

    @ResponseBody
    @PostMapping("order/{orderId}/owndevice")
    public HashMap<String,String> ownDeviceAndSendHistorySave(@PathVariable Integer orderId, @RequestParam(value="deviceSerialList[]") List<String> deviceSerialList , Integer companyId){
        HashMap<String,String> resultMap = adminService.addOwnDeviceAndSendHistory(orderId,deviceSerialList,companyId);

        return resultMap;
    }

    @ResponseBody
    @PostMapping("company/owndevice")
    public HashMap<String,String> ownDeviceSave(@RequestBody Map<String, String> param){
        HashMap<String,String> resultMap = adminService.addOwnDevice(param.get("deviceSerial"),Integer.parseInt(param.get("companyId")));
        return resultMap;
    }


}
