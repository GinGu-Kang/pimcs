package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class MatOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String deliveryAddress;
  private String postCode;
  private long depositStatus;
  private Date hopeDeliveryDate;
  private String depositerName;
  @CreationTimestamp
  private Timestamp createdAt;
  private int deliveryStatus;
  private String deliveryCode;
  @Transient
  private String detailAddress;
  private String pgOrderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userEmail")
  private User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="companyId")
  private Company company;

  @OneToMany(mappedBy = "matOrder",fetch = FetchType.LAZY)
  private List<MatCategoryOrder> matCategoryOrderList;



  public MatOrder() {

  }
}
