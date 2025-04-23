package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class PaymentRequestListResponse {
  private Long payment_request_id;
  private Long payment_type_id;
  private BigDecimal pr_amount;
  private LocalDateTime pr_created_at;
  private LocalDateTime pr_pay_by;
  private String pr_comments;
  private BigDecimal late_fee;
  private String fee_type;
  private Integer late_fee_frequency;
  private Date payment_month;
  private Integer payment_status_id;
  private String ps_pr_name;
  private String pt_name;
  private BigDecimal late_fee_total;
  private Integer late_periods;
  private BigDecimal to_pay;
  private LocalDateTime pay_created_at;
  private BigDecimal pay_amount;
  private String ps_pay_name;
  private Long student_id;
  private Long user_id;
  private String payment_reference;
  private String generation;
  private String email;
  private String personal_email;
  private String student_full_name;
  private String phone_number;
  private String school_description;
  private String scholar_level_name;
  private Boolean g_enabled;
  private Boolean u_enabled;
  private Boolean sc_enabled;
  private String school_status;
  private String user_status;
  private String group_status;
  private String grade_group;
  private String validator_full_name;
  private String validator_phone_number;
  private String validator_username;
  
  public Long getPayment_request_id() {
    return payment_request_id;
  }
  public void setPayment_request_id(Long payment_request_id) {
    this.payment_request_id = payment_request_id;
  }
  public Long getPayment_type_id() {
    return payment_type_id;
  }
  public void setPayment_type_id(Long payment_type_id) {
    this.payment_type_id = payment_type_id;
  }
  public BigDecimal getPr_amount() {
    return pr_amount;
  }
  public void setPr_amount(BigDecimal pr_amount) {
    this.pr_amount = pr_amount;
  }
  public LocalDateTime getPr_created_at() {
    return pr_created_at;
  }
  public void setPr_created_at(LocalDateTime pr_created_at) {
    this.pr_created_at = pr_created_at;
  }
  public LocalDateTime getPr_pay_by() {
    return pr_pay_by;
  }
  public void setPr_pay_by(LocalDateTime pr_pay_by) {
    this.pr_pay_by = pr_pay_by;
  }
  public String getPr_comments() {
    return pr_comments;
  }
  public void setPr_comments(String pr_comments) {
    this.pr_comments = pr_comments;
  }
  public BigDecimal getLate_fee() {
    return late_fee;
  }
  public void setLate_fee(BigDecimal late_fee) {
    this.late_fee = late_fee;
  }
  public String getFee_type() {
    return fee_type;
  }
  public void setFee_type(String fee_type) {
    this.fee_type = fee_type;
  }
  public Integer getLate_fee_frequency() {
    return late_fee_frequency;
  }
  public void setLate_fee_frequency(Integer late_fee_frequency) {
    this.late_fee_frequency = late_fee_frequency;
  }
  public Date getPayment_month() {
    return payment_month;
  }
  public void setPayment_month(Date payment_month) {
    this.payment_month = payment_month;
  }
  public Integer getPayment_status_id() {
    return payment_status_id;
  }
  public void setPayment_status_id(Integer payment_status_id) {
    this.payment_status_id = payment_status_id;
  }
  public String getPs_pr_name() {
    return ps_pr_name;
  }
  public void setPs_pr_name(String ps_pr_name) {
    this.ps_pr_name = ps_pr_name;
  }
  public String getPt_name() {
    return pt_name;
  }
  public void setPt_name(String pt_name) {
    this.pt_name = pt_name;
  }
  public BigDecimal getLate_fee_total() {
    return late_fee_total;
  }
  public void setLate_fee_total(BigDecimal late_fee_total) {
    this.late_fee_total = late_fee_total;
  }
  public Integer getLate_periods() {
    return late_periods;
  }
  public void setLate_periods(Integer late_periods) {
    this.late_periods = late_periods;
  }
  public BigDecimal getTo_pay() {
    return to_pay;
  }
  public void setTo_pay(BigDecimal to_pay) {
    this.to_pay = to_pay;
  }
  public LocalDateTime getPay_created_at() {
    return pay_created_at;
  }
  public void setPay_created_at(LocalDateTime pay_created_at) {
    this.pay_created_at = pay_created_at;
  }
  public BigDecimal getPay_amount() {
    return pay_amount;
  }
  public void setPay_amount(BigDecimal pay_amount) {
    this.pay_amount = pay_amount;
  }
  public String getPs_pay_name() {
    return ps_pay_name;
  }
  public void setPs_pay_name(String ps_pay_name) {
    this.ps_pay_name = ps_pay_name;
  }
  public Long getStudent_id() {
    return student_id;
  }
  public void setStudent_id(Long student_id) {
    this.student_id = student_id;
  }
  public Long getUser_id() {
    return user_id;
  }
  public void setUser_id(Long user_id) {
    this.user_id = user_id;
  }
  public String getPayment_reference() {
    return payment_reference;
  }
  public void setPayment_reference(String payment_reference) {
    this.payment_reference = payment_reference;
  }
  public String getGeneration() {
    return generation;
  }
  public void setGeneration(String generation) {
    this.generation = generation;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPersonal_email() {
    return personal_email;
  }
  public void setPersonal_email(String personal_email) {
    this.personal_email = personal_email;
  }
  public String getStudent_full_name() {
    return student_full_name;
  }
  public void setStudent_full_name(String student_full_name) {
    this.student_full_name = student_full_name;
  }
  public String getPhone_number() {
    return phone_number;
  }
  public void setPhone_number(String phone_number) {
    this.phone_number = phone_number;
  }
  public String getSchool_description() {
    return school_description;
  }
  public void setSchool_description(String school_description) {
    this.school_description = school_description;
  }
  public String getScholar_level_name() {
    return scholar_level_name;
  }
  public void setScholar_level_name(String scholar_level_name) {
    this.scholar_level_name = scholar_level_name;
  }
  public Boolean getG_enabled() {
    return g_enabled;
  }
  public void setG_enabled(Boolean g_enabled) {
    this.g_enabled = g_enabled;
  }
  public Boolean getU_enabled() {
    return u_enabled;
  }
  public void setU_enabled(Boolean u_enabled) {
    this.u_enabled = u_enabled;
  }
  public Boolean getSc_enabled() {
    return sc_enabled;
  }
  public void setSc_enabled(Boolean sc_enabled) {
    this.sc_enabled = sc_enabled;
  }
  public String getSchool_status() {
    return school_status;
  }
  public void setSchool_status(String school_status) {
    this.school_status = school_status;
  }
  public String getUser_status() {
    return user_status;
  }
  public void setUser_status(String user_status) {
    this.user_status = user_status;
  }
  public String getGroup_status() {
    return group_status;
  }
  public void setGroup_status(String group_status) {
    this.group_status = group_status;
  }
  public String getGrade_group() {
    return grade_group;
  }
  public void setGrade_group(String grade_group) {
    this.grade_group = grade_group;
  }
  public String getValidator_full_name() {
    return validator_full_name;
  }
  public void setValidator_full_name(String validator_full_name) {
    this.validator_full_name = validator_full_name;
  }
  public String getValidator_phone_number() {
    return validator_phone_number;
  }
  public void setValidator_phone_number(String validator_phone_number) {
    this.validator_phone_number = validator_phone_number;
  }
  public String getValidator_username() {
    return validator_username;
  }
  public void setValidator_username(String validator_username) {
    this.validator_username = validator_username;
  }
  
  
}

