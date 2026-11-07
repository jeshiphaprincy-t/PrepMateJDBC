
package com.prepmate.main;

import java.util.Scanner;

import com.prepmate.model.User;
import com.prepmate.service.AuthService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AuthService authService = new AuthService();

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("          PREPMATE AI");
            System.out.println("   INTERVIEW PREPARATION PLATFORM");
            System.out.println("================================");

            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":

                    System.out.println("\n--- USER REGISTRATION ---");

                    System.out.print("Enter full name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    String result = authService.register(
                            name, email, password
                    );

                    System.out.println("\n" + result);

                    break;

                case "2":

                    System.out.println("\n--- USER LOGIN ---");

                    System.out.print("Enter email: ");
                    String loginEmail = sc.nextLine();

                    System.out.print("Enter password: ");
                    String loginPassword = sc.nextLine();

                    User loggedInUser = authService.login(
                            loginEmail, loginPassword
                    );

                    if (loggedInUser != null) {

                        System.out.println("\nWelcome, "
                                + loggedInUser.getFullName() + "!");

                        System.out.println("Login successful!");

                    } else {

                        System.out.println(
                                "Invalid email or password!"
                        );
                    }

                    break;

                case "3":

                    running = false;

                    System.out.println(
                            "Thank you for using PrepMate AI!"
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid choice! Try again."
                    );
            }
        }

        sc.close();
    }
}
