package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuizDAO;

public class RecommendationTest {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        QuizDAO quizDAO = new QuizDAO();

        System.out.print("Enter User ID: ");
        int userId = sc.nextInt();

        System.out.println("\n========== PREPMATE RECOMMENDATIONS ==========");

        List<String> unpractisedTopics =
                quizDAO.getUnpractisedTopics(userId);

        System.out.println("\nTopics you haven't practised:");

        if (unpractisedTopics.isEmpty()) {
            System.out.println("You've practised all available topics!");
        } else {
            for (String topic : unpractisedTopics) {
                System.out.println("- " + topic);
            }
        }

        System.out.println("\nRecommended next topic:");

        System.out.println(quizDAO.getRecommendedTopic(userId));

        System.out.println("\n==============================================");

        sc.close();
    }
}