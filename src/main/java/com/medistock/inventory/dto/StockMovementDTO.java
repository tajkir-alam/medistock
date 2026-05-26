package com.medistock.inventory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMovementDTO {

    private String month;

    private Integer inflow;

    private Integer outflow;
}