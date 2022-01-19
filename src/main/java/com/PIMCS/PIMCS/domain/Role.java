package com.sample;


public class Role {

  private long id;
  private String userEmail;
  private long userManagement;
  private long matManagement;
  private long categoryManagement;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }


  public long getUserManagement() {
    return userManagement;
  }

  public void setUserManagement(long userManagement) {
    this.userManagement = userManagement;
  }


  public long getMatManagement() {
    return matManagement;
  }

  public void setMatManagement(long matManagement) {
    this.matManagement = matManagement;
  }


  public long getCategoryManagement() {
    return categoryManagement;
  }

  public void setCategoryManagement(long categoryManagement) {
    this.categoryManagement = categoryManagement;
  }

}
