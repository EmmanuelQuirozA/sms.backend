package com.monarchsolutions.sms.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.ValidatePaymentRequestExistence;
import com.monarchsolutions.sms.service.CatalogsService;
import com.monarchsolutions.sms.service.PaymentRequestService;
import com.monarchsolutions.sms.util.JwtUtil;

@RestController
@RequestMapping("/api/payment-requests")
public class PaymentRequestController {

    @Autowired
    private PaymentRequestService paymentRequestService;

    @Autowired
    private JwtUtil jwtUtil;
    
    // Endpoint to create a new group
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createPaymentRequest(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) Long school_id,
      @RequestParam(required = false) Long group_id,
      @RequestParam(required = false) Long student_id,
      @RequestBody CreatePaymentRequestDTO request
    ) throws Exception {

          // 1) Normalize “YYYY-MM” → “YYYY-MM-01”
          String pm = request.getPayment_month();
          if (pm != null && pm.matches("\\d{4}-\\d{2}")) {
            request.setPayment_month(pm + "-01");
          }
          // Extract the token (remove "Bearer " prefix)
          String token = authHeader.substring(7);
          // Extract schoolId from the token (if available)
          Long token_user_id = jwtUtil.extractUserId(token);
          // Call the service method (which will hash the password and pass the JSON data to the SP)
          List<Map<String,Object>> createdRows = paymentRequestService.createPaymentRequest(token_user_id, school_id, group_id, student_id, request);
          return ResponseEntity.ok(createdRows);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/validate-existence")
    public ResponseEntity<List<ValidatePaymentRequestExistence>> validatePaymentRequests(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) Long school_id,
        @RequestParam(required = false) Long group_id,
        @RequestParam(required = false) Long payment_concept_id,
        @RequestParam(required = false) String payment_month  // <-- accept String here
    ) {
        // 1) Normalize “YYYY-MM” → “YYYY-MM-01” and parse to java.sql.Date
        java.sql.Date monthAsDate = null;
        if (payment_month != null) {
            String pm = payment_month;
            // if they only passed “YYYY-MM”, append “-01”
            if (pm.matches("\\d{4}-\\d{2}")) {
                pm = pm + "-01";
            }
            // Now parse “YYYY-MM-dd” into LocalDate → java.sql.Date
            LocalDate ld = LocalDate.parse(pm); // e.g. “2025-06-01”
            monthAsDate = java.sql.Date.valueOf(ld);
        }

        // 2) Extract the token and get userId
        String token = authHeader.substring(7);
        Long token_user_id = jwtUtil.extractUserId(token);

        // 3) Call your service with the parsed Date (or null)
        List<ValidatePaymentRequestExistence> results =
            paymentRequestService.validatePaymentRequests(
                token_user_id,
                school_id,
                group_id,
                payment_concept_id,
                monthAsDate
            );
        return ResponseEntity.ok(results);
    }
  
}
