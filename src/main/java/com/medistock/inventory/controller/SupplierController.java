package com.medistock.inventory.controller;

import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.findAll();
    }

    @GetMapping("/active")
    public List<Supplier> getActiveSuppliers() {
        return supplierService.findActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable @NonNull Long id) {
        return supplierService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/registration-number/{registrationNumber}")
    public ResponseEntity<Supplier> getSupplierByRegistrationNumber(@PathVariable String registrationNumber) {
        return supplierService.findByRegistrationNumber(registrationNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Supplier> getSupplierByEmail(@PathVariable String email) {
        return supplierService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierService.save(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable @NonNull Long id, @RequestBody Supplier supplier) {
        return supplierService.findById(id)
                .map(existing -> {
                    supplier.setId(existing.getId());
                    return ResponseEntity.ok(supplierService.save(supplier));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable @NonNull Long id) {
        if (!supplierService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}