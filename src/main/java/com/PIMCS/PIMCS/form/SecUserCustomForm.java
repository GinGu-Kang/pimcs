package com.PIMCS.PIMCS.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class SecUserCustomForm extends User  {

    private String companyCode;
    public SecUserCustomForm(String username, String password, Collection<? extends GrantedAuthority> authorities,String companyCode) {
        super(username, password, authorities);
        this.companyCode = companyCode;
    }

    public SecUserCustomForm(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.companyCode = companyCode;
    }

}
