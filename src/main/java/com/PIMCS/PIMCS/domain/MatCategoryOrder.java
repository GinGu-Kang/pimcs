package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class MatCategoryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private int orderCnt;
  private int pricePerDevice;
  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JoinColumn(name = "orderId")
  private MatOrder matOrder;
  private String matCategoryName;


  public MatCategoryOrder() {

  }
}
