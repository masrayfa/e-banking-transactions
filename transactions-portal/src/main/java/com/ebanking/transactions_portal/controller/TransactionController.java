package com.ebanking.transactions_portal.controller;

import java.util.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
// Removed Authentication import - no longer needed for public access
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ebanking.transactions_portal.model.dto.TransactionPageResponse;
import com.ebanking.transactions_portal.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
@Tag(name = "Transaction API", description = "Operations for retrieving customer transactions")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @GetMapping
  @Operation(summary = "Get paginated transactions for a specific month")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<TransactionPageResponse> getTransactions(
      @Parameter(description = "Year of transactions", example = "2024") @RequestParam @Min(2020) @Max(2030) int year,

      @Parameter(description = "Month of transactions (1-12)", example = "3") @RequestParam @Min(1) @Max(12) int month,

      @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

      @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

      @Parameter(description = "Base currency for conversion", example = "GBP") @RequestParam(defaultValue = "EUR") Currency baseCurrency,

      @Parameter(description = "Customer ID", example = "customer123") @RequestParam String customerId) {

    Pageable pageable = PageRequest.of(page, size);

    TransactionPageResponse response = transactionService
        .getCustomerTransactions(customerId, year, month,
            pageable, baseCurrency);

    return ResponseEntity.ok(response);
  }
}
