package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.repository.OrderItemRepository;
import com.medistock.inventory.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl
        implements OrderItemService {

    private final OrderItemRepository
            orderItemRepository;

    @Override
    public List<OrderItem> findAll() {

        return orderItemRepository.findAll();
    }

    @Override
    public Optional<OrderItem> findById(
            Long id
    ) {

        return orderItemRepository.findById(id);
    }

    @Override
    public List<OrderItem>
    findByMedicineOrderId(
            Long orderId
    ) {

        return orderItemRepository
                .findByMedicineOrderId(
                        orderId
                );
    }

    @Override
    public List<OrderItem>
    findByMedicineId(
            Long medicineId
    ) {

        return orderItemRepository
                .findByMedicineId(
                        medicineId
                );
    }

    @Override
    public OrderItem save(
            OrderItem orderItem
    ) {

        return orderItemRepository
                .save(orderItem);
    }

    @Override
    public void deleteById(
            Long id
    ) {

        orderItemRepository
                .deleteById(id);
    }

    @Override
    public boolean existsById(
            Long id
    ) {

        return orderItemRepository
                .existsById(id);
    }
}