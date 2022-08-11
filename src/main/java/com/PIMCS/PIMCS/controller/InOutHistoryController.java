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
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/mats/history")
    public String findInOutHistory(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            InOutHistorySearchForm searchForm,
            Model model){

        DynamoResultPage dynamoResultPage;
        if(searchForm.isExist()){
            dynamoResultPage = inOutHistoryService.findInOutHistoryByAllService(secUserCustomForm.getCompany(), searchForm, pageable);
        }else{
            dynamoResultPage = inOutHistoryService.findInOutHistoryService(secUserCustomForm.getCompany(), pageable);
        }

        model.addAttribute("dynamoResultPage",dynamoResultPage);
        model.addAttribute("inOutHistorySearchForm",searchForm);
        return "inOutHistory/inOutHistory";
    }



    /**
     * 그래프 페이지 새로고침할때 사용
     */

    @GetMapping("/mats/history-graph")
    public String inOutHistoryGet(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, MatGraphForm matGraphForm, Model model) throws JsonProcessingException {
        System.out.println(matGraphForm.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(matGraphForm);
        model.addAttribute("matGraphForm",jsonString);
        return "inOutHistory/inOutHistoryGraph";
    }

    /**
     *  재고목록에서 체크한 데이터를 post로 받아서 그래프 html에 전달
     */
    @PostMapping("/mats/history-graph")
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
     * hour, day, week, month별 데이터 추출해서 응답
     */
    @PostMapping("/mats/history-graph/{timeDimension}")
    @ResponseBody
    public List<ResultGraph> matsHistoryGraphDay(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            MatGraphForm matGraphForm,
            @PathVariable String timeDimension){


        if(matGraphForm.getStartDate().isAfter(matGraphForm.getEndDate())) {
            throw new IllegalStateException("start time is greater than end time");
        }

        if(timeDimension.equals("hour")){
           return inOutHistoryService.matsHistoryGraphHourService(secUserCustomForm.getCompany(),matGraphForm);
        }else if(timeDimension.equals("day")){
            return inOutHistoryService.matsHistoryGraphDayService(secUserCustomForm.getCompany(), matGraphForm);
        }else if(timeDimension.equals("week")){
            return inOutHistoryService.matsHistoryGraphWeekService(secUserCustomForm.getCompany(), matGraphForm);
        }else if(timeDimension.equals("month")){
            return inOutHistoryService.matsHistoryGraphMonthService(secUserCustomForm.getCompany(), matGraphForm);
        }

        return null;
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
    @PostMapping("/mats/csv/history-graph")
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
