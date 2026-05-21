package com.medistock.inventory.controller;

import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.repository.OrderItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;

    public OrderItemController(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        return orderItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getItemsByOrderId(@PathVariable Long orderId) {
        return orderItemRepository.findByMedicineOrderId(orderId);
    }

    @GetMapping("/medicine/{medicineId}")
    public List<OrderItem> getItemsByMedicineId(@PathVariable Long medicineId) {
        return orderItemRepository.findByMedicineId(medicineId);
    }

    @PostMapping
    public OrderItem createOrderItem(@RequestBody OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        return orderItemRepository.findById(id)
                .map(existing -> {
                    orderItem.setId(existing.getId());
                    return ResponseEntity.ok(orderItemRepository.save(orderItem));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        if (!orderItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        orderItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}