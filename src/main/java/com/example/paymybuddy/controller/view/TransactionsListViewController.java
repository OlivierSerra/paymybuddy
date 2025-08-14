package com.example.paymybuddy.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.paymybuddy.repository.UserRepository;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/transactions")
public class TransactionsListViewController {

    private final UserRepository userRepository;

    public TransactionsListViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/MesTransactions")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "ListeTransactions";
    }
}
