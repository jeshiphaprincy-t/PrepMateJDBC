package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserSearch {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your email: ");
        String email = sc.nextLine();

        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            Connection con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection failed!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, email);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("User Found!");
                System.out.println("Name: " + rs.getString("full_name"));
                System.out.println("Email: " + rs.getString("email"));
            } else {
                System.out.println("User Not Found!");
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }
}