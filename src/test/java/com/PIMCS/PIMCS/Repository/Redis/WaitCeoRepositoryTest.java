package com.PIMCS.PIMCS.Repository.Redis;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.Redis.WaitCeoRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WaitCeoRepositoryTest {
    @Autowired
    WaitCeoRedisRepository waitCeoRedisRepository;

    @Test
    public void 테스트(){
        User user = User.builder().email("hi").name("찡꾸").build();
        Company company = Company.builder().companyName("hihi").build();
        WaitCeo waitCeo = WaitCeo.builder().company(company).user(user).build();
        waitCeoRedisRepository.save(waitCeo);
        System.out.println(waitCeo.getId());
    }
}
