package com.monarchsolutions.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;
import com.monarchsolutions.sms.service.UserLogsService;

@RestController
@RequestMapping("/api/usersLogs")
public class UserLogsController {
    
    @Autowired
    private UserLogsService UserLogsService;
    
        // Endpoint for retrieving the list of usersLogs.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getUsersActivityLog(
                                        // @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long school_id,
                                        @RequestParam(defaultValue = "en") String lang) {
        try {
            // String token = authHeader.substring(7);
            // Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<UserLogsListDto> usersLogs = UserLogsService.getUsersActivityLog(school_id, lang);
            return ResponseEntity.ok(usersLogs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
