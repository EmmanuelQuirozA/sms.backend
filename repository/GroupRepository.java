package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.groups.CreateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GroupsListResponse;
import com.monarchsolutions.sms.dto.groups.UpdateGroupRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Get Groups List
    public List<GroupsListResponse> getGroupsList(Long tokenSchoolId, Long school_id, String lang) {
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getGroupsList");

        // Register IN parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        // Set the parameter values
        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("school_id", school_id);
        query.setParameter("lang", lang);

        // Execute the stored procedure
        query.execute();

        // Retrieve the results as a list of Object arrays
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<GroupsListResponse> groups = new ArrayList<>();

        for (Object[] data : results) {
            groups.add(mapGroup(data));
        }
        return groups;
    }

    private GroupsListResponse mapGroup(Object[] data) {
        GroupsListResponse group = new GroupsListResponse();

        group.setGroup_id(data[0] != null ? ((Number) data[0]).longValue() : null);
        group.setSchool_id(data[1] != null ? ((Number) data[1]).longValue() : null);
        group.setScholar_level_id(data[2] != null ? ((Number) data[2]).longValue() : null);
        group.setName(data[3] != null ? (String) data[3] : null);
        group.setGeneration(data[4] != null ? (String) data[4] : null);
        group.setGrade_group(data[5] != null ? (String) data[5] : null);
        group.setGrade(data[6] != null ? (String) data[6] : null);
        group.setGroup(data[7] != null ? (String) data[7] : null);
        group.setScholar_level_name(data[8] != null ? (String) data[8] : null);
        group.setEnabled(data[9] != null ? (Boolean) data[9] : null);
        group.setGroup_status(data[10] != null ? (String) data[10] : null);
        
        return group;
    }

    // Create User
    public String createGroup(Long userSchoolId, String lang, CreateGroupRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String groupDataJson = objectMapper.writeValueAsString(request);

        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createGroup");

        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("group_data", String.class, ParameterMode.IN);

        // Set the parameters. If user_school_id is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("lang", lang);
        query.setParameter("group_data", groupDataJson);

        // Execute the stored procedure
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    public String updateGroup(Long userSchoolId, Long group_id, String lang, UpdateGroupRequest request) throws Exception {
        // Convert the request DTO to a JSON string
        String groupDataJson = objectMapper.writeValueAsString(request);
        
        // Create the stored procedure query
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateGroup");
        
        // Register the stored procedure parameters
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("group_data", String.class, ParameterMode.IN);
        
        // Set the parameters. If userSchoolId is null, it will be passed as null.
        query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
        query.setParameter("p_group_id", group_id);
        query.setParameter("lang", lang);
        query.setParameter("group_data", groupDataJson);

        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

    public String changeGroupStatus(Long tokenSchoolId, Long group_id, String lang) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("changeGroupStatus");
        query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

        query.setParameter("user_school_id", tokenSchoolId);
        query.setParameter("p_group_id", group_id);
        query.setParameter("lang", lang);

        // If the group does not exist, the stored procedure will signal an error.
        query.execute();
        Object result = query.getSingleResult();
        return result != null ? result.toString() : null;
    }

}
