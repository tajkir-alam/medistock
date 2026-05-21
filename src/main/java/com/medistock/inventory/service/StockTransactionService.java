package com.medistock.inventory.service;

import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.repository.StockTransactionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
@Transactional
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;

    public StockTransactionService(StockTransactionRepository stockTransactionRepository) {
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @Transactional(readOnly = true)
    public List<StockTransaction> findAll() {
        return stockTransactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StockTransaction> findById(@NonNull Long id) {
        return stockTransactionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<StockTransaction> findByMedicineId(@NonNull Long medicineId) {
        return stockTransactionRepository.findByMedicineId(medicineId);
    }

    @Transactional(readOnly = true)
    public List<StockTransaction> findByPerformedById(@NonNull Long userId) {
        return stockTransactionRepository.findByPerformedById(userId);
    }

    public StockTransaction save(@NonNull StockTransaction stockTransaction) {
        return stockTransactionRepository.save(Objects.requireNonNull(stockTransaction, "stockTransaction must not be null"));
    }

    public void deleteById(@NonNull Long id) {
        stockTransactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(@NonNull Long id) {
        return stockTransactionRepository.existsById(id);
    }
}