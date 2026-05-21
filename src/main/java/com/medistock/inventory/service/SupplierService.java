package com.medistock.inventory.service;

import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.repository.SupplierRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Supplier> findActive() {
        return supplierRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findById(@NonNull Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findByRegistrationNumber(String registrationNumber) {
        return supplierRepository.findByRegistrationNumber(registrationNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findByEmail(String email) {
        return supplierRepository.findByEmail(email);
    }

    public Supplier save(@NonNull Supplier supplier) {
        return supplierRepository.save(Objects.requireNonNull(supplier, "supplier must not be null"));
    }

    public void deleteById(@NonNull Long id) {
        supplierRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(@NonNull Long id) {
        return supplierRepository.existsById(id);
    }
}