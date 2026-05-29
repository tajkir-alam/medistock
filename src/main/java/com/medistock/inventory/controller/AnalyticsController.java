package com.medistock.inventory.controller;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;
import com.medistock.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public AnalyticsSummaryDTO getSummary(@RequestParam(defaultValue = "90D") String period) {
        return analyticsService.getSummary(resolveFromDate(period));
    }

    @GetMapping("/stock-movement")
    public List<StockMovementDTO> getStockMovement(@RequestParam(defaultValue = "90D") String period) {
        return analyticsService.getMonthlyMovement(resolveFromDate(period));
    }

    @GetMapping("/categories")
    public List<CategoryDistributionDTO> getCategoryDistribution() {
        return analyticsService.getCategoryDistribution();
    }

    @GetMapping("/top-medicines")
    public List<TopMedicineDTO> getTopMedicines(@RequestParam(defaultValue = "90D") String period) {
        return analyticsService.getTopMovingMedicines(resolveFromDate(period));
    }

    private LocalDate resolveFromDate(String period) {
        if (period == null) {
            return LocalDate.now().minusDays(90);
        }

        return switch (period.trim().toUpperCase(Locale.ROOT)) {
            case "30D" -> LocalDate.now().minusDays(30);
            case "180D" -> LocalDate.now().minusDays(180);
            case "ALL" -> null;
            default -> LocalDate.now().minusDays(90);
        };
    }
}