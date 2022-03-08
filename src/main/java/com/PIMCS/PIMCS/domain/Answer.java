package com.PIMCS.PIMCS.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Answer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String comment;
  @CreationTimestamp
  private Timestamp createdAt;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionId")
  private Question question;

  public Answer() {

  }


}
