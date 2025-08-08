package com.example.paymybuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.stereotype.Controller;

@RestController
public class MyController {
    // home sera la vue de la page d'accueil
    @GetMapping("/home")
    public String viewhome() {
        return "home";
    }

}
