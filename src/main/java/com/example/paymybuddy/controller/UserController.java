package com.example.paymybuddy.controller;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//import jakarta.persistence.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // créer un nouvel user
    @PostMapping("")
    public String create(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/MyUsers";
    }

    // lire un user avec un id
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    // modifier un utilisateur
    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute User form) {
        userService.updateUser(id, form);
        return "redirect:/MyUsers";
    }

    // supprimer un utilisateur
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/MyUsers";
    }

}
