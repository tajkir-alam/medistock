package com.medistock.inventory.controller;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.repository.MedicineOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class MedicineOrderController {

    private final MedicineOrderRepository medicineOrderRepository;

    public MedicineOrderController(MedicineOrderRepository medicineOrderRepository) {
        this.medicineOrderRepository = medicineOrderRepository;
    }

    @GetMapping
    public List<MedicineOrder> getAllOrders() {
        return medicineOrderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineOrder> getOrderById(@PathVariable Long id) {
        return medicineOrderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    public List<MedicineOrder> getOrdersBySupplier(@PathVariable Long supplierId) {
        return medicineOrderRepository.findBySupplierId(supplierId);
    }

    @GetMapping("/status/{status}")
    public List<MedicineOrder> getOrdersByStatus(@PathVariable OrderStatus status) {
        return medicineOrderRepository.findByStatus(status);
    }

    @PostMapping
    public MedicineOrder createOrder(@RequestBody MedicineOrder medicineOrder) {
        attachParentToOrderItems(medicineOrder);
        return medicineOrderRepository.save(medicineOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineOrder> updateOrder(@PathVariable Long id, @RequestBody MedicineOrder medicineOrder) {
        return medicineOrderRepository.findById(id)
                .map(existing -> {
                    medicineOrder.setId(existing.getId());
                    attachParentToOrderItems(medicineOrder);
                    return ResponseEntity.ok(medicineOrderRepository.save(medicineOrder));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!medicineOrderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medicineOrderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void attachParentToOrderItems(MedicineOrder medicineOrder) {
        if (medicineOrder.getOrderItems() == null) {
            return;
        }

        for (OrderItem orderItem : medicineOrder.getOrderItems()) {
            orderItem.setMedicineOrder(medicineOrder);
        }
    }
}