package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class CompanyManagementServiceTest {
    @Autowired
    CompanyManagementService companyManagementService;
    @DisplayName("Fetch Join 테스트")
    @Test
    public void findMyCompanyWorkersTest(){
        List<User> myCompanyWorkes=companyManagementService.findMyCompanyWorker("code123");
        for (User user:myCompanyWorkes) {
            System.out.println(user.getAuthorities());
        }
    }
    @Test
    public void findApproveWaitWorkerTest(){
        List<User> myCompanyWorkes=companyManagementService.findApproveWaitWorker("code123");
        for (User user:myCompanyWorkes) {
            System.out.println(user.getAuthorities());
        }
    }
}
