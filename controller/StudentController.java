package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.student.StudentListResponse;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.service.StudentService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint to create a new user
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<String> createStudent(@RequestBody CreateStudentRequest request,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "en") String lang) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            studentService.createStudent(tokenSchoolId, lang, request);
            return ResponseEntity.ok("Student user created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Endpoint for retrieving the list of students.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseEntity<?> getStudentsList(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long group_id,
                                        @RequestParam(defaultValue = "en") String lang,
                                        @RequestParam(defaultValue = "-1") int status_filter) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<StudentListResponse> students = studentService.getStudentsList(tokenSchoolId, group_id, lang, status_filter);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to update an existing user.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin/update/{user_id}")
    public ResponseEntity<String> updateStudent(@RequestBody UpdateStudentRequest request,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "en") String lang,
                                             @PathVariable("user_id") Long user_id) {
        try {
            request.setUser_id(user_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = studentService.updateStudent(tokenSchoolId, user_id, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
}
