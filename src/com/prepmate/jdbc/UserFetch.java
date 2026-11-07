package com.prepmate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserFetch {

    public static void main(String[] args) {

        String sql = "SELECT * FROM users";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                System.out.println("User ID: " + rs.getInt("user_id"));
                System.out.println("Name: " + rs.getString("full_name"));
                System.out.println("Email: " + rs.getString("email"));

                System.out.println("--------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}