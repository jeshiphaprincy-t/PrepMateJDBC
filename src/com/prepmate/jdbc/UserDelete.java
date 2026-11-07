package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserDelete {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/prepmate_db";
        String username = "root";
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("DB_PASSWORD is not set.");
            return;
        }
        
        String sql = "DELETE FROM users WHERE user_id = ?";

        try {
            Connection con = DriverManager.getConnection(url, username, password);

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, 1);

            int rows = pst.executeUpdate();

            System.out.println(rows + " user deleted successfully!");

            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}