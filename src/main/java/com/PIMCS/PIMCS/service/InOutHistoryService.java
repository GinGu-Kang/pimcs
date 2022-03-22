package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.DynamoResultPage;
import com.PIMCS.PIMCS.form.MatSerialNumberForm;
import com.PIMCS.PIMCS.form.ResultGraph;
import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
     * 입출고 내역 일별그래프
     */
    public ResultGraph inOutHistoryDayGraphService(Company company, MatSerialNumberForm serialNumberForm){
        LocalDate curDate = LocalDate.now();
        LocalDate queryDate = LocalDate.of(curDate.getYear(),curDate.getMonth(),1);
        queryDate = queryDate.minusMonths(1);
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<InOutHistory> inOutHistories = dynamoDBUtils.loadByCompanyAndSerialNumberAndDate(company,serialNumberForm.getSerialNumberList(), queryDate, curDate);


        for(int i=1; i<=curDate.getDayOfMonth(); i++){
            LocalDate date = LocalDate.of(curDate.getYear(), curDate.getMonth(), i);
            System.out.println("=========");
            for(String serialNumber : serialNumberForm.getSerialNumberList()){

                System.out.println(serialNumber);
                System.out.println(InOutHistory.findByDay(inOutHistories,date,serialNumber));

            }
            System.out.println(date.toString());
            System.out.println("====end=====");
        }


//        ResultGraph resultGraph = new ResultGraph();
//        List<Mat> mats = matRepository.findByCompany(company);
        return null;
    }


}
