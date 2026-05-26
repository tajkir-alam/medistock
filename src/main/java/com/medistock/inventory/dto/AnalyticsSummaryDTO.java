package com.medistock.inventory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsSummaryDTO {

    private Double totalInflowValue;

    private Double averageTurnoverDays;

    private Double fulfillmentRate;

    private Integer expiringItems;
}
