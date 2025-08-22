package com.example.paymybuddy.controller;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;
import com.example.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

//import jakarta.persistence.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new User());
        }
        return "users";
    }

    // créer un nouvel user
    @PostMapping("")
    public String create(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    // lire un user avec un id
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute User form, RedirectAttributes ra) {
        try {
            userService.updateUser(id, form);
            ra.addFlashAttribute("success", "Utilisateur mis à jour.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    // modifier un utilisateur
    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute User form) {
        userService.updateUser(id, form);
        return "redirect:/users";
    }

    // supprimer un utilisateur
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

}
