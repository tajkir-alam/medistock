package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.StockType;
import com.medistock.inventory.repository.MedicineRepository;
import com.medistock.inventory.repository.StockTransactionRepository;
import com.medistock.inventory.service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class StockTransactionServiceImpl
        implements StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public StockTransaction saveTransaction(
            StockTransaction transaction
    ) {

        return transactionRepository.save(transaction);
    }

    @Override
    public List<StockTransaction> getAllTransactions() {

        return transactionRepository.findAll();
    }

    @Override
    public List<StockTransaction> getTransactionsByType(
            StockType stockType
    ) {

        return transactionRepository.findByStockType(stockType);
    }

    @Override
    public void stockIn(Long medicineId, Integer quantity) {

        Medicine medicine = medicineRepository
                .findById(medicineId)
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found"));

        medicine.setQuantity(
                medicine.getQuantity() + quantity
        );

        medicineRepository.save(medicine);
    }

    @Override
    public void stockOut(Long medicineId, Integer quantity) {

        Medicine medicine = medicineRepository
                .findById(medicineId)
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found"));

        if (medicine.getQuantity() < quantity) {

            throw new RuntimeException(
                    "Insufficient stock"
            );
        }

        medicine.setQuantity(
                medicine.getQuantity() - quantity
        );

        medicineRepository.save(medicine);
    }
}