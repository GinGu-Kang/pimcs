package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.FindPwdVO;
import com.PIMCS.PIMCS.domain.Redis.WaitingUser;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.Redis.FindPwdVORedisRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitingUserRedisRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService  implements UserDetailsService {//implements UserDetailsService
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final WaitingUserRedisRepository waitingUserRedisRepository;
    private final EmailUtilImpl emailUtilImpl;
    private final FindPwdVORedisRepository findPwdVoRedisRepository;





    @Autowired
    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository, WaitingUserRedisRepository waitingUserRedisRepository, EmailUtilImpl emailUtilImpl, FindPwdVORedisRepository findPwdVoRedisRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.waitingUserRedisRepository = waitingUserRedisRepository;
        this.emailUtilImpl = emailUtilImpl;
        this.findPwdVoRedisRepository = findPwdVoRedisRepository;
    }



    //?????? ????????? ????????? ?????? ??????
    public void signUp(User user){
        String[] emailSednList=new String[]{user.getEmail()};
        String url="http://localhost:8080/auth/signUp/verify?verifyKey=";
        WaitingUser waitingUser=WaitingUser.builder()
                .user(user)
                .build();
        waitingUserRedisRepository.save(waitingUser);
        String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                "    <p style='margin-top: 40px;'>??????????????? PIMCS?????????.</p>\n" +
                "    <p >?????? ????????? ????????? ????????? ???????????????.</p>\n" +
                "<a href='"+url+waitingUser.getId()+"'>?????? ??????</a>"+
                "</div>\n";
        emailUtilImpl.sendEmail(
                emailSednList
                , "PIMCS?????? ??? ?????? ???????????????."
                , orderMail
                ,true
        );
    }
    public User signUpVerify(String verifyKey) {
        User user = waitingUserRedisRepository.findById(verifyKey).get().getUser();
        Optional<Company> company= companyRepository.findByCompanyCode(user.getCompanyCode());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //???????????? ?????????
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

    /*
     * ??????????????????
     */
    public void userUpdate(User user){
        userRepository.save(user);

    }
    /*
     * ?????? ???????????? ??????
     */
    public void userPwdUpdate(String email,String password){
        User user =userRepository.findByEmail(email).get();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //???????????? ?????????
        user.setPassword(encoder.encode(password));

        userRepository.save(user);
    }

    public void roleUpdate(List<Role> roleList){
        roleRepository.saveAll(roleList);
    }

    //???????????? ?????? ?????????
    public void UserRoleSave(String email, String roleName){
        UserRole userRole=UserRole.builder()
                .user(userRepository.findByEmail(email).get())
                .role(roleRepository.findByName(roleName)).build();
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
        return "?????????????????????.";
    }

    public List<Role> findRole(){
        return roleRepository.findAll();

    }
    public boolean emailCheck(String email) {
        System.out.println("asdfasdf");
        boolean isEmail = userRepository.findByEmail(email).isEmpty();
        return isEmail;
    }
    public boolean companyCheck(String companyCode) {
        boolean isCompany = companyRepository.findByCompanyCode(companyCode).isEmpty();
        return isCompany;
    }

    public User userDetail(String email){
        return userRepository.findByEmail(email).get();
    }

    public Boolean pwdFind(String email){
        System.out.println(email+"@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Optional<User> user=userRepository.findByEmail(email);
        String url="http://localhost:8080/auth/pwd/find/verify?verifyKey=";
        if(user.isPresent()){
            FindPwdVO findPwdVO =FindPwdVO.builder().email(email).build();
            findPwdVoRedisRepository.save(findPwdVO);
            String[] emailSednList=new String[]{email};
            String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                    "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                    "    <p style='margin-top: 40px;'>??????????????? PIMCS?????????.</p>\n" +
                    "    <p >?????? ????????? ???????????? ???????????? ?????? ???????????? ?????? ?????????.</p>\n" +
                    "<a href='"+url+findPwdVO.getId()+"'>?????? ??????</a>"+
                    "</div>\n";
            emailUtilImpl.sendEmail(
                    emailSednList
                    , "PIMCS?????? ??? ???????????? ?????? ???????????????."
                    , orderMail
                    ,true
            );
            return true;
        }else {
            return false;
        }

    }
    public boolean pwdFindVerify(String verifyKey,String password){
        Optional<FindPwdVO> findPwdVO=findPwdVoRedisRepository.findById(verifyKey);
            if(findPwdVO.isPresent()){
                User user = userRepository.findByEmail(findPwdVO.get().getEmail()).get();
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //???????????? ?????????
                user.setPassword(encoder.encode(password));
                userRepository.save(user);
                return true;
            }else {
                return false;
            }
    }

}
