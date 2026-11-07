package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserInsert {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/prepmate_db";
        String username = "root";
        String password = System.getenv("DB_PASSWORD");

        String sql = "INSERT INTO users (full_name, email, password_hash) VALUES (?, ?, ?)";

        if (password == null || password.isBlank()) {
            System.out.println("DB_PASSWORD is not set.");
            return;
        }
        
        try {
            Connection con = DriverManager.getConnection(url, username, password);

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, "Priya");
            pst.setString(2, "priya@gmail.com");
            pst.setString(3, "demo-hash");

            int rows = pst.executeUpdate();

            System.out.println(rows + " user inserted successfully!");

            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}