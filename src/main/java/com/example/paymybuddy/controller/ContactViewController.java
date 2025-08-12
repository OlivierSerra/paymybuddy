package com.example.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.ui.Model;

@Controller
public class ContactViewController {

    private final UserRepository userRepository;

    public ContactViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/ListeContacts")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "ListeContacts";
    }
}
