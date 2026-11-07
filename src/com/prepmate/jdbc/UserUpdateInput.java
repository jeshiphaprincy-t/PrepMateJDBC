package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class UserUpdateInput {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter User ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter New Email: ");
        String email = sc.nextLine();

        String sql = "UPDATE users SET email = ? WHERE user_id = ?";

        try {
            Connection con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection failed!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, email);
            pst.setInt(2, id);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Email updated successfully!");
            } else {
                System.out.println("User ID not found!");
            }

            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }
}