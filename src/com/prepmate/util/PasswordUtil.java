
package com.prepmate.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    // Convert password into a secure hash
    public static String hashPassword(String password) {

        try {

            byte[] salt = new byte[SALT_LENGTH];

            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = factory.generateSecret(spec).getEncoded();

            Base64.Encoder encoder = Base64.getEncoder();

            return ITERATIONS + ":" +
                    encoder.encodeToString(salt) + ":" +
                    encoder.encodeToString(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Password hashing failed", e
            );
        }
    }

    // Verify entered password
    public static boolean verifyPassword(
            String password,
            String storedHash) {

        try {

            String[] parts = storedHash.split(":");

            int iterations = Integer.parseInt(parts[0]);

            byte[] salt = Base64.getDecoder().decode(parts[1]);

            byte[] originalHash =
                    Base64.getDecoder().decode(parts[2]);

            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    originalHash.length * 8
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] newHash = factory.generateSecret(spec).getEncoded();

            if (newHash.length != originalHash.length) {
                return false;
            }

            int difference = 0;

            for (int i = 0; i < newHash.length; i++) {
                difference |= newHash[i] ^ originalHash[i];
            }

            return difference == 0;

        } catch (Exception e) {

            return false;
        }
    }
}
