package com.medistock.inventory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDistributionDTO {

    private String category;

    private Long total;

    private Double percentage;
}