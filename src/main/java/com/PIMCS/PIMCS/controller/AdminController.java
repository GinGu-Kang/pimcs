package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.service.AdminOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        adminOrderService.addMatCategory(matCategory);
        return "redirect:/admin/matcategory/read";
    }


    @GetMapping("matcategory/read")
    public String matCategoryList(Model model){
        model.addAttribute(adminOrderService.findMatCategory());
        return "admin/categoryManagement";
    }

    @PostMapping("matcategory/modify")
    @ResponseBody
    public boolean matCategoryModify(MatCategory matCategory){

        adminOrderService.modifyMatCategory(matCategory);
        return true;
    }

    @ResponseBody
    @PostMapping("matcategory/remove")
    public boolean matCategoryRemove(Integer DBId){
        adminOrderService.removeMatCategory(DBId);
        return true;
    }
}
