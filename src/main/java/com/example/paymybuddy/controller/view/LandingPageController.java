package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.service.UserService;
import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.ProcessBuilder.Redirect;
import java.security.Principal;
import java.util.Objects;

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
        User me = userService.getRequiredByEmail(principal.getName());

        if (!Boolean.TRUE.equals(me.getOnboardingCompleted())) {
            return "redirect:/onboarding";
        }
        return "landingPageUser";
    }
    /*
     * // Ajouter un buddy pour l'utilisateur connecté
     * 
     * @PostMapping("/buddies/add")
     * public String add(@RequestParam String buddyEmail, Principal principal,
     * RedirectAttributes ra) {
     * User me = userRepository.findByEmail(principal.getName())
     * .orElseThrow(() -> new RuntimeException("User not found"));
     * User b = userRepository.findByEmail(buddyEmail)
     * .orElseThrow(() -> new RuntimeException("Buddy not found"));
     * 
     * if (Objects.equals(me.getId(), b.getId())) {
     * ra.addFlashAttribute("error", "Impossible de vous ajouter vous-même.");
     * return "redirect:/buddies";
     * }
     * 
     * boolean alreadyBuddy = me.getBuddies().stream()
     * .anyMatch(bb -> Objects.equals(bb.getId(), b.getId()));
     * if (alreadyBuddy) {
     * ra.addFlashAttribute("error", "Cet utilisateur est déjà dans vos buddies.");
     * return "redirect:/buddies";
     * }
     * 
     * me.getBuddies().add(b);
     * userRepository.save(me);
     * ra.addFlashAttribute("success", "Buddy ajouté !");
     * return "redirect:/buddies";
     * }
     * 
     * // Retirer un buddy
     * 
     * @PostMapping("/buddies/remove")
     * public String remove(@RequestParam String buddyEmail, Principal principal,
     * RedirectAttributes ra) {
     * User me = userRepository.findByEmail(principal.getName())
     * .orElseThrow(() -> new RuntimeException("User not found"));
     * User b = userRepository.findByEmail(buddyEmail)
     * .orElseThrow(() -> new RuntimeException("Buddy not found"));
     * 
     * me.getBuddies().removeIf(bb -> Objects.equals(bb.getId(), b.getId()));
     * userRepository.save(me);
     * 
     * ra.addFlashAttribute("success", "Buddy retiré.");
     * return "redirect:/buddies";
     * }
     * //---------------------------------------------------------------------------
     * ----------
     */

    // buddies: vue + ajout
    /*
     * @GetMapping("/buddies")
     * public String showBuddies(Model model, Principal principal) {
     * User me = userService.getRequiredByEmail(principal.getName());
     * model.addAttribute("me", me);
     * model.addAttribute("buddies", me.getBuddies());
     * return "buddies";
     * }
     * 
     * @PostMapping("/buddies")
     * public String addBuddy(@RequestParam("email") String buddyEmail,
     * Principal principal,
     * RedirectAttributes ra) {
     * User me = userService.getRequiredByEmail(principal.getName());
     * }
     * 
     * if (buddyEmail == null || buddyEmail.isBlank()) {
     * ra.addFlashAttribute("error", "Veuillez saisir une adresse e-mail.");
     * return "redirect:/buddies";
     * }
     * )
     */

}
