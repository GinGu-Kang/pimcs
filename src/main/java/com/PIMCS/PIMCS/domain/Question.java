package com.sample;


public class Question {

  private long id;
  private String userEmail;
  private long isSecret;
  private String title;
  private String question;
  private java.sql.Timestamp createat;
  private String companyCompanyCode;


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


  public long getIsSecret() {
    return isSecret;
  }

  public void setIsSecret(long isSecret) {
    this.isSecret = isSecret;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }


  public java.sql.Timestamp getCreateat() {
    return createat;
  }

  public void setCreateat(java.sql.Timestamp createat) {
    this.createat = createat;
  }


  public String getCompanyCompanyCode() {
    return companyCompanyCode;
  }

  public void setCompanyCompanyCode(String companyCompanyCode) {
    this.companyCompanyCode = companyCompanyCode;
  }

}
