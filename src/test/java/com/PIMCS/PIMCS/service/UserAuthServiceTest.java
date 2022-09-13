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
    @Commit
    void 유저권한생성(){
        userAuthService.UserRoleSave("ryongho1997@gmail.com","ChiefOfPimcs");
//        userAuthService.UserRoleSave("wisp212@gmail.com","User");
//        userAuthService.UserRoleSave("wisp212@gmail.com","InventoryManagement");
//        userAuthService.UserRoleSave("wisp212@gmail.com","ChiefOfPimcs");
    }

    @Test
    public void findRoleTest(){
        System.out.println(userAuthService.findRole());
    }

    @Test
    void createUserService() {
    }

    @Test
    void createUserVerifyService() {
    }

    @Test
    void findUser() {
    }

    @Test
    void updateUserFormService() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void userUpdate() {
    }

    @Test
    void userPwdUpdate() {
    }

    @Test
    void roleUpdate() {
    }

    @Test
    void userRoleSave() {
    }

    @Test
    void loadUserByUsername() {
    }

    @Test
    void deleteUserAllRole() {
    }

    @Test
    void findRole() {
    }

    @Test
    void emailCheckService() {
    }

    @Test
    void companyCheck() {
    }

    @Test
    void userDetail() {
    }

    @Test
    void pwdFind() {
    }

    @Test
    void pwdFindVerify() {
    }
}