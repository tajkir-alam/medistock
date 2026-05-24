package com.medistock.inventory.service;

import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.StockType;

import java.util.List;

public interface StockTransactionService {

    StockTransaction saveTransaction(
            StockTransaction transaction
    );

    List<StockTransaction> getAllTransactions();

    List<StockTransaction> getTransactionsByType(
            StockType stockType
    );

    void stockIn(Long medicineId, Integer quantity);

    void stockOut(Long medicineId, Integer quantity);
}