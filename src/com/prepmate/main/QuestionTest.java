package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuestionDAO;
import com.prepmate.model.Question;

public class QuestionTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        QuestionDAO questionDAO = new QuestionDAO();

        try {

            System.out.println("===== PREPMATE QUESTION BANK =====");

            System.out.print("Enter Topic ID: ");
            int topicId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter question: ");
            String questionText = sc.nextLine();

            System.out.print("Enter difficulty (EASY/MEDIUM/HARD): ");
            String difficulty = sc.nextLine().trim().toUpperCase();

            System.out.print("Enter correct answer: ");
            String answer = sc.nextLine();

            System.out.print("Enter explanation: ");
            String explanation = sc.nextLine();

            Question question = new Question(
                    topicId,
                    questionText,
                    difficulty,
                    answer,
                    explanation
            );

            boolean result = questionDAO.addQuestion(question);

            if (result) {
                System.out.println("\nQuestion added successfully!");
            } else {
                System.out.println("\nInvalid Topic ID!");
            }

            System.out.println("\n===== QUESTIONS FOR THIS TOPIC =====");

            List<Question> questions =
                    questionDAO.getQuestionsByTopic(topicId);

            if (questions.isEmpty()) {

                System.out.println("No questions found.");

            } else {

                for (Question q : questions) {

                    System.out.println("\nQuestion ID: " + q.getQuestionId());
                    System.out.println("Question: " + q.getQuestionText());
                    System.out.println("Difficulty: " + q.getDifficulty());
                    System.out.println("Answer: " + q.getCorrectAnswer());
                    System.out.println("Explanation: " + q.getExplanation());
                }
            }

        } catch (NumberFormatException e) {

            System.out.println("Please enter a valid Topic ID!");

        } catch (IllegalArgumentException e) {

            System.out.println("Invalid difficulty! Use EASY, MEDIUM or HARD.");

        } catch (SQLException e) {

            e.printStackTrace();
        }

        sc.close();
    }
}