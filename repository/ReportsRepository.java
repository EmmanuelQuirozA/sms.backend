package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.reports.PayDetailResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ReportsRepository {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public String getPaymentsPivotReport(Long schoolId) throws Exception {
        String sql = "CALL getPaymentsPivotReport(?)";
        List<Map<String, Object>> list = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setInt(1, schoolId.intValue());
            boolean hasResultSet = stmt.execute();
            
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        // Use LinkedHashMap to preserve column order.
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
    public List<PayDetailResponse> getPayDetails(Long tokenSchoolId, Long payment_id, String lang){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPayDetails");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("payment_id", payment_id);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<PayDetailResponse> payDetails = new ArrayList<>();

        for (Object[] data : results) {
            payDetails.add(mapPayDetails(data));
        }
        return payDetails;
    }

    private PayDetailResponse mapPayDetails(Object[] data) {
        PayDetailResponse payDetail = new PayDetailResponse();
        payDetail.setPayment_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        payDetail.setSchool_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        payDetail.setPayment_month(data[2] != null ? data[2].toString() : null);
        payDetail.setAmount(data[3] != null ? data[3].toString() : null);
        payDetail.setValidated(data[4] != null ? data[4].toString() : null);
        payDetail.setValidated_at(data[5] != null ? data[5].toString() : null);
        payDetail.setPay_created_at(data[6] != null ? data[6].toString() : null);
        payDetail.setUpdated_at(data[7] != null ? data[7].toString() : null);
        payDetail.setComments(data[8] != null ? data[8].toString() : null);
        payDetail.setPt_name(data[9] != null ? data[9].toString() : null);
        payDetail.setPayment_reference(data[10] != null ? data[10].toString() : null);
        payDetail.setGeneration(data[11] != null ? data[11].toString() : null);
        payDetail.setEmail(data[12] != null ? data[12].toString() : null);
        payDetail.setPersonal_email(data[13] != null ? data[13].toString() : null);
        payDetail.setFull_name(data[14] != null ? data[14].toString() : null);
        payDetail.setAddress(data[15] != null ? data[15].toString() : null);
        payDetail.setPhone_number(data[16] != null ? data[16].toString() : null);
        payDetail.setSchool_description(data[17] != null ? data[17].toString() : null);
        payDetail.setScholar_level_name(data[18] != null ? data[18].toString() : null);
        payDetail.setG_enabled(data[19] != null ? (data[19] instanceof Boolean ? (Boolean)data[19] : (((Number)data[19]).intValue() == 1)) : null);
        payDetail.setU_enabled(data[20] != null ? (data[20] instanceof Boolean ? (Boolean)data[20] : (((Number)data[20]).intValue() == 1)) : null);
        payDetail.setSc_enabled(data[21] != null ? (data[21] instanceof Boolean ? (Boolean)data[21] : (((Number)data[21]).intValue() == 1)) : null);
        payDetail.setSchool_status(data[22] != null ? data[22].toString() : null);
        payDetail.setUser_status(data[23] != null ? data[23].toString() : null);
        payDetail.setGroup_status(data[24] != null ? data[24].toString() : null);
        payDetail.setPayment_status_name(data[25] != null ? data[25].toString() : null);
        payDetail.setGrade_group(data[26] != null ? data[26].toString() : null);
        payDetail.setValidator_full_name(data[27] != null ? data[27].toString() : null);
        payDetail.setValidator_phone_number(data[28] != null ? data[28].toString() : null);
        payDetail.setValidator_username(data[29] != null ? data[29].toString() : null);

        return payDetail;
    }
}
