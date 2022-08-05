package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class MatCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String matCategoryName;
  private String mappingSerialCode;
  private int matPrice;
  private String matInformation;
  private int maxWeight;

  @OneToMany(mappedBy = "matCategory",fetch = FetchType.LAZY)
  private List<MatCategoryOrder> matCategoryOrderList;



  public MatCategory() {

  }
}
