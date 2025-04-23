package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.UserLoginDTO;
import com.monarchsolutions.sms.dto.user.UserListDTO;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Get User by username for login
    public UserLoginDTO getUserByUsernameOrEmail(String usernameOrEmail, String lang) {
        // Create the stored procedure query (the procedure name must match exactly)
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUserByUsernameOrEmail");
        
        // Register the IN parameters
        query.registerStoredProcedureParameter("p_username_or_email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        
        // Set the parameters
        query.setParameter("p_username_or_email", usernameOrEmail);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();
        
        // Get the single result.
        Object result = query.getSingleResult();
        if (result == null) {
            return null;
        }
        
        // The SP returns a row with multiple columns.
        // Cast the result to Object[]
        Object[] data = (Object[]) result;
        UserLoginDTO user = new UserLoginDTO();
        // Mapping
        user.setUserId(data[0] != null ? ((Number) data[0]).longValue() : null);
        user.setPersonId(data[1] != null ? ((Number) data[1]).longValue() : null);
        user.setSchoolId(data[2] != null ? ((Number) data[2]).longValue() : null);
        user.setRoleId(data[3] != null ? ((Number) data[3]).longValue() : null);
        user.setEmail(data[4] != null ? (String) data[4] : null);
        user.setUsername(data[5] != null ? (String) data[5] : null);
        user.setPassword(data[6] != null ? (String) data[6] : null);
        user.setRoleName(data[7] != null ? (String) data[7] : null);
        user.setRoleNameLang(data[8] != null ? (String) data[8] : null);
        user.setFullName(data[9] != null ? (String) data[9] : null);
        user.setAddress(data[10] != null ? (String) data[10] : null);
        user.setCommercialName(data[11] != null ? (String) data[11] : null);
        user.setBusinessName(data[12] != null ? (String) data[12] : null);
        user.setFirstName(data[13] != null ? (String) data[13] : null);
        user.setLastNameFather(data[14] != null ? (String) data[14] : null);
        user.setLastNameMother(data[15] != null ? (String) data[15] : null);
        user.setBirthDate(data[16] != null ? data[16].toString() : null);
        user.setPhoneNumber(data[17] != null ? (String) data[17] : null);
        user.setTaxId(data[18] != null ? (String) data[18] : null);
        user.setExtNumber(data[19] != null ? (String) data[19] : null);
        user.setIntNumber(data[20] != null ? (String) data[20] : null);
        user.setSuburb(data[21] != null ? (String) data[21] : null);
        user.setLocality(data[22] != null ? (String) data[22] : null);
        user.setMunicipality(data[23] != null ? (String) data[23] : null);
        user.setState(data[24] != null ? (String) data[24] : null);
        user.setPersonalEmail(data[25] != null ? (String) data[25] : null);
        user.setImage(data[26] != null ? (String) data[26] : null);

        // Boolean status values
        user.setEnabledUser(data[27] != null ? ((Boolean) data[27]) : null);
        user.setEnabledRole(data[28] != null ? ((Boolean) data[28]) : null);
        user.setEnabledSchool(data[29] != null ? ((Boolean) data[29]) : null);

        user.setBirthDateFormated(data[30] != null ? (String) data[30] : null);

        // String status values
        user.setUserStatus(data[31] != null ? (String) data[31] : null);
        user.setRoleStatus(data[32] != null ? (String) data[32] : null);
        user.setSchoolStatus(data[33] != null ? (String) data[33] : null);
        
        return user;
    }
    
    // Get Users List
    public List<UserListDTO> getUsersList(Long tokenSchoolId, String lang, int statusFilter) {
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUsersList");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("status_filter", Integer.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("lang", lang);
        query.setParameter("status_filter", statusFilter);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<UserListDTO> users = new ArrayList<>();

        for (Object[] data : results) {
            users.add(mapUser(data));
        }
        return users;
    }

    private UserListDTO mapUser(Object[] data) {
        UserListDTO user = new UserListDTO();

        user.setUser_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        user.setPerson_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        user.setSchool_id(data[2] != null ? ((Number) data[2]).longValue() : null);
        user.setRole_id(data[3] != null ? ((Number) data[3]).longValue() : null);
        user.setEmail(data[4] != null ? (String) data[4] : null);
        user.setUsername(data[5] != null ? (String) data[5] : null);
        user.setRole_name(data[6] != null ? (String) data[6] : null);
        user.setFull_name(data[7] != null ? (String) data[7] : null);
        user.setAddress(data[8] != null ? (String) data[8] : null);
        user.setCommercial_name(data[9] != null ? (String) data[9] : null);
        user.setBusiness_name(data[10] != null ? (String) data[10] : null);
        user.setFirst_name(data[11] != null ? (String) data[11] : null);
        user.setLast_name_father(data[12] != null ? (String) data[12] : null);
        user.setLast_name_mother(data[13] != null ? (String) data[13] : null);
        user.setBirth_date(
            data[14] != null 
              ? (data[14] instanceof java.sql.Date 
                    ? new SimpleDateFormat("yyyy-MM-dd").format((java.sql.Date) data[14])
                    : data[14].toString())
              : null
        );
        user.setPhone_number(data[15] != null ? (String) data[15] : null);
        user.setTax_id(data[16] != null ? (String) data[16] : null);
        user.setCurp(data[17] != null ? (String) data[17] : null);
        user.setStreet(data[18] != null ? (String) data[18] : null);
        user.setExt_number(data[19] != null ? (String) data[19] : null);
        user.setInt_number(data[20] != null ? (String) data[20] : null);
        user.setSuburb(data[21] != null ? (String) data[21] : null);
        user.setLocality(data[22] != null ? (String) data[22] : null);
        user.setMunicipality(data[23] != null ? (String) data[23] : null);
        user.setState(data[24] != null ? (String) data[24] : null);
        user.setPersonal_email(data[25] != null ? (String) data[25] : null);
        user.setImage(data[26] != null ? (String) data[26] : null);
        user.setUser_enabled(data[27] != null ? (Boolean) data[27] : null);
        user.setRole_enabled(data[28] != null ? (Boolean) data[28] : null);
        user.setSchool_enabled(data[29] != null ? (Boolean) data[29] : null);
        user.setBirth_date_formated(data[30] != null ? (String) data[30] : null);
        user.setUser_status(data[31] != null ? (String) data[31] : null);
        user.setRole_status(data[32] != null ? (String) data[32] : null);
        user.setSchool_status(data[33] != null ? (String) data[33] : null);
        user.setBalance(data[34] != null ? (BigDecimal) data[34] : null);

        return user;
    }
    
    // Create User
    public String createUser(CreateUserRequest request, Long userSchoolId, String lang, Long responsible_user_id) throws Exception {
        // Convert the request DTO to a JSON string
        String userDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createUser");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);

        // Set the parameters. If user_school_id is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("lang", lang);
        query.setParameter("responsible_user_id", responsible_user_id);
        query.setParameter("user_data", userDataJson);

        // Execute the stored procedure
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }
    
    public String updateUser(UpdateUserRequest request, Long userSchoolId, Long user_id, String lang, Long responsible_user_id) throws Exception {
        // Convert the request DTO to a JSON string
        String userDataJson = objectMapper.writeValueAsString(request);
        
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateUser");
        
        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);
        
        // Set the parameters. If userSchoolId is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("p_user_id", user_id);
        query.setParameter("lang", lang);
        query.setParameter("responsible_user_id", responsible_user_id);
        query.setParameter("user_data", userDataJson);

        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }
    
    public String changeUserStatus(Integer userId, String lang, Long tokenSchoolId, Long responsible_user_id) throws Exception {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("changeUserStatus");
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
        
        query.setParameter("user_school_id", tokenSchoolId != null ? tokenSchoolId.intValue() : null);
        query.setParameter("p_user_id", userId);
        query.setParameter("lang", lang);
        query.setParameter("responsible_user_id", responsible_user_id);
        
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

}
