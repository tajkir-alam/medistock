package com.medistock.inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_suppliers_registration_number",
                        columnNames = "registration_number"
                ),
                @UniqueConstraint(
                        name = "uk_suppliers_email",
                        columnNames = "email"
                )
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Column(name = "supplier_name", nullable = false, length = 150)
    private String supplierName;

    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "contact_person_name", nullable = false, length = 100)
    private String contactPersonName;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "account_password", nullable = false, length = 255)
    private String accountPassword;

    @Column(name = "address", length = 300)
    private String address;

        @Column(name = "notes", length = 500)
        private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}