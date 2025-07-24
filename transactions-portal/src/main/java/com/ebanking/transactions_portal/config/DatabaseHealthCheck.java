package com.ebanking.transactions_portal.config;

import com.ebanking.transactions_portal.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseHealthCheck {

  @Autowired
  private TransactionRepository transactionRepository;

  @PostConstruct
  public void checkDatabaseConnection() {
    try {
      long count = transactionRepository.count();
      System.out.println("✅ Database connected successfully. Total transactions: " + count);
    } catch (Exception e) {
      System.err.println("❌ Database connection failed");
    }
  }

  public boolean isDatabaseHealthy() {
    try {
      transactionRepository.count();
      return true;
    } catch (Exception e) {
      System.out.println("Database health check failed");
      return false;
    }
  }
}
