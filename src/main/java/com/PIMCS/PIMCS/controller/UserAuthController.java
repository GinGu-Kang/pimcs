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
    @GetMapping("/signUp")
    private String signUpForm(){
        return "user/auth/signUp.html";
    }


    //회원가입 이메일 인증
    @PostMapping("/signUp")
    private String signUp(User user){
        userAuthService.signUp(user);
        return "redirect:/auth/login";
    }
    @GetMapping("signUp/verify")
    public String signUpVerify(@RequestParam("verifyKey") String verifyKey){
        log.info("오긴오니");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        userAuthService.signUpVerify(verifyKey);
        return "redirect:/auth/login";
    }
    //회원가입 선택
    @GetMapping("choice/signUp")
    private String signUpChoice(){
        return "user/auth/signUpChoice.html";
    }


    //회원정보수정
    @GetMapping("update")
    private String editUserInfoForm(@RequestParam("email") String email, Model model){
        model.addAttribute("user",userAuthService.findUser(email).get());
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

    //유저 정보
    @GetMapping("/user/info")
    private String userInfo(Model model,@AuthenticationPrincipal SecUserCustomForm user){
        model.addAttribute("user",userAuthService.userDetail(user.getUsername()));
        return "user/auth/userInfo";
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
        return "/user/auth/userInfo";
    }

    @GetMapping("/pwd/change")
    public String pwdChangeForm(){
        return "/user/auth/pwdChange";
    }





    @GetMapping("/pwd/find")
    public String pwdFindForm(){
        return "/user/auth/pwdFind";
    }

    @ResponseBody
    @PostMapping("/pwd/find")
    public Boolean pwdFind(String email){
        Boolean isEmail=userAuthService.pwdFind(email);
        return isEmail;
    }

    @GetMapping("/pwd/find/verify")
    public String pwdFindVerify(@RequestParam("verifyKey") String verifyKey,Model model){
        model.addAttribute("verifyKey",verifyKey);
        return "/user/auth/pwdFindVerify";
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
        return "/user/auth/pwdChange";
    }






}
