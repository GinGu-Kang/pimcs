package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)//,cascade=CascadeType.ALL
    @JoinColumn(name = "roleId")
    private Role role;

    @ManyToOne()//cascade=CascadeType.ALL
    @JoinColumn(name = "userEmail")
    private User user;

    public UserRole() {

    }
}
