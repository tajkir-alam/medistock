package com.medistock.inventory.controller;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.repository.MedicineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineRepository medicineRepository;

    public MedicineController(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @GetMapping("/active")
    public List<Medicine> getActiveMedicines() {
        return medicineRepository.findByActiveTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        return medicineRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Medicine> getMedicineBySku(@PathVariable String sku) {
        return medicineRepository.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Medicine> getMedicinesBySupplier(@PathVariable Long supplierId) {
        return medicineRepository.findBySupplierId(supplierId);
    }

    @GetMapping("/category/{category}")
    public List<Medicine> getMedicinesByCategory(@PathVariable MedicineCategory category) {
        return medicineRepository.findByCategory(category);
    }

    @PostMapping
    public Medicine createMedicine(@RequestBody Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        return medicineRepository.findById(id)
                .map(existing -> {
                    medicine.setId(existing.getId());
                    return ResponseEntity.ok(medicineRepository.save(medicine));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        if (!medicineRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medicineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}