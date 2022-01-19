package com.PIMCS.PIMCS.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String userEmail;
  private long userManagement;
  private long matManagement;
  private long categoryManagement;

}
