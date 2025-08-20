package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.security.Principal;

@Controller
// @RequestMapping("/users")
public class UserViewController {
    private final UserService userService;

    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    // GET /onboarding : affiche le formulaire avec les données actuelles
    @GetMapping("/onboarding")
    public String onboarding(Model model, Principal principal) {
        User me = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Si déjà complété, on peut envoyer vers /users/me
        if (Boolean.TRUE.equals(me.getOnboardingCompleted())) {
            return "redirect:/users/me";
        }

        model.addAttribute("user", me);
        return "/onboarding"; // ton template
    }

    // POST onboarding : traite le formulaire
    @PostMapping("/onboarding")
    public String completeOnboarding(@Valid @ModelAttribute("user") User form,
            BindingResult binding,
            Principal principal,
            Model model) {
        if (binding.hasErrors()) {
            // réaffiche le formulaire avec erreurs
            return "/onboarding";
        }

        userService.completeOnboarding(principal.getName(), form);
        return "redirect:/me?onboarded";
    }

    // (facultatif) ta page profil
    @GetMapping("/me")
    public String showMyProfile(Model model, Principal principal) {
        User me = userService.getRequiredByUsername(principal.getName());
        model.addAttribute("user", me);
        return "users/me";
    }

}
