package com.example.paymybuddy.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;

//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

//a besoin d’un moyen de retrouver un utilisateur 
//à partir d’un nom d’utilisateur (username) fourni lors de la connexion.
// donc charger un utilisateur depuis ta base de données lors de l’authentification.

@Service
public class CustomUserDetailsService implements UserDetailsService {
    // créer un variable userRepository générée par Repository pour ajouter des
    // comportements à la variable
    // notamment celui de pouvoir supporter l'authentification
    @Autowired
    private UserRepository userRepository;

    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // font de userREpository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username)); // gestion d'une
                                                                                                 // exception à la
                                                                                                 // và-vite
        // gèrer l'authentification - On regarde si on trouve le user par son username
        // et son password
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                // on le cherche gràce aussi à son rôle
                List.of(new SimpleGrantedAuthority(user.getRole())));
    }

}
