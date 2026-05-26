package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.StockType;
import com.medistock.inventory.repository.MedicineRepository;
import com.medistock.inventory.repository.StockTransactionRepository;
import com.medistock.inventory.service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional

public class StockTransactionServiceImpl
        implements StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public StockTransaction save(
            StockTransaction transaction
    ) {

        return transactionRepository.save(
                Objects.requireNonNull(transaction)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransaction> findAll() {

        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockTransaction> findById(Long id) {

        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransaction> findByMedicineId(Long medicineId) {

        return transactionRepository.findByMedicineId(medicineId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransaction> findByPerformedById(Long userId) {

        return transactionRepository.findByPerformedById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransaction> findByStockType(
            StockType stockType
    ) {

        return transactionRepository.findByStockType(stockType);
    }

    @Override
    public void deleteById(Long id) {

        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {

        return transactionRepository.existsById(id);
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