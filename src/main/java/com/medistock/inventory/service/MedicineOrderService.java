package com.medistock.inventory.service;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.repository.MedicineOrderRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
@Transactional
public class MedicineOrderService {

    private final MedicineOrderRepository medicineOrderRepository;

    public MedicineOrderService(MedicineOrderRepository medicineOrderRepository) {
        this.medicineOrderRepository = medicineOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicineOrder> findAll() {
        return medicineOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MedicineOrder> findById(@NonNull Long id) {
        return medicineOrderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MedicineOrder> findBySupplierId(@NonNull Long supplierId) {
        return medicineOrderRepository.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public List<MedicineOrder> findByStatus(OrderStatus status) {
        return medicineOrderRepository.findByStatus(status);
    }

    public MedicineOrder save(@NonNull MedicineOrder medicineOrder) {
        MedicineOrder nonNullOrder = Objects.requireNonNull(medicineOrder, "medicineOrder must not be null");
        attachParentToOrderItems(nonNullOrder);
        return medicineOrderRepository.save(nonNullOrder);
    }

    public void deleteById(@NonNull Long id) {
        medicineOrderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(@NonNull Long id) {
        return medicineOrderRepository.existsById(id);
    }

    private void attachParentToOrderItems(MedicineOrder medicineOrder) {
        List<OrderItem> orderItems = medicineOrder.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        for (OrderItem orderItem : orderItems) {
            orderItem.setMedicineOrder(medicineOrder);
        }
    }
}