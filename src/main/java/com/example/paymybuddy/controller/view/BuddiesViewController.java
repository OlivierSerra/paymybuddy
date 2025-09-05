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
        public String list(Principal principal, Model model,
                        @ModelAttribute("error") String error,
                        @ModelAttribute("success") String success) {
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

        // Ajouter un buddy pour l'utilisateur connecté

        @PostMapping("/add")
        public String add(@RequestParam String buddyEmail, Principal principal,
                        RedirectAttributes ra) {
                String email = buddyEmail == null ? "" : buddyEmail.trim().toLowerCase();

                if (email.isEmpty()) {
                        ra.addFlashAttribute("error", "Veuillez saisir une adresse e-mail.");
                        return "redirect:/buddies";
                }

                User me = userRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (me.getEmail().equalsIgnoreCase(email)) {
                        ra.addFlashAttribute("error", "Impossible de vous ajouter vous-même.");
                        return "redirect:/buddies";
                }

                User buddy = userRepository.findByEmailIgnoreCase(email)
                                .orElse(null);

                if (buddy == null) {
                        ra.addFlashAttribute("error", "cet e-mail ne correspond à aucun user.");
                        return "redirect:/buddies";
                }

                boolean alreadyBuddy = me.getBuddies().stream().anyMatch(b -> Objects.equals(b.getId(), buddy.getId()));

                if (alreadyBuddy) {
                        ra.addFlashAttribute("error", "Cet utilisateur appartient déjà à vos buddies.");
                        return "redirect:/buddies";
                }

                me.getBuddies().add(buddy);
                userRepository.save(me);

                ra.addFlashAttribute("success", "Buddy ajouté!");
                return "redirect:/buddies";

        }

        // Retirer un buddy

        @PostMapping("/remove")
        public String remove(@RequestParam String buddyEmail, Principal principal,
                        RedirectAttributes ra) {

                String email = buddyEmail == null ? "" : buddyEmail.trim().toLowerCase();

                User me = userRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User buddy = userRepository.findByEmailIgnoreCase(email)
                                .orElse(null);

                if (buddy == null) {
                        ra.addFlashAttribute("error", "Aucun user ne correspond à cet email.");
                        return "redirect:/buddies";
                }

                boolean removed = me.getBuddies().removeIf(b -> Objects.equals(b.getId(), buddy.getId()));
                userRepository.save(me);

                ra.addFlashAttribute(removed ? "success" : "error",
                                removed ? "Buddy retiré" : "user pas dans la lsite buddies.");
                return "redirect:/buddies";
        }
}
