package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.service.InOutHistoryService;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            @PageableDefault(size=10) Pageable pageable,
            Model model){

        DynamoResultPage dynamoResultPage = inOutHistoryService.inOutHistoryService(secUserCustomForm.getCompany(), pageable);
        model.addAttribute("dynamoResultPage",dynamoResultPage);
        return "inOutHistory/inOutHistory";
    }

    /**
     * 입출고내역 검색
     */
    @GetMapping("/inout/history/search")
    public String inOutHistorySearch(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            InOutHistorySearchForm inOutHistorySearchForm,
            Model model){
        //검색폼 전체 null이면 입출고내역 페이지로 redirect
        if(inOutHistorySearchForm.getQuery() == null &&
            inOutHistorySearchForm.getStartDate() == null &&
            inOutHistorySearchForm.getEndDate() == null){
            return "redirect:/inout/history";
        }

        DynamoResultPage dynamoResultPage = inOutHistoryService.inOutHistorySearchService(secUserCustomForm.getCompany(), inOutHistorySearchForm, pageable);
        model.addAttribute("dynamoResultPage",dynamoResultPage);
        model.addAttribute("inOutHistorySearchForm",inOutHistorySearchForm);
        return "inOutHistory/inOutHistory";
    }

    /**
     * 입출고 내역 그래프
     */

    @GetMapping("/inout/history/graph")
    public String inOutHistoryGet(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatGraphForm matGraphForm, Model model) throws JsonProcessingException {
        System.out.println(matGraphForm.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(matGraphForm);
        model.addAttribute("matGraphForm",jsonString);
        return "inOutHistory/inOutHistoryGraph";
    }

    @PostMapping("/inout/history/graph")
    public String inOutHistoryGraphPost(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatGraphForm matGraphForm, Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // 시작날짜, 종료날짜, (HOUR,DAY,WEEK,MONTH) 설정
        LocalDate endData = LocalDate.now();
        if(matGraphForm.getTimeDimension() == null) matGraphForm.setTimeDimension("DAY");
        if(matGraphForm.getStartDate() == null){
            matGraphForm.setStartDate(LocalDate.of(endData.getYear(),endData.getMonth(),1));
        }

        if(matGraphForm.getEndDate() == null){
            matGraphForm.setEndDate(endData);
        }
        String jsonString = objectMapper.writeValueAsString(matGraphForm);
        model.addAttribute("matGraphForm",jsonString);
        return "inOutHistory/inOutHistoryGraph";
    }

    /**
     * 시간별 그래프 데이터로드
     */
    @PostMapping("/inout/history/graph/hour")
    @ResponseBody
    public List<ResultGraph> inOutHistoryGraphHour(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,MatGraphForm matGraphForm){
        if(matGraphForm.getStartDate().isAfter(matGraphForm.getEndDate())){
            throw new IllegalStateException("start time is greater than end time");
        }

        return inOutHistoryService.inOutHistoryHourGraphService(secUserCustomForm.getCompany(),matGraphForm);
    }
    /**
     * 일별 그래프 데이터로드
     */
    @PostMapping("/inout/history/graph/day")
    @ResponseBody
    public List<ResultGraph> inOutHistoryGraphDay(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm,MatGraphForm matGraphForm){
        if(matGraphForm.getStartDate().isAfter(matGraphForm.getEndDate())) {
            throw new IllegalStateException("start time is greater than end time");
        }

        return inOutHistoryService.inOutHistoryDayGraphService(secUserCustomForm.getCompany(), matGraphForm);
    }

    /**
     * 주별 그래프 데이터 로드
     */
    @PostMapping("/inout/history/graph/week")
    @ResponseBody
    public List<ResultGraph> inOutHistoryGraphWeek(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatGraphForm matGraphForm){
        if(matGraphForm.getStartDate().isAfter(matGraphForm.getEndDate())) {
            throw new IllegalStateException("start time is greater than end time");
        }
        return inOutHistoryService.inOutHistoryWeekGraphService(secUserCustomForm.getCompany(), matGraphForm);
    }

    /**
     * 월별 그래프 데이터 로드
     */
    @PostMapping("/inout/history/graph/month")
    @ResponseBody
    public List<ResultGraph> inOutHistoryGraphMonth(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatGraphForm matGraphForm){
        if(matGraphForm.getStartDate().isAfter(matGraphForm.getEndDate())) {
            throw new IllegalStateException("start time is greater than end time");
        }
        return inOutHistoryService.inOutHistoryMonthGraphService(secUserCustomForm.getCompany(), matGraphForm);
    }

    /**
     * 입출고 내역 csv 다운로드
     */
    @GetMapping("/download/inout/history/csv")
    public void downloadInOutHistoryCsv(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            InOutHistorySearchForm inOutHistorySearchForm,
            HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "InOutHistory-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        inOutHistoryService.downloadInOutHistoryCsvService(secUserCustomForm.getCompany(), inOutHistorySearchForm, response.getWriter());
    }

    /**
     * 그래프 데이터 csv다운로드
     */
    @PostMapping("/download/inout/history/graph/csv")
    public void downloadInOutHistoryGraphCsv(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            MatGraphForm matGraphForm,
            HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "graph-data-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        inOutHistoryService.downloadInOutHistoryGraphCsvService(secUserCustomForm.getCompany(), matGraphForm, response.getWriter());
    }
}
