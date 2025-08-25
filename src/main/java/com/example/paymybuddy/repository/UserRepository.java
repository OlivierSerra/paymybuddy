package com.example.paymybuddy.repository;

import com.example.paymybuddy.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//utilise le jpaRepository qui a des fonctions natives
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // utile pour la génération automatique d'un username (à changer par la suite)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
