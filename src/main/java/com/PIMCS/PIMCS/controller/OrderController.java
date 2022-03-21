package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.OrderMailForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.AdminService;
import com.PIMCS.PIMCS.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("order")
public class OrderController {
    private final AdminService adminService;
    private final OrderService orderService;
    private final EmailUtilImpl emailUtilImpl;


    public OrderController(AdminService adminService, OrderService orderService, EmailUtilImpl emailUtilImpl) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.emailUtilImpl = emailUtilImpl;
    }


    @GetMapping("mat")
    public String orderMat(Model model){
        List<MatCategory> matCategoryList=adminService.findMatCategory();
        model.addAttribute("matCategoryList",matCategoryList);
        return "order/orderMat";
    }

    @PostMapping("mat")
    public String orderMat(MatOrder matOrder, @AuthenticationPrincipal SecUserCustomForm user, MatCategoryAndOrderForm matCategoryAndOrderForm){
        orderService.saveOrder(matOrder,user, matCategoryAndOrderForm);
        return "redirect:/order/mat";
    }

//    @RequestMapping(value = "/email/sendEmail",method = RequestMethod.POST)
//    public Map<String, Object> sendEmail(@RequestBody Map<String, Object> params){
//        log.info("email params={}", params);
//
//        return emailUtilImpl.sendEmail(
//                (String) params.get("userId")
//                , (String) params.get("subject")
//                , (String) params.get("body")
//        );
//    }

    @GetMapping(value = "/email")
    public String emailTest(Model model){

        OrderMailForm orderMailForm=orderService.selectOrderMailForm();
        orderMailForm.getMatOrder().getCreatedAt().toString().substring(0,11);
        model.addAttribute("orderMailFrame",orderMailForm.getOrderMailFrame());
        model.addAttribute("matOrder",orderMailForm.getMatOrder());
        model.addAttribute("matCategoryOrderList",orderMailForm.getMatCategoryOrderList());
        return "user/email";
    }

}
