package com.PIMCS.PIMCS.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
  @Column(updatable =false)
  @CreationTimestamp
  private Timestamp createdAt;
  @JsonBackReference
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionId")
  private Question question;

  public Answer() {

  }


}
