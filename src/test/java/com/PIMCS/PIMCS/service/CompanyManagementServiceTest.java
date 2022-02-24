package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class CompanyManagementServiceTest {
    CompanyRepository companyRepository;
    @Autowired
    CompanyManagementService companyManagementService;

//    @DisplayName("Fetch Join 테스트")
//    @Test
//    public void findMyCompanyWorkersTest(){
//        List<User> myCompanyWorkes=companyManagementService.findMyCompanyWorker();
//        for (User user:myCompanyWorkes) {
//            System.out.println(user.getAuthorities());
//        }
//    }
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

    @Test
    public void streamPractice(){
        Stream<String> builderStream =Stream.<String>builder()
                    .add("Eric").add("Elena").add("Java")
                    .build(); // [Eric, Elena, Java]
        Stream<User> generatedStream =
                Stream.generate(() -> User.builder().email("fadsfas").build()).limit(5);// [30, 32, 34, 36, 38]

        Stream<User> user=generatedStream.filter(okUser->okUser.getEmail().contains("fadsfas"));
        Stream<String> emails=user.map(User::getEmail);
//        System.out.println(emails.collect(Collectors.toList()));
        for (String asd:emails.collect(Collectors.toList())
             ) {
            System.out.println(asd);

        }
    }



}
