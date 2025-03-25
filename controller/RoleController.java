package com.monarchsolutions.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.dto.roles.RolesListResponse;
import com.monarchsolutions.sms.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;

    // Endpoint for retrieving the list of roles.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseEntity<?> getRoles(@RequestParam(defaultValue = "en") String lang,
                                      @RequestParam(defaultValue = "1") int role_level,
                                      @RequestParam(defaultValue = "-1") int status_filter) {
        try {
            List<RolesListResponse> roles = roleService.getRoles(lang, role_level, status_filter);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
