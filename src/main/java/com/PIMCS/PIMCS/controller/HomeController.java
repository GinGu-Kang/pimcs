package com.PIMCS.PIMCS.controller;


import com.PIMCS.PIMCS.form.SecUserCustomForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public String Home(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        model.addAttribute("user",user);
        return "/home";
    }
    @GetMapping("hello")
    public String Hello(Model model, @AuthenticationPrincipal SecUserCustomForm user){
        model.addAttribute("user",user);
        return "/hello";
    }
    @GetMapping("/noneRole")
    public String noneRole(){
        return "/noneRole";
    }
}
