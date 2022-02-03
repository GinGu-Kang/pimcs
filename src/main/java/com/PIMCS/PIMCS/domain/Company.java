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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String companyCode;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="businessCategoryId")
  private BusinessCategory businessCategoryId;


  private String companyName;


  private String companyAddress;

  @CreationTimestamp
  private java.sql.Timestamp createdAt;


  private String contactPhone;


  private String ceoEmail;

  @OneToMany(mappedBy = "company")
  private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "company")
  private List<User> companyWorker=new ArrayList<>();





}
