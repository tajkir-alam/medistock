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

    public SupplierController(
            SupplierService supplierService
    ) {

        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {

        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(
            @PathVariable @NonNull Long id
    ) {

        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/registration-number/{registrationNumber}")
    public ResponseEntity<Supplier>
    getSupplierByRegistrationNumber(
            @PathVariable String registrationNumber
    ) {

        return supplierService
                .getSupplierByRegistrationNumber(
                        registrationNumber
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Supplier> getSupplierByEmail(
            @PathVariable String email
    ) {

        return supplierService
                .getSupplierByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier createSupplier(
            @RequestBody Supplier supplier
    ) {

        return supplierService.saveSupplier(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable @NonNull Long id,
            @RequestBody Supplier supplier
    ) {

        return ResponseEntity.ok(
                supplierService.updateSupplier(
                        id,
                        supplier
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable @NonNull Long id
    ) {

        supplierService.deleteSupplier(id);

        return ResponseEntity.noContent().build();
    }
}