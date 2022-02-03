package com.PIMCS.PIMCS.controller;



import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.LoginForm;
import com.PIMCS.PIMCS.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    //회원 가입
    @GetMapping("/signUp")
    private String signUpForm(){
        return "user/auth/signUp.html";
    }
    //회사등록하며 대표가입시에는 CompanyManagementController companyRegistration참조

    @PostMapping("/signUp")
    private String signUp(User user){
        System.out.println("이곳오는거니 ??");
        userAuthService.signUp(user);
        return "user/auth/login.html";
    }

    //회원정보수정
    @GetMapping("update")
    private String editPersonalInfoForm(@RequestParam("email") String email, Model model){
        model.addAttribute("user",userAuthService.findUser(email).get());
        return "user/auth/userUpdate.html";
    }
    @PostMapping("update")
    private String editPersonalInfo(User user){
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
    public boolean idCheck(@RequestParam("id") String id){
        System.out.println("userIdCheck 진입");
        System.out.println("전달받은 id:"+id);
        boolean cnt = userAuthService.idCheck(id);
        System.out.println("확인 결과:"+cnt);
        return cnt;
    }




}
