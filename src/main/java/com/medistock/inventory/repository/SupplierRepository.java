package com.medistock.inventory.repository;

import com.medistock.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByEmail(
            String email
    );

    Optional<Supplier> findByRegistrationNumber(
            String registrationNumber
    );

    boolean existsByEmail(
            String email
    );

    boolean existsByRegistrationNumber(
            String registrationNumber
    );
}