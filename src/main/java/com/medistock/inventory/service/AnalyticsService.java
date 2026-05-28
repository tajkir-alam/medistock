package com.medistock.inventory.service;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;

import java.util.List;

public interface AnalyticsService {

    AnalyticsSummaryDTO getSummary();

    List<StockMovementDTO> getMonthlyMovement();

    List<CategoryDistributionDTO> getCategoryDistribution();

    List<TopMedicineDTO> getTopMovingMedicines();
}