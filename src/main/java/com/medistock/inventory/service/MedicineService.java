package com.medistock.inventory.service;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;

import java.util.List;
import java.util.Optional;

public interface MedicineService {

    Medicine saveMedicine(Medicine medicine);

    List<Medicine> getAllMedicines();

    Optional<Medicine> getMedicineById(Long id);

    Optional<Medicine> getMedicineBySku(String sku);

    List<Medicine> searchMedicines(String keyword);

    List<Medicine> getMedicinesByCategory(
            MedicineCategory category
    );

    List<Medicine> getLowStockMedicines();

    List<Medicine> getExpiredMedicines();

    Medicine updateMedicine(Long id, Medicine medicine);

    void deleteMedicine(Long id);
}