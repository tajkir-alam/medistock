package com.medistock.inventory.repository;

import com.medistock.inventory.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByMedicineId(Long medicineId);

    List<StockTransaction> findByPerformedById(Long userId);
}