package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.repository.MedicineRepository;
import com.medistock.inventory.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public Medicine saveMedicine(Medicine medicine) {

        return medicineRepository.save(medicine);
    }

    @Override
    public List<Medicine> getAllMedicines() {

        return medicineRepository.findAll();
    }

    @Override
    public Optional<Medicine> getMedicineById(Long id) {

        return medicineRepository.findById(id);
    }

    @Override
    public Optional<Medicine> getMedicineBySku(String sku) {

        return medicineRepository.findBySku(sku);
    }

    @Override
    public List<Medicine> searchMedicines(String keyword) {

        return medicineRepository
                .findByMedicineNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Medicine> getMedicinesByCategory(
            MedicineCategory category
    ) {

        return medicineRepository.findByCategory(category);
    }

    @Override
    public List<Medicine> getLowStockMedicines() {

        return medicineRepository
                .findByQuantityLessThanEqual(10);
    }

    @Override
    public List<Medicine> getExpiredMedicines() {

        return medicineRepository
                .findByExpiryDateBefore(LocalDate.now());
    }

    @Override
    public Medicine updateMedicine(Long id, Medicine medicine) {

        Medicine existingMedicine = medicineRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found"));

        existingMedicine.setMedicineName(
                medicine.getMedicineName()
        );

        existingMedicine.setGenericName(
                medicine.getGenericName()
        );

        existingMedicine.setSku(
                medicine.getSku()
        );

        existingMedicine.setBatchId(
                medicine.getBatchId()
        );

        existingMedicine.setCategory(
                medicine.getCategory()
        );

        existingMedicine.setSupplier(
                medicine.getSupplier()
        );

        existingMedicine.setQuantity(
                medicine.getQuantity()
        );

        existingMedicine.setLowStockThreshold(
                medicine.getLowStockThreshold()
        );

        existingMedicine.setUnitType(
                medicine.getUnitType()
        );

        existingMedicine.setUnitPrice(
                medicine.getUnitPrice()
        );

        existingMedicine.setExpiryDate(
                medicine.getExpiryDate()
        );

        existingMedicine.setStorageInstructions(
                medicine.getStorageInstructions()
        );

        existingMedicine.setDosageNotes(
                medicine.getDosageNotes()
        );

        existingMedicine.setActive(
                medicine.getActive()
        );

        return medicineRepository.save(existingMedicine);
    }

    @Override
    public void deleteMedicine(Long id) {

        medicineRepository.deleteById(id);
    }
}