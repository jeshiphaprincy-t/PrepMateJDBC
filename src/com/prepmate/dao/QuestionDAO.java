package com.prepmate.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import com.prepmate.connection.DBConnection;
import com.prepmate.model.Question;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.Question;

public class QuestionDAO {

    // Check whether the topic exists
    public boolean topicExists(int topicId) throws SQLException {

        String sql = "SELECT topic_id FROM topics WHERE topic_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, topicId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Add a new question
    public boolean addQuestion(Question question) throws SQLException {

        if (!topicExists(question.getTopicId())) {
            return false;
        }
        
        if (!isValidDifficulty(question.getDifficulty())) {
            throw new IllegalArgumentException("Invalid difficulty level!");
        }

        String sql = "INSERT INTO questions "
                + "(topic_id, question_text, difficulty, "
                + "correct_answer, explanation) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, question.getTopicId());
            pst.setString(2, question.getQuestionText());
            pst.setString(3, question.getDifficulty());
            pst.setString(4, question.getCorrectAnswer());
            pst.setString(5, question.getExplanation());

            return pst.executeUpdate() > 0;
        }
    }

    // Retrieve questions belonging to a topic
    public List<Question> getQuestionsByTopic(int topicId)
            throws SQLException {

        List<Question> questions = new ArrayList<>();

        String sql = "SELECT * FROM questions WHERE topic_id = ? "
                + "ORDER BY question_id ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, topicId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Question question = new Question();

                    question.setQuestionId(rs.getInt("question_id"));
                    question.setTopicId(rs.getInt("topic_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setDifficulty(rs.getString("difficulty"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setExplanation(rs.getString("explanation"));

                    questions.add(question);
                }
            }
        }

        return questions;
    }
    
    private boolean isValidDifficulty(String difficulty) {

        return difficulty != null &&
                (difficulty.equalsIgnoreCase("EASY") ||
                 difficulty.equalsIgnoreCase("MEDIUM") ||
                 difficulty.equalsIgnoreCase("HARD"));
    }
    
    public List<Question> getQuestionsByTopicAndDifficulty(
            int topicId, String difficulty) throws SQLException {

        List<Question> questions = new ArrayList<>();

        String sql = "SELECT * FROM questions "
                + "WHERE topic_id = ? AND difficulty = ? "
                + "ORDER BY RAND() LIMIT 5";
        
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, topicId);
            pst.setString(2, difficulty.toUpperCase());

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Question question = new Question();

                    question.setQuestionId(rs.getInt("question_id"));
                    question.setTopicId(rs.getInt("topic_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setDifficulty(rs.getString("difficulty"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setExplanation(rs.getString("explanation"));

                    questions.add(question);
                }
            }
        }

        return questions;
    }
    
    public Question getQuestionById(int questionId) throws SQLException {

        String sql = "SELECT * FROM questions WHERE question_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, questionId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    Question question = new Question();

                    question.setQuestionId(rs.getInt("question_id"));
                    question.setTopicId(rs.getInt("topic_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setDifficulty(rs.getString("difficulty"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setExplanation(rs.getString("explanation"));

                    return question;
                }
            }
        }

        return null;
    }
}