package com.prepmate.service;

import java.sql.SQLException;

import com.prepmate.dao.QuestionOptionDAO;
import com.prepmate.model.QuestionOption;
import java.util.List;

import com.prepmate.dao.QuestionDAO;
import com.prepmate.model.Question;

public class QuizService {

    private final QuestionDAO questionDAO = new QuestionDAO();

    public boolean checkAnswer(int questionId, String userAnswer)
            throws SQLException {

        Question question = questionDAO.getQuestionById(questionId);

        if (question == null || userAnswer == null) {
            return false;
        }

        String correctAnswer = question.getCorrectAnswer();

        return correctAnswer.trim().equalsIgnoreCase(
                userAnswer.trim()
        );
    }
    
    private QuestionOptionDAO optionDAO = new QuestionOptionDAO();

    public boolean checkMCQAnswer(int optionId, int questionId) {

        List<QuestionOption> options =
                optionDAO.getOptionsByQuestion(questionId);

        for (QuestionOption option : options) {

            if (option.getOptionId() == optionId) {
                return option.isCorrect();
            }
        }

        return false;
    }
    
    public List<Question> getQuizQuestions(int topicId, String difficulty)
            throws SQLException {

        QuestionDAO questionDAO = new QuestionDAO();

        return questionDAO.getQuestionsByTopicAndDifficulty(
                topicId, difficulty);
    }
    
    public int calculateScore(List<Boolean> answers) {

        int score = 0;

        for (Boolean correct : answers) {

            if (Boolean.TRUE.equals(correct)) {
                score++;
            }
        }

        return score;
    }
}