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


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId")
  private MatOrder matOrder;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="matCategoryId")
  private MatCategory matCategory;


  public MatCategoryOrder() {

  }
}
