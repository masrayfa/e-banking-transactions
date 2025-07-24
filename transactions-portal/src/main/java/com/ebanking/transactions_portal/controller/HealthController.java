package com.ebanking.transactions_portal.controller;

import com.ebanking.transactions_portal.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {

  @Autowired
  private TransactionRepository transactionRepository;

  @GetMapping("/database")
  public ResponseEntity<Map<String, Object>> checkDatabaseHealth() {
    Map<String, Object> response = new HashMap<>();

    try {
      long count = transactionRepository.count();

      response.put("status", "UP");
      response.put("database", "Connected");
      response.put("total_transactions", count);
      response.put("timestamp", LocalDateTime.now());

      System.out.println("Database health check passed");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("status", "DOWN");
      response.put("database", "Connection failed");
      response.put("error", e.getMessage());
      response.put("timestamp", LocalDateTime.now());

      System.out.println("Database health check failed" + e);
      return ResponseEntity.status(503).body(response);
    }
  }
}
