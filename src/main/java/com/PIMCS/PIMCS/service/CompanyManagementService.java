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
import java.util.Optional;
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


    public List<User> findMyCompanyWorker(String companyCode){
        Company company = companyRepository.findByCompanyCode(companyCode).get();
        List<User> myCompanyWorkers =company.getCompanyWorker();
        return myCompanyWorkers;
    }


    //회사와 대표 저장 회사코드 : UUID
    public void companyRegistration(User ceo, Company company){
        List<UserRole> userRoles = new ArrayList<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CompanyServiceUtils companyServiceUtils = new CompanyServiceUtils();

        ceo.setPassword(encoder.encode(ceo.getPassword()));
        company.setCompanyCode(companyServiceUtils.UUIDgeneration().substring(0,30));
        ceo.setCompany(company);

        for (Role role:roleRepository.findAll()
        ) {
            if(!role.getName().equals("ChiefOfPimcs")){
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



    public void saveCompany(Company company) {
        companyRepository.save(company);
    }


}
