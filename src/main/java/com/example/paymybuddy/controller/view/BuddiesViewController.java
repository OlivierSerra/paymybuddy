package com.example.paymybuddy.controller.view;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/buddies")
public class BuddiesViewController {

    private final UserRepository userRepository;

    public BuddiesViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Liste des buddies pour un "owner" (à remplacer par l’utilisateur connecté
    // plus tard)
    @GetMapping
    public String list(@RequestParam String owner, Model model) {
        User u = userRepository.findByUsername(owner)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Set<User> buddies = u.getBuddies(); // assure-toi que c’est initialisé à new HashSet<>() dans l’entité

        // On compare par id (plus fiable que contains si equals/hashCode ne sont pas
        // override)
        Set<Integer> buddyIds = buddies.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<User> candidates = userRepository.findAll().stream()
                .filter(x -> x.getId() != u.getId()) // exclure soi-même
                .filter(x -> !buddyIds.contains(x.getId())) // exclure déjà buddies
                .collect(Collectors.toList());

        model.addAttribute("owner", u);
        model.addAttribute("buddies", buddies);
        model.addAttribute("candidates", candidates);
        return "buddies"; // templates/buddies.html
    }

    @PostMapping("/add")
    public String add(@RequestParam String owner, @RequestParam String buddy, RedirectAttributes ra) {
        User u = userRepository.findByUsername(owner)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        User b = userRepository.findByUsername(buddy)
                .orElseThrow(() -> new RuntimeException("Buddy not found"));

        if (u.getId() == b.getId()) {
            ra.addFlashAttribute("error", "Impossible de vous ajouter vous-même.");
            return "redirect:/buddies?owner=" + owner;
        }

        boolean alreadyBuddy = u.getBuddies().stream().anyMatch(bb -> bb.getId() == b.getId());
        if (alreadyBuddy) {
            ra.addFlashAttribute("error", "Cet utilisateur est déjà dans vos buddies.");
            return "redirect:/buddies?owner=" + owner;
        }

        u.getBuddies().add(b);
        userRepository.save(u);
        ra.addFlashAttribute("success", "Buddy ajouté !");
        return "redirect:/buddies?owner=" + owner;
    }

    @PostMapping("/remove")
    public String remove(@RequestParam String owner, @RequestParam String buddy, RedirectAttributes ra) {
        User u = userRepository.findByUsername(owner)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        User b = userRepository.findByUsername(buddy)
                .orElseThrow(() -> new RuntimeException("Buddy not found"));

        // retirer par id pour être sûr
        u.getBuddies().removeIf(bb -> bb.getId() == b.getId());
        userRepository.save(u);

        ra.addFlashAttribute("success", "Buddy retiré.");
        return "redirect:/buddies?owner=" + owner;
    }
}
