package com.PIMCS.PIMCS.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;


@Data
@Entity
public class User {
    @Id
    private String email;
    private String company_code;
    private String password;
    private String name;
    private String phone;
    private String department;
    @CreationTimestamp
    private Timestamp creatat;
    @UpdateTimestamp
    private Timestamp updatedate;


}
