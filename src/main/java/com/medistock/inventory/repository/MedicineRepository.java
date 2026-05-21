package com.medistock.inventory.repository;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Medicine> findByActiveTrue();

    List<Medicine> findBySupplierId(Long supplierId);

    List<Medicine> findByCategory(MedicineCategory category);
}