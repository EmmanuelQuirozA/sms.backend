package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.reports.PaymentDetailResponse;
import com.monarchsolutions.sms.dto.reports.PaymentRequestListResponse;
import com.monarchsolutions.sms.dto.reports.StudentsBalanceRechargeResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

@Repository
public class ReportsRepository {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public String getPaymentsPivotReport(Long schoolId, Long student_id, LocalDate start_date, LocalDate end_date) throws Exception {
        // Update the SQL to include four parameters.
        String sql = "CALL getPaymentsPivotReport(?,?,?,?)";
        List<Map<String, Object>> list = new ArrayList<>();
    
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
    
            // Set the first parameter: schoolId.
            stmt.setInt(1, schoolId.intValue());
            
            // Set the second parameter: student_id (or SQL NULL if student_id is null).
            if (student_id != null) {
                stmt.setInt(2, student_id.intValue());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            // Set the third parameter: start_date.
            if (start_date != null) {
                stmt.setDate(3, start_date != null ? java.sql.Date.valueOf(start_date) : null);
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            
            // Set the fourth parameter: end_date.
            if (end_date != null) {
                stmt.setDate(4, end_date != null ? java.sql.Date.valueOf(end_date) : null);
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
    
            // Execute the stored procedure.
            boolean hasResultSet = stmt.execute();
            
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // Use a LinkedHashMap to preserve the column order.
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++){
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        list.add(row);
                    }
                }
            }
        }
        return objectMapper.writeValueAsString(list);
    }
    

    // Get Pay Details Logs List
    public List<PaymentDetailResponse> getPayments(Long tokenSchoolId, Long student_id, Long payment_id, Long payment_request_id, String lang){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPayments");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("payment_request_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("student_id", student_id);
        query.setParameter("payment_id", payment_id);
        query.setParameter("payment_request_id", payment_request_id);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<PaymentDetailResponse> Payments = new ArrayList<>();

        for (Object[] data : results) {
            Payments.add(mapPayments(data));
        }
        return Payments;
    }

    private PaymentDetailResponse mapPayments(Object[] data) {
        PaymentDetailResponse d = new PaymentDetailResponse();
        d.setPayment_id(data[0]!=null?((Number)data[0]).longValue():null);
        d.setSchool_id(data[1]!=null?((Number)data[1]).longValue():null);
        d.setPayment_month(data[2]!=null? data[2].toString():null);
        d.setAmount(data[3]!=null?(BigDecimal)data[3]:null);
        d.setValidated(data[4]!=null? data[4].toString():null);
        d.setValidated_at(data[5]!=null? data[5].toString():null);
        d.setPay_created_at(data[6]!=null? data[6].toString():null);
        d.setUpdated_at(data[7]!=null? data[7].toString():null);
        d.setComments(data[8]!=null? data[8].toString():null);
        d.setPt_name(data[9]!=null? data[9].toString():null);
        d.setPayment_reference(data[10]!=null?data[10].toString():null);
        d.setGeneration(data[11]!=null? data[11].toString():null);
        d.setEmail(data[12]!=null? data[12].toString():null);
        d.setPersonal_email(data[13]!=null? data[13].toString():null);
        d.setFull_name(data[14]!=null? data[14].toString():null);
        d.setAddress(data[15]!=null? data[15].toString():null);
        d.setPhone_number(data[16]!=null? data[16].toString():null);
        d.setSchool_description(data[17]!=null?data[17].toString():null);
        d.setScholar_level_name(data[18]!=null?data[18].toString():null);
        d.setG_enabled(data[19] != null ? (data[19] instanceof Boolean ? (Boolean)data[19] : (((Number)data[19]).intValue() == 1)) : null);
        d.setU_enabled(data[20] != null ? (data[20] instanceof Boolean ? (Boolean)data[20] : (((Number)data[20]).intValue() == 1)) : null);
        d.setSc_enabled(data[21] != null ? (data[21] instanceof Boolean ? (Boolean)data[21] : (((Number)data[21]).intValue() == 1)) : null);
        d.setSchool_status(data[22]!=null? data[22].toString():null);
        d.setUser_status(data[23]!=null? data[23].toString():null);
        d.setGroup_status(data[24]!=null? data[24].toString():null);
        d.setPayment_status_name(data[25]!=null?data[25].toString():null);
        d.setGrade_group(data[26]!=null? data[26].toString():null);
        d.setValidator_full_name(data[27]!=null?data[27].toString():null);
        d.setValidator_phone_number(data[28]!=null?data[28].toString():null);
        d.setValidator_username(data[29]!=null?data[29].toString():null);
        // now the PR_ fields
        d.setPayment_request_id(data[30]!=null?((Number)data[30]).longValue():null);
        d.setPr_amount(data[31]!=null?(BigDecimal)data[31]:null);
        d.setPr_created_at(data[32]!=null? data[32].toString():null);
        d.setPr_pay_by(data[33]!=null? data[33].toString():null);
        d.setPr_comments(data[34]!=null? data[34].toString():null);
        d.setLate_fee(data[35]!=null?((Number)data[35]).intValue():null);
        d.setFee_type(data[36]!=null? data[36].toString():null);
        d.setLate_fee_frequency(data[37]!=null?data[37].toString():null);
        d.setLate_fee_total(data[38]!=null?(BigDecimal)data[38]:null);
        d.setLate_periods(data[39]!=null?data[39].toString():null);
        d.setTo_pay(data[40]!=null?(BigDecimal)data[40]:null);

        return d;
    }

    // Get Pay Details Logs List
    public List<PaymentRequestListResponse> getPaymentRequests(Long tokenSchoolId, Long student_id, Long payment_id, String lang){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPaymentRequests");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("student_id", student_id);
        query.setParameter("payment_id", payment_id);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<PaymentRequestListResponse> paymentRequests = new ArrayList<>();

        for (Object[] data : results) {
            paymentRequests.add(mapPaymentRequests(data));
        }
        return paymentRequests;
    }
    
    private PaymentRequestListResponse mapPaymentRequests(Object[] data) {
        MappingConfig[] config = new MappingConfig[] {
            new MappingConfig("payment_request_id", Long.class),
            new MappingConfig("payment_type_id", Long.class),
            new MappingConfig("pr_amount", BigDecimal.class),
            new MappingConfig("pr_created_at", LocalDateTime.class),
            new MappingConfig("pr_pay_by", LocalDateTime.class),
            new MappingConfig("pr_comments", String.class),
            new MappingConfig("late_fee", BigDecimal.class),
            new MappingConfig("fee_type", String.class),
            new MappingConfig("late_fee_frequency", Integer.class),
            new MappingConfig("payment_month", Date.class),
            new MappingConfig("payment_status_id", Integer.class),
            new MappingConfig("ps_pr_name", String.class),
            new MappingConfig("pt_name", String.class),
            new MappingConfig("late_fee_total", BigDecimal.class),
            new MappingConfig("late_periods", Integer.class),
            new MappingConfig("to_pay", BigDecimal.class),
            new MappingConfig("pay_created_at", LocalDateTime.class),
            new MappingConfig("pay_amount", BigDecimal.class),
            new MappingConfig("ps_pay_name", String.class),
            new MappingConfig("student_id", Long.class),
            new MappingConfig("user_id", Long.class),
            new MappingConfig("payment_reference", String.class),
            new MappingConfig("generation", String.class),
            new MappingConfig("email", String.class),
            new MappingConfig("personal_email", String.class),
            new MappingConfig("student_full_name", String.class),
            new MappingConfig("phone_number", String.class),
            new MappingConfig("school_description", String.class),
            new MappingConfig("scholar_level_name", String.class),
            new MappingConfig("g_enabled", Boolean.class),
            new MappingConfig("u_enabled", Boolean.class),
            new MappingConfig("sc_enabled", Boolean.class),
            new MappingConfig("school_status", String.class),
            new MappingConfig("user_status", String.class),
            new MappingConfig("group_status", String.class),
            new MappingConfig("grade_group", String.class),
            new MappingConfig("validator_full_name", String.class),
            new MappingConfig("validator_phone_number", String.class),
            new MappingConfig("validator_username", String.class),
        };
    
        return MapperUtil.mapRow(data, config, PaymentRequestListResponse.class);
    }
    // Get Student Balance List
    public List<StudentsBalanceRechargeResponse> getStudentsBalanceRecharge(Long tokenSchoolId, Long user_id, String lang){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getStudentsBalanceRecharge");

        // Register IN parameters
        query.registerStoredProcedureParameter("tokenSchoolId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("tokenSchoolId", tokenSchoolId);
        query.setParameter("user_id", user_id);
        query.setParameter("lang", lang);
        
        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<StudentsBalanceRechargeResponse> balanceRecharges = new ArrayList<>();

        for (Object[] data : results) {
            balanceRecharges.add(mapBalanceRecharges(data));
        }
        return balanceRecharges;
    }

    private StudentsBalanceRechargeResponse mapBalanceRecharges(Object[] data) {
        StudentsBalanceRechargeResponse rows = new StudentsBalanceRechargeResponse();

        rows.setBalance_recharge_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        rows.setUser_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        rows.setStudent_id(data[2] != null ? ((Number) data[2]).longValue() : null);
        rows.setResponsable_user_id(data[3] != null ? ((Number) data[3]).longValue() : null);
        rows.setDate(data[4] != null ? data[4].toString() : null);
        rows.setAmount(data[5] != null ? (BigDecimal) data[5] : null);
        rows.setStudent_full_name(data[6] != null ? data[6].toString() : null);
        rows.setResponsable_full_name(data[7] != null ? data[7].toString() : null);
        rows.setSchool_description(data[8] != null ? data[8].toString() : null);
        rows.setGroup_name(data[9] != null ? data[9].toString() : null);
        rows.setGeneration(data[10] != null ? data[10].toString() : null);
        rows.setGrade_group(data[11] != null ? data[11].toString() : null);
        rows.setGrade(data[12] != null ? data[12].toString() : null);
        rows.setGroup(data[13] != null ? data[13].toString() : null);
        rows.setScholar_level_id(data[14] != null ? data[14].toString() : null);
        rows.setScholar_level_name(data[15] != null ? data[15].toString() : null);
        
        return rows;
    }
}
