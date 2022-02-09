package com.PIMCS.PIMCS.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
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
  @JsonIgnore
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productCategoryId")
  @JsonIgnore
  private ProductCategory productCategory;

  private String productCode;

  private String productImage;

  private int productWeight;

  private String productName;

  @CreationTimestamp
  private java.sql.Timestamp createdAt;

  @UpdateTimestamp
  private java.sql.Timestamp updatedate;

  @OneToMany(mappedBy = "product")
  @JsonBackReference
  List<Mat> mats = new ArrayList<>();
}
