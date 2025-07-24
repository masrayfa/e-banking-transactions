package com.ebanking.transactions_portal.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_customer_value_date", columnList = "customerId, valueDate"),
    @Index(name = "idx_value_date", columnList = "valueDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  private String transactionId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(nullable = false)
  private String customerId;

  @Column(nullable = false, length = 34)
  private String accountIban;

  @Column(nullable = false)
  private LocalDate valueDate;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private LocalDate createdDate;

  public Currency getCurrencyObject() {
    return Currency.getInstance(this.currency);
  }

  public String getTransactionId() {
    return transactionId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getAccountIban() {
    return accountIban;
  }

  public LocalDate getValueDate() {
    return valueDate;
  }

  public String getDescription() {
    return description;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public void setCreatedDate(LocalDate createdDate) {
    this.createdDate = createdDate;
  }
}
