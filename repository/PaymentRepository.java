package com.monarchsolutions.sms.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class PaymentRepository {

  @PersistenceContext
  private EntityManager em;
    
  @PersistenceContext
  private EntityManager entityManager;
    
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Calls the createPayment SP and returns its JSON_OBJECT(...) result.
   */
  public String createPayment(
    Long tokenUserId,
    com.monarchsolutions.sms.dto.payments.CreatePaymentRequest req,
    String lang
  ) {

    StoredProcedureQuery q = em
      .createStoredProcedureQuery("createPayment")
      .registerStoredProcedureParameter("p_token_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_student_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_concept_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_month", java.sql.Date.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_amount", java.math.BigDecimal.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_comments", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_request_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_through_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_receipt_path", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_receipt_file_name", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_responsable_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("lang", String.class, ParameterMode.IN)
      // this SP always returns a single row with a single column "result" (the JSON)
    ;

    q.setParameter("p_token_user_id", tokenUserId.intValue());
    q.setParameter("p_student_id", req.getStudent_id().intValue());
    q.setParameter("p_payment_concept_id", req.getPayment_concept_id());
    q.setParameter("p_payment_month",
        req.getPayment_month() != null
            ? java.sql.Date.valueOf(req.getPayment_month())
            : null);
    q.setParameter("p_amount", req.getAmount());
    q.setParameter("p_comments", req.getComments());
    q.setParameter("p_payment_request_id",
        req.getPayment_request_id() != null
            ? req.getPayment_request_id().intValue()
            : null);
    q.setParameter("p_payment_through_id", req.getPayment_through_id());
    q.setParameter("p_receipt_path", req.getReceipt_path());
    q.setParameter("p_receipt_file_name", req.getReceipt_file_name());
    // the one who’s performing the action is the same as token user
    q.setParameter("p_responsable_user_id", tokenUserId.intValue());
    q.setParameter("lang", lang);

    q.execute();

    // getSingleResult() will be a single‐element Object[], or in some MySQL/JPA
    // configurations a single String if you SELECT JSON_OBJECT(...) as result
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
        // first element is the JSON text
        return (String) ((Object[]) single)[0];
    } else {
        return (String) single;
    }
  }

  
    
  public String updatePayment(Long token_user_id, Long payment_id, UpdatePaymentDTO request, String lang) throws Exception {
    // Convert the request DTO to a JSON string
    String paymentDataJson = objectMapper.writeValueAsString(request);

    // Create the stored procedure query
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updatePayment");

    // Register the stored procedure parameters
    query.registerStoredProcedureParameter("responsable_user_id", Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_json", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
    
    // Set the parameters. I
    query.setParameter("responsable_user_id", token_user_id);
    query.setParameter("payment_id", payment_id);
    query.setParameter("p_json", paymentDataJson);
    query.setParameter("lang", lang);

    query.execute();
    Object result = query.getSingleResult();
    return result != null ? result.toString() : null;
  }
}