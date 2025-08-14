package com.example.paymybuddy.repository;

import com.example.paymybuddy.model.Transaction;
import com.example.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserSender(User userSender);

    List<Transaction> findByUserReceiver(User userReceiver);

    List<Transaction> findAllByOrderByTimestampDesc();
}
