package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.reports.PayDetailResponse;
import com.monarchsolutions.sms.service.ReportsService;
import com.monarchsolutions.sms.util.JwtUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    // Endpoint to retrieve the payments pivot report.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/payments/report")
    public ResponseEntity<?> getPaymentsPivotReport(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method to get the report
            String reportJson = reportsService.getPaymentsPivotReport(tokenSchoolId);
            return ResponseEntity.ok(reportJson);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of payDetails.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/paydetails")
    public ResponseEntity<?> getPayDetails(
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = true) Long payment_id,
                                        @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<PayDetailResponse> payDetails = reportsService.getPayDetails(tokenSchoolId, payment_id, lang);
            return ResponseEntity.ok(payDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
