package com.example.paymybuddy.config;

import com.example.paymybuddy.repository.UserRepository;

//import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // indication dans la trace que "No authentificationProviders ... returning null
        // donc création d'un DaoAuthenticationProvider"
        @Bean
        public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                        PasswordEncoder encoder) {
                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
                authenticationProvider.setUserDetailsService(userDetailsService);
                authenticationProvider.setPasswordEncoder(encoder);
                return authenticationProvider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider)
                        throws Exception {
                http
                                // .csrf(csrf -> csrf.disable()) // pour tes formulaires actuels sans token CSRF
                                .authenticationProvider(authenticationProvider)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login", "/favicon.ico",
                                                                "/style.css", "/css/**", "/js/**", "/images/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/ListeUsers", "/ListeTransactions").permitAll()
                                                .requestMatchers("/buddies/**", "/transactions/**", "/landingPageUser/")
                                                .authenticated()
                                                .requestMatchers("/landingPageUser/**", "/user/**", "/buddies/**",
                                                                "/transactions/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login").permitAll()
                                                .usernameParameter("email")
                                                .defaultSuccessUrl("/landingPageUser", false)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout"));

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(UserRepository userRepository) {
                return loginEmail -> userRepository.findByEmail(loginEmail)
                                .map(u -> org.springframework.security.core.userdetails.User
                                                .withUsername(u.getEmail())
                                                .password(u.getPassword()) // doit être BCrypt !
                                                .roles(u.getRole()) // ex: "USER" ou "ADMIN" (sans le prefix "ROLE_")
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginEmail));
        }
}
