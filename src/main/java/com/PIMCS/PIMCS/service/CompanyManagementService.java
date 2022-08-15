package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.CompanyServiceUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitingCeoRedisRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@EnableAsync
public class CompanyManagementService {
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final WaitingCeoRedisRepository waitingCeoRedisRepository;
    private final EmailUtilImpl emailUtilImpl;




    @Autowired
    public CompanyManagementService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository, WaitingCeoRedisRepository waitingCeoRedisRepository, EmailUtilImpl emailUtilImpl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.waitingCeoRedisRepository = waitingCeoRedisRepository;
        this.emailUtilImpl = emailUtilImpl;
    }


    //회사원 전체검색
    public List<User> findMyCompanyWorker(Company company){
        List<User> myCompanyWorkers =userRepository.findByCompany(company);
        return myCompanyWorkers;
    }

    //회사원 필터링 검색
    public List<User> filterMyCompanyWorker(String keyword,String selectOption,Company company){
        List<User> myCompanyWorkers= new ArrayList<>();

        switch (selectOption){
            case "이름":
                myCompanyWorkers =userRepository.findByCompanyAndNameLike(company,"%"+keyword+"%");
                break;
            case "부서":
                myCompanyWorkers =userRepository.findByCompanyAndDepartmentLike(company,"%"+keyword+"%");
                break;
        }

        return myCompanyWorkers;
    }

    /*선택된 회사원 삭제
    * 회사원 들을 불러오고 companyid가 다르다면 정지.
    *
    * */
    @Transactional(rollbackFor = Exception.class)
    public void companyWorkerDelete(List<String> selectWorkersEmail,Company managerCompany){
        List<User> selectWorker=userRepository.findAllByEmailIn(selectWorkersEmail);
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
    public void companyRegistration(User ceo, Company company){
        String[] emailSendList=new String[]{ceo.getEmail()};
        String url="http://localhost:8080/company/registration/verify?verifyKey=";
        WaitingCeo waitingCeo=WaitingCeo.builder()
                .company(company)
                .user(ceo)
                .build();
        waitingCeoRedisRepository.save(waitingCeo);

        String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                "    <p style='margin-top: 40px;'>안녕하세요 PIMCS입니다.</p>\n" +
                "    <p >인증 확인을 누르면 회사가 등록됩니다.</p>\n" +
                "<a href='"+url+waitingCeo.getId()+"'>인증 확인</a>"+
                "</div>\n";
        emailUtilImpl.sendEmail(
                emailSendList
                , "PIMCS에서 온 인증 메일입니다."
                , orderMail
                ,true
        );
    }
    //회사와 대표 저장 회사코드 : UUID
    @Transactional(rollbackFor = Exception.class)
    public void companyRegistrationVerify(String verifyKey){
        System.out.println(waitingCeoRedisRepository.findById(verifyKey).isPresent());
        User ceo=waitingCeoRedisRepository.findById(verifyKey).get().getUser();

        Company company=waitingCeoRedisRepository.findById(verifyKey).get().getCompany();
        List<UserRole> userRoles = new ArrayList<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();

        ceo.setPassword(encoder.encode(ceo.getPassword()));
        company.setCompanyCode(companyServiceUtils.UUIDgeneration().substring(0,30));
        company.setCompanyAddress(company.getCompanyAddress().concat(company.getCompanyAddressdetail()));
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


    public boolean userRoleSave(String email, String authority,String companyCode){
        Optional<User> companyWorker=userRepository.findByEmail(email);
        String tartgetCompanyCode=companyWorker.get().getCompany().getCompanyCode();

        if(tartgetCompanyCode.equals(companyCode)){//해당회사가 맞는지 체크
            UserRole userRole=UserRole.builder()
                    .user(companyWorker.get())
                    .role(roleRepository.findByName(authority)).build();
            userRoleRepository.save(userRole);
        }else{
            return false;
        }
        return true;
    }

    public boolean userRoleDelete(String email, String authority,String companyCode){
        Optional<User> companyWorker=userRepository.findByEmail(email);
        String tartgetCompanyCode=companyWorker.get().getCompany().getCompanyCode();

        if(tartgetCompanyCode.equals(companyCode)){
            List<UserRole> userRoles= userRoleRepository.findByUserAndRole(companyWorker.get(),roleRepository.findByName(authority));
            if(!userRoles.isEmpty()){
                userRoleRepository.deleteAllInBatch(userRoles);
            }
        }else{
            return false;
        }
        return true;

    }



    /*
    회사 수정
     */
    public void updateCompany(Company company) {
        companyRepository.save(company);
    }


}
