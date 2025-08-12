package com.example.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.stereotype.Controller;

@Controller
public class MyController {
    // home sera la vue de la page d'accueil

    @GetMapping("/home")
    public String viewhome() {
        return "home";
    }

}
