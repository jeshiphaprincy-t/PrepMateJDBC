
package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/prepmate_db";
        String username = "root";
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("DB_PASSWORD environment variable is not set.");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                url, username, password)) {

            System.out.println("Database Connected Successfully!");

        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}
