package com.example.paymybuddy.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.paymybuddy.dto.TransactionRequest;
import com.example.paymybuddy.service.TransactionService;
import com.example.paymybuddy.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.paymybuddy.dto.TransactionRequest;
import com.example.paymybuddy.model.Transaction;
import com.example.paymybuddy.repository.TransactionRepository;
import com.example.paymybuddy.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/transactions")
public class TransactionsViewController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public TransactionsViewController(UserRepository userRepository,
            TransactionRepository transactionRepository,
            TransactionService transactionService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @GetMapping("/new")
    public String newTransaction(Model model) {
        // model.addAttribute("transaction", new TransactionRequest());
        if (!model.containsAttribute("transaction")) {
            model.addAttribute("transaction", new TransactionRequest());
        }

        model.addAttribute("receivers", userRepository.findAll());
        return "transaction";
    }

    @PostMapping
    public String createTransaction(@Valid @ModelAttribute("transaction") TransactionRequest form,
            BindingResult br,
            @RequestParam String senderUsername, // temporaire
            RedirectAttributes ra) {
        if (br.hasErrors()) {
            // renvoyer le formulaire avec les erreurs et les valeurs saisies
            ra.addFlashAttribute("org.springframework.validation.BindingResult.transaction", br);
            ra.addFlashAttribute("transaction", form);
            return "redirect:/transactions/new";
        }
        try {
            transactionService.makeTransaction(
                    senderUsername,
                    form.getReceiverUsername(),
                    form.getAmount(),
                    form.getDescription());
            ra.addFlashAttribute("success", "Virement effectué !");
            return "redirect:transactions/ListeTransactions";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("transaction", form);
            return "redirect:/transactions/new";
        }
    }

    @GetMapping("/ListeTransactions")
    public String listTransaction(Model model) {
        model.addAttribute("transactions",
                transactionRepository.findAllByOrderByTimestampDesc());
        return "ListeTransactions";
    }
    /**
     * fonctionnement=> TransactionRequest.receiverUsername ->service fait
     * findByUsername(...)
     * -> remplit transaction.userReceiver / transaction.userSender
     * -> sauvegarde → liste affiche t.userSender.username & t.userReceiver.username
     * 
     */

}
