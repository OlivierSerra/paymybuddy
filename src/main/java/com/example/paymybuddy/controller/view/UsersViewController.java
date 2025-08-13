package com.example.paymybuddy.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.ui.Model;

@Controller
public class UsersViewController {

    private final UserRepository userRepository;

    public UsersViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String Users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "Users";
    }

}
