package com.medistock.inventory.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.medistock.inventory.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicine_orders")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "supplier_id",
            nullable = false
    )
    private Supplier supplier;

    @Column(
            name = "order_date",
            nullable = false
    )
    private LocalDateTime orderDate;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "order_status",
            nullable = false
    )
    @Builder.Default
    private OrderStatus status =
            OrderStatus.PENDING;

    @OneToMany(
            mappedBy = "medicineOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    @Builder.Default
    private List<OrderItem> orderItems =
            new ArrayList<>();

    @PrePersist
    protected void onCreate() {

        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }

        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    public void addOrderItem(
            OrderItem orderItem
    ) {

        orderItems.add(orderItem);
        orderItem.setMedicineOrder(this);
    }

    public void removeOrderItem(
            OrderItem orderItem
    ) {

        orderItems.remove(orderItem);
        orderItem.setMedicineOrder(null);
    }
}