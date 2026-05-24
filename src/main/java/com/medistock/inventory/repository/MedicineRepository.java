package com.medistock.inventory.repository;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.model.enums.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findBySku(String sku);

    Optional<Medicine> findByBatchId(String batchId);

    boolean existsBySku(String sku);

    List<Medicine> findByCategory(MedicineCategory category);

    List<Medicine> findBySupplier(Supplier supplier);

    List<Medicine> findByQuantityLessThanEqual(Integer quantity);

    List<Medicine> findByExpiryDateBefore(LocalDate date);

    List<Medicine> findByMedicineNameContainingIgnoreCase(String keyword);
}