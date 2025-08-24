package com.example.paymybuddy.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//Je dois encore intégrer connections au USER 
@Entity
@Table(name = "users") // ou "users", selon ton schéma
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String username;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Integer bankAccount;

    private Boolean onboardingCompleted = false;

    public User() {
    }

    // Constructeur de User
    public User(int id, String username, String email, String password, BigDecimal balance, Integer bankAccount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.bankAccount = bankAccount;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // role de celui qui se connecte (user ou admin)
    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(Integer bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Boolean getOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(Boolean v) {
        this.onboardingCompleted = v;
    }

    // les connexions

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_buddies", joinColumns = @JoinColumn(name = "user_id"), // clé étrangères utilisateur
                                                                                   // propriétaire
            inverseJoinColumns = @JoinColumn(name = "buddy_id"), // clé étrangères vers le buddy //pour la contrainte
                                                                 // d'unicité.
            uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "buddy_id" }))
    private Set<User> buddies = new HashSet<>();

    public Set<User> getBuddies() {
        return buddies;
    }

    public void setBuddies(Set<User> buddies) {
        this.buddies = buddies;
    }

}