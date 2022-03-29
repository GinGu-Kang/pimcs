package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InOutHistoryService {

    private final DynamoDBMapper dynamoDBMapper;
    private final MatRepository matRepository;
    @Autowired
    public InOutHistoryService(DynamoDBMapper dynamoDBMapper, MatRepository matRepository) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.matRepository = matRepository;
    }

    /**
     * 입출고내역 서비스
     */
    public DynamoResultPage inOutHistoryService(Company company, Pageable pageable){
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        return dynamoDBUtils.loadByCompany(company,pageable);
    }
    /**
     * 입출내역 검색 서비스
     */
    public DynamoResultPage inOutHistorySearchService(
        Company company,
        InOutHistorySearchForm inOutHistorySearchForm,
        Pageable pageable){

        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        return dynamoDBUtils.searchInOutHistory(company, inOutHistorySearchForm, pageable);
    }

    /**
     * 입출고 내열 시별그래프
     */
    public List<ResultGraph> inOutHistoryHourGraphService(Company company, MatGraphForm matGraphForm){
        //사용자 선택한 시작시간과 종료시간
        LocalDate startDate = matGraphForm.getStartDate();
        LocalDate endDate = matGraphForm.getEndDate();

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm.getSerialNumberList(), queryDate, endDate);

        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");



        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            resultGraph.setProductName(productNameList.get(i));
            String serialNumber = serialNumberList.get(i);
            LocalDateTime startLocalDateTime = LocalDateTime.of(
                    startDate,
                    LocalTime.of(00,00)
            );
            LocalDateTime endLocalDateTime = null;
            if(endDate.isEqual(LocalDate.now())){
                endLocalDateTime = LocalDateTime.of(
                        endDate,
                        LocalTime.now()
                );
            }else{
                endLocalDateTime = LocalDateTime.of(
                        endDate,
                        LocalTime.of(22,00)
                );
            }

            while(startLocalDateTime.isBefore(endLocalDateTime) || startLocalDateTime.isEqual(endLocalDateTime)){
                InOutHistory inOutHistoryDay = InOutHistory.findByLocalDateTime(inOutHistories,startLocalDateTime,serialNumber);
                //queryDate 보다 이전날짜의 채워넣을 마지막값 검색
                if(inOutHistoryDay == null) inOutHistoryDay = dynamoDBUtils.findLastInOutHistory(serialNumber, queryDate);

                //graph label과 data
                resultGraph.setLabels(startLocalDateTime.toString());
                resultGraph.setData(
                        ( inOutHistoryDay != null ) ? inOutHistoryDay.getUpdateCurrentInventory() : 0
                );


                startLocalDateTime = startLocalDateTime.plusHours(2);
            }
            resultGraphs.add(resultGraph);
        }
        return resultGraphs;
    }

    /**
     * 입출고 내역 일별그래프
     */
    public List<ResultGraph> inOutHistoryDayGraphService(Company company, MatGraphForm matGraphForm){
        //사용자 선택한 시작시간과 종료시간
        LocalDate startDate = matGraphForm.getStartDate();
        LocalDate endDate = matGraphForm.getEndDate();

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm.getSerialNumberList(), queryDate, endDate);

        System.out.println("===========");
        System.out.println(inOutHistories.size());
        System.out.println("===========");
        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            resultGraph.setProductName(productNameList.get(i));
            String serialNumber = serialNumberList.get(i);

            while(startDate.isBefore(endDate) || startDate.isEqual(endDate)){ //startDate <= endDate
                InOutHistory inOutHistoryDay = InOutHistory.findByLocalDate(inOutHistories,startDate,serialNumber);

                //queryDate 보다 이전날짜의 채워넣을 마지막값 검색
                if(inOutHistoryDay == null) inOutHistoryDay = dynamoDBUtils.findLastInOutHistory(serialNumber, queryDate);

                //graph label과 data
                resultGraph.setLabels(startDate.toString());
                resultGraph.setData(
                        ( inOutHistoryDay != null ) ? inOutHistoryDay.getUpdateCurrentInventory() : 0
                );

                startDate = startDate.plusDays(1); //시작시간에 하루 plus
            }
            resultGraphs.add(resultGraph);
        }

        return resultGraphs;
    }

    /**
     * 주별 입출고 그래프
     */
    public List<ResultGraph> inOutHistoryWeekGraphService(Company company, MatGraphForm matGraphForm){

        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");

        LocalDate startDate = LocalDate.of(matGraphForm.getStartDate().getYear(), matGraphForm.getStartDate().getMonth(), 7);
        LocalDate endDate = matGraphForm.getEndDate();

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm.getSerialNumberList(), queryDate, endDate);

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            resultGraph.setProductName(productNameList.get(i));
            String serialNumber = serialNumberList.get(i);

            while(startDate.isBefore(endDate) || startDate.isEqual(endDate)){
                InOutHistory inOutHistoryDay = InOutHistory.findByLocalDate(inOutHistories,startDate,serialNumber);
                //queryDate 보다 이전날짜의 채워넣을 마지막값 검색
                if(inOutHistoryDay == null) inOutHistoryDay = dynamoDBUtils.findLastInOutHistory(serialNumber, queryDate);

                //graph label과 data
                resultGraph.setLabels(startDate.toString());
                resultGraph.setData(
                        ( inOutHistoryDay != null ) ? inOutHistoryDay.getUpdateCurrentInventory() : 0
                );

                startDate = startDate.plusDays(7); //시작시간에 7일 plus

            }
            resultGraphs.add(resultGraph);
        }

        return resultGraphs;
    }

    /**
     * 월별 입출고 그래프
     */
    public List<ResultGraph> inOutHistoryMonthGraphService(Company company, MatGraphForm matGraphForm){

        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");

        LocalDate startDate = matGraphForm.getStartDate();
        LocalDate endDate = matGraphForm.getEndDate();
        startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.lengthOfMonth());
        endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.lengthOfMonth());

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm.getSerialNumberList(), queryDate, endDate);

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            resultGraph.setProductName(productNameList.get(i));
            String serialNumber = serialNumberList.get(i);
            while(startDate.isBefore(endDate) || startDate.isEqual(endDate)){ // startDate <= endDate

                InOutHistory inOutHistoryDay = InOutHistory.findByLocalDate(inOutHistories,startDate,serialNumber);
                //queryDate 보다 이전날짜의 채워넣을 마지막값 검색
                if(inOutHistoryDay == null) inOutHistoryDay = dynamoDBUtils.findLastInOutHistory(serialNumber, queryDate);

                //graph label과 data
                resultGraph.setLabels(startDate.getYear()+"년 "+startDate.getMonthValue()+"월");
                resultGraph.setData(
                        ( inOutHistoryDay != null ) ? inOutHistoryDay.getUpdateCurrentInventory() : 0
                );

                startDate = startDate.plusMonths(1);
                startDate = LocalDate.of(startDate.getYear(),startDate.getMonthValue(),startDate.lengthOfMonth());
            }
            resultGraphs.add(resultGraph);
        }

        return resultGraphs;
    }

}
