package com.PIMCS.PIMCS.form;


import lombok.Data;

@Data
public class UserForm {
    private String email;
    private String companyCode;
    private String password;
//    private String password_verify;
    private String name;
    private String phone;
    private String department;
}
