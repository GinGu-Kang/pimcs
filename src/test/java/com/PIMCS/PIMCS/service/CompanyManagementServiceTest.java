package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.noSqlDomain.DynamoMat;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class CompanyManagementServiceTest {
    CompanyRepository companyRepository;
    @Autowired
    CompanyManagementService companyManagementService;

    @Autowired
    GenerateEntity generateEntity;
    @Autowired
    DynamoQuery dynamoQuery;


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
        companyManagementService.updateCompany(company);
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

    @Test
    public void test(){
        List<KeyPair> arr = new ArrayList<>();
        KeyPair keyPair = new KeyPair();
        keyPair.withHashKey(1);
        keyPair.withRangeKey("WS01E210001");

        KeyPair keyPair2 = new KeyPair();
        keyPair2.withHashKey(114);
        keyPair2.withRangeKey("test_01");


        arr.add(keyPair);
        arr.add(keyPair2);
        List<Object> map = dynamoQuery.batchLoad(DynamoMat.class,arr);


        System.out.println(map);
    }

}
