package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.TopicDAO;
import com.prepmate.model.Topic;

public class TopicTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TopicDAO topicDAO = new TopicDAO();

        try {

            System.out.println("===== PREPMATE TOPIC MANAGEMENT =====");

            System.out.print("Enter Subject ID: ");
            int subjectId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Topic Name: ");
            String topicName = sc.nextLine();

            Topic topic = new Topic(subjectId, topicName);

            boolean result = topicDAO.addTopic(topic);

            if (result) {
                System.out.println("Topic added successfully!");
            } else {
                System.out.println("Topic already exists!");
            }

            System.out.println("\n===== TOPICS =====");

            List<Topic> topics =
                    topicDAO.getTopicsBySubject(subjectId);

            if (topics.isEmpty()) {
                System.out.println("No topics found.");
            } else {
                for (Topic t : topics) {
                    System.out.println(
                            t.getTopicId() + " - " + t.getTopicName()
                    );
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid Subject ID!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sc.close();
    }
}