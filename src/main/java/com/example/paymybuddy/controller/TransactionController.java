package com.example.paymybuddy.controller;

import com.example.paymybuddy.dto.TransactionRequest;
import com.example.paymybuddy.model.Transaction;
import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.TransactionRepository;
import com.example.paymybuddy.repository.UserRepository;
import com.example.paymybuddy.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transactions/view")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Transaction> makeTransaction(@Valid @RequestBody TransactionRequest request,
            Principal principal) {
        String senderUsername = principal.getName();
        Transaction transaction = transactionService.makeTransaction(
                senderUsername,
                request.getReceiverUsername(),
                request.getAmount(),
                request.getDescription());
        return ResponseEntity.ok(transaction);

    }

    // afficher l'ensemble des transactions d'un utilisateur
    @GetMapping("/sent")
    public List<Transaction> getTransactions(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepository.findByUserSender(user);
    }

    @GetMapping("/received")
    public List<Transaction> getReceived(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepository.findByUserSender(user);
    }
}
