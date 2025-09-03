package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/buddies")
public class BuddiesViewController {

        private final UserRepository userRepository;

        public BuddiesViewController(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        // Liste des buddies de l'utilisateur connecté
        @GetMapping
        public String list(Principal principal, Model model) {
                User me = userRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Set<User> buddies = me.getBuddies();

                Set<Integer> buddyIds = buddies.stream()
                                .map(User::getId)
                                .collect(Collectors.toSet());

                List<User> candidates = userRepository.findAll().stream()
                                .filter(u -> !Objects.equals(u.getId(), me.getId())) // exclure soi-même
                                .filter(u -> !buddyIds.contains(u.getId())) // exclure déjà buddies
                                .collect(Collectors.toList());

                // Pour ne pas toucher à ton template existant
                model.addAttribute("owner", me);
                model.addAttribute("buddies", buddies);
                model.addAttribute("candidates", candidates);
                return "buddies";
        }

        /*
         * // Ajouter un buddy pour l'utilisateur connecté
         * 
         * @PostMapping("/add")
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
         * @PostMapping("/remove")
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
         *
         *
         */

}
