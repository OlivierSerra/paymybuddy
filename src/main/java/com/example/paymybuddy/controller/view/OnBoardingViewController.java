package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.security.Principal;

@Controller
// @RequestMapping("/users")
public class OnBoardingViewController {

    private final UserService userService;

    public OnBoardingViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/onboarding")
    public String onboarding(Model model, Principal principal) {
        User me = userService.getRequiredByEmail(principal.getName());

        if (Boolean.TRUE.equals(me.getOnboardingCompleted())) {
            return "redirect:/me";
        }
        model.addAttribute("user", me);
        return "onboarding"; // ton template
    }

    @PostMapping("/onboarding")
    public String completeOnboarding(@Valid @ModelAttribute("user") User form,
            BindingResult binding,
            Principal principal,
            Model model,
            RedirectAttributes ra) {
        if (binding.hasErrors()) {
            // réaffiche le formulaire avec erreurs
            return "onboarding";
        }
        userService.completeOnboarding(principal.getName(), form);
        ra.addFlashAttribute("info", "Bienvenue ! Votre profil est prêt.");
        return "redirect:/buddies";
    }

    @GetMapping({ "/me", "/me/" })
    public String showMyProfile(Model model, Principal principal) {
        User me = userService.getRequiredByEmail(principal.getName());
        model.addAttribute("user", me);
        return "users";
    }

    @PostMapping({ "/me", "/me/" })
    public String updateMyProfile(@Valid @ModelAttribute("user") User form,
            BindingResult binding,
            Principal principal,
            Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("user", form);
            return "users/me";
        }
        userService.updateProfile(principal.getName(), form);
        return "redirect:/me";
    }

}
