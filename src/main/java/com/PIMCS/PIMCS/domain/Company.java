package com.PIMCS.PIMCS.domain;

import org.hibernate.annotations.CreationTimestamp;


public class Company {

  private String companyCode;
  private String businessCategoryName;
  private String companyName;
  private String companyAddress;
  @CreationTimestamp
  private java.sql.Timestamp createat;
  private String contactPhone;
  private String ceoEmail;


  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }


  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }


  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }


  public String getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(String companyAddress) {
    this.companyAddress = companyAddress;
  }


  public java.sql.Timestamp getCreateat() {
    return createat;
  }

  public void setCreateat(java.sql.Timestamp createat) {
    this.createat = createat;
  }


  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }


  public String getCeoEmail() {
    return ceoEmail;
  }

  public void setCeoEmail(String ceoEmail) {
    this.ceoEmail = ceoEmail;
  }

}
