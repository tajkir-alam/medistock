package com.medistock.inventory.service;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {

    AnalyticsSummaryDTO getSummary();

    AnalyticsSummaryDTO getSummary(LocalDate fromDate);

    List<StockMovementDTO> getMonthlyMovement();

    List<StockMovementDTO> getMonthlyMovement(LocalDate fromDate);

    List<CategoryDistributionDTO> getCategoryDistribution();

    List<TopMedicineDTO> getTopMovingMedicines();

    List<TopMedicineDTO> getTopMovingMedicines(LocalDate fromDate);
}