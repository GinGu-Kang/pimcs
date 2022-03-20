package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class OrderMail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String greeting;

    private String managerInfo;

    @Column(updatable =false)
    @CreationTimestamp
    private Timestamp createdAt;

    public OrderMail() {

    }
}
