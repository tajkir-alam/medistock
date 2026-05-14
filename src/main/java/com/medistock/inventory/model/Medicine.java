package com.medistock.inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "Medicined",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_medicines_brandName", columnNames = "brandName"),
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
    private Long id;

    @Column(nullable = false, length = 50)
    private String brandName;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 50)
    private String unitType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
