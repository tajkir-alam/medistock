package com.medistock.inventory.service;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface MedicineOrderService {

    MedicineOrder saveOrder(MedicineOrder order);

    List<MedicineOrder> getAllOrders();

    Optional<MedicineOrder> getOrderById(Long id);

    List<MedicineOrder> getOrdersByStatus(
            OrderStatus status
    );

    void deleteOrder(Long id);
}