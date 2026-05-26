package com.medistock.inventory.service;

import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.StockType;

import java.util.List;
import java.util.Optional;

public interface StockTransactionService {

    StockTransaction save(StockTransaction transaction);

    List<StockTransaction> findAll();

    Optional<StockTransaction> findById(Long id);

    List<StockTransaction> findByMedicineId(Long medicineId);

    List<StockTransaction> findByPerformedById(Long userId);

    List<StockTransaction> findByStockType(StockType stockType);

    void deleteById(Long id);

    boolean existsById(Long id);

    void stockIn(Long medicineId, Integer quantity);

    void stockOut(Long medicineId, Integer quantity);
}