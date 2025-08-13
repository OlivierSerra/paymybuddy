package com.example.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class RootController {
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
}
