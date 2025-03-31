package com.monarchsolutions.sms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.dto.user.UserListDTO;
import com.monarchsolutions.sms.service.UserService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Endpoint to create a new user or multiple users.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Object payload,
                                            @RequestHeader("Authorization") String authHeader,
                                            @RequestParam(defaultValue = "en") String lang) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            Long responsible_user_id = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);
            // Decide the validation group based on the role.
            Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
            
            List<CreateUserRequest> requests = new ArrayList<>();
            
            // Check if payload is an array or a single object.
            // (Using ObjectMapper conversion instead of "instanceof List" on the already-converted type.)
            if (objectMapper.convertValue(payload, Object.class) instanceof List) {
                requests = objectMapper.convertValue(payload, new TypeReference<List<CreateUserRequest>>() {});
            } else {
                CreateUserRequest singleRequest = objectMapper.convertValue(payload, CreateUserRequest.class);
                requests.add(singleRequest);
            }
            
            // Validate each request using the chosen validation group.
            for (CreateUserRequest req : requests) {
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(req, validationGroup);
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; "));
                    return ResponseEntity.badRequest().body(errorMessage);
                }
            }
            
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            String jsonResponse = "";
            // Process each request (mass or single upload)
            for (CreateUserRequest req : requests) {
                jsonResponse = userService.createUser(tokenSchoolId, lang, responsible_user_id, req);
            }
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to update an existing user.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PutMapping("/update/{user_id}")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request,
                                            @RequestHeader("Authorization") String authHeader,
                                            @RequestParam(defaultValue = "en") String lang,
                                            @PathVariable("user_id") Long user_id) {
        try {
            request.setUser_id(user_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            String role = jwtUtil.extractUserRole(token);
            // Decide the validation group based on the role.
            Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
            
            // Validate the payload using the chosen validation group.
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request, validationGroup);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                return ResponseEntity.badRequest().body(errorMessage);
            }
            
            // Extract data from the token.
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            Long responsible_user_id = jwtUtil.extractUserId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = userService.updateUser(tokenSchoolId, lang, user_id, responsible_user_id, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to toggle or change the user's status.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/update/{userId}/status")
    public ResponseEntity<?> changeUserStatus(@PathVariable("userId") Integer userId,
                                                @RequestParam(defaultValue = "en") String lang,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract data from the token
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            Long responsible_user_id = jwtUtil.extractUserId(token);
            // Call the service method 
            String jsonResponse = userService.changeUserStatus(userId, lang, tokenSchoolId, responsible_user_id);
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of users.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getUsersList(@RequestParam(defaultValue = "en") String lang,
                                        @RequestParam(defaultValue = "-1") int status_filter,
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) String getRole) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            String userRole = jwtUtil.extractUserRole(token);
            List<UserListDTO> users = userService.getUsersList(tokenSchoolId, lang, status_filter);
            
            // If the authenticated user is SCHOOL_ADMIN, filter out users with role_id 1 or 4.
            if ("SCHOOL_ADMIN".equalsIgnoreCase(userRole) && getRole.equalsIgnoreCase("workers")) {
                users = users.stream()
                            .filter(u -> u.getRole_id() != 1 && u.getRole_id() != 4 && u.getRole_id() != 2)
                            .collect(Collectors.toList());
            }else if ("SCHOOL_ADMIN".equalsIgnoreCase(userRole) && getRole.equalsIgnoreCase("teachers")) {
                users = users.stream()
                        .filter(u -> u.getRole_id() == 2)
                        .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
