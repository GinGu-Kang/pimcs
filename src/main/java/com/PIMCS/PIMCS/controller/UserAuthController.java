package com.PIMCS.PIMCS.controller;



import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.service.CompanyManagementService;
import com.PIMCS.PIMCS.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * user_author
 * @author: GinGu-Kang
 *     구현 page
 *     1.회원가입: singUp
 *     2.로그인: login
 *     3.아이디찾기: UserEmailSearch
 *     4.비밀번호찾기: UserPasswordReset
 *     5.개인정보수정: UserInfoModify
 *     6.이메일 인증: EmailCertification
 */

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

    //회원 가입
    @GetMapping("/signUp")
    private String signUpForm(){
        return "user/auth/signUp.html";
    }


    @PostMapping("/signUp")
    private String signUp(User user){
        userAuthService.signUp(user);
        return "user/auth/login.html";
    }

    //회원정보수정
    @GetMapping("update")
    private String editUserInfoForm(@RequestParam("email") String email, Model model){
        model.addAttribute("user",userAuthService.findUser(email).get());
        return "user/auth/userUpdate.html";
    }

    @PostMapping("update")
    private String editUserInfo(User user){
        userAuthService.userUpdate(user);
        return "user/auth/userUpdate.html";
    }

    //로그인
    @GetMapping("/login")
    private String loginForm(){
        return "user/auth/login";
    }


    @PostMapping("/idCheck")
    @ResponseBody
    public boolean idCheck(@RequestParam("email") String email){
        boolean isEmail = userAuthService.emailCheck(email);
        return isEmail;
    }

    @PostMapping("/companyCheck")
    @ResponseBody
    public boolean companyCheck(@RequestParam("company") String companyCode){
        boolean isCompany = userAuthService.companyCheck(companyCode);
        return isCompany;
    }




}
