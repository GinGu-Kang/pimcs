package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.CompanyServiceUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CompanyManagementService {
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;



    @Autowired
    public CompanyManagementService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
    }
    public void insertCompanyRegistration(Company company, User user){
        userRepository.save(user);
        companyRepository.save(company);

    }

    public List<User> findMyCompanyWorker(String companyCode){

        List<User> myCompanyWorkers =userRepository.findByCompany(companyRepository.findByCompanyCode(companyCode).get());
        return myCompanyWorkers;
    }

    //승인대기중 사용자
    public List<User> findApproveWaitWorker (String companyCode){
        List<User> myCompanyWorkers =companyRepository.findByCompanyCode(companyCode).get().getCompanyWorker();
        List<User> approveWaitWorker=myCompanyWorkers.stream()
                .filter(user->user.getAuthorities().toString().equals("[ROLE_unapprovedUser]"))
                .collect(Collectors.toList());
        return approveWaitWorker;
    }
    //회사와 대표 저장 회사코드 : UUID
    public void companyRegistration(User ceo, Company company){
        List<UserRole> userRoles = new ArrayList<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ceo.setPassword(encoder.encode(ceo.getPassword()));
        ceo.setUserRoles(userRoles);
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();
        company.setCompanyCode(companyServiceUtils.UUIDgeneration().substring(0,30));
        companyRepository.save(company);
        ceo.setCompany(company);


        for (Role role:roleRepository.findAll()
             ) {
            UserRole userRole=new UserRole();
            userRole.setUser(ceo);
            userRole.setRole(role);
            userRoles.add(userRole);
        }

        ceo.setUserRoles(userRoles);
        userRepository.save(ceo);
        userRoleRepository.saveAll(userRoles);




    }
}
