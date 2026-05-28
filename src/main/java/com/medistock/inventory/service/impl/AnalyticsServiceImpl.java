package com.medistock.inventory.service.impl;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;
import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.model.enums.StockType;
import com.medistock.inventory.repository.MedicineOrderRepository;
import com.medistock.inventory.repository.MedicineRepository;
import com.medistock.inventory.repository.StockTransactionRepository;
import com.medistock.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final MedicineRepository medicineRepository;
    private final MedicineOrderRepository medicineOrderRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public AnalyticsSummaryDTO getSummary() {
        List<Medicine> medicines = medicineRepository.findAll();
        BigDecimal totalInflowValue = medicines.stream()
                .map(medicine -> {
                    BigDecimal unitPrice = medicine.getUnitPrice() == null ? BigDecimal.ZERO : medicine.getUnitPrice();
                    Integer quantity = medicine.getQuantity() == null ? 0 : medicine.getQuantity();
                    return unitPrice.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double averageTurnoverDays = medicines.isEmpty() ? 0.0 : medicines.stream()
                .filter(medicine -> medicine.getExpiryDate() != null)
                .mapToLong(medicine -> ChronoUnit.DAYS.between(LocalDate.now(), medicine.getExpiryDate()))
                .average()
                .orElse(0.0);

        List<MedicineOrder> orders = medicineOrderRepository.findAll();
        long completedOrders = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .count();
        double fulfillmentRate = orders.isEmpty() ? 0.0 : (completedOrders * 100.0) / orders.size();

        int expiringItems = (int) medicines.stream()
                .filter(medicine -> medicine.getExpiryDate() != null)
                .filter(medicine -> !medicine.getExpiryDate().isBefore(LocalDate.now()))
                .filter(medicine -> !medicine.getExpiryDate().isAfter(LocalDate.now().plusDays(30)))
                .count();

        return AnalyticsSummaryDTO.builder()
                .totalInflowValue(totalInflowValue.doubleValue())
                .averageTurnoverDays(averageTurnoverDays)
                .fulfillmentRate(fulfillmentRate)
                .expiringItems(expiringItems)
                .build();
    }

    @Override
    public List<StockMovementDTO> getMonthlyMovement() {
        List<StockTransaction> transactions = stockTransactionRepository.findAll();
        Map<YearMonth, int[]> monthlyTotals = new java.util.TreeMap<>();

        for (StockTransaction transaction : transactions) {
            if (transaction.getTransactionDate() == null || transaction.getStockType() == null) {
                continue;
            }
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            int[] totals = monthlyTotals.computeIfAbsent(month, key -> new int[2]);
            if (transaction.getStockType() == StockType.STOCK_IN) {
                totals[0] += transaction.getQuantity() == null ? 0 : transaction.getQuantity();
            } else {
                totals[1] += transaction.getQuantity() == null ? 0 : transaction.getQuantity();
            }
        }

        return monthlyTotals.entrySet().stream()
                .map(entry -> StockMovementDTO.builder()
                        .month(entry.getKey().toString())
                        .inflow(entry.getValue()[0])
                        .outflow(entry.getValue()[1])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDistributionDTO> getCategoryDistribution() {
        List<Medicine> medicines = medicineRepository.findAll();
        Map<String, Long> counts = medicines.stream()
                .collect(Collectors.groupingBy(
                        medicine -> medicine.getCategory() == null ? "UNKNOWN" : medicine.getCategory().name(),
                        Collectors.counting()
                ));

        long total = medicines.size();
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> CategoryDistributionDTO.builder()
                        .category(entry.getKey())
                        .total(entry.getValue())
                        .percentage(total == 0 ? 0.0 : (entry.getValue() * 100.0) / total)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TopMedicineDTO> getTopMovingMedicines() {
        return medicineRepository.findAll().stream()
                .sorted(Comparator.comparing(Medicine::getQuantity, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(medicine -> TopMedicineDTO.builder()
                        .medicineName(medicine.getMedicineName())
                        .sku(medicine.getSku())
                        .category(medicine.getCategory() == null ? null : medicine.getCategory().name())
                        .remainingStock(medicine.getQuantity())
                        .turnoverRate(medicine.getQuantity() == null ? 0.0 : Math.min(100.0, medicine.getQuantity()))
                        .stockStatus(resolveStockStatus(medicine))
                        .build())
                .collect(Collectors.toList());
    }

    private String resolveStockStatus(Medicine medicine) {
        Integer quantity = medicine.getQuantity() == null ? 0 : medicine.getQuantity();
        Integer threshold = medicine.getLowStockThreshold() == null ? 10 : medicine.getLowStockThreshold();
        if (quantity <= 0) {
            return "Out of Stock";
        }
        if (quantity <= threshold) {
            return "Low Stock";
        }
        return "Stable";
    }
}