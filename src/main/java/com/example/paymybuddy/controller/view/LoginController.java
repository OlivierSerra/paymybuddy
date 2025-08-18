
package com.example.paymybuddy.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /*
     * @GetMapping("/login")
     * public String login(Model model) {
     * return "login";
     * }
     */

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
