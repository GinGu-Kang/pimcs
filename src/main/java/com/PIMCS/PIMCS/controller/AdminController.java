package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.service.AdminOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminOrderService adminOrderService;

    public AdminController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping("/matcategory/add")
    public String matCategoryAddForm(){
        return "admin/addMatCategory";
    }


    @PostMapping("/matcategory/add")
    public String matCategoryAdd(MatCategory matCategory){
        System.out.println("여기안와 ?");
        adminOrderService.addMatCategory(matCategory);
        return "admin/addMatCategory";
    }


    @GetMapping("matcategory/read")
    public String matCategoryList(Model model){
        model.addAttribute(adminOrderService.findMatCategory());
        return "admin/buyMat";
    }

    @GetMapping("matcategory/modify")
    public String matCategoryModify(MatCategory matCategory){
        adminOrderService.modifyMatCategory(matCategory);
        return "buy/buyMat";
    }
    @PostMapping("matcategory/remove")
    public String matCategoryRemove(String matCategoryName){
        adminOrderService.removeMatCategory(matCategoryName);
        return "buy/buyMat";
    }




}
