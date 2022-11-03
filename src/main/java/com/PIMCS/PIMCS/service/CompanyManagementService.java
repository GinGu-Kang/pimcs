package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.CompanyServiceUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.response.ResponseForm;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitCeoRedisRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.stream.Collectors;


@Service
@EnableAsync
@Slf4j
public class CompanyManagementService {
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final WaitCeoRedisRepository waitCeoRedisRepository;
    private final EmailUtilImpl emailUtilImpl;

    @Value("${server.address}")
    private String address;

    @Value("${server.port}")
    private int port;



    @Autowired
    public CompanyManagementService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository, WaitCeoRedisRepository waitCeoRedisRepository, EmailUtilImpl emailUtilImpl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.waitCeoRedisRepository = waitCeoRedisRepository;
        this.emailUtilImpl = emailUtilImpl;
    }


    //회사원 전체검색
    public List<User> findCompanyWorkersService(Company company){
        List<User> myCompanyWorkers =userRepository.findByCompany(company);
        return myCompanyWorkers;
    }

    //회사원 필터링 검색
    public List<User> findCompanyWorkersByNameOrDepartmentService(SearchForm searchForm, Company company){
        List<User> myCompanyWorkers= new ArrayList<>();

        switch (searchForm.getSearchType()){
            case "이름":
                myCompanyWorkers =userRepository.findByCompanyAndNameLike(company,"%"+searchForm.getSearchQuery()+"%");
                break;
            case "부서":
                myCompanyWorkers =userRepository.findByCompanyAndDepartmentLike(company,"%"+searchForm.getSearchQuery()+"%");
                break;
        }

        return myCompanyWorkers;
    }

    /*선택된 회사원 삭제
    * 회사원 들을 불러오고 companyid가 다르다면 정지.
    *
    * */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompanyWorkersService(List<String> selectWorkersEmail,Company managerCompany){
        List<User> selectWorker=userRepository.findByCompanyAndEmailIn(managerCompany,selectWorkersEmail);
        selectWorker=selectWorker.stream().filter(worker -> worker.getCompany().getCompanyCode().equals(managerCompany.getCompanyCode())).collect(Collectors.toList());

        for (User user:selectWorker
             ) {
            System.out.println(user.getName());
        }
        userRepository.deleteAllInBatch(selectWorker);
    }

    //회사 등록후 이메일 인증 대기
    @Async
    @Transactional
    public void createCompanyService(User ceo, Company company){
        String[] emailSednList=new String[]{ceo.getEmail()};

        WaitCeo waitCeo=WaitCeo.builder()
                .company(company)
                .user(ceo)
                .build();
        waitCeoRedisRepository.save(waitCeo);

        String confirmUrl = String.format("http://%s:%s/companies/verification/%s", address, port, waitCeo.getId());
        System.out.println(confirmUrl);



        String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                "    <p style='margin-top: 40px;'>안녕하세요 PIMCS입니다.</p>\n" +
                "    <p >인증 확인을 누르면 회사가 등록됩니다.</p>\n" +
                "<a href='"+confirmUrl+"'>인증 확인</a>"+
                "</div>\n";
        emailUtilImpl.sendEmail(
                    emailSednList
                    , "PIMCS에서 온 인증 메일입니다."
                    , orderMail
                    ,true
            );
    }


    //회사와 대표계정 생성
    @Transactional(rollbackFor = Exception.class)
    public void createCompanyAndCeoService(Company company, User ceo){

        List<UserRole> userRoles = new ArrayList<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();

        company.setCompanyCode(companyServiceUtils.UUIDgeneration().substring(0,30));
        company.setCompanyAddress(company.getCompanyAddress().concat(company.getCompanyAddressdetail()));
        ceo.setPassword(encoder.encode(ceo.getPassword()));
        ceo.setCompany(company);
        for (Role role:roleRepository.findAll()
        ) {
            if(!role.getName().equals("ChiefOfPimcs")){
                System.out.println(role.getName());
                UserRole userRole=new UserRole();
                userRole.setUser(ceo);
                userRole.setRole(role);
                userRoles.add(userRole);
            }
        }

        companyRepository.save(company);
        userRepository.save(ceo);
        userRoleRepository.saveAll(userRoles);

    }

    //사용자 권한 생성
    public ResponseForm createWorkerAuthorityService(String email, String authority, String companyCode){
        Optional<User> companyWorker=userRepository.findByEmail(email);
        String tartgetCompanyCode=companyWorker.get().getCompany().getCompanyCode();

        if(tartgetCompanyCode.equals(companyCode)){//해당회사가 맞는지 체크
            UserRole userRole=UserRole.builder()
                    .user(companyWorker.get())
                    .role(roleRepository.findByName(authority)).build();
            userRoleRepository.save(userRole);
        }else{
            return new ResponseForm(false, "해당 회사의 사원만 수정할 수 있습니다.", null);
        }
        return new ResponseForm(true, "", null);
    }

    //사용자 권한 삭제
    public ResponseForm deleteWorkerAuthorityService(String email, String authority,String companyCode){
        Optional<User> companyWorker=userRepository.findByEmail(email);
        String tartgetCompanyCode=companyWorker.get().getCompany().getCompanyCode();

        if(tartgetCompanyCode.equals(companyCode)){
            List<UserRole> userRoles= userRoleRepository.findByUserAndRole(companyWorker.get(),roleRepository.findByName(authority));
            if(!userRoles.isEmpty()){
                userRoleRepository.deleteAllInBatch(userRoles);
            }
        }else{
            return new ResponseForm(false, "해당 회사의 사원만 수정할 수 있습니다.", null);
        }
        return new ResponseForm(true, "", null);

    }



    /*
    회사 수정
     */
    public void updateCompanyService(Company company) {
        companyRepository.save(company);
    }


}
