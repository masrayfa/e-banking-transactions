package com.ebanking.transactions_portal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ebanking.transactions_portal.model.dto.ExchangeRateResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExchangeRateService {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${app.exchange-rate.api-url}")
  private String exchangeRateApiUrl;

  @Value("${app.exchange-rate.api-key}")
  private String apiKey;

  @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency + '_' + #date")
  public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency, LocalDate date) {
    if (fromCurrency.equals(toCurrency)) {
      return BigDecimal.ONE;
    }

    try {
      String url = String.format("%s/historical/%s?access_key=%s&currencies=%s&source=%s",
          exchangeRateApiUrl, date.toString(), apiKey, toCurrency, fromCurrency);

      ExchangeRateResponse response = restTemplate
          .getForObject(url, ExchangeRateResponse.class);

      if (response != null && response.isSuccess()) {
        return response.getQuotes()
            .get(fromCurrency + toCurrency.toString());
      }

      System.out.println("Failed to get exchange rate for {} to {} on {}, using default rate" +
          fromCurrency + toCurrency + date);
      return BigDecimal.ONE;

    } catch (Exception e) {
      System.out.println("Error fetching exchange rate" + e);
      return BigDecimal.ONE;
    }
  }
}
