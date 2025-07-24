package com.ebanking.transactions_portal.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateResponse {
  @JsonProperty("success")
  private boolean success;

  @JsonProperty("quotes")
  private Map<String, BigDecimal> quotes;

  @JsonProperty("error")
  private ErrorInfo error;

  public boolean isSuccess() {
    return success;
  }

  public Map<String, BigDecimal> getQuotes() {
    return quotes;
  }

  @Data
  public static class ErrorInfo {
    @JsonProperty("code")
    private int code;

    @JsonProperty("info")
    private String info;
  }
}
