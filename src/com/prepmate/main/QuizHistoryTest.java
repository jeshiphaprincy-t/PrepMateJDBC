
package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuizDAO;
import com.prepmate.model.QuizAttempt;

public class QuizHistoryTest {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        QuizDAO quizDAO = new QuizDAO();

        System.out.print("Enter User ID: ");
        int userId = sc.nextInt();

        List<QuizAttempt> attempts =
                quizDAO.getAttemptsByUser(userId);

        if (attempts.isEmpty()) {
            System.out.println("No quiz history found!");
        } else {

            System.out.println("\n========== QUIZ HISTORY ==========");

            for (QuizAttempt attempt : attempts) {

                System.out.println("\nAttempt ID: "
                        + attempt.getAttemptId());

                System.out.println("Topic ID: "
                        + attempt.getTopicId());

                System.out.println("Total Questions: "
                        + attempt.getTotalQuestions());

                System.out.println("Correct Answers: "
                        + attempt.getCorrectAnswers());

                System.out.printf("Score: %.2f%%%n",
                        attempt.getScore());

                System.out.println("Started At: "
                        + attempt.getStartedAt());

                System.out.println("Completed At: "
                        + attempt.getCompletedAt());
            }

            System.out.println("\n==================================");
        }

        sc.close();
    }
}
