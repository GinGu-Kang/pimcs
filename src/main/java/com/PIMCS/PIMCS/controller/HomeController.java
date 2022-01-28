package com.PIMCS.PIMCS.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("")
    public String Home(){
        return "/home";

    }
    @GetMapping("hello")
    public String Hello(){
        return "/hello";
    }
    @GetMapping("/noneRole")
    public String noneRole(){
        return "/noneRole";
    }
}
