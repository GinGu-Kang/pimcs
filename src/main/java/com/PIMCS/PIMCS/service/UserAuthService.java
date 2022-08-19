package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.FindPwdVO;
import com.PIMCS.PIMCS.domain.Redis.WaitUser;
import com.PIMCS.PIMCS.domain.Role;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.email.EmailUtilImpl;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.Redis.FindPwdVORedisRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitUserRedisRepository;
import com.PIMCS.PIMCS.repository.RoleRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService  implements UserDetailsService {//implements UserDetailsService
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyRepository companyRepository;
    private final WaitUserRedisRepository waitUserRedisRepository;
    private final EmailUtilImpl emailUtilImpl;
    private final FindPwdVORedisRepository findPwdVoRedisRepository;





    @Autowired
    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CompanyRepository companyRepository, WaitUserRedisRepository waitUserRedisRepository, EmailUtilImpl emailUtilImpl, FindPwdVORedisRepository findPwdVoRedisRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.companyRepository = companyRepository;
        this.waitUserRedisRepository = waitUserRedisRepository;
        this.emailUtilImpl = emailUtilImpl;
        this.findPwdVoRedisRepository = findPwdVoRedisRepository;
    }



    //회사 등록후 이메일 인증 대기
    public void createUserService(User user){
        String[] emailSednList=new String[]{user.getEmail()};
        String domain="http://localhost:8080";
        String path = "/auth/user/verify";
        String parameter="?verifyKey=";
        String url = domain + path+parameter;
        WaitUser waitUser = WaitUser.builder()
                .user(user)
                .build();
        waitUserRedisRepository.save(waitUser);
        String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                "    <p style='margin-top: 40px;'>안녕하세요 PIMCS입니다.</p>\n" +
                "    <p >인증 확인을 누르면 사원이 등록됩니다.</p>\n" +
                "<a href='"+url+ waitUser.getId()+"'>인증 확인</a>"+
                "</div>\n";
        emailUtilImpl.sendEmail(
                emailSednList
                , "PIMCS에서 온 인증 메일입니다."
                , orderMail
                ,true
        );
    }
    public boolean createUserVerifyService(String verifyKey) {
        Optional<WaitUser> optionalUser = waitUserRedisRepository.findById(verifyKey);
        if (optionalUser.isPresent()){
            User user = optionalUser.get().getUser();
            Optional<Company> company= companyRepository.findByCompanyCode(user.getCompanyCode());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            UserRole userRole =UserRole.builder()
                    .user(user)
                    .role(roleRepository.findByName("User"))
                    .build();

            user.setPassword(encoder.encode(user.getPassword()));
            user.setCompany(company.get());

            userRepository.save(user);
            userRoleRepository.save(userRole);

            return true;
        }else {
            return false;
        }



    }

    public Optional<User> findUser(String email){
        return userRepository.findByEmail(email);
    }

    public void deleteUser(String email){
        userRepository.deleteByEmail(email);
    }

    /*
     * 유저정보수정
     */
    public void userUpdate(User user){
        userRepository.save(user);

    }
    /*
     * 유저 비밀번호 변경
     */
    public void userPwdUpdate(String email,String password){
        User user =userRepository.findByEmail(email).get();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //비밀번호 암호화
        user.setPassword(encoder.encode(password));

        userRepository.save(user);
    }

    public void roleUpdate(List<Role> roleList){
        roleRepository.saveAll(roleList);
    }

    //유저권한 생성 메소드
    public void UserRoleSave(String email, String roleName){
        UserRole userRole=UserRole.builder()
                .user(userRepository.findByEmail(email).get())
                .role(roleRepository.findByName(roleName)).build();
        userRoleRepository.save(userRole);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));
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

    public boolean emailCheckService(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean companyCheck(String companyCode) {
        return companyRepository.findByCompanyCode(companyCode).isEmpty();
    }

    public User userDetail(String email){
        return userRepository.findByEmail(email).get();
    }

    public Boolean pwdFind(String email){
        Optional<User> user=userRepository.findByEmail(email);
        String url="http://localhost:8080/auth/pwd/find/verify?verifyKey=";
        if(user.isPresent()){
            FindPwdVO findPwdVO =FindPwdVO.builder().email(email).build();
            findPwdVoRedisRepository.save(findPwdVO);
            String[] emailSednList=new String[]{email};
            String orderMail="<div style='text-align:center;width: 600px;flex-float:column;' >\n" +
                    "    <span style='margin-right: 205px;text-align:center;width: 188px;height: 40px;font-family: Roboto;font-size: 22px;font-weight: bold;font-stretch: normal;font-style: normal;line-height: normal;letter-spacing: normal;text-align: left;color: #4282ff;'>PIMCS</span>\n" +
                    "    <p style='margin-top: 40px;'>안녕하세요 PIMCS입니다.</p>\n" +
                    "    <p >인증 확인을 누르시면 비밀번호 변경 페이지로 안내 됩니다.</p>\n" +
                    "<a href='"+url+findPwdVO.getId()+"'>인증 확인</a>"+
                    "</div>\n";
            emailUtilImpl.sendEmail(
                    emailSednList
                    , "PIMCS에서 온 비밀번호 인증 메일입니다."
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
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //비밀번호 암호화
                user.setPassword(encoder.encode(password));
                userRepository.save(user);
                return true;
            }else {
                return false;
            }
    }

}
