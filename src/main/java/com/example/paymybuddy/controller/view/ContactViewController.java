package com.example.paymybuddy.controller.view;

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

    @GetMapping("/contacts")
    public String listeUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "ListeUsers";
    }
}
