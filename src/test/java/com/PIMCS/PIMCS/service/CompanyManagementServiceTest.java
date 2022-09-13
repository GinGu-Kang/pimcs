package com.PIMCS.PIMCS.service;


import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitCeoRedisRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import com.PIMCS.PIMCS.utils.UserDetailsServiceTest;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.util.*;
import java.util.concurrent.Future;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class CompanyManagementServiceTest {



    @Autowired
    private CompanyManagementService companyManagementService;

    @Autowired
    private  UserAuthService userAuthService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private  GenerateEntity generateEntity;

    @Autowired
    private  ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EmailUtilImpl emailUtilImpl;


    @Autowired
    private WaitCeoRedisRepository waitCeoRedisRepository;

    private MockMvc mockMvc;

    private SecUserCustomForm secUserCustomForm;

    private UserDetailsServiceTest userDetailsServiceTest;

    private MultiValueMap<String,String> form;





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
    @DisplayName("이메일_정상전송_체크")
    public void checkSendEmail() throws Exception {
        String toEmail = "wisp212@gmail.com";
        Future<Map> f = emailUtilImpl.sendEmail(
                new String[]{toEmail}
                , "PIMCS에서 온 인증 메일입니다."
                , "<div>test</div>"
                ,true
        );
        while (!f.isDone()) {
        }
        Assertions.assertEquals((int)f.get().get("resultCode"), 200);
    }

    @Test
    @DisplayName("회사_및_대표등록")
    public void createCompanyAndUser() throws Exception {
        BusinessCategory businessCategory = generateEntity.createBusinessCategory(true);

        form = new LinkedMultiValueMap<>();
        form.add("companyName",UUID.randomUUID().toString().substring(0,20));
        form.add("companyAddress","test");
        form.add("companyAddressdetail","detail");
        form.add("contactPhone","1234");
        form.add("businessCategoryId.id", businessCategory.getId()+"");
        form.add("name", UUID.randomUUID().toString().substring(0,10));
        form.add("phone", "12345");
        form.add("ceoEmail","2543817958@qq.com");
        form.add("department","sw");
        form.add("password", "12345");

        //  /compaines post 요청
        ResultActions resultActions = mockMvc.perform(post("/companies").with(csrf().asHeader()).contentType(APPLICATION_JSON).params(form));
        resultActions.andExpect(status().isOk());

        // redis에 정상 저장되었는 체크
        WaitCeo waitCeo = findWaitCeo();
        Assertions.assertNotNull(waitCeo);

        // 검증 get 요청 및 rdbms에저장
        mockMvc.perform(get("/companies/verification/"+waitCeo.getId()))
                .andExpect(status().isOk());

        Company company = companyRepository.findByCompanyName(getFormItem("companyName")).orElse(null);
        Assertions.assertNotNull(company);
        Assertions.assertEquals(company.getCompanyName(), getFormItem("companyName"));
        Assertions.assertEquals(company.getCompanyAddress(), getFormItem("companyAddress").concat(getFormItem("companyAddressdetail")));
        Assertions.assertEquals(company.getContactPhone(), getFormItem("contactPhone"));
        Assertions.assertEquals(company.getCeoEmail(), getFormItem("ceoEmail"));
        Assertions.assertEquals(company.getCeoName(), getFormItem("name"));
        Assertions.assertEquals(company.getBusinessCategoryId().getId(), Integer.parseInt(getFormItem("businessCategoryId.id")));
        Assertions.assertNotNull(company.getBusinessCategoryId().getCategoryName());

        User user = userRepository.findByEmail(getFormItem("ceoEmail")).orElse(null);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getEmail(), getFormItem("ceoEmail"));
        Assertions.assertEquals(user.getCompany().getId(), company.getId());
        Assertions.assertEquals(user.getName(), getFormItem("name"));
        Assertions.assertEquals(user.getPhone(), getFormItem("phone"));
        Assertions.assertEquals(user.getDepartment(), getFormItem("department"));


        //관리자 권한 부여했는 체크
        //실수로 관리자 권한 부여시 큰일
        List<UserRole> userRoleList = userRoleRepository.findByUserEmail(user.getEmail());
        for(UserRole userRole : userRoleList){
            Role role = userRole.getRole();
            Assertions.assertFalse(role.getName().equals("ChiefOfPimcs"));
        }

    }

    @Test
    @DisplayName("회사정보_변경")
    public void updateCompany() throws Exception{
        BusinessCategory businessCategory = generateEntity.createBusinessCategory(true);
        form = new LinkedMultiValueMap<>();
        form.add("companyName", UUID.randomUUID().toString().substring(0,20));
        form.add("ceoEmail", UUID.randomUUID().toString()+"@pimcs.com");
        form.add("companyAddress", "abcd");
        form.add("contactPhone","4567");
        form.add("businessCategoryId.id", businessCategory.getId()+"");
        form.add("ceoName",UUID.randomUUID().toString().substring(0,7));

        mockMvc.perform(put("/companies")
                .with(user(secUserCustomForm))

                .with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(form)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/companies/details"));


        Company company = companyRepository.findByCompanyCode(secUserCustomForm.getCompany().getCompanyCode()).orElse(null);
        Assertions.assertNotNull(company);
        Assertions.assertEquals(company.getCompanyName(), getFormItem("companyName"));
        Assertions.assertEquals(company.getCeoEmail(), getFormItem("ceoEmail"));
        Assertions.assertEquals(company.getCompanyAddress(), getFormItem("companyAddress"));
        Assertions.assertEquals(company.getContactPhone(), getFormItem("contactPhone"));
        Assertions.assertEquals(company.getCeoName(), getFormItem("ceoName"));
        Assertions.assertEquals(company.getBusinessCategoryId().getId(), Integer.parseInt(getFormItem("businessCategoryId.id")));
        Assertions.assertEquals(company.getBusinessCategoryId().getCategoryName(), businessCategory.getCategoryName());

    }

    @Test
    @DisplayName("사원삭제")
    public void deleteWorker() throws Exception {
        User testUser = generateEntity.createUser(secUserCustomForm.getCompany(), true);
        User testUser2 = generateEntity.createUser(secUserCustomForm.getCompany(), true);
        String[] emailList = {testUser.getEmail(), testUser2.getEmail()};

        for(String email : emailList){
            User findUser = userRepository.findByEmail(email).orElse(null);
            Assertions.assertNotNull(findUser);
        }
        form = new LinkedMultiValueMap<>();
        form.addAll("selectWorkersEmail[]", Arrays.asList(emailList));
        mockMvc.perform(delete("/companies/workers")
                .with(user(secUserCustomForm))
                        .with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(form))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", true).exists())
                .andExpect(jsonPath("message","회원 삭제 되었습니다.").exists());

        for(String email : emailList){
            User findUser = userRepository.findByEmail(email).orElse(null);
            Assertions.assertNull(findUser);
        }

    }

    @Test
    @DisplayName("존재하지않는_검증키_테스트")
    public void notExistVerifyKeyTest() throws Exception {
                mockMvc.perform(get("/companies/verification/jaiosjoisjsoij"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("재고관리_권한_부여")
    public void createInventoryManagementAuthority() throws Exception {


        User testUser = generateEntity.createUser(secUserCustomForm.getCompany(), true);

        MultiValueMap<String, String> authForm =new LinkedMultiValueMap<>();
        authForm.add("email", testUser.getEmail());
        authForm.add("authority", "inventoryManagement");
        mockMvc.perform(post("/companies/worker/authority")
                            .with(user(secUserCustomForm))
                            .with(csrf().asHeader())
                             .contentType(APPLICATION_JSON)
                            .params(authForm)).andExpect(status().isOk());

        List<UserRole> userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        UserRole userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("InventoryManagement")).findFirst().orElse(null);
        Assertions.assertNotNull(userRole);


    }

    @Test
    @DisplayName("사용자관리_권한_부여")
    public void createUserManagementAuthority() throws Exception{
        User testUser = generateEntity.createUser(secUserCustomForm.getCompany(), true);

        MultiValueMap<String, String> authForm =new LinkedMultiValueMap<>();
        authForm.add("email", testUser.getEmail());
        authForm.add("authority", "UserManagement");
        mockMvc.perform(post("/companies/worker/authority")
                .with(user(secUserCustomForm))
                .with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(authForm)).andExpect(status().isOk());

        List<UserRole> userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        UserRole userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("UserManagement")).findFirst().orElse(null);
        Assertions.assertNotNull(userRole);
    }

    @Test
    @DisplayName("재고관리권한_삭제")
    public void deleteInventoryManagementAuthority() throws Exception {

        User testUser = generateEntity.createUser(secUserCustomForm.getCompany(), true);
        generateEntity.createUserRoles(testUser);

        List<UserRole> userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        UserRole userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("InventoryManagement")).findFirst().orElse(null);
        Assertions.assertNotNull(userRole);

        MultiValueMap<String, String> authForm =new LinkedMultiValueMap<>();
        authForm.add("email", testUser.getEmail());
        authForm.add("authority", "inventoryManagement");
        mockMvc.perform(delete("/companies/worker/authority")
                .with(user(secUserCustomForm))
                .with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(authForm)).andExpect(status().isOk());

        userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("InventoryManagement")).findFirst().orElse(null);
        Assertions.assertNull(userRole);

    }

    @Test
    @DisplayName("사용자관리_권한_삭제")
    public void deleteUserManagementAuthority() throws Exception{
        User testUser = generateEntity.createUser(secUserCustomForm.getCompany(), true);
        generateEntity.createUserRoles(testUser);

        List<UserRole> userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        UserRole userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("UserManagement")).findFirst().orElse(null);
        Assertions.assertNotNull(userRole);

        MultiValueMap<String, String> authForm =new LinkedMultiValueMap<>();
        authForm.add("email", testUser.getEmail());
        authForm.add("authority", "UserManagement");
        mockMvc.perform(delete("/companies/worker/authority")
                .with(user(secUserCustomForm))
                .with(csrf().asHeader())
                .contentType(APPLICATION_JSON)
                .params(authForm)).andExpect(status().isOk());

        userRoleList = userRoleRepository.findByUserEmail(testUser.getEmail());
        userRole = userRoleList.stream().filter(o -> o.getRole().getName().equals("UserManagement")).findFirst().orElse(null);
        Assertions.assertNull(userRole);
    }


    public WaitCeo findWaitCeo(){
        List<WaitCeo> waitCeoList = waitCeoRedisRepository.findAll();
        int start = waitCeoList.size() - 1;
        for(int i = start; i > -1; i-- ){
            WaitCeo o = waitCeoList.get(i);
            if(o == null) continue;
            if(o.getCompany().getCeoEmail().equals(form.get("ceoEmail").get(0))){
                return o;
            }
        }
        return null;
    }

    public String getFormItem(String key){
        return form.get(key).get(0);
    }




    @AfterEach
    public void afterEach(){
        if(waitCeoRedisRepository.findAll().size() > 0)
            waitCeoRedisRepository.deleteAll();
    }



}
