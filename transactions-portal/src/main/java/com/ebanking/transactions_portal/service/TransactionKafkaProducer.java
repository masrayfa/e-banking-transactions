package com.ebanking.transactions_portal.service;

import com.ebanking.transactions_portal.model.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TransactionKafkaProducer {

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${kafka.topic.transactions:transactions}")
  private String transactionTopic;

  public void publishTransaction(Transaction transaction) {
    try {
      log.info("Publishing transaction to Kafka: {}", transaction.getTransactionId());

      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          transactionTopic,
          transaction.getTransactionId(),
          transaction);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("Successfully published transaction [{}] with offset=[{}]",
              transaction.getTransactionId(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to publish transaction [{}]: {}",
              transaction.getTransactionId(),
              ex.getMessage());
        }
      });

    } catch (Exception e) {
      log.error("Error publishing transaction to Kafka: {}", e.getMessage(), e);
    }
  }

  public void publishTransactionStatusUpdate(String transactionId, String status) {
    try {
      String message = String.format("{\"transactionId\":\"%s\",\"status\":\"%s\"}",
          transactionId, status);

      CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
          "transaction-status-updates",
          transactionId,
          message);

      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("Published status update for transaction: {} - {}", transactionId, status);
        } else {
          log.error("Error publishing status update: {}", ex.getMessage(), ex);
        }
      });

    } catch (Exception e) {
      log.error("Error publishing status update: {}", e.getMessage(), e);
    }
  }
}
