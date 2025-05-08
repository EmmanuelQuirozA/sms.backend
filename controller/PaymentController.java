package com.monarchsolutions.sms.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.dto.payments.CreatePaymentRequest;
import com.monarchsolutions.sms.service.PaymentService;
import com.monarchsolutions.sms.util.JwtUtil;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Example:
     * POST /api/payments/create?lang=es
     * Authorization: Bearer <token>
     *
     * Body:
     * {
     *   "student_id": 42,
     *   "payment_type_id": 3,
     *   "payment_month": "2025-05-01",
     *   "amount": 1500.00,
     *   "payment_status_id": 1,
     *   "comments": "May tuition",
     *   "payment_request_id": 7,
     *   "payment_through_id": 2
     * }
     */
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping(
      path="/create",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createPayment(
          @RequestHeader("Authorization") String authHeader,
          @RequestBody CreatePaymentRequest request,
          @RequestParam(defaultValue="en") String lang
    ) {
      // 1) normalize “YYYY-MM” → “YYYY-MM-01”
      String pm = request.getPayment_month();
      if (pm != null && pm.matches("\\d{4}-\\d{2}")) {
        request.setPayment_month(pm + "-01");
      }
      // strip "Bearer "
      String token = authHeader.substring(7);
      Long userId = jwtUtil.extractUserId(token);  // you’ll need an extractUserId()
      String jsonResult = paymentService.createPayment(userId, request, lang);
      // the SP already gives you JSON_OBJECT(…) — just return it
      return ResponseEntity
              .ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(jsonResult);
    }
}