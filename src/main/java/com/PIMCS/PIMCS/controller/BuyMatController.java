package com.PIMCS.PIMCS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("buy")
public class BuyMatController {
    @RequestMapping("mat")
    public String buyMatform(){


        return "buy/buyMat";
    }


}
