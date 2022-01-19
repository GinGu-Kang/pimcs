package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.UserAuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.Optional;
@SpringBootTest
@Transactional
public class UserAuthServiceTest {
    @Autowired
    UserAuthService userAuthService;


    @Test
    @Commit
    public void saveTest(){
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        user.get().setEmail("hihi@naver.com");
        userAuthService.userUpdate(user.get());
        System.out.println(user.isEmpty());
    }

    @Test
    public void emailCheck() {
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        System.out.println(user.get().getName());
    }

    @Test
    public void nullTest() {
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        System.out.println(user.isEmpty());
    }
    @Test
    public void deleteTest(){
        userAuthService.userDelete("wlsrn212@gmail.com");
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        System.out.println(user.isEmpty());
    }
    @Test
    @Commit
    public void updateTest(){
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        user.get().setName("강진구");
        userAuthService.userUpdate(user.get());
        System.out.println(user.isEmpty());
    }


}