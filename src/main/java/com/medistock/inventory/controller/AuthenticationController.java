package com.medistock.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}