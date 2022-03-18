package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.AdminService;
import com.PIMCS.PIMCS.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("order")
public class OrderController {
    private final AdminService adminService;
    private final OrderService orderService;

    public OrderController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
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

//    public String orderList(){
//
//
//        return
//    }
}
