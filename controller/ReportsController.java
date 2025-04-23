package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.reports.StudentsBalanceRechargeResponse;
import com.monarchsolutions.sms.dto.reports.PaymentDetailResponse;
import com.monarchsolutions.sms.dto.reports.PaymentRequestListResponse;
import com.monarchsolutions.sms.service.ReportsService;
import com.monarchsolutions.sms.util.JwtUtil;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<?> getPaymentsPivotReport(@RequestHeader("Authorization") String authHeader,
                                                    @RequestParam(required = false) Long student_id,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method to get the report
            String reportJson = reportsService.getPaymentsPivotReport(tokenSchoolId,student_id,start_date,end_date);
            return ResponseEntity.ok(reportJson);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of paymentDetails.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/paymentdetails")
    public ResponseEntity<?> getPayments(
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long student_id,
                                        @RequestParam(required = false) Long payment_id,
                                        @RequestParam(required = false) Long payment_request_id,
                                        @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<PaymentDetailResponse> paymentDetails = reportsService.getPayments(tokenSchoolId, student_id, payment_id, payment_request_id, lang);
            return ResponseEntity.ok(paymentDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of paymentDetails.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/paymentrequestslist")
    public ResponseEntity<?> getPaymentRequests(
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long student_id,
                                        @RequestParam(required = false) Long payment_id,
                                        @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<PaymentRequestListResponse> paymentDetails = reportsService.getPaymentRequests(tokenSchoolId, student_id, payment_id, lang);
            return ResponseEntity.ok(paymentDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of paymentDetails.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/studentsbalancerecharges")
    public ResponseEntity<?> getPayments(
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long student_id,
                                        @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<StudentsBalanceRechargeResponse> paymentDetails = reportsService.getStudentsBalanceRecharge(tokenSchoolId, student_id, lang);
            return ResponseEntity.ok(paymentDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
