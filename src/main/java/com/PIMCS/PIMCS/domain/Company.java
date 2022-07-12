package com.PIMCS.PIMCS.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NamedEntityGraph(name = "Company.companyWorker", attributeNodes = @NamedAttributeNode("companyWorker"))
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String companyCode;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="businessCategoryId")
  private BusinessCategory businessCategoryId;


  private String companyName;


  private String companyAddress;
  @Transient
  private String companyAddressdetail;

  @Column(updatable =false)
  @CreationTimestamp
  private java.sql.Timestamp createdAt;


  private String contactPhone;


  private String ceoEmail;
  private String ceoName;


  @OneToMany(mappedBy = "company")
  private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
  private List<User> companyWorker=new ArrayList<>();

  @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
  private List<OwnDevice> ownDeviceList=new ArrayList<>();

  public Company() {

  }
}
