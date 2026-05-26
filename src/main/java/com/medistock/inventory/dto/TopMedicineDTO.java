package com.medistock.inventory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopMedicineDTO {

    private String medicineName;

    private String sku;

    private String category;

    private Integer remainingStock;

    private Double turnoverRate;

    private String stockStatus;
}