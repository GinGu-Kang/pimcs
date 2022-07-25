package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.domain.Company;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class SecUserCustomForm extends User  {

    private Company company;
    public SecUserCustomForm(String username, String password, Collection<? extends GrantedAuthority> authorities,Company company) {
        super(username, password, authorities);
        this.company = company;

    }

    public SecUserCustomForm(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.company = company;
    }

}
