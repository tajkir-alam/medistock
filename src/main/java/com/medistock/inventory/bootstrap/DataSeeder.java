package com.medistock.inventory.bootstrap;

import com.medistock.inventory.model.Medicine;
import com.medistock.inventory.model.MedicineOrder;
import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.model.StockTransaction;
import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.model.User;
import com.medistock.inventory.model.enums.MedicineCategory;
import com.medistock.inventory.model.enums.OrderStatus;
import com.medistock.inventory.model.enums.Role;
import com.medistock.inventory.model.enums.StockType;
import com.medistock.inventory.repository.MedicineOrderRepository;
import com.medistock.inventory.repository.MedicineRepository;
import com.medistock.inventory.repository.StockTransactionRepository;
import com.medistock.inventory.repository.SupplierRepository;
import com.medistock.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.initial-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineOrderRepository medicineOrderRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0 || supplierRepository.count() > 0 || medicineRepository.count() > 0
                || medicineOrderRepository.count() > 0 || stockTransactionRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .username("admin")
                .passwordHash(hashPassword("admin123"))
                .fullName("System Admin")
                .email("admin@medistock.local")
                .role(Role.ADMIN)
                .active(true)
                .build());

        Supplier supplier1 = supplierRepository.save(Supplier.builder()
                .supplierName("Square Pharmaceuticals Ltd.")
                .registrationNumber("REG-2026-001")
                .contactPersonName("Ayesha Rahman")
                .email("sales@squarepharma.local")
                .phoneNumber("+8801700000001")
                .accountPassword(hashPassword("supplier123"))
                .address("Dhaka, Bangladesh")
                .notes("Primary antibiotics and pain relief supplier")
                .active(true)
                .build());

        Supplier supplier2 = supplierRepository.save(Supplier.builder()
                .supplierName("Beximco Pharma")
                .registrationNumber("REG-2026-002")
                .contactPersonName("Rahim Khan")
                .email("orders@beximco.local")
                .phoneNumber("+8801700000002")
                .accountPassword(hashPassword("supplier123"))
                .address("Gazipur, Bangladesh")
                .notes("High volume cardiovascular and endocrine products")
                .active(true)
                .build());

        Supplier supplier3 = supplierRepository.save(Supplier.builder()
                .supplierName("Incepta Pharmaceuticals")
                .registrationNumber("REG-2026-003")
                .contactPersonName("Nusrat Jahan")
                .email("care@incepta.local")
                .phoneNumber("+8801700000003")
                .accountPassword(hashPassword("supplier123"))
                .address("Dhaka, Bangladesh")
                .notes("Respiratory and vitamin formulations")
                .active(true)
                .build());

        Medicine medicine1 = medicineRepository.save(buildMedicine("Amoxicillin 500mg", "Amoxicillin", "MED-1001", "BAT-1001",
                MedicineCategory.ANTIBIOTICS, supplier1, 8, 10, "Capsule", new BigDecimal("18.50"),
                LocalDate.now().plusMonths(8), "Store below 25°C", "Take after meals"));
        Medicine medicine2 = medicineRepository.save(buildMedicine("Paracetamol 650mg", "Paracetamol", "MED-1002", "BAT-1002",
                MedicineCategory.ANALGESICS, supplier1, 45, 20, "Tablet", new BigDecimal("8.75"),
                LocalDate.now().plusMonths(14), "Keep in dry place", "Use as needed for pain or fever"));
        Medicine medicine3 = medicineRepository.save(buildMedicine("Atorvastatin 20mg", "Atorvastatin", "MED-1003", "BAT-1003",
                MedicineCategory.CARDIOVASCULAR, supplier2, 12, 10, "Tablet", new BigDecimal("32.00"),
                LocalDate.now().plusMonths(10), "Store in a cool dry place", "Take once daily"));
        Medicine medicine4 = medicineRepository.save(buildMedicine("Omeprazole 20mg", "Omeprazole", "MED-1004", "BAT-1004",
                MedicineCategory.GASTROENTEROLOGY, supplier2, 6, 10, "Capsule", new BigDecimal("14.25"),
                LocalDate.now().plusMonths(6), "Protect from moisture", "Take before meals"));
        Medicine medicine5 = medicineRepository.save(buildMedicine("Vitamin D3 1000IU", "Cholecalciferol", "MED-1005", "BAT-1005",
                MedicineCategory.VITAMINS, supplier3, 80, 15, "Softgel", new BigDecimal("22.00"),
                LocalDate.now().plusMonths(18), "Store away from sunlight", "Take once daily"));
        Medicine medicine6 = medicineRepository.save(buildMedicine("Salbutamol Inhaler", "Salbutamol", "MED-1006", "BAT-1006",
                MedicineCategory.RESPIRATORY, supplier3, 3, 8, "Inhaler", new BigDecimal("125.00"),
                LocalDate.now().plusMonths(4), "Keep capped when not in use", "Use during bronchospasm"));

        MedicineOrder order1 = new MedicineOrder();
        order1.setSupplier(supplier1);
        order1.setOrderDate(LocalDateTime.now().minusDays(3));
        order1.setEstimatedDelivery(LocalDate.now().plusDays(2));
        order1.setStatus(OrderStatus.COMPLETED);
        order1.setTotalAmount(new BigDecimal("673.50"));
        order1.addOrderItem(buildOrderItem(medicine1, 20, medicine1.getUnitPrice()));
        order1.addOrderItem(buildOrderItem(medicine2, 30, medicine2.getUnitPrice()));
        order1.addOrderItem(buildOrderItem(medicine4, 12, medicine4.getUnitPrice()));
        medicineOrderRepository.save(order1);

        MedicineOrder order2 = new MedicineOrder();
        order2.setSupplier(supplier2);
        order2.setOrderDate(LocalDateTime.now().minusDays(1));
        order2.setEstimatedDelivery(LocalDate.now().plusDays(4));
        order2.setStatus(OrderStatus.PENDING);
        order2.setTotalAmount(new BigDecimal("880.00"));
        order2.addOrderItem(buildOrderItem(medicine3, 15, medicine3.getUnitPrice()));
        order2.addOrderItem(buildOrderItem(medicine5, 20, medicine5.getUnitPrice()));
        medicineOrderRepository.save(order2);

        MedicineOrder order3 = new MedicineOrder();
        order3.setSupplier(supplier3);
        order3.setOrderDate(LocalDateTime.now().minusDays(10));
        order3.setEstimatedDelivery(LocalDate.now().minusDays(4));
        order3.setStatus(OrderStatus.CANCELLED);
        order3.setTotalAmount(new BigDecimal("500.00"));
        order3.addOrderItem(buildOrderItem(medicine6, 4, medicine6.getUnitPrice()));
        medicineOrderRepository.save(order3);

        List<StockTransaction> transactions = List.of(
                buildTransaction(medicine1, StockType.STOCK_IN, 20, admin, LocalDateTime.now().minusDays(3)),
                buildTransaction(medicine2, StockType.STOCK_IN, 30, admin, LocalDateTime.now().minusDays(3)),
                buildTransaction(medicine3, StockType.STOCK_IN, 15, admin, LocalDateTime.now().minusDays(2)),
                buildTransaction(medicine4, StockType.STOCK_OUT, 4, admin, LocalDateTime.now().minusDays(1)),
                buildTransaction(medicine5, StockType.STOCK_IN, 20, admin, LocalDateTime.now().minusDays(1)),
                buildTransaction(medicine6, StockType.STOCK_OUT, 1, admin, LocalDateTime.now())
        );
        stockTransactionRepository.saveAll(transactions);
    }

    private Medicine buildMedicine(String medicineName,
                                   String genericName,
                                   String sku,
                                   String batchId,
                                   MedicineCategory category,
                                   Supplier supplier,
                                   Integer quantity,
                                   Integer lowStockThreshold,
                                   String unitType,
                                   BigDecimal unitPrice,
                                   LocalDate expiryDate,
                                   String storageInstructions,
                                   String dosageNotes) {
        Medicine medicine = new Medicine();
        medicine.setMedicineName(medicineName);
        medicine.setGenericName(genericName);
        medicine.setSku(sku);
        medicine.setBatchId(batchId);
        medicine.setCategory(category);
        medicine.setSupplier(supplier);
        medicine.setQuantity(quantity);
        medicine.setLowStockThreshold(lowStockThreshold);
        medicine.setUnitType(unitType);
        medicine.setUnitPrice(unitPrice);
        medicine.setExpiryDate(expiryDate);
        medicine.setStorageInstructions(storageInstructions);
        medicine.setDosageNotes(dosageNotes);
        medicine.setActive(true);
        return medicine;
    }

    private OrderItem buildOrderItem(Medicine medicine, Integer quantity, BigDecimal unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setMedicine(medicine);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        return orderItem;
    }

    private StockTransaction buildTransaction(Medicine medicine,
                                              StockType stockType,
                                              Integer quantity,
                                              User performedBy,
                                              LocalDateTime transactionDate) {
        StockTransaction stockTransaction = new StockTransaction();
        stockTransaction.setMedicine(medicine);
        stockTransaction.setStockType(stockType);
        stockTransaction.setQuantity(quantity);
        stockTransaction.setPerformedBy(performedBy);
        stockTransaction.setTransactionDate(transactionDate);
        return stockTransaction;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash seed password", exception);
        }
    }
}