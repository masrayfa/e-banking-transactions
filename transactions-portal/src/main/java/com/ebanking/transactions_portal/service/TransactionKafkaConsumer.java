package com.ebanking.transactions_portal.service;

import com.ebanking.transactions_portal.model.entity.Transaction;
import com.ebanking.transactions_portal.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@Slf4j
public class TransactionKafkaConsumer {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.topic.transactions:transactions}", groupId = "${kafka.consumer.group:transaction-service}")
  public void consumeTransaction(@Payload String transactionJson,
      @Header(KafkaHeaders.RECEIVED_KEY) String transactionId) {
    try {
      System.out.println("Received transaction: " + transactionId);

      Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);
      transaction.setTransactionId(transactionId);
      transaction.setCreatedDate(LocalDate.now());

      transactionRepository.save(transaction);
      System.out.println("Successfully processed transaction: " + transactionId);

    } catch (Exception e) {
      System.out.println("Error processing transaction : " + transactionId + e.getMessage() + e);
    }
  }
}
