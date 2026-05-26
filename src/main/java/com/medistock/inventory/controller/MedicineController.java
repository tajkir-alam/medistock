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

        return medicineService.getAllMedicines();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(
            @PathVariable @NonNull Long id
    ) {

        return medicineService.getMedicineById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Medicine> getMedicineBySku(
            @PathVariable String sku
    ) {

        return medicineService.getMedicineBySku(sku)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Medicine> searchMedicines(
            @RequestParam String keyword
    ) {

        return medicineService.searchMedicines(keyword);
    }

    @GetMapping("/category/{category}")
    public List<Medicine> getMedicinesByCategory(
            @PathVariable MedicineCategory category
    ) {

        return medicineService
                .getMedicinesByCategory(category);
    }

    @GetMapping("/low-stock")
    public List<Medicine> getLowStockMedicines() {

        return medicineService.getLowStockMedicines();
    }

    @GetMapping("/expired")
    public List<Medicine> getExpiredMedicines() {

        return medicineService.getExpiredMedicines();
    }

    @PostMapping
    public Medicine createMedicine(
            @RequestBody Medicine medicine
    ) {

        return medicineService.saveMedicine(medicine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(
            @PathVariable @NonNull Long id,
            @RequestBody Medicine medicine
    ) {

        return ResponseEntity.ok(
                medicineService.updateMedicine(
                        id,
                        medicine
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(
            @PathVariable @NonNull Long id
    ) {

        medicineService.deleteMedicine(id);

        return ResponseEntity.noContent().build();
    }
}