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
        return adminService.createMatCategoryService(matCategory);
    }

    @GetMapping("/matcategories")
    public String findMatCategoryList(Model model){
        model.addAttribute(adminService.findMatCategoryListService());
        return "admin/findMatCategoryList.html";
    }

    @ResponseBody
    @PutMapping("/matcategories")
    public boolean updateMatCategory(@RequestBody MatCategory matCategory){
        adminService.updateMatCategoryService(matCategory);
        return true;
    }

    @ResponseBody
    @DeleteMapping("/matcategories")
    public boolean deleteMatCategory(@RequestBody MatCategory matCategory){
        adminService.deleteMatCategoryService(matCategory.getId());
        return true;
    }


    //회사 조회
    @GetMapping("companies")
    public String findCompanyList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                @RequestParam(value = "selectOption", defaultValue = "") String selectOption,
                                Model model){
        Page<Company> companyPage=adminService.findCompanyListService(keyword,selectOption,pageable);
        model.addAttribute("companyPage",companyPage);
        model.addAttribute("companyList",companyPage.getContent());

        return "admin/findCompanyList";
    }

    /*회사 상세 보기*/
    @GetMapping("companies/{companyId}")
    public String detailsCompany(Model model,@PathVariable Integer companyId){
        Company company = adminService.detailsCompanyService(companyId);
        model.addAttribute("company",company);

        return "admin/companyView";
    }


    /*매핑된 기기 삭제*/
    @ResponseBody
    @DeleteMapping (value = "owndevices")
    public Boolean deleteOwnDeviceList(@RequestParam(value = "ownDeviceIdList[]") List<Integer> ownDeviceIdList) {
        adminService.deleteOwnDeviceListService(ownDeviceIdList);
        return true;
    }




    //필터링 조회
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @GetMapping("qnas")
    public String findAdminQuestionList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      @RequestParam(value = "selectOption", defaultValue = "") String selectOption,
                                      Model model){
        Page<Question> questionPage=adminService.findAdminQuestionListService(keyword,selectOption,pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());
        return "admin/findQnaList";
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
        return "redirect:/admin/qnas";
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
    public String adminOrderQuestion(@PageableDefault(page = 0,
                                     size=10, sort="createdAt",
                                     direction = Sort.Direction.DESC)
                                     Pageable pageable,
                                     String keyword,
                                     Integer totalPriceStart,
                                     Integer totalPriceEnd,
                                     Model model){
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
