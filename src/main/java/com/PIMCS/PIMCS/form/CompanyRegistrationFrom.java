package com.PIMCS.PIMCS.form;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Component
public class CompanyRegistrationFrom {
    private String password;
    private String passwordVerify ;
    private String name;
    private String phone;
    private String department;
    private Boolean enabled;
    private String businessCategoryName;
    private String companyName;
    private String companyAddress;
    private String companyAddressdetail;
    private String contactPhone;
    private String ceoEmail;
}
