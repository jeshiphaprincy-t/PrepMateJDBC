
package com.prepmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.User;

public class UserDAO {

    // Save a new user
    public boolean registerUser(User user) throws SQLException {

        String sql = "INSERT INTO users " +
                "(full_name, email, password_hash) VALUES (?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, user.getFullName());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPasswordHash());

            int rows = pst.executeUpdate();

            return rows > 0;
        }
    }

    // Find a user using email
    public User findByEmail(String email) throws SQLException {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, email);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(
                            rs.getString("password_hash")
                    );

                    return user;
                }
            }
        }

        return null;
    }
}
