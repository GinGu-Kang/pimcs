package com.PIMCS.PIMCS.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
public class Company {
  @Id
  @Column(name = "company_code")
  private String companyCode;

  @Column(name = "business_category_name")
  private String businessCategoryName;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "company_address")
  private String companyAddress;

  @CreationTimestamp
  private java.sql.Timestamp createat;

  @Column(name = "contact_phone")
  private String contactPhone;

  @Column(name = "ceo_email")
  private String ceoEmail;

  @OneToMany(mappedBy = "company")
  private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "company")
  private List<User> companyWorker=new ArrayList<>();





}
