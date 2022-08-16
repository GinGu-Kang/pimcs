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

    /*관리자 qna*/
    @GetMapping("qnas")
    public String findAdminQnaList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      @RequestParam(value = "selectOption", defaultValue = "") String selectOption,
                                      Model model){
        Page<Question> questionPage=adminService.findAdminQnaListService(keyword,selectOption,pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());
        return "admin/findAdminQnaList";
    }

    @GetMapping("qnas/{questionId}")
    public String detailsAdminQna(Model model,@PathVariable(value = "questionId") Integer questionId){
        Question question = adminService.detailsAdminQnaService(questionId);
        model.addAttribute(question);
        return "admin/detailsAdminQna";
    }


    @ResponseBody
    @PostMapping("qnas/answer")
    public Answer createAnswer(@RequestBody Answer answer){
        return adminService.createAnswerService(answer);
    }


    @GetMapping("order-mail-frame/create")
    public String createOrderMailFrameForm(Model model){
        model.addAttribute("OrderMailFrame",adminService.createOrderMailFrameFormService());
        return "admin/createOrderMail";
    }


    @PostMapping("order-mail-frame")
    public String createOrderMailFrame(OrderMailFrame orderMailFrame){
        adminService.createOrderMailFrameService(orderMailFrame);
        return "redirect:/admin/order-mail-frame/create";
    }

    /*
     *주문 목록
     */
    @GetMapping("/orders")
    public String findOrderList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                @RequestParam(value = "totalPriceStart", defaultValue = "0") Integer totalPriceStart,
                                @RequestParam(value = "totalPriceEnd", defaultValue = "999999999") Integer totalPriceEnd,
                                Model model){
        Page<MatOrder> matOrderPage=adminService.findOrderListService(keyword,totalPriceStart,totalPriceEnd,pageable);
        model.addAttribute("matOrderPage",matOrderPage);
        model.addAttribute("matOrderList",matOrderPage.getContent());
        return "admin/findOrderList";
    }


    @GetMapping("orders/{orderId}")
    public String detailsOrder(Model model,@PathVariable Integer orderId){
        MatOrder matOrder = adminService.detailsOrderService(orderId);
        Company orderCompany=matOrder.getCompany();
        model.addAttribute(matOrder);
        model.addAttribute(orderCompany);
        return "admin/orderView";
    }

    @ResponseBody
    @PutMapping("orders/{orderId}/deposit")
    public MatOrder updateOrderDeposit(@PathVariable Integer orderId,Boolean isDeposit){
        return adminService.updateOrderDepositService(orderId,isDeposit);
    }

    @ResponseBody
    @PostMapping("order/{orderId}/owndevice")
    public HashMap<String,String> createOwnDeviceAndSendHistory(
            @PathVariable Integer orderId,
            @RequestParam(value="deviceSerialList[]") List<String> deviceSerialList ,
            Integer companyId){

        HashMap<String,String> resultMap = adminService.createOwnDeviceAndSendHistoryService(orderId,deviceSerialList,companyId);
        return resultMap;
    }

    @ResponseBody
    @PostMapping("company/owndevice")
    public HashMap<String,String> createOwnDevice(@RequestBody Map<String, String> param){
        HashMap<String,String> resultMap = adminService.createOwnDeviceService(param.get("deviceSerial"),Integer.parseInt(param.get("companyId")));
        return resultMap;
    }


}
