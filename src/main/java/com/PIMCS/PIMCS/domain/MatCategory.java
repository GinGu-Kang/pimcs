package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Blob;

@Getter
@Setter
@Entity

public class MatCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String matCategory;
  private int matPrice;
  private String matInformation;
  private int maxWeight;



  public MatCategory() {

  }
}
