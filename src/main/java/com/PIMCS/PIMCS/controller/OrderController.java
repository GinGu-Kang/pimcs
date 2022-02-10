package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderList;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.AdminOrderService;
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
    private final AdminOrderService adminOrderService;
    private final OrderService orderService;

    public OrderController(AdminOrderService adminOrderService, OrderService orderService) {
        this.adminOrderService = adminOrderService;
        this.orderService = orderService;
    }


    @GetMapping("mat")
    public String orderMat(Model model){
        List<MatCategory> matCategoryList=adminOrderService.findMatCategory();
        model.addAttribute("matCategoryList",matCategoryList);
        return "order/orderMat";
    }
    @PostMapping("mat")
    public String orderMat(MatOrder matOrder,MatCategoryAndOrderList matCategoryAndOrderList,@AuthenticationPrincipal SecUserCustomForm user){
        orderService.saveOrder(matOrder,user,matCategoryAndOrderList.getMatCategoryOrderList(),matCategoryAndOrderList.getMatCategoryIdList());
        return "redirect:/order/mat";
    }

//    @PostMapping("test")
//    public String testOrder(MatCategoryAndOrderList matCategoryAndOrderList){
//        for (MatCategoryOrder mco: matCategoryAndOrderList.getMatCategoryOrderList()
//             ) {
//            System.out.println(mco.getOrderCnt());
//        }
//        return "redirect:/order/mat";
//    }
}
