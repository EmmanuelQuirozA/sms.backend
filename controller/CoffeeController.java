package com.monarchsolutions.sms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monarchsolutions.sms.dto.coffe.SaleGroupDTO;
import com.monarchsolutions.sms.service.CoffeeService;
import com.monarchsolutions.sms.util.JwtUtil;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {
  private final CoffeeService service;
  private final JwtUtil jwtUtil;

  public CoffeeController(
      CoffeeService service,
      JwtUtil jwtUtil
  ) {
    this.service = service;
    this.jwtUtil = jwtUtil;
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/my-purchases")
  public ResponseEntity<List<SaleGroupDTO>> getMyPurchases(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "en") String lang
  ) {
    String token = authHeader.replaceFirst("^Bearer\\s+", "");
    Long   userId= jwtUtil.extractUserId(token);

    List<SaleGroupDTO> grouped = service.getGroupedUserCoffeePurchases(userId, lang);
    return ResponseEntity.ok(grouped);
  }
}
