package com.medistock.inventory.repository;

import com.medistock.inventory.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByMedicineOrderId(Long orderId);

    List<OrderItem> findByMedicineId(Long medicineId);
}