package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.MatRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class MatServiceIntegrationTest {

    @Autowired
    MatRepository matRepository;
    @Autowired
    MatService matService;


    @Test
    @Commit
    public void creatMat(){
        /**
         *  1. 생성권한 있는지 검증
         *  2. 매트 정상등록 되었는지 검증
         */
        
        //given
        Mat mat = new Mat();
        mat.setSerialNumber("w1234");
        mat.setProdCode("prod123");
        mat.setCompanyCode("code123");
        mat.setCalcMethod(1);
        mat.setThreshold(5);
        mat.setInventoryWeight(0);
        mat.setRecentlyNoticeDate(new Timestamp(System.currentTimeMillis()));
        mat.setIsSendEmail(0);
        mat.setMatLocation("창고");
        mat.setProductOrderCnt(2);
        mat.setBoxWeight(5);
        mat.setBattery(10);

        //when
        String serialNum = matService.createMat(mat);

        //then
        //정상등록 되었는 검증
        Optional<Mat> matOpt = matRepository.findBySerialNumber(serialNum);
        if(!matOpt.isPresent()) Assertions.fail("정상등록되 않았습니다.");

    }

    @Test
    public void updateMat(){
        /**
         * 1. 수정권한 있는지 검증 하고 해당해사에스 등록된 매트가 맞는지 검
         * 2. 정상수정 되어있는지 검증
         */

        //given

    }
}
