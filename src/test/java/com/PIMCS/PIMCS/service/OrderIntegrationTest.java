package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.MatCategoryOrderRepository;
import com.PIMCS.PIMCS.repository.MatOrderRepository;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import com.PIMCS.PIMCS.utils.UserDetailsServiceTest;
import com.amazonaws.services.dynamodbv2.xspec.S;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    private MockMvc mockMvc;

    private SecUserCustomForm secUserCustomForm;

    private UserDetailsServiceTest userDetailsServiceTest;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private GenerateEntity generateEntity;

    @Autowired
    private MatOrderRepository matOrderRepository;

    @Autowired
    private MatCategoryOrderRepository matCategoryOrderRepository;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();


        userDetailsServiceTest = new UserDetailsServiceTest();

        User user = generateEntity.createUser(null, true);
        user.setUserRoles(generateEntity.createUserRoles(user));
        secUserCustomForm =  userDetailsServiceTest.getPrincipalDetails(user);
    }

    @Test
    @DisplayName("매트 주문")
    public void createOrderMat() throws  Exception{
        MatCategory matCategory = generateEntity.createMatCategory(null, true);

        Random random = new Random();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("hopeDeliveryDate","2025-08-18");
        form.add("depositerName", UUID.randomUUID().toString().substring(0,10));
        form.add("postCode","28668");
        form.add("deliveryAddress","청주시");
        form.add("detailAddress", "청원구");
        form.add("matCategoryIdList[0]", matCategory.getId()+"");
        form.add("matCategoryOrderList[0].orderCnt", 5+"");
        int orderCnt =  matOrderRepository.findByCompany(secUserCustomForm.getCompany()).size();

        mockMvc.perform(post("/orders/mats").with(user(secUserCustomForm)).with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/mats/create"));

        List<MatOrder> matOrders = matOrderRepository.findByCompany(secUserCustomForm.getCompany());
        Assertions.assertTrue(matOrders.size() > orderCnt );

        MatOrder matOrder = matOrders.get(matOrders.size() - 1);
        Assertions.assertEquals(matOrder.getHopeDeliveryDate().toString(), form.get("hopeDeliveryDate").get(0));
        Assertions.assertEquals(matOrder.getDepositerName(), form.get("depositerName").get(0));
        Assertions.assertEquals(matOrder.getPostCode(), form.get("postCode").get(0));
        Assertions.assertEquals(matOrder.getDeliveryAddress(), form.get("deliveryAddress").get(0));
        Assertions.assertEquals(matOrder.getDetailAddress(), form.get("detailAddress").get(0));

//        MatCategoryOrder matCategoryOrder = matCategoryOrderRepository.findByMatOrderAndMatCategory(matOrder,matCategory).orElse(null);
//        Assertions.assertNotNull(matCategoryOrder);
    }

    @Test
    @DisplayName("매트 0개 주문")
    public void createOrderMatZero() throws  Exception{
        MatCategory matCategory = generateEntity.createMatCategory(null, true);

        Random random = new Random();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("hopeDeliveryDate","2025-08-18");
        form.add("depositerName", UUID.randomUUID().toString().substring(0,10));
        form.add("postCode","28668");
        form.add("deliveryAddress","청주시");
        form.add("detailAddress", "청원구");
        form.add("matCategoryIdList[0]", matCategory.getId()+"");
        form.add("matCategoryOrderList[0].orderCnt", 0+"");

        int orderCnt =  matOrderRepository.findByCompany(secUserCustomForm.getCompany()).size();

        mockMvc.perform(post("/orders/mats").with(user(secUserCustomForm)).with(csrf().asHeader())
                        .contentType(APPLICATION_JSON)
                        .params(form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/mats/create"));

        List<MatOrder> matOrders = matOrderRepository.findByCompany(secUserCustomForm.getCompany());
        Assertions.assertTrue(matOrders.size() == orderCnt );

    }
}
