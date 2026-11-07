package com.prepmate.main;

import com.prepmate.util.PasswordUtil;

public class PasswordTest {

    public static void main(String[] args) {

    	String password = "DemoPassword123!";

        String hash = PasswordUtil.hashPassword(password);

        System.out.println("Password hashed successfully!");

        System.out.println(
                "Correct password: " +
                PasswordUtil.verifyPassword(password, hash)
        );

        System.out.println(
                "Wrong password: " +
                PasswordUtil.verifyPassword("wrong123", hash)
        );
    }
}