package com.monarchsolutions.sms.repository;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.util.*;

import javax.sql.DataSource;

import java.sql.*;

@Repository
public class PaymentRepository {

	@Autowired
	private DataSource dataSource;

  @PersistenceContext
  private EntityManager em;
    
  @PersistenceContext
  private EntityManager entityManager;
    
  @Autowired
  private ObjectMapper objectMapper;

  public PageResult<Map<String,Object>> getPayments(
		Long schoolId,
		Long studentId,
		Long paymentId,
		Long paymentRequestId,
		String studentFullName,
		String paymentReference,
		String generation,
		String gradeGroup,
		String ptName,
		String scholarLevelName,
		LocalDate paymentMonth,
		Date paymentCreatedAt,
		String lang,
		int page,
		int size,
		boolean exportAll,
		String order_by,
		String order_dir
	) throws SQLException {
		String call = "{CALL getPayments(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (studentId != null) { stmt.setInt(idx++, studentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (paymentId != null) { stmt.setInt(idx++, paymentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (paymentRequestId != null) { stmt.setInt(idx++, paymentRequestId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, studentFullName);
			stmt.setString(idx++, paymentReference);
			stmt.setString(idx++, generation);
			stmt.setString(idx++, gradeGroup);
			stmt.setString(idx++, ptName);
			stmt.setString(idx++, scholarLevelName);

			if (paymentMonth != null) { stmt.setDate(idx++, java.sql.Date.valueOf(paymentMonth)); } else { stmt.setNull(idx++, Types.DATE); }				
			// if (paymentMonth != null) { stmt.setDate(idx++, paymentMonth); } else { stmt.setNull(idx++, Types.DATE); }
			if (paymentCreatedAt != null) { stmt.setDate(idx++, paymentCreatedAt); } else { stmt.setNull(idx++, Types.DATE); }

			stmt.setString(idx++, lang);
			int offsetParam = page;     // rename 'page' var to 'offsetParam'
			int limitParam  = size;     // rename 'size' var to 'limitParam'
			// 15. offset
			if (exportAll) {
				stmt.setNull(idx++, Types.INTEGER);
				stmt.setNull(idx++, Types.INTEGER);
			} else {
				stmt.setInt(idx++, offsetParam);
				stmt.setInt(idx++, limitParam);
			}
			// 17. export_all
			stmt.setBoolean(idx++, exportAll);
			stmt.setString(idx++, order_by);
			stmt.setString(idx++, order_dir);
				
			// -- execute & read page result --
			boolean hasRs = stmt.execute();
			if (hasRs) {
				try (ResultSet rs = stmt.getResultSet()) {
					ResultSetMetaData md = rs.getMetaData();
					int cols = md.getColumnCount();
					while (rs.next()) {
						Map<String,Object> row = new LinkedHashMap<>();
						for (int c = 1; c <= cols; c++) {
							row.put(md.getColumnLabel(c), rs.getObject(c));
						}
						content.add(row);
					}
				}
			}

			// -- advance to the second resultset: total count --
			if (stmt.getMoreResults()) {
				try (ResultSet rs2 = stmt.getResultSet()) {
					if (rs2.next()) {
						totalCount = rs2.getLong(1);
					}
				}
			}
		}

		return new PageResult<>(content, totalCount, page, size);
	}


  /**
   * Calls the createPayment SP and returns its JSON_OBJECT(...) result.
   */
  public String createPayment(
    Long tokenUserId,
    com.monarchsolutions.sms.dto.payments.CreatePayment req,
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

  
    
  public String updatePayment(Long token_user_id, Long payment_id, UpdatePaymentDTO request, Boolean removeReceipt, String lang) throws Exception {
    // Convert the request DTO to a JSON string
    String paymentDataJson = objectMapper.writeValueAsString(request);

    // Create the stored procedure query
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updatePayment");

    // Register the stored procedure parameters
    query.registerStoredProcedureParameter("responsable_user_id", Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_json", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("removeReceipt", Boolean.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
    
    // Set the parameters. I
    query.setParameter("responsable_user_id", token_user_id);
    query.setParameter("payment_id", payment_id);
    query.setParameter("p_json", paymentDataJson);
    query.setParameter("removeReceipt", removeReceipt);
    query.setParameter("lang", lang);

    query.execute();
    Object result = query.getSingleResult();
    return result != null ? result.toString() : null;
  }
}