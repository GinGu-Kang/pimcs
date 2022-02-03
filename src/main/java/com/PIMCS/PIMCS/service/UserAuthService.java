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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<UserRole> userRoles = new ArrayList<>();
        UserRole userRole = new UserRole();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCompany(company.get());
        userRole.setUser(user);
        userRole.setRole(roleRepository.findByName("User"));
        userRoles.add(userRole);
        System.out.println(userRole.getUser().getEmail());
        user.setUserRoles(userRoles);
        userRepository.save(user);
        userRoleRepository.save(userRole);

        return user;
    }

    public Optional<User> findUser(String email){
        Optional<User> findUser = userRepository.findByEmail(email);
        return findUser;
    }
    public void deleteUser(String email){
        userRepository.deleteByEmail(email);

    }
    public String userUpdate(User user){
        userRepository.save(user);
        return user.getEmail();
    }

    public String roleUpdate(Role role){
        roleRepository.save(role);
        return role.getName();
    }

    public void UserRoleUpdate(String email, String roleName){
        UserRole userRole=new UserRole();
        userRole.setUser(userRepository.findByEmail(email).get());
        userRole.setRole(roleRepository.findByName(roleName));
        userRoleRepository.save(userRole);
    }
    //UserDetailSurvice Defaultmethod 이름 권한 이메일 설정
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));
        System.out.println(user.getAuthorities());
        return new SecUserCustomForm(user.getEmail(),user.getPassword(),user.getAuthorities(),user.getCompany().getCompanyCode());
        //org.springframework.security.core.userdetails.User
    }

    public String deleteUserAllRole(String email){
        List<UserRole> userRoles=userRepository.findByEmail(email).get().getUserRoles();
        userRoleRepository.deleteAllInBatch(userRoles);
        return "삭제되었습니다.";
    }

    public List<Role> findRole(){
        return roleRepository.findAll();

    }
    public boolean idCheck(String id) {
        boolean cnt = userRepository.findByEmail(id).isEmpty();
        return cnt;
    }






}
