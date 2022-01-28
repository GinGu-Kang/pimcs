package com.PIMCS.PIMCS.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Role {
  @Id
  private String name;
  @OneToMany(mappedBy = "role",fetch = FetchType.EAGER)
  private List<UserRole> userRoles = new ArrayList<>();

}
