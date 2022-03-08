package com.PIMCS.PIMCS.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private boolean isSecret;
  private String title;
  private String content;

  @CreationTimestamp
  private Timestamp createdAt;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userEmail")
  private User user;
  @Transient
  private String radioValue;

  @OneToOne(mappedBy = "question",fetch = FetchType.EAGER)
  private Answer answer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "companyId")
  private Company company;


  public Question() {

  }
}
