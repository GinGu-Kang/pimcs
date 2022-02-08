package com.PIMCS.PIMCS.domain;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProductCategory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String categoryName;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companyId")
  private Company company;
}
