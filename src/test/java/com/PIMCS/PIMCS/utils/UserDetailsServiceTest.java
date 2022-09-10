package com.PIMCS.PIMCS.utils;

import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsServiceTest {

    public SecUserCustomForm getPrincipalDetails(User user){
        return new SecUserCustomForm(user.getEmail(),user.getPassword(),getAuthorities(user.getUserRoles()),user.getCompany());
    }

    public Collection<? extends GrantedAuthority> getAuthorities(List<UserRole> userRoleList) {
        Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>(); //List인 이유 : 여러개의 권한을 가질 수 있다
        if (userRoleList.isEmpty()){
            roles.add(new SimpleGrantedAuthority("ROLE_unapprovedUser"));
        }else{
            for (UserRole userRole:userRoleList) {
                roles.add(new SimpleGrantedAuthority("ROLE_"+userRole.getRole().getName()));
            }
        }
        return roles;
    }
}
