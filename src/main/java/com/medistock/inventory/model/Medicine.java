package com.medistock.inventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "medicines",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_medicines_brand_name", columnNames = "brand_name"),
                @UniqueConstraint(name = "uk_medicines_sku", columnNames = "sku")
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long id;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "generic_name", nullable = false, length = 50)
    private String genericName;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "unit_type", nullable = false, length = 30)
    private String unitType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;

    @Column(name = "buying_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal buyingPrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}