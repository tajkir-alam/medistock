package com.medistock.inventory.controller;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.model.enums.StockType;
import com.medistock.inventory.service.AnalyticsService;
import com.medistock.inventory.service.MedicineOrderService;
import com.medistock.inventory.service.MedicineService;
import com.medistock.inventory.service.StockTransactionService;
import com.medistock.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;

@Controller
@RequiredArgsConstructor
public class PageViewController {

    private final MedicineService medicineService;
    private final SupplierService supplierService;
    private final MedicineOrderService medicineOrderService;
    private final StockTransactionService stockTransactionService;
    private final AnalyticsService analyticsService;

    @GetMapping({"/index", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/dashboard", "/dashboard.html"})
    public String dashboard(Model model) {
        model.addAttribute("medicineCount", medicineService.getAllMedicines().size());
        model.addAttribute("lowStockCount", medicineService.getLowStockMedicines().size());
        model.addAttribute("expiredCount", medicineService.getExpiredMedicines().size());
        model.addAttribute("lowStockMedicines", medicineService.getLowStockMedicines());
        model.addAttribute("supplierCount", supplierService.getAllSuppliers().size());
        model.addAttribute("activeSupplierCount", supplierService.getAllSuppliers().stream()
                .filter(supplier -> Boolean.TRUE.equals(supplier.getActive()))
                .count());
        model.addAttribute("orderCount", medicineOrderService.getAllOrders().size());
        model.addAttribute("stockTransactionCount", stockTransactionService.findAll().size());
        model.addAttribute("recentTransactions", stockTransactionService.findAll().stream()
                .sorted((left, right) -> {
                    if (left.getTransactionDate() == null && right.getTransactionDate() == null) {
                        return 0;
                    }
                    if (left.getTransactionDate() == null) {
                        return 1;
                    }
                    if (right.getTransactionDate() == null) {
                        return -1;
                    }
                    return right.getTransactionDate().compareTo(left.getTransactionDate());
                })
                .limit(5)
                .toList());
        return "dashboard";
    }

    @GetMapping({"/inventory", "/inventory.html", "/medicines", "/medicines.html"})
    public String inventory(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("lowStockMedicines", medicineService.getLowStockMedicines());
        model.addAttribute("expiredMedicines", medicineService.getExpiredMedicines());
        model.addAttribute("totalInventoryValue", medicineService.getAllMedicines().stream()
                .map(medicine -> {
                    BigDecimal unitPrice = medicine.getUnitPrice() == null ? BigDecimal.ZERO : medicine.getUnitPrice();
                    Integer quantity = medicine.getQuantity() == null ? 0 : medicine.getQuantity();
                    return unitPrice.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("upcomingExpirationsCount", medicineService.getAllMedicines().stream()
                .filter(medicine -> medicine.getExpiryDate() != null)
                .filter(medicine -> !medicine.getExpiryDate().isBefore(java.time.LocalDate.now()))
                .filter(medicine -> !medicine.getExpiryDate().isAfter(java.time.LocalDate.now().plusDays(30)))
                .count());
        return "inventory";
    }

    @GetMapping({"/orders", "/orders.html"})
    public String orders(Model model) {
        var orders = medicineOrderService.getAllOrders();
        long completedOrderCount = orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .count();
        long pendingOrderCount = orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PENDING)
            .count();
        long overdueOrderCount = orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PENDING)
            .filter(order -> order.getOrderDate() != null && order.getOrderDate().isBefore(java.time.LocalDateTime.now().minusDays(7)))
            .count();

        model.addAttribute("orders", orders);
        model.addAttribute("ordersThisMonthCount", orders.size());
        model.addAttribute("pendingOrderCount", pendingOrderCount);
        model.addAttribute("completedOrderCount", completedOrderCount);
        model.addAttribute("orderCompletionRate", orders.isEmpty() ? 0 : Math.round((completedOrderCount * 100.0) / orders.size()));
        model.addAttribute("overdueOrderCount", overdueOrderCount);
        return "orders";
    }

    @GetMapping({"/suppliers", "/suppliers.html"})
    public String suppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("supplierCount", supplierService.getAllSuppliers().size());
        model.addAttribute("activeSupplierCount", supplierService.getAllSuppliers().stream()
            .filter(supplier -> Boolean.TRUE.equals(supplier.getActive()))
            .count());
        return "suppliers";
    }

    @GetMapping({"/analytics", "/analytics.html", "/reports", "/reports.html"})
    public String analytics(Model model) {
        AnalyticsSummaryDTO summary = analyticsService.getSummary();
        model.addAttribute("medicineCount", medicineService.getAllMedicines().size());
        model.addAttribute("summary", summary);
        model.addAttribute("stockMovement", analyticsService.getMonthlyMovement());
        model.addAttribute("categoryDistribution", analyticsService.getCategoryDistribution());
        model.addAttribute("topMedicines", analyticsService.getTopMovingMedicines());
        return "analytics";
    }

    @GetMapping({"/create-order", "/createOrder", "/createOrder.html"})
    public String createOrder(Model model) {
        MedicineOrder order = new MedicineOrder();
        order.setOrderItems(new ArrayList<>());
        order.getOrderItems().add(new OrderItem());
        order.getOrderItems().add(new OrderItem());
        order.getOrderItems().add(new OrderItem());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("order", order);
        return "createOrder";
    }

    @PostMapping({"/orders/save", "/orders"})
    public String saveOrder(@ModelAttribute MedicineOrder order,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (order.getSupplier() == null || order.getSupplier().getId() == null) {
            model.addAttribute("errorMessage", "Please select a supplier.");
            prepareOrderModel(model, order);
            return "createOrder";
        }

        Supplier supplier = supplierService.getSupplierById(order.getSupplier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        BigDecimal totalAmount = BigDecimal.ZERO;
        ArrayList<OrderItem> validItems = new ArrayList<>();

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                if (item == null || item.getMedicine() == null || item.getMedicine().getId() == null || item.getQuantity() == null) {
                    continue;
                }

                Medicine medicine = medicineService.getMedicineById(item.getMedicine().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Medicine not found"));

                item.setMedicine(medicine);
                item.setMedicineOrder(order);

                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(medicine.getUnitPrice());
                }

                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(BigDecimal.ZERO);
                }

                BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(lineTotal);
                validItems.add(item);
            }
        }

        if (validItems.isEmpty()) {
            model.addAttribute("errorMessage", "Please add at least one medicine row.");
            prepareOrderModel(model, order);
            return "createOrder";
        }

        order.setSupplier(supplier);
        order.setOrderItems(validItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItem item : validItems) {
            item.setMedicineOrder(order);
        }

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        medicineOrderService.saveOrder(order);
        redirectAttributes.addFlashAttribute("successMessage", "Purchase order saved successfully.");
        return "redirect:/orders";
    }

    @GetMapping({"/add-supplier", "/addSupplier", "/addSupplier.html"})
    public String addSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "addSupplier";
    }

    @GetMapping({"/add-medicine", "/addMedicine", "/addMedicine.html"})
    public String addMedicine(Model model) {
        Medicine medicine = new Medicine();
        medicine.setSupplier(new Supplier());
        model.addAttribute("medicine", medicine);
        model.addAttribute("categories", MedicineCategory.values());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "addMedicine";
    }

    @PostMapping({"/medicines/save", "/medicines"})
    public String saveMedicine(@ModelAttribute Medicine medicine,
                               RedirectAttributes redirectAttributes) {
        if (medicine.getSupplier() == null || medicine.getSupplier().getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a supplier.");
            return "redirect:/add-medicine";
        }

        Supplier supplier = supplierService.getSupplierById(medicine.getSupplier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        medicine.setSupplier(supplier);
        medicineService.saveMedicine(medicine);
        redirectAttributes.addFlashAttribute("successMessage", "Medicine saved successfully.");
        return "redirect:/medicines";
    }

    @PostMapping({"/suppliers/save", "/suppliers"})
    public String saveSupplier(@ModelAttribute Supplier supplier,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("supplier", supplier);
            model.addAttribute("errorMessage", "Password and confirm password do not match.");
            return "addSupplier";
        }

        supplier.setAccountPassword(hashPassword(password));
        supplierService.saveSupplier(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier saved successfully.");
        return "redirect:/suppliers";
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    private void prepareOrderModel(Model model, MedicineOrder order) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("order", order);
    }
}