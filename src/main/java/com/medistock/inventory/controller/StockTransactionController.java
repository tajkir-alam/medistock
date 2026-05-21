package com.medistock.inventory.controller;

import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.repository.StockTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-transactions")
public class StockTransactionController {

    private final StockTransactionRepository stockTransactionRepository;

    public StockTransactionController(StockTransactionRepository stockTransactionRepository) {
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @GetMapping
    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockTransaction> getTransactionById(@PathVariable Long id) {
        return stockTransactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    public List<StockTransaction> getTransactionsByMedicine(@PathVariable Long medicineId) {
        return stockTransactionRepository.findByMedicineId(medicineId);
    }

    @GetMapping("/user/{userId}")
    public List<StockTransaction> getTransactionsByUser(@PathVariable Long userId) {
        return stockTransactionRepository.findByPerformedById(userId);
    }

    @PostMapping
    public StockTransaction createTransaction(@RequestBody StockTransaction stockTransaction) {
        return stockTransactionRepository.save(stockTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockTransaction> updateTransaction(@PathVariable Long id, @RequestBody StockTransaction stockTransaction) {
        return stockTransactionRepository.findById(id)
                .map(existing -> {
                    stockTransaction.setId(existing.getId());
                    return ResponseEntity.ok(stockTransactionRepository.save(stockTransaction));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!stockTransactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stockTransactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}