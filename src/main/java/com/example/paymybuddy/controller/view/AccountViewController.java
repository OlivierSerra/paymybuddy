package com.example.paymybuddy.controller.view;

import java.lang.ProcessBuilder.Redirect;
import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.service.UserService;

@Controller
@RequestMapping("/account")
public class AccountViewController {
    private final UserService userService;

    public AccountViewController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("")
    public String account(Model model, Principal principal) {
        User me = userService.getCurrentUser(principal);
        model.addAttribute("user", me);
        model.addAttribute("balance", me.getBalance());
        return "account";
    }

    @PostMapping("/creditAccount")
    public String creditAccount(@RequestParam BigDecimal amount,
            Principal principal,
            RedirectAttributes ra) {
        try {
            userService.creditAccount(principal, amount);
            ra.addFlashAttribute("success", "Compte crédité de " + amount + " €.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/withdrawMoney")
    public String withdrawMoney(@RequestParam BigDecimal amount,
            Principal principal,
            RedirectAttributes ra) {
        try {
            userService.withdrawMoney(principal, amount);
            ra.addFlashAttribute("success", "Virement vers compte bancaire de " + amount + " €.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/account";
    }
}
