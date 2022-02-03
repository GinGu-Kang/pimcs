package com.PIMCS.PIMCS.domain;


import javax.persistence.*;

@Entity
@Table(name="businessCategory")
public class BusinessCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String categoryName;



}
