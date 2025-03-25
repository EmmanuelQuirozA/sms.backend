package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.dto.user.UserListDTO;
import com.monarchsolutions.sms.service.UserService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint to create a new user
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "en") String lang) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = userService.createUser(tokenSchoolId, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to update an existing user.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin/update/{user_id}")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest request,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "en") String lang,
                                             @PathVariable("user_id") Long user_id) {
        try {
            request.setUser_id(user_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = userService.updateUser(tokenSchoolId, lang, user_id, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to toggle or change the user's status.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/update/{userId}/status")
    public ResponseEntity<?> changeUserStatus(@PathVariable("userId") Integer userId,
                                                @RequestParam(defaultValue = "en") String lang,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method 
            String jsonResponse = userService.changeUserStatus(userId, lang, tokenSchoolId);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of users.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseEntity<?> getUsersList(@RequestParam(defaultValue = "en") String lang,
                                          @RequestParam(defaultValue = "-1") int status_filter,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<UserListDTO> users = userService.getUsersList(tokenSchoolId, lang, status_filter);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
