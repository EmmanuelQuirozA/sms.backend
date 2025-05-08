package com.monarchsolutions.sms.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class PaymentRepository {

  @PersistenceContext
  private EntityManager em;

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
}