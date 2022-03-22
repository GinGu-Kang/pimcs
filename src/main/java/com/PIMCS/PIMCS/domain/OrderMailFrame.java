package com.PIMCS.PIMCS.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class OrderMailFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String greeting;

    private String managerInfo;

    @Column(updatable =false)
    @CreationTimestamp
    private Timestamp createdAt;

    public OrderMailFrame() {

    }
}
