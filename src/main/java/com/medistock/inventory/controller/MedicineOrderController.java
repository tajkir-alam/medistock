package com.medistock.inventory.controller;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.service.MedicineOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class MedicineOrderController {

    private final MedicineOrderService medicineOrderService;

    public MedicineOrderController(MedicineOrderService medicineOrderService) {
        this.medicineOrderService = medicineOrderService;
    }

    @GetMapping
    public List<MedicineOrder> getAllOrders() {
        return medicineOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineOrder> getOrderById(@PathVariable @NonNull Long id) {
        return medicineOrderService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    public List<MedicineOrder> getOrdersBySupplier(@PathVariable @NonNull Long supplierId) {
        return medicineOrderService.findBySupplierId(supplierId);
    }

    @GetMapping("/status/{status}")
    public List<MedicineOrder> getOrdersByStatus(@PathVariable OrderStatus status) {
        return medicineOrderService.findByStatus(status);
    }

    @PostMapping
    public MedicineOrder createOrder(@RequestBody MedicineOrder medicineOrder) {
        return medicineOrderService.save(medicineOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineOrder> updateOrder(@PathVariable @NonNull Long id, @RequestBody MedicineOrder medicineOrder) {
        return medicineOrderService.findById(id)
                .map(existing -> {
                    medicineOrder.setId(existing.getId());
                    return ResponseEntity.ok(medicineOrderService.save(medicineOrder));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable @NonNull Long id) {
        if (!medicineOrderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medicineOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}