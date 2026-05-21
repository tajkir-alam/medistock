package com.medistock.inventory.model;

import com.medistock.inventory.model.enums.MedicineCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "medicines",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_medicines_sku",
                        columnNames = "sku"
                )
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

    @Column(name = "medicine_name", nullable = false, length = 120)
    private String medicineName;

    @Column(name = "generic_name", length = 120)
    private String genericName;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "batch_id", length = 50)
    private String batchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private MedicineCategory category;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;

    @Column(name = "unit_type", nullable = false, length = 30)
    private String unitType;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "storage_instructions", length = 500)
    private String storageInstructions;

    @Column(name = "dosage_notes", length = 500)
    private String dosageNotes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}