
package com.prepmate.service;

import java.sql.SQLException;

import com.prepmate.dao.UserDAO;
import com.prepmate.model.User;
import com.prepmate.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    // USER REGISTRATION
    public String register(
            String fullName,
            String email,
            String password) {

        // Validate name
        if (fullName == null || fullName.isBlank()) {
            return "Name cannot be empty!";
        }

        // Validate email
        if (email == null ||
                !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Please enter a valid email!";
        }

        // Validate password
        if (password == null || password.length() < 8) {
            return "Password must contain at least 8 characters!";
        }

        try {

            email = email.trim().toLowerCase();

            // Check duplicate email
            if (userDAO.findByEmail(email) != null) {
                return "This email is already registered!";
            }

            // Hash password
            String passwordHash =
                    PasswordUtil.hashPassword(password);

            // Create user object
            User user = new User(
                    fullName.trim(),
                    email,
                    passwordHash
            );

            // Save user
            boolean inserted = userDAO.registerUser(user);

            if (inserted) {
                return "Registration successful!";
            }

            return "Registration failed!";

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                return "This email is already registered!";
            }

            e.printStackTrace();
            return "Database error during registration!";
        }
    }

    // USER LOGIN
    public User login(String email, String password) {

        if (email == null || email.isBlank() ||
                password == null || password.isEmpty()) {
            return null;
        }

        try {

            User user = userDAO.findByEmail(
                    email.trim().toLowerCase()
            );

            if (user == null) {
                return null;
            }

            boolean valid = PasswordUtil.verifyPassword(
                    password,
                    user.getPasswordHash()
            );

            if (valid) {
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
