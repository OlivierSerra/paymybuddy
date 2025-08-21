package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.service.UserService;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LandingPageController {

    private final UserService userService;
    private final UserRepository userRepository;

    public LandingPageController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    /*
     * @GetMapping("/")
     * public String rootRedirect() {
     * return "redirect:/landingPageUser";
     * }
     * 
     */

    @GetMapping("/landingPageUser")
    public String landing(Model model, Principal principal) {
        if (principal == null) {
            // visiteur : landing publique
            return "landingPageUser"; // templates/landingPageUser.html
        }
        var me = userService.getRequiredByEmail(principal.getName());

        if (!Boolean.TRUE.equals(me.getOnboardingCompleted())) {
            return "redirect:/onboarding";
        }
        return "redirect:/me";
    }
}
