package com.medistock.inventory.service;

import com.medistock.inventory.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    Supplier saveSupplier(
            Supplier supplier
    );

    List<Supplier> getAllSuppliers();

    Optional<Supplier> getSupplierById(
            Long id
    );

    Optional<Supplier> getSupplierByRegistrationNumber(
            String registrationNumber
    );

    Optional<Supplier> getSupplierByEmail(
            String email
    );

    Supplier updateSupplier(
            Long id,
            Supplier supplier
    );

    void deleteSupplier(Long id);
}