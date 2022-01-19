package com.PIMCS.PIMCS.domain;


public class Product {

  private String prodCode;
  private String companyCode;
  private java.sql.Timestamp creatat;
  private long productCategoryId;
  private String prodImage;
  private long prodWeight;
  private String prodName;


  public String getProdCode() {
    return prodCode;
  }

  public void setProdCode(String prodCode) {
    this.prodCode = prodCode;
  }


  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }


  public java.sql.Timestamp getCreatat() {
    return creatat;
  }

  public void setCreatat(java.sql.Timestamp creatat) {
    this.creatat = creatat;
  }


  public long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }


  public String getProdImage() {
    return prodImage;
  }

  public void setProdImage(String prodImage) {
    this.prodImage = prodImage;
  }


  public long getProdWeight() {
    return prodWeight;
  }

  public void setProdWeight(long prodWeight) {
    this.prodWeight = prodWeight;
  }


  public String getProdName() {
    return prodName;
  }

  public void setProdName(String prodName) {
    this.prodName = prodName;
  }

}
