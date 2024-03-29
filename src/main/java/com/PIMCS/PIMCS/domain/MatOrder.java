package com.PIMCS.PIMCS.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
  private int id;
  private int totalPrice;
  private int totalCnt;
  private String deliveryAddress;
  private String postCode;
  private long depositStatus;
  private Date hopeDeliveryDate;
  private String depositerName;
  @Column(updatable =false)
  @CreationTimestamp
  private Timestamp createdAt;
  private int deliveryStatus;
  private String deliveryCode;
  @Transient
  private String detailAddress;

  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JoinColumn(name = "userEmail")
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="companyId")
  @JsonIgnore
  private Company company;

  @OneToMany(mappedBy = "matOrder",fetch = FetchType.LAZY,cascade=CascadeType.ALL)
  @JsonIgnore
  private List<MatCategoryOrder> matCategoryOrderList;


  @OneToOne(mappedBy = "matOrder",fetch = FetchType.EAGER,cascade=CascadeType.ALL)
  @JsonIgnore
  private SendHistory sendHistory;



  public MatOrder() {

  }
}
