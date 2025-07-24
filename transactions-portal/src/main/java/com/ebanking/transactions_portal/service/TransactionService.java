package com.ebanking.transactions_portal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebanking.transactions_portal.model.dto.TransactionDto;
import com.ebanking.transactions_portal.model.dto.TransactionPageResponse;
import com.ebanking.transactions_portal.model.entity.Transaction;
import com.ebanking.transactions_portal.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TransactionService {

        @Autowired
        private TransactionRepository transactionRepository;

        @Autowired
        private ExchangeRateService exchangeRateService;

        public TransactionPageResponse getCustomerTransactions(
                        String customerId, int year, int month,
                        Pageable pageable, Currency baseCurrency) {

                System.out.println("Fetching transactions for customer: {}, year: {}, month: {}" + customerId +
                                year + month);

                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = startDate.plusMonths(1).minusDays(1);

                Page<Transaction> transactionPage = transactionRepository
                                .findByCustomerIdAndValueDateBetween(customerId, startDate, endDate, pageable);

                List<TransactionDto> transactionDtos = transactionPage.getContent()
                                .stream()
                                .map(transaction -> convertToDto(transaction, baseCurrency))
                                .collect(Collectors.toList());

                BigDecimal totalCredit = calculateTotalCredit(transactionDtos);
                BigDecimal totalDebit = calculateTotalDebit(transactionDtos);

                return TransactionPageResponse.builder()
                                .transactions(transactionDtos)
                                .totalCredit(totalCredit)
                                .totalDebit(totalDebit)
                                .baseCurrency(baseCurrency)
                                .pageNumber(transactionPage.getNumber())
                                .pageSize(transactionPage.getSize())
                                .totalElements(transactionPage.getTotalElements())
                                .totalPages(transactionPage.getTotalPages())
                                .hasNext(transactionPage.hasNext())
                                .hasPrevious(transactionPage.hasPrevious())
                                .build();
        }

        private TransactionDto convertToDto(Transaction transaction, Currency baseCurrency) {
                Currency transactionCurrency = Currency.getInstance(transaction.getCurrency());
                BigDecimal exchangeRate = exchangeRateService
                                .getExchangeRate(transactionCurrency, baseCurrency, transaction.getValueDate());

                BigDecimal convertedAmount = transaction.getAmount().multiply(exchangeRate);

                return TransactionDto.builder()
                                .transactionId(transaction.getTransactionId())
                                .amount(transaction.getAmount())
                                .currency(transactionCurrency)
                                .accountIban(transaction.getAccountIban())
                                .valueDate(transaction.getValueDate())
                                .description(transaction.getDescription())
                                .exchangeRate(exchangeRate)
                                .convertedAmount(convertedAmount)
                                .build();
        }

        private BigDecimal calculateTotalCredit(List<TransactionDto> transactions) {
                return transactions.stream()
                                .filter(t -> t.getConvertedAmount().compareTo(BigDecimal.ZERO) > 0)
                                .map(TransactionDto::getConvertedAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal calculateTotalDebit(List<TransactionDto> transactions) {
                return transactions.stream()
                                .filter(t -> t.getConvertedAmount().compareTo(BigDecimal.ZERO) < 0)
                                .map(TransactionDto::getConvertedAmount)
                                .map(BigDecimal::abs)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
}
