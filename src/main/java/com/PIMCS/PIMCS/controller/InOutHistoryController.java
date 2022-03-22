package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.service.InOutHistoryService;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InOutHistoryController {
    private final InOutHistoryService inOutHistoryService;

    @Autowired
    public InOutHistoryController(InOutHistoryService inOutHistoryService) {
        this.inOutHistoryService = inOutHistoryService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }

    /**
     * 입출고 내역
     */
    @GetMapping("/inout/history")
    public String inOutHistory(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(page=1,size=10) Pageable pageable,
            Model model){
        DynamoResultPage dynamoResultPage = inOutHistoryService.inOutHistoryService(secUserCustomForm.getCompany(), pageable);
        model.addAttribute("dynamoResultPage",dynamoResultPage);
        return "inOutHistory/inOutHistory";
    }

    /**
     * 입출고 내역 그래프
     */
    @PostMapping("/inout/history/graph")
    public String inOutHistoryGraph(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatSerialNumberForm serialNumberForm, Model model) throws JsonProcessingException {
//        JSONObject object = new JSONObject();
//        object.put("items", serialNumberForm.getSerialNumberList());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(serialNumberForm);

        model.addAttribute("matSerialNumberList",jsonString);
        return "inOutHistory/inOutHistoryGraph";
    }

    @PostMapping("/inout/history/graph/day")
    @ResponseBody
    public String inOutHistoryGraphDay(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatSerialNumberForm serialNumberForm){
        return "inOutHistory/inOutHistoryGraph";
    }
}
