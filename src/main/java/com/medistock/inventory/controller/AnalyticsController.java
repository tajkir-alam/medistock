package com.medistock.inventory.controller;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;
import com.medistock.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public AnalyticsSummaryDTO getSummary() {
        return analyticsService.getSummary();
    }

    @GetMapping("/stock-movement")
    public List<StockMovementDTO> getStockMovement() {
        return analyticsService.getMonthlyMovement();
    }

    @GetMapping("/categories")
    public List<CategoryDistributionDTO> getCategoryDistribution() {
        return analyticsService.getCategoryDistribution();
    }

    @GetMapping("/top-medicines")
    public List<TopMedicineDTO> getTopMedicines() {
        return analyticsService.getTopMovingMedicines();
    }
}