package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<User> myCompanyWorkers =userRepository.findByCompanyCode(companyCode);
        return myCompanyWorkers;
    }

    //승인대기중 사용자
    public List<User> findApproveWaitWorker (String companyCode){
        List<User> myCompanyWorkers =userRepository.findByCompanyCode(companyCode);
        List<User> approveWaitWorker=myCompanyWorkers.stream()
                .filter(user->user.getAuthorities().toString().equals("[ROLE_unapprovedUser]"))
                .collect(Collectors.toList());
        return approveWaitWorker;
    }
}
