package com.PIMCS.PIMCS.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="businessCategory")
@Getter
@Setter
public class BusinessCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String categoryName;



}
