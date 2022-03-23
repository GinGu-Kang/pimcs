package com.PIMCS.PIMCS.Repository.Redis;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.Redis.WaitingCeoRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WaitingCeoRepositoryTest {
    @Autowired
    WaitingCeoRedisRepository waitingCeoRedisRepository;

    @Test
    public void 테스트(){
        User user = User.builder().email("hi").name("찡꾸").build();
        Company company = Company.builder().companyName("hihi").build();
        WaitingCeo waitingCeo = WaitingCeo.builder().company(company).user(user).build();
        waitingCeoRedisRepository.save(waitingCeo);
        System.out.println(waitingCeo.getId());
    }
}
