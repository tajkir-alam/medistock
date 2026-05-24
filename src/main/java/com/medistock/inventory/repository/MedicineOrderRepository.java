package com.medistock.inventory.repository;

import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Long> {

    List<MedicineOrder> findBySupplier(Supplier supplier);

    List<MedicineOrder> findByStatus(OrderStatus status);

    List<MedicineOrder> findByOrderDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}