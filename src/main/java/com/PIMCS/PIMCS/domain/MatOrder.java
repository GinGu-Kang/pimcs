package com.PIMCS.PIMCS.domain;


public class MatOrder {

  private long id;
  private String email;
  private String deliveryAddress;
  private String orderPhone;
  private java.sql.Timestamp hopeDeliveryDate;
  private String username;
  private java.sql.Timestamp createat;
  private long depositStatus;
  private long deliveryStatus;
  private String deliveryCode;
  private String depositName;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  public String getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(String deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }


  public String getOrderPhone() {
    return orderPhone;
  }

  public void setOrderPhone(String orderPhone) {
    this.orderPhone = orderPhone;
  }


  public java.sql.Timestamp getHopeDeliveryDate() {
    return hopeDeliveryDate;
  }

  public void setHopeDeliveryDate(java.sql.Timestamp hopeDeliveryDate) {
    this.hopeDeliveryDate = hopeDeliveryDate;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public java.sql.Timestamp getCreateat() {
    return createat;
  }

  public void setCreateat(java.sql.Timestamp createat) {
    this.createat = createat;
  }


  public long getDepositStatus() {
    return depositStatus;
  }

  public void setDepositStatus(long depositStatus) {
    this.depositStatus = depositStatus;
  }


  public long getDeliveryStatus() {
    return deliveryStatus;
  }

  public void setDeliveryStatus(long deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }


  public String getDeliveryCode() {
    return deliveryCode;
  }

  public void setDeliveryCode(String deliveryCode) {
    this.deliveryCode = deliveryCode;
  }


  public String getDepositName() {
    return depositName;
  }

  public void setDepositName(String depositName) {
    this.depositName = depositName;
  }

}
