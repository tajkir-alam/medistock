package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.repository.OrderItemRepository;
import com.medistock.inventory.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl
        implements OrderItemService {

    private final OrderItemRepository
            orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> findAll() {

        return orderItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItem> findById(
            Long id
    ) {

        return orderItemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

        return orderItemRepository.save(
                Objects.requireNonNull(orderItem)
        );
    }

    @Override
    public void deleteById(
            Long id
    ) {

        orderItemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(
            Long id
    ) {

        return orderItemRepository.existsById(id);
    }
}