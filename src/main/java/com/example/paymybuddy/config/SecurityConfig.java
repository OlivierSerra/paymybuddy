package com.example.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    // configuration du bean par rapport à la sécurité

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/favicon.ico").permitAll()
                        .requestMatchers("/style.css", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasAuthority("ADMIN")
                        .requestMatchers("/contacts/**", "/transactions/**").authenticated()
                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated() // ← y compris "/": requiert login
                )
                .formLogin(form -> form
                        // commente cette ligne si tu veux la page de login par défaut :
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/Users", true) // <- envoie toujours l’admin ici
                        .failureUrl("/login?error")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    // permet de créer la variable qui va recevoir l'encodage du password de
    // l'utilisateur.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}