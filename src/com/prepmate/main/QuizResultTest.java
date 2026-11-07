
package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.prepmate.dao.QuizDAO;
import com.prepmate.dao.QuestionDAO;
import com.prepmate.dao.QuestionOptionDAO;

import com.prepmate.model.QuizAttempt;
import com.prepmate.model.AttemptAnswer;
import com.prepmate.model.Question;
import com.prepmate.model.QuestionOption;

public class QuizResultTest {

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);

        QuizDAO quizDAO = new QuizDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        QuestionOptionDAO optionDAO = new QuestionOptionDAO();

        System.out.print("Enter Attempt ID: ");
        int attemptId = sc.nextInt();

        QuizAttempt attempt = quizDAO.getAttemptById(attemptId);

        if (attempt == null) {
            System.out.println("Quiz attempt not found!");
            sc.close();
            return;
        }

        System.out.println("\n========== QUIZ RESULT ==========");

        System.out.println("Attempt ID: " + attempt.getAttemptId());
        System.out.println("Topic ID: " + attempt.getTopicId());

        System.out.println("Total Questions: "
                + attempt.getTotalQuestions());

        System.out.println("Correct Answers: "
                + attempt.getCorrectAnswers());

        System.out.println("Wrong Answers: "
                + (attempt.getTotalQuestions()
                - attempt.getCorrectAnswers()));

        System.out.printf("Percentage: %.2f%%%n",
                attempt.getScore());

        System.out.println("\n========== ANSWER REVIEW ==========");

        List<AttemptAnswer> answers =
                quizDAO.getAnswersByAttempt(attemptId);

        for (AttemptAnswer answer : answers) {

            Question question =
                    questionDAO.getQuestionById(answer.getQuestionId());

            System.out.println("\nQuestion: "
                    + question.getQuestionText());

            List<QuestionOption> options =
                    optionDAO.getOptionsByQuestion(
                            answer.getQuestionId());

            QuestionOption correctOption = null;

            for (QuestionOption option : options) {

                if (option.isCorrect()) {
                    correctOption = option;
                    break;
                }
            }

            System.out.println("Your Answer: "
                    + answer.getUserAnswer());

            if (correctOption != null) {
                System.out.println("Correct Answer: "
                        + correctOption.getOptionText());
            }

            System.out.println("Result: "
                    + (answer.isCorrect() ? "CORRECT" : "WRONG"));
        }

        System.out.println("\n==================================");

        sc.close();
    }
}
