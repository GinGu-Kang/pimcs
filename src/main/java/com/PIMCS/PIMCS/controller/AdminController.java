package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/matcategory/add")
    public String matCategoryAddForm(){
        return "admin/addMatCategory";
    }


    @PostMapping("/matcategory/add")
    public String matCategoryAdd(MatCategory matCategory){
        adminService.addMatCategory(matCategory);
        return "redirect:/admin/matcategory/read";
    }


    @GetMapping("matcategory/read")
    public String matCategoryList(Model model){
        model.addAttribute(adminService.findMatCategory());
        return "admin/categoryManagement";
    }

    @PostMapping("matcategory/modify")
    @ResponseBody
    public boolean matCategoryModify(MatCategory matCategory){

        adminService.modifyMatCategory(matCategory);
        return true;
    }

    @ResponseBody
    @PostMapping("matcategory/remove")
    public boolean matCategoryRemove(Integer DBId){
        adminService.removeMatCategory(DBId);
        return true;
    }
    /*회사 관리*/
    @GetMapping("company/list")
    public String companyList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                               Model model){
        Page<Company> questionPage=adminService.findAllCompany(pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());

        return "admin/companyList";
    }
    //필터링 조회
    @GetMapping("company/search")
    public String companySearch(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,String keyword,String selectOption,Model model){
        System.out.println(keyword);
        Page<Question> questionPage=adminService.filterQuestion(keyword,selectOption,pageable);
        model.addAttribute("questionPage",questionPage);
        model.addAttribute("questionList",questionPage.getContent());
        return "admin/adminQnaList";
    }
    /*회사 상세 보기*/
    @GetMapping("company/view")
    public String companyDetail(Model model,Integer questionId){
        Question question = adminService.findQuestion(questionId);
        model.addAttribute(question);
        return "admin/adminQnaView";
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

    @GetMapping("order/view")
    public String detailOrder(Model model,Integer matId){
        MatOrder matOrder = adminService.findOrder(matId);
        User orderer=matOrder.getUser();
        Company orderCompany=matOrder.getCompany();
        model.addAttribute(matOrder);
        model.addAttribute(orderer);
        model.addAttribute(orderCompany);
        return "admin/orderView";
    }





}
