package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.domain.Answer;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.Question;
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

    @GetMapping("qna/list")
    public String watingQnaList(@PageableDefault(page = 0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
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

}
