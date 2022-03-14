package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.service.InOutHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InOutHistoryController {
    private final InOutHistoryService inOutHistoryService;

    @Autowired
    public InOutHistoryController(InOutHistoryService inOutHistoryService) {
        this.inOutHistoryService = inOutHistoryService;
    }

    @GetMapping("/inout/history")
    public String inOutHistory(){

        return "inOutHistory/inOutHistory";
    }

}
