package com.ebanking.transactions_portal.model.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionPageResponse {
  private List<TransactionDto> transactions;
  private BigDecimal totalCredit;
  private BigDecimal totalDebit;
  private Currency baseCurrency;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean hasNext;
  private boolean hasPrevious;
}
