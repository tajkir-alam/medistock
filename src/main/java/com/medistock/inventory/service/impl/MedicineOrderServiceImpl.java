package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.repository.MedicineOrderRepository;
import com.medistock.inventory.service.MedicineOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineOrderServiceImpl
        implements MedicineOrderService {

    private final MedicineOrderRepository
            orderRepository;

    @Override
    public MedicineOrder saveOrder(
            MedicineOrder order
    ) {

        return orderRepository.save(order);
    }

    @Override
    public List<MedicineOrder> getAllOrders() {

        return orderRepository.findAll();
    }

    @Override
    public Optional<MedicineOrder> getOrderById(
            Long id
    ) {

        return orderRepository.findById(id);
    }

    @Override
    public List<MedicineOrder> getOrdersByStatus(
            OrderStatus status
    ) {

        return orderRepository.findByStatus(status);
    }

    @Override
    public List<MedicineOrder> getOrdersBySupplierId(
            Long supplierId
    ) {

        return orderRepository.findBySupplierId(
                supplierId
        );
    }

    @Override
    public MedicineOrder updateOrder(
            Long id,
            MedicineOrder medicineOrder
    ) {

        MedicineOrder existingOrder =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        existingOrder.setSupplier(
                medicineOrder.getSupplier()
        );

        existingOrder.setEstimatedDelivery(
                medicineOrder.getEstimatedDelivery()
        );

        existingOrder.setTotalAmount(
                medicineOrder.getTotalAmount()
        );

        existingOrder.setStatus(
                medicineOrder.getStatus()
        );

        existingOrder.setOrderItems(
                medicineOrder.getOrderItems()
        );

        return orderRepository.save(
                existingOrder
        );
    }

    @Override
    public void deleteOrder(Long id) {

        orderRepository.deleteById(id);
    }
}