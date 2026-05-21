package com.medistock.inventory.service;

import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.repository.OrderItemRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OrderItem> findById(@NonNull Long id) {
        return orderItemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findByMedicineOrderId(@NonNull Long orderId) {
        return orderItemRepository.findByMedicineOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findByMedicineId(@NonNull Long medicineId) {
        return orderItemRepository.findByMedicineId(medicineId);
    }

    public OrderItem save(@NonNull OrderItem orderItem) {
        return orderItemRepository.save(Objects.requireNonNull(orderItem, "orderItem must not be null"));
    }

    public void deleteById(@NonNull Long id) {
        orderItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(@NonNull Long id) {
        return orderItemRepository.existsById(id);
    }
}