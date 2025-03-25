package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.student.StudentListResponse;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ObjectMapper objectMapper;


    // Get Students List
    public List<StudentListResponse> getStudentsList(Long tokenSchoolId, Long group_id, String lang, int statusFilter){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getStudentsList");

                // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("group_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("status_filter", Integer.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("group_id", group_id);
        query.setParameter("lang", lang);
        query.setParameter("status_filter", statusFilter);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<StudentListResponse> students = new ArrayList<>();

        for (Object[] data : results) {
            students.add(mapStudent(data));
        }
        return students;
    }

    private StudentListResponse mapStudent(Object[] data) {
        StudentListResponse student = new StudentListResponse();
    
        student.setStudent_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        student.setGroup_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        student.setRegister_id(data[2] != null ? (String) data[2] : null);
        student.setPayment_reference(data[3] != null ? (String) data[3] : null);
        student.setUser_id(data[4] != null ? ((Number) data[4]).longValue() : null);
        student.setPerson_id(data[5] != null ? ((Number) data[5]).longValue() : null);
        student.setSchool_id(data[6] != null ? ((Number) data[6]).longValue() : null);
        student.setRole_id(data[7] != null ? ((Number) data[7]).longValue() : null);
        student.setEmail(data[8] != null ? (String) data[8] : null);
        student.setUsername(data[9] != null ? (String) data[9] : null);
        student.setRole_name(data[10] != null ? (String) data[10] : null);
        student.setFull_name(data[11] != null ? (String) data[11] : null);
        student.setAddress(data[12] != null ? (String) data[12] : null);
        student.setCommercial_name(data[13] != null ? (String) data[13] : null);
        student.setBusiness_name(data[14] != null ? (String) data[14] : null);
        student.setGroup_name(data[15] != null ? (String) data[15] : null);
        student.setGeneration(data[16] != null ? (String) data[16] : null);
        student.setGrade_group(data[17] != null ? (String) data[17] : null);
        student.setGrade(data[18] != null ? (String) data[18] : null);
        student.setGroup(data[19] != null ? (String) data[19] : null);
        student.setScholar_level_id(data[20] != null ? ((Number) data[20]).longValue() : null);
        student.setScholar_level_name(data[21] != null ? (String) data[21] : null);
        student.setFirst_name(data[22] != null ? (String) data[22] : null);
        student.setLast_name_father(data[23] != null ? (String) data[23] : null);
        student.setLast_name_mother(data[24] != null ? (String) data[24] : null);
        
        // p.birth_date (index 25): format to "yyyy-MM-dd"
        student.setBirth_date(
            data[25] != null 
              ? (data[25] instanceof java.sql.Date 
                    ? new SimpleDateFormat("yyyy-MM-dd").format((java.sql.Date) data[25])
                    : data[25].toString())
              : null
        );
        
        student.setPhone_number(data[26] != null ? (String) data[26] : null);
        student.setTax_id(data[27] != null ? (String) data[27] : null);
        student.setStreet(data[28] != null ? (String) data[28] : null);
        student.setExt_number(data[29] != null ? (String) data[29] : null);
        student.setInt_number(data[30] != null ? (String) data[30] : null);
        student.setSuburb(data[31] != null ? (String) data[31] : null);
        student.setLocality(data[32] != null ? (String) data[32] : null);
        student.setMunicipality(data[33] != null ? (String) data[33] : null);
        student.setState(data[34] != null ? (String) data[34] : null);
        student.setPersonal_email(data[35] != null ? (String) data[35] : null);
        student.setImage(data[36] != null ? (String) data[36] : null);
        
        // Enabled fields are numeric; assuming 1 for true.
        student.setUser_enabled(data[37] != null ? (Boolean) data[37] : null);
        student.setRole_enabled(data[38] != null ? (Boolean) data[38] : null);
        student.setSchool_enabled(data[39] != null ? (Boolean) data[39] : null);
        student.setGroup_enabled(data[40] != null ? (Boolean) data[40] : null);
        
        // birth_date_formated (index 40): sometimes returned as a Date, so format it.
        student.setBirth_date_formated(
            data[41] != null 
              ? (data[41] instanceof java.sql.Date 
                    ? new SimpleDateFormat("MM-dd-yyyy").format((java.sql.Date) data[41])
                    : data[41].toString())
              : null
        );
        
        student.setUser_status(data[42] != null ? (String) data[42] : null);
        student.setRole_status(data[43] != null ? (String) data[43] : null);
        student.setSchool_status(data[44] != null ? (String) data[44] : null);
        student.setGroup_status(data[45] != null ? (String) data[45] : null);
    
        return student;
    }

    public String updateStudent(Long userSchoolId, Long user_id, String lang, UpdateStudentRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String studentDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateStudent");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("student_data", String.class, ParameterMode.IN);
        
        // Set the parameters. If userSchoolId is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("p_user_id", user_id);
        query.setParameter("lang", lang);
        query.setParameter("student_data", studentDataJson);

        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    // Create Student
    public void createStudent(CreateStudentRequest request, Long userSchoolId, String lang) throws Exception {
        // Convert the request DTO to a JSON string
        String studentDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createStudent");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);

        // Set the parameters. If user_school_id is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("lang", lang);
        query.setParameter("user_data", studentDataJson);

        // Execute the stored procedure
        query.execute();
    }
}
