package com.monarchsolutions.sms.controller;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.monarchsolutions.sms.dto.payments.CreatePaymentRequest;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;
import com.monarchsolutions.sms.service.PaymentService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
  
  // ← make this a String
  @Value("${app.upload-dir}")
  private String uploadDir;

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
    path = "/create",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> createPayment(
      @RequestHeader("Authorization") String authHeader,
      @RequestPart("request") CreatePaymentRequest request,   // JSON part
      @RequestPart(value = "receipt", required = false) MultipartFile receipt,
      @RequestParam(defaultValue = "es") String lang
  ) throws IOException {

    // 1) Normalize “YYYY-MM” → “YYYY-MM-01”
    String pm = request.getPayment_month();
    if (pm != null && pm.matches("\\d{4}-\\d{2}")) {
      request.setPayment_month(pm + "-01");
    }

    // 2) If there’s a receipt, save it to your protected area
    if (receipt != null && !receipt.isEmpty()) {
      try {
          // a) Clean the original filename
          String original = StringUtils.cleanPath(receipt.getOriginalFilename());
          
          // b) Build a prefix using today’s date (YYYY-MM-DD) and the student ID
          String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);  // e.g. "2025-05-19"
          String prefix = date + "-" + request.getStudent_id();  // e.g. "2025-05-19-317"
          
          // c) Prepare protected directory
          Path uploadPath = Paths.get(uploadDir);
          Files.createDirectories(uploadPath);
          
          // d) Create the stored filename: prefix + "_" + original
          String storedName = prefix + "_" + original;

          // Ensure the directory exists
          Path dir = Paths.get(uploadDir);
          if (!Files.exists(dir)) {
            Files.createDirectories(dir);
          }

          // Resolve target and copy the file
          Path target = dir.resolve(storedName);
          try (InputStream in = receipt.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
          }

          // e) Save the file
          receipt.transferTo(target.toFile());
          
          // f) Store back into your DTO
          request.setReceipt_file_name(original);
          // receipt_file_path is the URL path your clients will use:
          request.setReceipt_path("/protectedfiles/" + storedName);
      } catch (IOException e) {
          // Log the real error
          log.error("Error saving receipt file to {}", uploadDir, e);
          String err = String.format(
            "{\"title\":\"Upload Failed\",\"message\":\"%s\",\"type\":\"error\"}",
            e.getMessage().replace("\"","'")
          );
          return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .contentType(MediaType.APPLICATION_JSON)
              .body(err);
      }
    }

    // 3) Auth extraction
    String token = authHeader.substring(7);
    Long userId = jwtUtil.extractUserId(token);

    // 4) Delegate to your service / stored procedure
    String jsonResult = paymentService.createPayment(userId, request, lang);

    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(jsonResult);
  };

  // Endpoint to update an existing user.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PutMapping("/update/{payment_id}")
  public ResponseEntity<String> updatePayment(@RequestBody UpdatePaymentDTO request,
                                              @RequestHeader("Authorization") String authHeader,
                                              @RequestParam(defaultValue = "es") String lang,
                                              @PathVariable("payment_id") Long payment_id) {
    try {
      request.setPayment_id(payment_id);
      // Extract the token (remove "Bearer " prefix)
      String token = authHeader.substring(7);
      Long responsible_user_id = jwtUtil.extractUserId(token);
      // Call the service method (which will hash the password and pass the JSON data to the SP)
      String jsonResponse = paymentService.updatePayment(responsible_user_id, payment_id, request, lang);
      return ResponseEntity.ok(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}