package com.medistock.inventory.controller;

import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @GetMapping("/active")
    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/registration-number/{registrationNumber}")
    public ResponseEntity<Supplier> getSupplierByRegistrationNumber(@PathVariable String registrationNumber) {
        return supplierRepository.findByRegistrationNumber(registrationNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Supplier> getSupplierByEmail(@PathVariable String email) {
        return supplierRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierRepository.findById(id)
                .map(existing -> {
                    supplier.setId(existing.getId());
                    return ResponseEntity.ok(supplierRepository.save(supplier));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        if (!supplierRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        supplierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}