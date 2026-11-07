package com.prepmate.main;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import com.prepmate.dao.QuizDAO;

public class DashboardTest {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        QuizDAO quizDAO = new QuizDAO();

        System.out.print("Enter User ID: ");
        int userId = sc.nextInt();

        int totalAttempts = quizDAO.getTotalAttempts(userId);

        Map<String, Double> statistics =
                quizDAO.getScoreStatistics(userId);

        int totalQuestions =
                quizDAO.getTotalQuestionsAnswered(userId);

        System.out.println("\n========== PREPMATE DASHBOARD ==========");

        System.out.println("Total Quizzes Attempted: " + totalAttempts);

        System.out.printf("Average Score: %.2f%%%n",
                statistics.get("average"));

        System.out.printf("Highest Score: %.2f%%%n",
                statistics.get("highest"));

        System.out.println("Total Questions Answered: " + totalQuestions);

        System.out.println("\n========== SUBJECT PERFORMANCE ==========");

        Map<String, Double> subjects =
                quizDAO.getSubjectWisePerformance(userId);

        if (subjects.isEmpty()) {
            System.out.println("No subject performance available.");
        } else {
            for (Map.Entry<String, Double> entry : subjects.entrySet()) {
                System.out.printf("%s: %.2f%%%n",
                        entry.getKey(), entry.getValue());
            }
        }

        System.out.println("\n========== TOPICS TO IMPROVE ==========");

        Map<String, Double> weakTopics =
                quizDAO.getWeakTopics(userId);

        if (weakTopics.isEmpty()) {
            System.out.println("No weak topics found. Keep practising!");
        } else {
            for (Map.Entry<String, Double> entry : weakTopics.entrySet()) {
                System.out.printf("%s: %.2f%%%n",
                        entry.getKey(), entry.getValue());
            }
        }

        System.out.println("\n========================================");

        sc.close();
    }
}