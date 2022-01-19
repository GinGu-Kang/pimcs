package com.PIMCS.PIMCS.domain;;


public class MatCategoryOrder {

  private long id;
  private long orderId;
  private String matCategoryMatCategory;
  private long orderCnt;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }


  public String getMatCategoryMatCategory() {
    return matCategoryMatCategory;
  }

  public void setMatCategoryMatCategory(String matCategoryMatCategory) {
    this.matCategoryMatCategory = matCategoryMatCategory;
  }


  public long getOrderCnt() {
    return orderCnt;
  }

  public void setOrderCnt(long orderCnt) {
    this.orderCnt = orderCnt;
  }

}
