package com.PIMCS.PIMCS.domain;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;


@Getter
@Setter
@Entity
@NamedEntityGraph(name="User.userRoles",attributeNodes = @NamedAttributeNode("userRoles"))
@Table(name="User")
public class User implements UserDetails {//implements UserDetails
    @Id
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_code")
    private Company company;
    private String password;
    private String name;
    private String phone;
    private String department;

    @CreationTimestamp
    private Timestamp creatat;
    @UpdateTimestamp
    private Timestamp updatedate;
    private Boolean enabled;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();



    /*
    권한을 ArrayList로 받아오는곳
    권한이 없으면 회사승인대기 중인 유저로 unapprovedUser 부여
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>(); //List인 이유 : 여러개의 권한을 가질 수 있다

        if (userRoles.isEmpty()){
            roles.add(new SimpleGrantedAuthority("ROLE_unapprovedUser"));
        }else{
            for (UserRole userRole:userRoles) {
                roles.add(new SimpleGrantedAuthority("ROLE_"+userRole.getRole().getName()));
            }
        }
        return roles;
    }
    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

