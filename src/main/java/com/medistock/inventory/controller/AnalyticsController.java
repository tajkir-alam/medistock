package com.medistock.inventory.controller;

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