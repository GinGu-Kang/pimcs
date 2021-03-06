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


    //????????? ????????????
    public List<User> findMyCompanyWorker(Company company){
        List<User> myCompanyWorkers =userRepository.findByCompany(company);
        return myCompanyWorkers;
    }

    //????????? ????????? ??????
    public List<User> filterMyCompanyWorker(String keyword,String selectOption,Company company){
        List<User> myCompanyWorkers= new ArrayList<>();

        switch (selectOption){
            case "??????":
                myCompanyWorkers =userRepository.findByCompanyAndNameLike(company,"%"+keyword+"%");
                break;
            case "??????":
                myCompanyWorkers =userRepository.findByCompanyAndDepartmentLike(company,"%"+keyword+"%");
                break;
        }

        return myCompanyWorkers;
    }

    /*????????? ????????? ??????
    * ????????? ?????? ???????????? companyid??? ???????????? ??????.
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

    //?????? ????????? ????????? ?????? ??????
    @Async
    @Transactional
    public void companyRegistration(User ceo, Company company){
        String[] emailSednList=new String[]{ceo.getEmail()};
        String url="http://localhost:8080/company/registration/verify?verifyKey=";
        WaitingCeo waitingCeo=WaitingCeo.builder()
                .company(company)
                .user(ceo)
                .build();
        waitingCeoRedisRepository.save(waitingCeo);

        String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                "    <p style='margin-top: 40px;'>??????????????? PIMCS?????????.</p>\n" +
                "    <p >?????? ????????? ????????? ????????? ???????????????.</p>\n" +
                "<a href='"+url+waitingCeo.getId()+"'>?????? ??????</a>"+
                "</div>\n";

        emailUtilImpl.sendEmail(
                emailSednList
                , "PIMCS?????? ??? ?????? ???????????????."
                , orderMail
                ,true
        );
    }
    //????????? ?????? ?????? ???????????? : UUID
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
        System.out.println("====================@@@@@@@@@@@");
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
        System.out.println("====================@@@@@@@@@@@");
        userRoleRepository.saveAll(userRoles);
    }


    public boolean userRoleSave(String email, String authority,String companyCode){
        Optional<User> companyWorker=userRepository.findByEmail(email);
        String tartgetCompanyCode=companyWorker.get().getCompany().getCompanyCode();

        if(tartgetCompanyCode.equals(companyCode)){//??????????????? ????????? ??????
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
    ?????? ??????
     */
    public void updateCompany(Company company) {
        companyRepository.save(company);
    }


}
