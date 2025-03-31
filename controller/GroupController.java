package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.groups.CreateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GroupsListResponse;
import com.monarchsolutions.sms.dto.groups.UpdateGroupRequest;
import com.monarchsolutions.sms.service.GroupService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint for retrieving the list of groups.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getGroupsList( @RequestHeader("Authorization") String authHeader,
                                            @RequestParam(required = false) Long school_id,
                                            @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<GroupsListResponse> groups = groupService.getGroupsList(tokenSchoolId, school_id, lang);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
        // Endpoint to create a new group
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "en") String lang,
                                             @RequestBody CreateGroupRequest request) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = groupService.createGroup(tokenSchoolId, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to update an existing group.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PutMapping("/update/{group_id}")
    public ResponseEntity<String> updateGroup(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable("group_id") Long group_id,
                                             @RequestParam(defaultValue = "en") String lang,
                                             @RequestBody UpdateGroupRequest request) {
        try {
            request.setGroup_id(group_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will pass the JSON data to the SP)
            String jsonResponse = groupService.updateGroup(tokenSchoolId, group_id, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to toggle or change the group's status.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/update/{group_id}/status")
    public ResponseEntity<String> changeGroupStatus( @RequestHeader("Authorization") String authHeader,
                                                    @RequestParam(defaultValue = "en") String lang,
                                                    @PathVariable("group_id") Long group_id) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            String jsonResponse = groupService.changeGroupStatus(tokenSchoolId, group_id, lang);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

}
