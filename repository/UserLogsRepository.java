package com.monarchsolutions.sms.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

@Repository
public class UserLogsRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    // Get Users Activity Logs List
    public List<UserLogsListDto> getUsersActivityLog(Long tokenSchoolId, String lang){
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUsersActivityLog");

                // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<UserLogsListDto> userLogs = new ArrayList<>();

        for (Object[] data : results) {
            userLogs.add(mapStudent(data));
        }
        return userLogs;
    }

    private UserLogsListDto mapStudent(Object[] data) {
        UserLogsListDto student = new UserLogsListDto();
        student.setActivity_log_users_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        student.setResponsible_fullname(data[1] != null ? ((String) data[1]) : null);
        student.setTarget_fullname(data[2] != null ? (String) data[2] : null);
        student.setSchool_commercial_name(data[3] != null ? (String) data[3] : null);
        student.setUsername(data[4] != null ? ((String) data[4]) : null);
        student.setFull_name(data[5] != null ? ((String) data[5]) : null);
        // birth_date_formated (index 40): sometimes returned as a Date, so format it.
        student.setCreated_at(
            data[6] != null 
              ? (data[6] instanceof java.sql.Date 
                    ? new SimpleDateFormat("MM-dd-yyyy").format((java.sql.Date) data[6])
                    : data[6].toString())
              : null
        );
        student.setRole_name(data[7] != null ? ((String) data[7]) : null);
        student.setCategory(data[8] != null ? (String) data[8] : null);
        student.setLog_description(data[9] != null ? (String) data[9] : null);
        student.setLog_action(data[10] != null ? (String) data[10] : null);
        
        return student;
    }

}
