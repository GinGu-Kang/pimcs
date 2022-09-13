package com.PIMCS.PIMCS.controller;



import com.PIMCS.PIMCS.domain.Redis.FindPwdVO;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import com.PIMCS.PIMCS.service.OrderService;
import com.PIMCS.PIMCS.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final CompanyManagementService companyManagementService;


    @Autowired
    public UserAuthController(UserAuthService userAuthService, CompanyManagementService companyManagementService) {
        this.userAuthService = userAuthService;
        this.companyManagementService = companyManagementService;
    }

    //회원 가입폼
    @GetMapping("/user/create")
    private String createUserForm(){
        return "user/signUp.html";
    }


    //회원가입 이메일 인증
    @PostMapping("/user")
    private String createUser(User user){
        userAuthService.createUserService(user);
        return "redirect:/auth/login";
    }

    @GetMapping("user/verify")
    public String createUserVerify(@RequestParam("verifyKey") String verifyKey,Model model){
        model.addAttribute("isWaitUser",userAuthService.createUserVerifyService(verifyKey));
        return "user/signUpSuccess.html";
    }


    //회원가입 선택
    @GetMapping("user/choice")
    private String signUpChoice(){
        return "user/signUpChoice.html";
    }

    //로그인
    @GetMapping("/login")
    private String loginForm(){
        return "user/login";
    }


    @PostMapping("/check-email")
    @ResponseBody
    public boolean emailCheck(@RequestParam("email") String email){
        System.out.println(email);
        boolean isEmail = userAuthService.emailCheckService(email);
        return isEmail;
    }

    @PostMapping("/check-company")
    @ResponseBody
    public boolean companyCheck(@RequestParam("company") String companyCode){
        boolean isCompany = userAuthService.companyCheckService(companyCode);
        return isCompany;
    }



    //회원정보수정
    @GetMapping("update")
    private String updateUserForm(@RequestParam("email") String email, Model model){
        model.addAttribute("user",userAuthService.updateUserFormService(email).get());
        return "user/userUpdate.html";
    }




    //유저 정보
    @GetMapping("/user/info")
    private String userInfo(Model model,@AuthenticationPrincipal SecUserCustomForm user){
        model.addAttribute("user",userAuthService.userDetail(user.getUsername()));
        return "user/userInfo";
    }

    //개인정보 변경
    @PostMapping("/user/info/modify")
    public String companyInfoModify(Model model,@AuthenticationPrincipal SecUserCustomForm currentUser,User userForm){
        User user =  userAuthService.findUser(currentUser.getUsername()).get();
        user.setName(userForm.getName());
        user.setPhone(userForm.getPhone());
        user.setDepartment(userForm.getDepartment());
        userAuthService.userUpdate(user);
        model.addAttribute(user);
        return "/user/userInfo";
    }

    @GetMapping("/pwd/change")
    public String pwdChangeForm(){
        return "/user/pwdChange";
    }


    @GetMapping("/pwd")
    public String pwdFindForm(){
        return "/user/pwdFind";
    }

    @ResponseBody
    @PostMapping("/pwd")
    public Boolean pwdFind(String email){
        Boolean isEmail=userAuthService.pwdFind(email);
        return isEmail;
    }

    @GetMapping("/pwd/find/verify")
    public String pwdFindVerify(@RequestParam("verifyKey") String verifyKey,Model model){
        model.addAttribute("verifyKey",verifyKey);
        return "/user/pwdFindVerify";
    }

    //인증키 만료 처리
    @ResponseBody
    @PostMapping("/pwd/verify/change")
    public boolean pwdVerifyChange(String verifyKey,String password){
        return userAuthService.pwdFindVerify(verifyKey,password);
    }

    @PostMapping("/pwd/change")
    public String pwdChange(@AuthenticationPrincipal SecUserCustomForm currentUser,String password){
        userAuthService.userPwdUpdate(currentUser.getUsername(),password);
        return "/user/pwdChange";
    }
}
