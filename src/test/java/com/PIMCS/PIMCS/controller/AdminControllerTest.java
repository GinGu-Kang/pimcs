package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdminController adminController;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup(){

    }


    @BeforeEach
    public void before() {
        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(AdminController.class) // 테스트 대상 Controller 를 넣어준다.
                        .alwaysExpect(MockMvcResultMatchers.status().isOk()) // 특정 필수 조건을 지정
                        .build();
    }


    @Test
    void testControllerMapping() throws Exception {
        RequestBuilder reqBuilder = MockMvcRequestBuilders
                .post("/auth/login");
        mockMvc.perform(reqBuilder)
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    void createMatCategoryForm() {

        Assertions.assertThat("12").isEqualTo("12");
    }

    @Test
    void createMatCategory() {
    }

    @Test
    void findMatCategoryList() {
    }

    @Test
    void updateMatCategory() {
    }

    @Test
    void deleteMatCategory() {
    }

    @Test
    void findCompanyList() {
    }

    @Test
    void detailsCompany() {
    }

    @Test
    void deleteOwnDeviceList() {
    }

    @Test
    void findAdminQnaList() {
    }

    @Test
    void detailsAdminQna() {
    }

    @Test
    void createAnswer() {
    }

    @Test
    void createOrderMailFrameForm() {
    }

    @Test
    void createOrderMailFrame() {
    }

    @Test
    void findOrderList() {
    }

    @Test
    void detailsOrder() {
    }

    @Test
    void updateOrderDeposit() {
    }

    @Test
    void createOwnDeviceAndSendHistory() {
    }

    @Test
    void createOwnDevice() {
    }
}