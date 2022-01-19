package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.annotation.CheckMatManageMentRole;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * user_author
 * @author: GinGu-Kang
 */

@Controller
@RequestMapping("/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("signUp")
    private String signUpForm(){
        return "user/auth/signUp.html";
    }


    @PostMapping("signUp")
    private String signUp(User user){

        userAuthService.signUp(user);
        return "user/auth/signUp.html";
    }
    @GetMapping("login")
    private String loginForm(){

        return "user/auth/login.html";
    }
    @PostMapping("login")
    private String login(){

        return "member/auth/login.html";
    }



}
