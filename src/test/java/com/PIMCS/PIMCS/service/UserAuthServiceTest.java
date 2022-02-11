package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class UserAuthServiceTest {
    @Autowired
    UserAuthService userAuthService;



    @Test
    @Commit
    public void saveTest(){
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
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
    public void deleteUserTest() {
        userAuthService.deleteUser("wlsrn212@gmail.com");
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        System.out.println(user.isEmpty());
    }

    @Test
    @Commit
    public void updateTest() {
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
        user.get().setName("강진구");
        userAuthService.userUpdate(user.get());
        System.out.println(user.isEmpty());
    }

    @Test
    public void findById() {
        Optional<User> user = userAuthService.findUser("wlsrn212@gmail.com");
    }

    @Test
    @Commit
    public void insertRoleTest() {
        List<Role> roleList= new ArrayList<>();

        roleList.add(Role.builder().name("UserManagement").build());
        roleList.add(Role.builder().name("User").build());
        roleList.add(Role.builder().name("InventoryManagement").build());
        roleList.add(Role.builder().name("ChiefOfPimcs").build());

        userAuthService.roleUpdate(roleList);
    }


    @Test
    public void userRoleFindTest() {
        System.out.println(userAuthService.findUser("rkdwlsrn212@gmail.com").get().getCompany().getCompanyCode());
    }

    @Test
    public void authTest() {
        System.out.println(userAuthService.loadUserByUsername("wlsrn212@gmail.com"));
    }

    @Test
    @Commit
    public void deleteUserRoleTest() {
        userAuthService.deleteUserAllRole("rkdwlsrn212@gmail.com");
    }



    @Test
    public void findRoleTest(){
        System.out.println(userAuthService.findRole());
    }
}