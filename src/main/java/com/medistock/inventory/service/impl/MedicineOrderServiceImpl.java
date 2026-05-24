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

    private final MedicineOrderRepository orderRepository;

    @Override
    public MedicineOrder saveOrder(MedicineOrder order) {

        return orderRepository.save(order);
    }

    @Override
    public List<MedicineOrder> getAllOrders() {

        return orderRepository.findAll();
    }

    @Override
    public Optional<MedicineOrder> getOrderById(Long id) {

        return orderRepository.findById(id);
    }

    @Override
    public List<MedicineOrder> getOrdersByStatus(
            OrderStatus status
    ) {

        return orderRepository.findByStatus(status);
    }

    @Override
    public void deleteOrder(Long id) {

        orderRepository.deleteById(id);
    }
}