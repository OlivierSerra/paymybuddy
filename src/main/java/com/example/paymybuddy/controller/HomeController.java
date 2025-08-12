
package com.example.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /*
     * @GetMapping("/")
     * public String home() {
     * return "redirect:/users/view";
     * }
     * 
     */

    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("username", "Olivier");
        return "home";
    }
}
