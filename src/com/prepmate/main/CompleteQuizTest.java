
package com.prepmate.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuestionOptionDAO;
import com.prepmate.dao.QuizDAO;
import com.prepmate.model.AttemptAnswer;
import com.prepmate.model.Question;
import com.prepmate.model.QuestionOption;
import com.prepmate.model.QuizAttempt;
import com.prepmate.service.QuizService;

public class CompleteQuizTest {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);

        QuizService quizService = new QuizService();
        QuestionOptionDAO optionDAO = new QuestionOptionDAO();
        QuizDAO quizDAO = new QuizDAO();

        // Test user and quiz details
        int userId = 2;
        int topicId = 1;
        String difficulty = "EASY";

        List<Question> questions =
                quizService.getQuizQuestions(topicId, difficulty);

        if (questions.isEmpty()) {
            System.out.println("No questions available!");
            sc.close();
            return;
        }

        int totalQuestions = questions.size();

        // 1. Create quiz attempt
        QuizAttempt attempt =
                new QuizAttempt(userId, topicId, totalQuestions);

        int attemptId = quizDAO.createAttempt(attempt);

        if (attemptId <= 0) {
            System.out.println("Unable to start quiz!");
            sc.close();
            return;
        }

        System.out.println("Quiz started! Attempt ID: " + attemptId);

        List<Boolean> answers = new ArrayList<>();

        int questionNumber = 1;

        // 2. Display questions and save answers
        for (Question question : questions) {

            System.out.println("\nQuestion " + questionNumber);
            System.out.println(question.getQuestionText());

            List<QuestionOption> options =
                    optionDAO.getOptionsByQuestion(question.getQuestionId());

            if (options.isEmpty()) {

                System.out.println("No options available.");

                answers.add(false);

                AttemptAnswer answer = new AttemptAnswer(
                        attemptId,
                        question.getQuestionId(),
                        null,
                        false
                );

                quizDAO.saveAnswer(answer);

                questionNumber++;
                continue;
            }

            for (QuestionOption option : options) {
                System.out.println(
                        option.getOptionLabel() + ". "
                        + option.getOptionText()
                );
            }

            System.out.print("Your answer: ");
            String choice = sc.nextLine().trim();

            QuestionOption selectedOption = null;

            for (QuestionOption option : options) {

                if (option.getOptionLabel().equalsIgnoreCase(choice)) {
                    selectedOption = option;
                    break;
                }
            }

            boolean correct = false;
            int selectedOptionId = 0;
            String userAnswer = choice;

            if (selectedOption == null) {

                System.out.println("Invalid option!");

            } else {

                selectedOptionId = selectedOption.getOptionId();
                userAnswer = selectedOption.getOptionText();

                correct = quizService.checkMCQAnswer(
                        selectedOptionId,
                        question.getQuestionId()
                );

                if (correct) {
                    System.out.println("Correct answer!");
                } else {
                    System.out.println("Incorrect answer!");
                }
            }

            answers.add(correct);

            // 3. Save individual answer
            AttemptAnswer answer = new AttemptAnswer(
                    attemptId,
                    question.getQuestionId(),
                    userAnswer,
                    correct
            );

            answer.setSelectedOptionId(selectedOptionId);

            boolean saved = quizDAO.saveAnswer(answer);

            if (!saved) {
                System.out.println("Warning: Answer could not be saved!");
            }

            questionNumber++;
        }

        // 4. Calculate final score
        int correctAnswers = quizService.calculateScore(answers);

        double percentage =
                (correctAnswers * 100.0) / totalQuestions;

        // 5. Save final quiz result
        boolean completed = quizDAO.completeAttempt(
                attemptId,
                correctAnswers,
                percentage
        );

        // 6. Display result
        System.out.println("\n========== QUIZ RESULT ==========");
        System.out.println("Attempt ID: " + attemptId);
        System.out.println("Total Questions: " + totalQuestions);
        System.out.println("Correct Answers: " + correctAnswers);
        System.out.println("Wrong Answers: "
                + (totalQuestions - correctAnswers));

        System.out.printf("Percentage: %.2f%%%n", percentage);

        System.out.println("Database Status: "
                + (completed ? "Saved Successfully" : "Update Failed"));

        System.out.println("=================================");

        sc.close();
    }
}
