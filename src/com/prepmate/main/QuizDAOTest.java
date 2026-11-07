package com.prepmate.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.prepmate.dao.QuizDAO;
import com.prepmate.model.QuizAttempt;
import com.prepmate.model.AttemptAnswer;

public class QuizDAOTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        QuizDAO quizDAO = new QuizDAO();

        try {
            System.out.println("===== PREPMATE QUIZ TEST =====");

            System.out.print("Enter User ID: ");
            int userId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Topic ID: ");
            int topicId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Question ID: ");
            int questionId = Integer.parseInt(sc.nextLine());

            // 1. Create quiz attempt
            QuizAttempt attempt =
                    new QuizAttempt(userId, topicId, 1);

            int attemptId = quizDAO.createAttempt(attempt);

            if (attemptId == -1) {
                System.out.println("Quiz attempt creation failed!");
                return;
            }

            System.out.println("Quiz attempt created! ID: " + attemptId);

            // 2. Save answer
            AttemptAnswer answer = new AttemptAnswer(
                    attemptId,
                    questionId,
                    "Object-Oriented Programming",
                    true
            );

            boolean saved = quizDAO.saveAnswer(answer);

            System.out.println(
                    saved ? "Answer saved successfully!"
                          : "Answer saving failed!"
            );

            // 3. Complete attempt
            boolean completed =
                    quizDAO.completeAttempt(attemptId, 1, 100.0);

            System.out.println(
                    completed ? "Quiz completed successfully!"
                              : "Quiz completion failed!"
            );

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numeric IDs!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}