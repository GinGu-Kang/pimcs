package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatOrder;
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
        System.out.println(matCategoryList.get(0).getMatCategoryName());
        model.addAttribute("matCategoryList",matCategoryList);

        return "order/orderMat";
    }
    @PostMapping("mat")
    public String orderMat(MatOrder matOrder,String categoryCount,String categoryName,@AuthenticationPrincipal SecUserCustomForm user){
        String[] categoryCountList=categoryCount.split(",");
        String[] categoryNameList=categoryName.split(",");
        orderService.saveOrder(matOrder,categoryCount,categoryName,user);


        System.out.println(categoryCountList);
        System.out.println(user.getCompany().getCompanyCode()+user.getUsername());
        System.out.println(categoryName);
        System.out.println(categoryCount);
        System.out.println(matOrder.getDeliveryAddress()+matOrder.getDetailAddress());
        return "redirect:/order/mat";
    }
}
