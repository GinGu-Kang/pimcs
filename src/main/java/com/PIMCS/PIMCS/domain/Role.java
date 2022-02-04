package com.PIMCS.PIMCS.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
  private List<UserRole> userRoles = new ArrayList<>();

  public Role() {

  }
}
