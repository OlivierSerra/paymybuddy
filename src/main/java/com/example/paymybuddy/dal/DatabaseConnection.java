package com.example.paymybuddy.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/paymybuddy";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("O3248Jam!&17");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/paymybuddy", "root", "O3248Jam!&17");
    }
}
