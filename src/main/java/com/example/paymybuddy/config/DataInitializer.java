package com.example.paymybuddy.config;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin4321"));
                admin.setRole("ADMIN");
                admin.setBalance(new BigDecimal("100"));
                userRepository.save(admin);
                System.out.println("admin créé");

            }
        };
    }
}
