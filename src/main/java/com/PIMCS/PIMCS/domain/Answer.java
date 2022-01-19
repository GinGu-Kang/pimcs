package com.PIMCS.PIMCS.domain;


public class Answer {

  private long id;
  private String answer;
  private java.sql.Timestamp creatat;
  private long questionId;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }


  public java.sql.Timestamp getCreatat() {
    return creatat;
  }

  public void setCreatat(java.sql.Timestamp creatat) {
    this.creatat = creatat;
  }


  public long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(long questionId) {
    this.questionId = questionId;
  }

}
