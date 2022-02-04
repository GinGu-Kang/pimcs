package com.PIMCS.PIMCS.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companyId")
  private Company company;
  private String productCode;
  @CreationTimestamp
  private java.sql.Timestamp createdAt;
  private int productCategoryId;
  private String productImage;
  private int productWeight;
  private String productName;

  @OneToMany(mappedBy = "product")
  List<Mat> mats = new ArrayList<>();
}
