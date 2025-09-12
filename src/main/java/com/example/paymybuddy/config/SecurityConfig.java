package com.example.paymybuddy.config;

import com.example.paymybuddy.repository.UserRepository;
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
                                .authenticationProvider(authenticationProvider)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login", "/register", "/favicon.ico",
                                                                "/style.css", "/css/**", "/js/**", "/images/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/landingPageUser/**", "/users/**", "/buddies/**",
                                                                "/transactions/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginPage("/login").permitAll()
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .failureUrl("/login?error")
                                                .defaultSuccessUrl("/onboarding", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(UserRepository userRepository) {
                return loginEmail -> userRepository.findByEmail(loginEmail)
                                .map(u -> org.springframework.security.core.userdetails.User
                                                .withUsername(u.getEmail())
                                                .password(u.getPassword())
                                                .roles(u.getRole())
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginEmail));
        }
}
