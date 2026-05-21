package com.medistock.inventory.controller;

import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.service.StockTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-transactions")
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    public StockTransactionController(StockTransactionService stockTransactionService) {
        this.stockTransactionService = stockTransactionService;
    }

    @GetMapping
    public List<StockTransaction> getAllTransactions() {
        return stockTransactionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockTransaction> getTransactionById(@PathVariable @NonNull Long id) {
        return stockTransactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    public List<StockTransaction> getTransactionsByMedicine(@PathVariable @NonNull Long medicineId) {
        return stockTransactionService.findByMedicineId(medicineId);
    }

    @GetMapping("/user/{userId}")
    public List<StockTransaction> getTransactionsByUser(@PathVariable @NonNull Long userId) {
        return stockTransactionService.findByPerformedById(userId);
    }

    @PostMapping
    public StockTransaction createTransaction(@RequestBody StockTransaction stockTransaction) {
        return stockTransactionService.save(stockTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockTransaction> updateTransaction(@PathVariable @NonNull Long id, @RequestBody StockTransaction stockTransaction) {
        return stockTransactionService.findById(id)
                .map(existing -> {
                    stockTransaction.setId(existing.getId());
                    return ResponseEntity.ok(stockTransactionService.save(stockTransaction));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @NonNull Long id) {
        if (!stockTransactionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stockTransactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}