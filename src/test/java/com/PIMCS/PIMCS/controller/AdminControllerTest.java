package com.PIMCS.PIMCS.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Transactional
/*서블릿 컨테이너 모킹 하기위한 어노테이션*/
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired AdminController adminController;

    /*HTTP GET, POST 등에 대한 API테스트 가능*/
    @Autowired
    private MockMvc mockMvc;

    @Test
    void matCategoryAddForm() {

    }

    @Test
    void matCategoryAdd() {
    }

    @Test
    void matCategoryList() {
    }

    @Test
    void matCategoryModify() {
    }

    @Test
    void matCategoryRemove() {
    }

    @Test
    void companySearch() {
    }

    @Test
    void companyDetail() {
    }

    @Test
    void deleteOwnDevices() {
    }

    @Test
    void adminQnaList() {
    }

    @Test
    void adminSearchQuestion() {
    }

    @Test
    void detailAdminQna() {
    }

    @Test
    void answerAdd() {
    }

    @Test
    void emailFrameModifyForm() {
    }

    @Test
    void emailFrameModify() {
    }

    @Test
    void orderList() {
    }

    @Test
    void adminOrderQuestion() {
    }

    @Test
    void detailOrder() {
    }

    @Test
    void depositModify() {
    }

    @Test
    void ownDeviceAndSendHistorySave() {
    }

    @Test
    void ownDeviceSave() {
    }
}