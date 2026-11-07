package com.prepmate.main;

import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuestionDAO;
import com.prepmate.dao.QuestionOptionDAO;
import com.prepmate.model.Question;
import com.prepmate.model.QuestionOption;
import com.prepmate.service.QuizService;
import java.sql.SQLException;

public class MCQQuizTest {

	public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);

        QuestionDAO questionDAO = new QuestionDAO();
        QuestionOptionDAO optionDAO = new QuestionOptionDAO();
        QuizService quizService = new QuizService();

        int questionId = 1;

        // Retrieve question
        Question question = questionDAO.getQuestionById(questionId);

        if (question == null) {
            System.out.println("Question not found!");
            sc.close();
            return;
        }

        // Display question
        System.out.println("\n========== PREPMATE QUIZ ==========");
        System.out.println(question.getQuestionText());

        // Retrieve and display options
        List<QuestionOption> options =
                optionDAO.getOptionsByQuestion(questionId);

        if (options.isEmpty()) {
            System.out.println("No options available!");
            sc.close();
            return;
        }

        for (QuestionOption option : options) {
            System.out.println(
                    option.getOptionLabel() + ". "
                    + option.getOptionText());
        }

        // Accept answer
        System.out.print("\nEnter your choice (A/B/C/D): ");
        String choice = sc.nextLine().trim().toUpperCase();

        // Find selected option
        QuestionOption selectedOption = null;

        for (QuestionOption option : options) {
            if (option.getOptionLabel().equalsIgnoreCase(choice)) {
                selectedOption = option;
                break;
            }
        }

        if (selectedOption == null) {
            System.out.println("Invalid option!");
        } else {

            boolean correct = quizService.checkMCQAnswer(
                    selectedOption.getOptionId(), questionId);

            if (correct) {
                System.out.println("Correct answer! Great job!");
            } else {
                System.out.println("Incorrect answer. Keep practising!");
            }
        }

        System.out.println("===================================");

        sc.close();
    }
}