package com.medistock.inventory.controller;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineService.findAll();
    }

    @GetMapping("/active")
    public List<Medicine> getActiveMedicines() {
        return medicineService.findActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable @NonNull Long id) {
        return medicineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Medicine> getMedicineBySku(@PathVariable String sku) {
        return medicineService.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Medicine> getMedicinesBySupplier(@PathVariable @NonNull Long supplierId) {
        return medicineService.findBySupplierId(supplierId);
    }

    @GetMapping("/category/{category}")
    public List<Medicine> getMedicinesByCategory(@PathVariable MedicineCategory category) {
        return medicineService.findByCategory(category);
    }

    @PostMapping
    public Medicine createMedicine(@RequestBody Medicine medicine) {
        return medicineService.save(medicine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable @NonNull Long id, @RequestBody Medicine medicine) {
        return medicineService.findById(id)
                .map(existing -> {
                    medicine.setId(existing.getId());
                    return ResponseEntity.ok(medicineService.save(medicine));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable @NonNull Long id) {
        if (!medicineService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medicineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}