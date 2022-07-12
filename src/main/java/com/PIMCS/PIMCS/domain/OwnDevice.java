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
public class OwnDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(updatable =false)
    @CreationTimestamp
    private Timestamp createdAt;
    private String serailNumber;

    @ManyToOne
    @JoinColumn(name = "compnayId")
    private Company company;



    public OwnDevice() {

    }
}
