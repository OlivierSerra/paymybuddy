package com.example.paymybuddy.config;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setRole("ADMIN");
                admin.setPassword(encoder.encode("admin4321"));
                admin.setBalance(new BigDecimal("100"));
                admin.setBankAccount(789456123);
                userRepository.save(admin);
                System.out.println("admin créé");

            }
            if (userRepository.findByUsername("aline").isEmpty()) {
                User user = new User();
                user.setUsername("aline");
                user.setEmail("aline@gmail.com");
                user.setPassword(encoder.encode("r1"));
                user.setRole("USER");
                user.setBalance(new BigDecimal("100"));
                user.setBankAccount(321654987);
                userRepository.save(user);
                System.out.println("aline créé");

            }
            if (userRepository.findByUsername("alain").isEmpty()) {
                User user = new User();
                user.setUsername("alain");
                user.setEmail("alain@gmail.com");
                user.setPassword(encoder.encode("r2"));
                user.setRole("USER");
                user.setBalance(new BigDecimal("100"));
                user.setBankAccount(456123789);
                userRepository.save(user);
                System.out.println("alain créé");

            }
        };
    }
}
