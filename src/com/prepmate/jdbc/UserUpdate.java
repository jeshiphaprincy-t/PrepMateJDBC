package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserUpdate {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/prepmate_db";
        String username = "root";
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("DB_PASSWORD is not set.");
            return;
        }
        
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";

        try {
            Connection con = DriverManager.getConnection(url, username, password);

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, "priya123@gmail.com");
            pst.setInt(2, 1);

            int rows = pst.executeUpdate();

            System.out.println(rows + " user updated successfully!");

            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}