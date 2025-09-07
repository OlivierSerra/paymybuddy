
package com.example.paymybuddy.controller.view;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.paymybuddy.dto.RegisterForm;
import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String Form(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }
    /*
     * @GetMapping
     * public String Form(Model model) {
     * if (!model.containsAttribute("form")) {
     * model.addAttribute("form", new RegisterForm());
     * }
     * return "register";
     * }
     */

    @PostMapping
    public String submit(@Valid @ModelAttribute("registerForm") RegisterForm form,
            BindingResult br,
            RedirectAttributes ra) {
        if (br.hasErrors())
            return "register";

        if (userService.existsByEmail(form.getEmail())) {
            br.rejectValue("email", "duplicate", "Un compte existe déjà avec cet email.");
            return "register";
        }
        User u = new User();
        u.setUsername(form.getUsername());
        u.setEmail(form.getEmail());
        u.setPassword(form.getPassword());
        u.setOnboardingCompleted(false);

        userService.createUser(u);

        ra.addFlashAttribute("info", "Compte créé. Connectez-vous pour compléter votre profil.");
        return "redirect:/login";
    }

}
