package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class CompanyManagementServiceTest {
    CompanyRepository companyRepository;
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
    @Commit
    public void saveCompanyTest(){
        Company company = new Company();
        company.setCompanyName("rebook");
        company.setContactPhone("sdf");
        company.setCompanyAddress("asdf");
        companyManagementService.saveCompany(company);
    }
    @Test
    @Commit
    public void userRoleSaveTest(){
        companyManagementService.userRoleSave("wlsrn", "ChiefOfPimcs","4e475f486a01467b8732476e673fc0");
    }

}
