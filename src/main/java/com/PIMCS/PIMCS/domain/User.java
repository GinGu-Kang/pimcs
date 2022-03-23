package com.PIMCS.PIMCS.domain;


import lombok.*;
import org.hibernate.annotations.BatchSize;
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
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "companyId")
    private Company company;
    private String password;
    private String name;
    private String phone;
    private String department;
    @Column(updatable =false)
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedate;
    private Boolean enabled;
    @Transient
    private String companyCode;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

    public User() {

    }


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

