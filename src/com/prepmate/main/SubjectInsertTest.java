package com.prepmate.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.prepmate.dao.SubjectDAO;

public class SubjectInsertTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        SubjectDAO subjectDAO = new SubjectDAO();

        System.out.print("Enter subject name: ");
        String subjectName = sc.nextLine();

        try {

            boolean result = subjectDAO.addSubject(subjectName);

            if (result) {
                System.out.println("Subject added successfully!");
            } else {
                System.out.println("Subject already exists!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}