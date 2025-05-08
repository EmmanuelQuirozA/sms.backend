package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {

	@Autowired
	private DataSource dataSource;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ObjectMapper objectMapper;


    // Get Students List
    public PageResult<Map<String,Object>> getStudentsList(
        Long tokenSchoolId,  
        Long student_id,
        String full_name,
        String payment_reference,
        String generation,
        String grade_group,
        Boolean status_filter,
		String lang,
		int page,
		int size,
		Boolean exportAll,
		String order_by,
		String order_dir
    ) throws SQLException {
		String call = "{CALL getStudentsList(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
		CallableStatement stmt = conn.prepareCall(call)) {

            int idx = 1;
            // 1) the IDs
            if (tokenSchoolId != null) { stmt.setInt(idx++, tokenSchoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
            if (student_id != null) { stmt.setInt(idx++, student_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

            // 2) the filters
            stmt.setString(idx++, full_name);
            stmt.setString(idx++, payment_reference);
            stmt.setString(idx++, generation);
            stmt.setString(idx++, grade_group);
            if (status_filter != null) {
                stmt.setBoolean(idx++, status_filter);
            } else {
                stmt.setNull(idx++, Types.BOOLEAN);
            }

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
    
    public String updateStudent(Long userSchoolId, Long user_id, String lang, Long responsible_user_id, UpdateStudentRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String studentDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateStudent");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("student_data", String.class, ParameterMode.IN);
        
        // Set the parameters. If userSchoolId is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("p_user_id", user_id);
        query.setParameter("lang", lang);
        query.setParameter("responsible_user_id", responsible_user_id);
        query.setParameter("student_data", studentDataJson);

        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    // Create Student
    public String createStudent(CreateStudentRequest request, Long userSchoolId, String lang, Long responsible_user_id) throws Exception {
        // Convert the request DTO to a JSON string
        String studentDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createStudent");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);

        // Set the parameters. If user_school_id is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("lang", lang);
        query.setParameter("responsible_user_id", responsible_user_id);
        query.setParameter("user_data", studentDataJson);

        // Execute the stored procedure
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }
}
