package com.medistock.inventory.service;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.repository.MedicineRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Medicine> findActive() {
        return medicineRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Medicine> findById(@NonNull Long id) {
        return medicineRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Medicine> findBySku(String sku) {
        return medicineRepository.findBySku(sku);
    }

    @Transactional(readOnly = true)
    public List<Medicine> findBySupplierId(@NonNull Long supplierId) {
        return medicineRepository.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public List<Medicine> findByCategory(MedicineCategory category) {
        return medicineRepository.findByCategory(category);
    }

    public Medicine save(@NonNull Medicine medicine) {
        return medicineRepository.save(Objects.requireNonNull(medicine, "medicine must not be null"));
    }

    public void deleteById(@NonNull Long id) {
        medicineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(@NonNull Long id) {
        return medicineRepository.existsById(id);
    }
}