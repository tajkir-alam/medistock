package com.medistock.inventory.controller;

import com.medistock.inventory.model.OrderItem;
import com.medistock.inventory.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService
            orderItemService;

    public OrderItemController(
            OrderItemService orderItemService
    ) {

        this.orderItemService =
                orderItemService;
    }

    @GetMapping
    public List<OrderItem> getAllOrderItems() {

        return orderItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem>
    getOrderItemById(
            @PathVariable @NonNull Long id
    ) {

        return orderItemService
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound()
                                .build());
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem>
    getItemsByOrderId(
            @PathVariable @NonNull Long orderId
    ) {

        return orderItemService
                .findByMedicineOrderId(
                        orderId
                );
    }

    @GetMapping("/medicine/{medicineId}")
    public List<OrderItem>
    getItemsByMedicineId(
            @PathVariable @NonNull Long medicineId
    ) {

        return orderItemService
                .findByMedicineId(
                        medicineId
                );
    }

    @PostMapping
    public OrderItem createOrderItem(
            @RequestBody OrderItem orderItem
    ) {

        return orderItemService
                .save(orderItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem>
    updateOrderItem(
            @PathVariable @NonNull Long id,
            @RequestBody OrderItem orderItem
    ) {

        return orderItemService
                .findById(id)
                .map(existing -> {

                    orderItem.setId(
                            existing.getId()
                    );

                    return ResponseEntity.ok(
                            orderItemService
                                    .save(orderItem)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.notFound()
                                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteOrderItem(
            @PathVariable @NonNull Long id
    ) {

        if (!orderItemService
                .existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        orderItemService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}