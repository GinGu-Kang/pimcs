package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.service.AdminService;
import com.PIMCS.PIMCS.service.OrderService;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
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
        List<MatCategory> matCategoryList=orderService.findMatCategoryListService();
        model.addAttribute("matCategoryList",matCategoryList);
        return "order/orderMat";
    }

    @PostMapping("mat")
    public String orderMat(MatOrder matOrder, @AuthenticationPrincipal SecUserCustomForm user, MatCategoryAndOrderForm matCategoryAndOrderForm){
        orderService.saveOrder(matOrder,user, matCategoryAndOrderForm);
        return "redirect:/order/mat";
    }

    @GetMapping("history")
    public String ordersHistory(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                               @PageableDefault(size=10) Pageable pageable,
                               InOutHistorySearchForm orderSearchForm,
                               Model model){

        DynamoResultPage dynamoResultPage;
        if(orderSearchForm.isExist()){
            dynamoResultPage = orderService.findOrderHistoryByAllService(secUserCustomForm.getCompany(), orderSearchForm, pageable);
        }else{
            dynamoResultPage = orderService.orderHistoryService(secUserCustomForm.getCompany(), pageable);
        }


        model.addAttribute("dynamoResultPage", dynamoResultPage);
        model.addAttribute("searchForm", orderSearchForm);
        return "order/orderHistory";
    }


    /**
     *  발주내역 csv 다운로드
     */
    @GetMapping("csv/history")
    public void downloadOrderHistoryCsv(  @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
                                          InOutHistorySearchForm searchForm,
                                          HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "OrderHistory-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);

        orderService.downloadOrderHistoryCsvService(secUserCustomForm.getCompany(), searchForm, response.getWriter());
    }
}
