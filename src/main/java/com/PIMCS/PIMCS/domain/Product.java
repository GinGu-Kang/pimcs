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
  @Column(name = "prod_code")
  private String prodCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_code")
  private Company company;

  @CreationTimestamp
  @Column(name = "createat")
  private java.sql.Timestamp creatat;

  @Column(name = "product_category_id")
  private long prodCategoryId;

  @Column(name = "prod_image")
  private String prodImage;

  @Column(name = "prod_weight")
  private long prodWeight;

  @Column(name = "prod_name")
  private String prodName;

  @OneToMany(mappedBy = "product")
  List<Mat> mats = new ArrayList<>();
}
