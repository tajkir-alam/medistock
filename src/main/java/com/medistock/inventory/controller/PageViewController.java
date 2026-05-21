package com.medistock.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageViewController {

    @GetMapping({"/index", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({"/inventory", "/inventory.html"})
    public String inventory() {
        return "inventory";
    }

    @GetMapping({"/orders", "/orders.html"})
    public String orders() {
        return "orders";
    }

    @GetMapping({"/suppliers", "/suppliers.html"})
    public String suppliers() {
        return "suppliers";
    }

    @GetMapping({"/analytics", "/analytics.html"})
    public String analytics() {
        return "analytics";
    }

    @GetMapping({"/create-order", "/createOrder", "/createOrder.html"})
    public String createOrder() {
        return "createOrder";
    }

    @GetMapping({"/add-supplier", "/addSupplier", "/addSupplier.html"})
    public String addSupplier() {
        return "addSupplier";
    }

    @GetMapping({"/add-medicine", "/addMedicine", "/addMedicine.html"})
    public String addMedicine() {
        return "addMedicine";
    }
}