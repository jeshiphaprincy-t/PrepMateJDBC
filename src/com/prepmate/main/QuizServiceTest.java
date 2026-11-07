package com.prepmate.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.prepmate.service.QuizService;

public class QuizServiceTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        QuizService quizService = new QuizService();

        try {

            System.out.println("===== PREPMATE ANSWER EVALUATION =====");

            System.out.print("Enter Question ID: ");
            int questionId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter your answer: ");
            String userAnswer = sc.nextLine();

            boolean result = quizService.checkAnswer(
                    questionId, userAnswer
            );

            if (result) {
                System.out.println("\nCorrect answer! Great job!");
            } else {
                System.out.println("\nIncorrect answer. Try again!");
            }

        } catch (NumberFormatException e) {

            System.out.println("Please enter a valid Question ID!");

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {
            sc.close();
        }
    }
}