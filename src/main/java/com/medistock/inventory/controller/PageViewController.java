package com.medistock.inventory.controller;

import com.medistock.inventory.dto.AnalyticsSummaryDTO;
import com.medistock.inventory.dto.CategoryDistributionDTO;
import com.medistock.inventory.dto.StockMovementDTO;
import com.medistock.inventory.dto.TopMedicineDTO;
import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.model.User;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.service.AnalyticsService;
import com.medistock.inventory.service.MedicineOrderService;
import com.medistock.inventory.service.MedicineService;
import com.medistock.inventory.service.StockTransactionService;
import com.medistock.inventory.service.SupplierService;
import com.medistock.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class PageViewController {

    private final MedicineService medicineService;
    private final SupplierService supplierService;
    private final MedicineOrderService medicineOrderService;
    private final StockTransactionService stockTransactionService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping({"/index", "/index.html"})
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes) {
        String identifier = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String passwordHash = hashPassword(password == null ? "" : password);

        User matchedUser = userService.getAllUsers().stream()
                .filter(user -> user.getUsername() != null || user.getEmail() != null)
                .filter(user -> {
                    String username = user.getUsername() == null ? "" : user.getUsername().toLowerCase(Locale.ROOT);
                    String userEmail = user.getEmail() == null ? "" : user.getEmail().toLowerCase(Locale.ROOT);
                    return identifier.equals(username) || identifier.equals(userEmail);
                })
                .filter(user -> Objects.equals(user.getPasswordHash(), passwordHash))
                .findFirst()
                .orElse(null);

        if (matchedUser == null || Boolean.FALSE.equals(matchedUser.getActive())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials.");
            return "redirect:/index";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + matchedUser.getFullName() + ".");
        return "redirect:/dashboard";
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
        public String inventory(@RequestParam(defaultValue = "") String q,
                    @RequestParam(required = false) MedicineCategory category,
                    @RequestParam(defaultValue = "ALL") String stockStatus,
                    @RequestParam(defaultValue = "1") int page,
                    @RequestParam(defaultValue = "10") int size,
                    Model model) {
        List<Medicine> allMedicines = medicineService.getAllMedicines();
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = stockStatus == null ? "ALL" : stockStatus.trim().toUpperCase(Locale.ROOT);

        List<Medicine> filteredMedicines = allMedicines.stream()
            .filter(medicine -> normalizedQuery.isBlank()
                || containsIgnoreCase(medicine.getMedicineName(), normalizedQuery)
                || containsIgnoreCase(medicine.getSku(), normalizedQuery)
                || containsIgnoreCase(medicine.getBatchId(), normalizedQuery))
            .filter(medicine -> category == null || medicine.getCategory() == category)
            .filter(medicine -> matchesInventoryStatus(medicine, normalizedStatus))
            .toList();

        PageResult<Medicine> pageResult = paginate(filteredMedicines, page, size);

        model.addAttribute("medicines", pageResult.items());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStockStatus", normalizedStatus);
        model.addAttribute("currentPage", pageResult.currentPage());
        model.addAttribute("totalPages", pageResult.totalPages());
        model.addAttribute("pageSize", pageResult.pageSize());
        model.addAttribute("categories", MedicineCategory.values());

        model.addAttribute("lowStockMedicines", medicineService.getLowStockMedicines());
        model.addAttribute("expiredMedicines", medicineService.getExpiredMedicines());
        model.addAttribute("totalInventoryValue", allMedicines.stream()
                .map(medicine -> {
                    BigDecimal unitPrice = medicine.getUnitPrice() == null ? BigDecimal.ZERO : medicine.getUnitPrice();
                    Integer quantity = medicine.getQuantity() == null ? 0 : medicine.getQuantity();
                    return unitPrice.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("upcomingExpirationsCount", allMedicines.stream()
                .filter(medicine -> medicine.getExpiryDate() != null)
            .filter(medicine -> !medicine.getExpiryDate().isBefore(LocalDate.now()))
            .filter(medicine -> !medicine.getExpiryDate().isAfter(LocalDate.now().plusDays(30)))
                .count());
        return "inventory";
    }

    @GetMapping({"/orders", "/orders.html"})
        public String orders(@RequestParam(defaultValue = "") String q,
                 @RequestParam(defaultValue = "ALL") String status,
                 @RequestParam(defaultValue = "1") int page,
                 @RequestParam(defaultValue = "10") int size,
                 Model model) {
        var orders = medicineOrderService.getAllOrders();
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);

        List<MedicineOrder> filteredOrders = orders.stream()
            .filter(order -> normalizedQuery.isBlank()
                || containsIgnoreCase(order.getSupplier() != null ? order.getSupplier().getSupplierName() : null, normalizedQuery)
                || containsIgnoreCase("PO-" + order.getId(), normalizedQuery))
            .filter(order -> "ALL".equals(normalizedStatus)
                || (order.getStatus() != null && order.getStatus().name().equals(normalizedStatus)))
            .toList();

        PageResult<MedicineOrder> pageResult = paginate(filteredOrders, page, size);

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

        model.addAttribute("orders", pageResult.items());
        model.addAttribute("ordersThisMonthCount", orders.size());
        model.addAttribute("pendingOrderCount", pendingOrderCount);
        model.addAttribute("completedOrderCount", completedOrderCount);
        model.addAttribute("orderCompletionRate", orders.isEmpty() ? 0 : Math.round((completedOrderCount * 100.0) / orders.size()));
        model.addAttribute("overdueOrderCount", overdueOrderCount);
        model.addAttribute("q", q);
        model.addAttribute("selectedOrderStatus", normalizedStatus);
        model.addAttribute("currentPage", pageResult.currentPage());
        model.addAttribute("totalPages", pageResult.totalPages());
        model.addAttribute("pageSize", pageResult.pageSize());
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "orders";
    }

    @GetMapping({"/suppliers", "/suppliers.html"})
    public String suppliers(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "ALL") String active,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "9") int size,
                            Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedActive = active == null ? "ALL" : active.trim().toUpperCase(Locale.ROOT);

        List<Supplier> filteredSuppliers = suppliers.stream()
                .filter(supplier -> normalizedQuery.isBlank()
                        || containsIgnoreCase(supplier.getSupplierName(), normalizedQuery)
                        || containsIgnoreCase(supplier.getContactPersonName(), normalizedQuery)
                        || containsIgnoreCase(supplier.getEmail(), normalizedQuery))
                .filter(supplier -> {
                    if ("ALL".equals(normalizedActive)) {
                        return true;
                    }
                    boolean supplierActive = Boolean.TRUE.equals(supplier.getActive());
                    return ("ACTIVE".equals(normalizedActive) && supplierActive)
                            || ("INACTIVE".equals(normalizedActive) && !supplierActive);
                })
                .toList();

        PageResult<Supplier> pageResult = paginate(filteredSuppliers, page, size);

        model.addAttribute("suppliers", pageResult.items());
        model.addAttribute("q", q);
        model.addAttribute("selectedActiveStatus", normalizedActive);
        model.addAttribute("currentPage", pageResult.currentPage());
        model.addAttribute("totalPages", pageResult.totalPages());
        model.addAttribute("pageSize", pageResult.pageSize());

        model.addAttribute("supplierCount", suppliers.size());
        model.addAttribute("activeSupplierCount", suppliers.stream()
            .filter(supplier -> Boolean.TRUE.equals(supplier.getActive()))
            .count());
        return "suppliers";
    }

    @GetMapping({"/analytics", "/analytics.html", "/reports", "/reports.html"})
    public String analytics(@RequestParam(defaultValue = "90D") String period,
                            Model model) {
        LocalDate fromDate = resolveAnalyticsFromDate(period);
        AnalyticsSummaryDTO summary = analyticsService.getSummary(fromDate);
        List<StockMovementDTO> stockMovement = analyticsService.getMonthlyMovement(fromDate);
        List<TopMedicineDTO> topMedicines = analyticsService.getTopMovingMedicines(fromDate);

        model.addAttribute("medicineCount", medicineService.getAllMedicines().size());
        model.addAttribute("summary", summary);
        model.addAttribute("stockMovement", stockMovement);
        model.addAttribute("stockMovementMax", stockMovement.stream()
                .mapToInt(item -> Math.max(item.getInflow() == null ? 0 : item.getInflow(), item.getOutflow() == null ? 0 : item.getOutflow()))
                .max()
                .orElse(0));
        model.addAttribute("categoryDistribution", analyticsService.getCategoryDistribution());
        model.addAttribute("topMedicines", topMedicines);
        model.addAttribute("selectedPeriod", period == null ? "90D" : period.toUpperCase(Locale.ROOT));
        model.addAttribute("periodOptions", List.of("30D", "90D", "180D", "ALL"));
        model.addAttribute("periodLabel", buildPeriodLabel(fromDate));
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
                            @RequestParam(defaultValue = "SUBMIT") String actionType,
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

        if ("DRAFT".equalsIgnoreCase(actionType)) {
            redirectAttributes.addFlashAttribute("successMessage", "Purchase order draft saved.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Purchase order saved successfully.");
        }

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

        @PostMapping("/medicines/{id}/delete")
        public String deleteMedicine(@PathVariable Long id,
                     RedirectAttributes redirectAttributes) {
        medicineService.deleteMedicine(id);
        redirectAttributes.addFlashAttribute("successMessage", "Medicine deleted successfully.");
        return "redirect:/medicines";
        }

        @PostMapping("/suppliers/{id}/delete")
        public String deleteSupplier(@PathVariable Long id,
                     RedirectAttributes redirectAttributes) {
        supplierService.deleteSupplier(id);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully.");
        return "redirect:/suppliers";
        }

        @PostMapping("/orders/{id}/delete")
        public String deleteOrder(@PathVariable Long id,
                      RedirectAttributes redirectAttributes) {
        medicineOrderService.deleteOrder(id);
        redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully.");
        return "redirect:/orders";
        }

        @GetMapping(value = "/analytics/export.csv", produces = "text/csv")
        public ResponseEntity<String> exportAnalyticsCsv(@RequestParam(defaultValue = "90D") String period) {
            LocalDate fromDate = resolveAnalyticsFromDate(period);
            AnalyticsSummaryDTO summary = analyticsService.getSummary(fromDate);
            List<StockMovementDTO> movement = analyticsService.getMonthlyMovement(fromDate);
            List<CategoryDistributionDTO> categories = analyticsService.getCategoryDistribution();
            List<TopMedicineDTO> topMedicines = analyticsService.getTopMovingMedicines(fromDate);

            StringBuilder csv = new StringBuilder();
            csv.append("section,item,value\n");
            csv.append(csvValue("summary")).append(',').append(csvValue("totalInflowValue")).append(',').append(csvValue(String.valueOf(summary.getTotalInflowValue()))).append('\n');
            csv.append(csvValue("summary")).append(',').append(csvValue("averageTurnoverDays")).append(',').append(csvValue(String.valueOf(summary.getAverageTurnoverDays()))).append('\n');
            csv.append(csvValue("summary")).append(',').append(csvValue("fulfillmentRate")).append(',').append(csvValue(String.valueOf(summary.getFulfillmentRate()))).append('\n');
            csv.append(csvValue("summary")).append(',').append(csvValue("expiringItems")).append(',').append(csvValue(String.valueOf(summary.getExpiringItems()))).append('\n');

            for (StockMovementDTO item : movement) {
                csv.append(csvValue("movement")).append(',')
                        .append(csvValue(item.getMonth())).append(',')
                        .append(csvValue("inflow=" + item.getInflow() + ";outflow=" + item.getOutflow())).append('\n');
            }

            for (CategoryDistributionDTO item : categories) {
                csv.append(csvValue("category")).append(',')
                        .append(csvValue(item.getCategory())).append(',')
                        .append(csvValue(String.valueOf(item.getPercentage()))).append('\n');
            }

            for (TopMedicineDTO item : topMedicines) {
                csv.append(csvValue("top_medicine")).append(',')
                        .append(csvValue(item.getMedicineName())).append(',')
                        .append(csvValue(String.valueOf(item.getTurnoverRate()))).append('\n');
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analytics.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv.toString());
        }

        @GetMapping(value = "/inventory/export.csv", produces = "text/csv")
        public ResponseEntity<String> exportInventoryCsv(@RequestParam(defaultValue = "") String q,
                                 @RequestParam(required = false) MedicineCategory category,
                                 @RequestParam(defaultValue = "ALL") String stockStatus) {
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = stockStatus == null ? "ALL" : stockStatus.trim().toUpperCase(Locale.ROOT);

        List<Medicine> filtered = medicineService.getAllMedicines().stream()
            .filter(medicine -> normalizedQuery.isBlank()
                || containsIgnoreCase(medicine.getMedicineName(), normalizedQuery)
                || containsIgnoreCase(medicine.getSku(), normalizedQuery)
                || containsIgnoreCase(medicine.getBatchId(), normalizedQuery))
            .filter(medicine -> category == null || medicine.getCategory() == category)
            .filter(medicine -> matchesInventoryStatus(medicine, normalizedStatus))
            .toList();

        StringBuilder csv = new StringBuilder("id,name,sku,category,quantity,unitPrice,supplier,status\n");
        for (Medicine medicine : filtered) {
            csv.append(medicine.getId()).append(',')
                .append(csvValue(medicine.getMedicineName())).append(',')
                .append(csvValue(medicine.getSku())).append(',')
                .append(csvValue(medicine.getCategory() == null ? "" : medicine.getCategory().name())).append(',')
                .append(medicine.getQuantity() == null ? 0 : medicine.getQuantity()).append(',')
                .append(medicine.getUnitPrice() == null ? "0.00" : medicine.getUnitPrice()).append(',')
                .append(csvValue(medicine.getSupplier() == null ? "" : medicine.getSupplier().getSupplierName())).append(',')
                .append(csvValue(resolveInventoryStatus(medicine)))
                .append('\n');
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv.toString());
        }

        @GetMapping(value = "/orders/export.csv", produces = "text/csv")
        public ResponseEntity<String> exportOrdersCsv(@RequestParam(defaultValue = "") String q,
                              @RequestParam(defaultValue = "ALL") String status) {
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);

        List<MedicineOrder> filtered = medicineOrderService.getAllOrders().stream()
            .filter(order -> normalizedQuery.isBlank()
                || containsIgnoreCase(order.getSupplier() != null ? order.getSupplier().getSupplierName() : null, normalizedQuery)
                || containsIgnoreCase("PO-" + order.getId(), normalizedQuery))
            .filter(order -> "ALL".equals(normalizedStatus)
                || (order.getStatus() != null && order.getStatus().name().equals(normalizedStatus)))
            .toList();

        StringBuilder csv = new StringBuilder("id,supplier,orderDate,estimatedDelivery,totalAmount,status\n");
        for (MedicineOrder order : filtered) {
            csv.append(order.getId()).append(',')
                .append(csvValue(order.getSupplier() == null ? "" : order.getSupplier().getSupplierName())).append(',')
                .append(csvValue(order.getOrderDate() == null ? "" : order.getOrderDate().toString())).append(',')
                .append(csvValue(order.getEstimatedDelivery() == null ? "" : order.getEstimatedDelivery().toString())).append(',')
                .append(order.getTotalAmount() == null ? "0.00" : order.getTotalAmount()).append(',')
                .append(csvValue(order.getStatus() == null ? "" : order.getStatus().name()))
                .append('\n');
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv.toString());
        }

        @GetMapping(value = "/suppliers/export.csv", produces = "text/csv")
        public ResponseEntity<String> exportSuppliersCsv(@RequestParam(defaultValue = "") String q,
                                 @RequestParam(defaultValue = "ALL") String active) {
        String normalizedQuery = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String normalizedActive = active == null ? "ALL" : active.trim().toUpperCase(Locale.ROOT);

        List<Supplier> filtered = supplierService.getAllSuppliers().stream()
            .filter(supplier -> normalizedQuery.isBlank()
                || containsIgnoreCase(supplier.getSupplierName(), normalizedQuery)
                || containsIgnoreCase(supplier.getContactPersonName(), normalizedQuery)
                || containsIgnoreCase(supplier.getEmail(), normalizedQuery))
            .filter(supplier -> {
                if ("ALL".equals(normalizedActive)) {
                return true;
                }
                boolean supplierActive = Boolean.TRUE.equals(supplier.getActive());
                return ("ACTIVE".equals(normalizedActive) && supplierActive)
                    || ("INACTIVE".equals(normalizedActive) && !supplierActive);
            })
            .toList();

        StringBuilder csv = new StringBuilder("id,name,contactPerson,email,phone,address,status\n");
        for (Supplier supplier : filtered) {
            csv.append(supplier.getId()).append(',')
                .append(csvValue(supplier.getSupplierName())).append(',')
                .append(csvValue(supplier.getContactPersonName())).append(',')
                .append(csvValue(supplier.getEmail())).append(',')
                .append(csvValue(supplier.getPhoneNumber())).append(',')
                .append(csvValue(supplier.getAddress())).append(',')
                .append(csvValue(Boolean.TRUE.equals(supplier.getActive()) ? "ACTIVE" : "INACTIVE"))
                .append('\n');
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suppliers.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv.toString());
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

    private boolean containsIgnoreCase(String source, String searchToken) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(searchToken);
    }

    private boolean matchesInventoryStatus(Medicine medicine, String normalizedStatus) {
        if ("ALL".equals(normalizedStatus)) {
            return true;
        }

        String resolvedStatus = resolveInventoryStatus(medicine).toUpperCase(Locale.ROOT);
        return switch (normalizedStatus) {
            case "IN_STOCK" -> "IN_STOCK".equals(resolvedStatus);
            case "LOW_STOCK" -> "LOW_STOCK".equals(resolvedStatus);
            case "OUT_OF_STOCK" -> "OUT_OF_STOCK".equals(resolvedStatus);
            default -> true;
        };
    }

    private String resolveInventoryStatus(Medicine medicine) {
        int quantity = medicine.getQuantity() == null ? 0 : medicine.getQuantity();
        int threshold = medicine.getLowStockThreshold() == null ? 0 : medicine.getLowStockThreshold();

        if (quantity <= 0) {
            return "OUT_OF_STOCK";
        }

        if (quantity <= threshold) {
            return "LOW_STOCK";
        }

        return "IN_STOCK";
    }

    private String csvValue(String input) {
        String safe = input == null ? "" : input;
        return '"' + safe.replace("\"", "\"\"") + '"';
    }

    private LocalDate resolveAnalyticsFromDate(String period) {
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

    private String buildPeriodLabel(LocalDate fromDate) {
        if (fromDate == null) {
            return "All available data";
        }
        return fromDate + " to today";
    }

    private <T> PageResult<T> paginate(List<T> source, int page, int size) {
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        int totalPages = Math.max(1, (int) Math.ceil((double) source.size() / safeSize));
        int safePage = Math.max(1, Math.min(page, totalPages));
        int fromIndex = (safePage - 1) * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, source.size());
        List<T> items = fromIndex >= source.size() ? List.of() : source.subList(fromIndex, toIndex);
        return new PageResult<>(items, safePage, totalPages, safeSize);
    }

    private record PageResult<T>(List<T> items, int currentPage, int totalPages, int pageSize) {
    }
}