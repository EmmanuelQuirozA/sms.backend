package com.monarchsolutions.sms.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.ValidatePaymentRequestExistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class PaymentRequestRepository {
    
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ObjectMapper objectMapper;

  // Create PaymentRequests
  public List<Map<String,Object>> createPaymentRequest(Long token_user_id, Long school_id, Long group_id, Long student_id, CreatePaymentRequestDTO request) throws Exception {
    // 0) if payment_month is the empty string, force it to null:
    if ("".equals(request.getPayment_month())) {
        request.setPayment_month(null);
    }

    // Convert the request DTO to a JSON string
    String payloadDataJson = objectMapper.writeValueAsString(request);

    // Create the stored procedure query
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createPaymentRequests");

    // 1) Register IN params exactly as your SP signature expects:
    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payload", String.class, ParameterMode.IN);

    // 2) Set each parameter value
    query.setParameter("token_user_id", token_user_id != null ? token_user_id.intValue() : null);
    query.setParameter("school_id", school_id != null ? school_id.intValue() : null);
    query.setParameter("group_id", group_id != null ? group_id.intValue() : null);
    query.setParameter("student_id", student_id != null ? student_id.intValue() : null);
    query.setParameter("payload", payloadDataJson);

    // 3) Execute. Because INSERT generates an “update count,” JPA sees multiple results.
    query.execute();
    
    // 4) Now grab the SELECT rows (the second resultset). JPA lets us call getResultList().
    List<Object[]> raw = query.getResultList();

    // 5) Map each Object[] → a Map with keys “payment_request_id” and “full_name”
    List<Map<String,Object>> result = new ArrayList<>();
    for (Object[] row : raw) {
      Map<String,Object> m = new LinkedHashMap<>();
      // note: MySQL returns numeric columns usually as BigInteger or BigDecimal,
      // so cast appropriately (often ((Number)row[0]).intValue())
      Number prId = (Number) row[0];
      String fullName = (String) row[1];
      m.put("payment_request_id", prId != null ? prId.longValue() : null);
      m.put("full_name", fullName);
      result.add(m);
    }

    return result;
  }

  // Get payment request Activity Logs List
	public List<ValidatePaymentRequestExistence> validatePaymentRequests(Long token_user_id, Long school_id, Long group_id, Long payment_concept_id, Date payment_month){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("validatePaymentRequests");

		// Register IN parameters
    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_concept_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_month", java.sql.Date.class, ParameterMode.IN);

		// Set the parameter values
    query.setParameter("token_user_id", token_user_id != null ? token_user_id.intValue() : null);
    query.setParameter("school_id", school_id != null ? school_id.intValue() : null);
    query.setParameter("group_id", group_id != null ? group_id.intValue() : null);
    query.setParameter("payment_concept_id", payment_concept_id != null ? payment_concept_id.intValue() : null);
    query.setParameter("payment_month", payment_month != null ? payment_month.toLocalDate() : null);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		List<?> raw = query.getResultList();

		// if SP returned a JSON string (e.g. error / “no rows”) we’ll see a String here
    if (raw.isEmpty()) {
			return Collections.emptyList();
		}
    // if the SP returned an error-json, it'll be a single-element List<String>
    if (raw.size() == 1 && raw.get(0) instanceof String) {
      return Collections.emptyList();
    }

		@SuppressWarnings("unchecked")
		List<Object[]> rows = (List<Object[]>) raw;
		List<ValidatePaymentRequestExistence> logs = new ArrayList<>(rows.size());
		for (Object[] data : rows) {
			logs.add(mapRows(data));
		}
		return logs;
	}

	private ValidatePaymentRequestExistence mapRows(Object[] data) {
		ValidatePaymentRequestExistence dto = new ValidatePaymentRequestExistence();
		dto.setStudent_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		dto.setFull_name(data[1] != null ? ((String) data[1]) : null);
		return dto;
	}
  
}
