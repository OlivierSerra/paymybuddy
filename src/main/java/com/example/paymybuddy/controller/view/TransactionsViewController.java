package com.example.paymybuddy.controller.view;

// ...
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import com.example.paymybuddy.dto.TransactionRequest;
import com.example.paymybuddy.model.Transaction;
import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.TransactionRepository;
import com.example.paymybuddy.repository.UserRepository;
import com.example.paymybuddy.service.TransactionService;

// ...

@Controller
@RequestMapping("/transactions")
public class TransactionsViewController {
    // ... ctor identique

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionsViewController(TransactionService transactionService,
            TransactionRepository transactionRepository,
            UserRepository userRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String newTransaction(Model model, Principal principal) {
        // model.addAttribute("transaction", new TransactionRequest());
        if (!model.containsAttribute("transaction")) {
            model.addAttribute("transaction", new TransactionRequest());
        }
        /*
         * List<User> users = userRepository.findAll().stream()
         * .filter(u -> !u.getEmail().equalsIgnoreCase(principal.getName()))
         * .toList();
         */
        model.addAttribute("users", userRepository.findAll());
        return "transaction";
    }

    @GetMapping("/ListeTransactions")
    public String listTransaction(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupère envoyées + reçues, fusionne, déduplique par id, trie par timestamp
        // DESC
        List<Transaction> myTransactions = Stream
                .concat(
                        transactionRepository.findByUserSender(user).stream(),
                        transactionRepository.findByUserReceiver(user).stream())
                .collect(LinkedHashMap<Integer, Transaction>::new, // garantit l'ordre d'insertion
                        (map, tx) -> map.put(tx.getId(), tx), // dédup par id
                        LinkedHashMap::putAll)
                .values().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();

        model.addAttribute("transactions", myTransactions);
        return "ListeTransactions"; // templates/ListeTransactions.html
    }

    @PostMapping
    public String createTransaction(@Valid @ModelAttribute("transaction") TransactionRequest form,
            BindingResult br,
            RedirectAttributes ra,
            Principal principal) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.transaction", br);
            ra.addFlashAttribute("transaction", form);
            return "redirect:/transactions/new";
        }
        try {
            transactionService.makeTransaction(
                    principal.getName(),
                    form.getReceiverEmail(),
                    form.getAmount(),
                    form.getDescription());
            ra.addFlashAttribute("success", "Virement effectué !");
            return "redirect:/transactions/ListeTransactions"; // <-- slash corrigé
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("transaction", form);
            return "redirect:/transactions/new";
        }
    }
}
