package com.monarchsolutions.sms.dto.payments;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CreatePaymentRequest {
  private Long student_id;
  private Long payment_concept_id;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private String payment_month;
  private BigDecimal amount;
  private String comments;
  private Long payment_request_id;
  private Long payment_through_id;

  public Long getStudent_id() {
    return student_id;
  }
  public void setStudent_id(Long student_id) {
    this.student_id = student_id;
  }
  public Long getPayment_concept_id() {
    return payment_concept_id;
  }
  public void setPayment_concept_id(Long payment_concept_id) {
    this.payment_concept_id = payment_concept_id;
  }
  public String getPayment_month() {
    return payment_month;
  }
  public void setPayment_month(String payment_month) {
    this.payment_month = payment_month;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  public Long getPayment_request_id() {
    return payment_request_id;
  }
  public void setPayment_request_id(Long payment_request_id) {
    this.payment_request_id = payment_request_id;
  }
  public Long getPayment_through_id() {
    return payment_through_id;
  }
  public void setPayment_through_id(Long payment_through_id) {
    this.payment_through_id = payment_through_id;
  }
}
