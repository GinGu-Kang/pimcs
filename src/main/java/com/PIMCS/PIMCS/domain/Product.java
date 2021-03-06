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
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productCategoryId")
  private ProductCategory productCategory;

  private String productCode;

  private String productImage;

  private int productWeight;

  private String productName;
  @Column(updatable =false)
  @CreationTimestamp
  private Timestamp createdAt;

  @UpdateTimestamp
  private Timestamp updatedate;

  @OneToMany(mappedBy = "product")
  @JsonBackReference
  List<Mat> mats = new ArrayList<>();

  public static Product findByProductId(List<Product> products, int productId){
    return products.stream()
            .filter(product -> productId == product.getId())
            .findAny()
            .orElse(null);
  }
}
