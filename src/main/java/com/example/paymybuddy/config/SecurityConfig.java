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
                // désactivation de protections pour le dvpt à suppr en prod
                .csrf(csrf -> csrf.disable())
                // config accès URL
                .authorizeHttpRequests(auth -> auth
                        // nécessité d'une authentification pour les URL commençant par users
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/transactions/**").authenticated()
                        // pour les autres (home) tout est permis
                        .anyRequest().permitAll())
                // activation de l'authentification via le formulaire de connexion
                .httpBasic(withDefaults());

        return http.build();
        // renvoie un objet SecurityFilterChain permettant la connexion
    }

    // autoriser tout le monde _dvlpt
    /*
     * http
     * .csrf(csrf -> csrf.disable())
     * .authorizeHttpRequests(auth -> auth
     * .requestMatchers(HttpMethod.POST, "/users").permitAll() // autorise les POST
     * sur /users sans login
     * .requestMatchers("/users/**").authenticated()
     * .anyRequest().permitAll())
     * .httpBasic();
     */

    // pour autoriser uniquement un utilisateur
    /*
     * http
     * // désactivation de protections pour le dvpt à suppr en prod
     * .csrf(csrf -> csrf.disable())
     * // config accès URL
     * .authorizeHttpRequests(auth -> auth
     * // nécessité d'une authentification pour les URL commençant par users
     * .requestMatchers("/users/**").authenticated()
     * // pour les autres (home) tout est permis
     * .anyRequest().permitAll())
     * // activation de l'authentification via le formulaire de connexion
     * .httpBasic();
     */

    // permet de créer la variable qui va recevoir l'encodage du password de
    // l'utilisateur.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}