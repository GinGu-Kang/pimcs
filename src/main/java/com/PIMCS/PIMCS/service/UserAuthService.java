package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService  implements UserDetailsService {//implements UserDetailsService
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;




    @Autowired
    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
    }



    public User signUp(User user) {
        Optional<Company> company= companyRepository.findByCompanyCode(user.getCompanyCode());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //비밀번호 암호화
        UserRole userRole =UserRole.builder()
                .user(user)
                .role(roleRepository.findByName("User"))
                .build();

        user.setPassword(encoder.encode(user.getPassword()));
        user.setCompany(company.get());

        userRepository.save(user);
        userRoleRepository.save(userRole);

        return user;
    }

    public Optional<User> findUser(String email){
        return userRepository.findByEmail(email);
    }

    public void deleteUser(String email){
        userRepository.deleteByEmail(email);
    }

    public String userUpdate(User user){
        boolean isUser=userRepository.findByEmail(user.getEmail()).isPresent();

        if(isUser){
            userRepository.save(user);
        }else{
            throw new UsernameNotFoundException("존재하지 않는 아이디");
        }
        return user.getEmail();
    }

    public void roleUpdate(List<Role> roleList){
        roleRepository.saveAll(roleList);
    }

    public void UserRoleSave(String email, String roleName){
        UserRole userRole=new UserRole();
        userRole.builder()
                .user(userRepository.findByEmail(email).get())
                .role(roleRepository.findByName(roleName));
        userRoleRepository.save(userRole);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));
        System.out.println(user.getAuthorities());
        return new SecUserCustomForm(user.getEmail(),user.getPassword(),user.getAuthorities(),user.getCompany());
    }


    public String deleteUserAllRole(String email){
        List<UserRole> userRoles=userRepository.findByEmail(email).get().getUserRoles();
        userRoleRepository.deleteAllInBatch(userRoles);
        return "삭제되었습니다.";
    }

    public List<Role> findRole(){
        return roleRepository.findAll();

    }
    public boolean emailCheck(String email) {
        boolean isEmail = userRepository.findByEmail(email).isEmpty();
        return isEmail;
    }
    public boolean companyCheck(String companyCode) {
        boolean isCompany = companyRepository.findByCompanyCode(companyCode).isEmpty();
        return isCompany;
    }






}
