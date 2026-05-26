package com.medistock.inventory.service;

import com.medistock.inventory.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    List<OrderItem> findAll();

    Optional<OrderItem> findById(Long id);

    List<OrderItem> findByMedicineOrderId(Long orderId);

    List<OrderItem> findByMedicineId(Long medicineId);

    OrderItem save(OrderItem orderItem);

    void deleteById(Long id);

    boolean existsById(Long id);
}