package com.prepmate.main;

import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuestionOptionDAO;
import com.prepmate.model.QuestionOption;

public class QuestionOptionTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        QuestionOptionDAO dao = new QuestionOptionDAO();

        int questionId = 1;

        System.out.println("Enter four options for Question ID: " + questionId);

        String[] labels = {"A", "B", "C", "D"};

        for (String label : labels) {

            System.out.print("Enter option " + label + ": ");
            String optionText = sc.nextLine();

            System.out.print("Is this the correct answer? (yes/no): ");
            boolean correct = sc.nextLine().equalsIgnoreCase("yes");

            QuestionOption option = new QuestionOption(
                    questionId, label, optionText, correct);

            if (dao.addOption(option)) {
                System.out.println("Option " + label + " added!");
            } else {
                System.out.println("Failed to add option " + label);
            }
        }

        System.out.println("\n--- Saved Options ---");

        List<QuestionOption> options =
                dao.getOptionsByQuestion(questionId);

        for (QuestionOption option : options) {
            System.out.println(
                    option.getOptionLabel() + ". "
                    + option.getOptionText());
        }

        sc.close();
    }
}