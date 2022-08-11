package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class InOutHistoryService {

    private final DynamoDBMapper dynamoDBMapper;
    private final MatRepository matRepository;
    private final DynamoQuery dynamoQuery;

    @Autowired
    public InOutHistoryService(DynamoDBMapper dynamoDBMapper, MatRepository matRepository, DynamoQuery dynamoQuery) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.matRepository = matRepository;
        this.dynamoQuery = dynamoQuery;
    }

    /**
     * 입출고내역 서비스
     */
    public DynamoResultPage findInOutHistoryService(Company company, Pageable pageable){
        DynamoDBQueryExpression<InOutHistory> queryExpression = InOutHistory.queryExpression(company, false);
        DynamoDBQueryExpression<InOutHistory> countQueryExpression = InOutHistory.queryExpression(company, true);
        return dynamoQuery.exePageQuery(InOutHistory.class, queryExpression, countQueryExpression, pageable);
    }
    /**
     * 입출내역 검색 서비스
     */
    public DynamoResultPage findInOutHistoryByAllService(
        Company company,
        InOutHistorySearchForm searchForm,
        Pageable pageable){
        DynamoDBQueryExpression<InOutHistory> queryExpression = InOutHistory.searchQueryExpression(company, searchForm);
        DynamoDBQueryExpression<InOutHistory> countQueryExpression = InOutHistory.searchQueryExpression(company, searchForm);
        return dynamoQuery.exePageQuery(InOutHistory.class, queryExpression, countQueryExpression, pageable);
    }

    /**
     * 입출고 내열 시별그래프
     */
    public List<ResultGraph> matsHistoryGraphHourService(Company company, MatGraphForm matGraphForm){
        //사용자 선택한 시작시간과 종료시간
        LocalDate startDate = matGraphForm.getStartDate();
        LocalDate endDate = matGraphForm.getEndDate();

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm, queryDate, endDate);

        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");



        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            String serialNumber = serialNumberList.get(i);
            resultGraph.setTitle(serialNumber);
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
    public List<ResultGraph> matsHistoryGraphDayService(Company company, MatGraphForm matGraphForm){
        //사용자 선택한 시작시간과 종료시간
        LocalDate startDate = matGraphForm.getStartDate();
        LocalDate endDate = matGraphForm.getEndDate();

        //데이터 miss를 줄이기위해 startMonth - 1달
        //dynamodb 쿼리 실행
        LocalDate queryDate = startDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm, queryDate, endDate);

        List<ResultGraph> resultGraphs = new ArrayList<>(); //최종 return lsit
        List<String> serialNumberList = matGraphForm.getSerialNumberList();
        List<String> productNameList = matGraphForm.getProductNameList();

        if(serialNumberList.size() != productNameList.size()) //상품갯수와 시리얼번호 갯수가 다를때 예외 발생시키기
            throw new IllegalStateException("serial number and product name differ in number");

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            String serialNumber = serialNumberList.get(i);
            resultGraph.setTitle(serialNumber);
            //startDate, endDate 초기
            startDate = matGraphForm.getStartDate();
            endDate = matGraphForm.getEndDate();

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
    public List<ResultGraph> matsHistoryGraphWeekService(Company company, MatGraphForm matGraphForm){

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
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm, queryDate, endDate);

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();

            String serialNumber = serialNumberList.get(i);
            resultGraph.setTitle(serialNumber);
            //startDate, endDate초기화
            startDate = LocalDate.of(matGraphForm.getStartDate().getYear(), matGraphForm.getStartDate().getMonth(), 7);
            endDate = matGraphForm.getEndDate();
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
    public List<ResultGraph> matsHistoryGraphMonthService(Company company, MatGraphForm matGraphForm){

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
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,matGraphForm, queryDate, endDate);

        for(int i=0; i < serialNumberList.size(); i++){
            ResultGraph resultGraph = new ResultGraph();
            String serialNumber = serialNumberList.get(i);
            resultGraph.setTitle(serialNumber);
            //startDate, endDate 초기화
            startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.lengthOfMonth());
            endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.lengthOfMonth());

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

    /**
     * 입출고 내역 csv 다운로드 서비스
     */
    public void downloadInOutHistoryCsvService(Company company, InOutHistorySearchForm inOutHistorySearchForm,  Writer writer) throws IOException{
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        String[] columns = {"입/출고시간","시리얼 번호","제품이름","제품코드","장소","입/출고 상태","갯수"};
        csvPrinter.printRecord(columns);

        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = null;
        System.out.println("===========");
        System.out.println(inOutHistorySearchForm.getStartDate());
        System.out.println("===========");
        //입출고 내역 조회내용있으면 조회된 내역만 다운로드
        if(inOutHistorySearchForm.getQuery() != "" || (inOutHistorySearchForm.getStartDate() != null && inOutHistorySearchForm.getEndDate() != null)){
            inOutHistories = dynamoDBUtils.searchInOutHistory(company, inOutHistorySearchForm);
        }else { // 전제데이터 다운로드
            inOutHistories = dynamoDBUtils.loadByCompany(company);
        }

        HashMap<String,String> inoutStatus = new HashMap<>();
        inoutStatus.put("IN","입고");
        inoutStatus.put("OUT","출고");

        for(InOutHistory inOutHistory : inOutHistories){
            List<String> record = new ArrayList<>();
            record.add(inOutHistory.getCreatedAt().toString());
            record.add(inOutHistory.getMatSerialNumber());
            record.add(inOutHistory.getProductName());
            record.add(inOutHistory.getProductCode());
            record.add(inOutHistory.getMatLocation());
            record.add(inoutStatus.get(inOutHistory.getInOutStatus()));
            record.add(inOutHistory.getUpdateCnt().toString());
            csvPrinter.printRecord(record);
        }

    }

    /**
     * 그래프 데이터 csv다운로드
     */
    public void downloadInOutHistoryGraphCsvService(Company company, MatGraphForm matGraphForm,  Writer writer) throws IOException{
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        System.out.println(matGraphForm);
        List<ResultGraph> resultGraphs = null;
        if(matGraphForm.getTimeDimension().equals("HOUR"))
            resultGraphs = matsHistoryGraphHourService(company, matGraphForm);
        else if(matGraphForm.getTimeDimension().equals("DAY"))
            resultGraphs = matsHistoryGraphDayService(company, matGraphForm);
        else if(matGraphForm.getTimeDimension().equals("WEEK"))
            resultGraphs = matsHistoryGraphWeekService(company, matGraphForm);
        else if(matGraphForm.getTimeDimension().equals("MONTH"))
            resultGraphs = matsHistoryGraphMonthService(company, matGraphForm);

        //csv컬럼 만들고 삽입
        List<String> columns = new ArrayList<>();
        columns.add("");
        for(ResultGraph resultGraph : resultGraphs){
            columns.add(resultGraph.getTitle());
        }
        csvPrinter.printRecord(columns);

        //데이터 삽입
        if(resultGraphs.isEmpty()) return;
        int dateLength = resultGraphs.get(0).getLabels().size();
        for(int i=0; i<dateLength; i++){
            ArrayList<Object> record = new ArrayList<>();
            for(int j=0; j<resultGraphs.size(); j++){
                ResultGraph resultGraph = resultGraphs.get(j);
                List<String> labels = resultGraph.getLabels();
                List<Integer> data = resultGraph.getData();
                if(j == 0) // label 한번만 추가
                    record.add(labels.get(i));
                record.add(data.get(i));
            }
            csvPrinter.printRecord(record);
        }

    }
}
