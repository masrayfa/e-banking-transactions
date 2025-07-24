package com.ebanking.transactions_portal.repository;

import com.ebanking.transactions_portal.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

  @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.valueDate BETWEEN :startDate AND :endDate ORDER BY t.valueDate DESC")
  Page<Transaction> findByCustomerIdAndValueDateBetween(
      @Param("customerId") String customerId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);
}
