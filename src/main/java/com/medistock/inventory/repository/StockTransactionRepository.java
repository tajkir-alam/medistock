package com.medistock.inventory.repository;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.StockType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByMedicine(Medicine medicine);

    List<StockTransaction> findByStockType(StockType stockType);

    List<StockTransaction> findByTransactionDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}