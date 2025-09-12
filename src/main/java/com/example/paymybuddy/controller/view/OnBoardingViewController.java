package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import com.example.paymybuddy.dto.OnboardingForm;
import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
public class OnBoardingViewController {

    private final UserService userService;

    public OnBoardingViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/onboarding")
    public String onboarding(Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + principal.getName()));
        OnboardingForm form = new OnboardingForm();
        form.setUsername(u.getUsername());
        form.setEmail(u.getEmail());
        form.setBankAccount(u.getBankAccount());
        model.addAttribute("onboardingForm", form);
        return "onboarding";
    }

    @PostMapping("/onboarding")
    public String completeOnboarding(
            @Valid @ModelAttribute("onboardingForm") OnboardingForm form,
            BindingResult binding,
            Principal principal,
            RedirectAttributes ra) {

        if (binding.hasErrors()) {
            return "onboarding";
        }

        User u = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + principal.getName()));
        u.setUsername(form.getUsername());
        u.setEmail(form.getEmail());
        u.setBankAccount(form.getBankAccount());
        u.setOnboardingCompleted(true);
        userService.createUser(u);

        ra.addFlashAttribute("success", "Profil complété !");
        return "redirect:/me";
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
