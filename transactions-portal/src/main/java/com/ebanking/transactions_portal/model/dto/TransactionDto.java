package com.ebanking.transactions_portal.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {
  private String transactionId;
  private BigDecimal amount;
  private Currency currency;
  private String accountIban;
  private LocalDate valueDate;
  private String description;
  private BigDecimal exchangeRate;
  private BigDecimal convertedAmount;
}
