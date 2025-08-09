package com.example.paymybuddy.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

//Je dois encore intégrer connections au USER 
@Entity
@Table(name = "user") // ou "users", selon ton schéma
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String role;

    public User() {
    }

    // Constructeur de User
    public User(int id, String username, String email, String password, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
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

}