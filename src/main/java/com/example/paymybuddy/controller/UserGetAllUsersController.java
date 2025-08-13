package com.example.paymybuddy.controller;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//import jakarta.persistence.*;

@Controller
@RequestMapping("/ListeUsers")
public class UserGetAllUsersController {

    @Autowired
    private UserService userService;

    // lire tous les user
    @GetMapping("")
    public String listeUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // plus tard: filtrer par user connecté
        return "ListeUsers"; // templates/ListeUsers.html
    }

}
