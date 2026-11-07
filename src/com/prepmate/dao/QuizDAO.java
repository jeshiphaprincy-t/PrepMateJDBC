package com.prepmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.AttemptAnswer;
import com.prepmate.model.QuizAttempt;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class QuizDAO {

    // Create a new quiz attempt
    public int createAttempt(QuizAttempt attempt) throws SQLException {

        String sql = "INSERT INTO quiz_attempts "
                + "(user_id, topic_id, total_questions) "
                + "VALUES (?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            pst.setInt(1, attempt.getUserId());
            pst.setInt(2, attempt.getTopicId());
            pst.setInt(3, attempt.getTotalQuestions());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public boolean saveAnswer(AttemptAnswer answer) throws SQLException {

        String sql = "INSERT INTO attempt_answers "
                + "(attempt_id, question_id, user_answer, is_correct, selected_option_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, answer.getAttemptId());
            ps.setInt(2, answer.getQuestionId());
            ps.setString(3, answer.getUserAnswer());
            ps.setBoolean(4, answer.isCorrect());

            if (answer.getSelectedOptionId() > 0) {
                ps.setInt(5, answer.getSelectedOptionId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        }
    }
    // Update final quiz result
    public boolean completeAttempt(int attemptId, int correctAnswers,
                                   double score) throws SQLException {

        String sql = "UPDATE quiz_attempts "
                + "SET correct_answers = ?, score = ?, "
                + "completed_at = CURRENT_TIMESTAMP "
                + "WHERE attempt_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, correctAnswers);
            pst.setDouble(2, score);
            pst.setInt(3, attemptId);

            return pst.executeUpdate() > 0;
        }
    }
    
    public QuizAttempt getAttemptById(int attemptId) throws SQLException {

        String sql = "SELECT * FROM quiz_attempts WHERE attempt_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attemptId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    QuizAttempt attempt = new QuizAttempt();

                    attempt.setAttemptId(rs.getInt("attempt_id"));
                    attempt.setUserId(rs.getInt("user_id"));
                    attempt.setTopicId(rs.getInt("topic_id"));
                    attempt.setTotalQuestions(rs.getInt("total_questions"));
                    attempt.setCorrectAnswers(rs.getInt("correct_answers"));
                    attempt.setScore(rs.getDouble("score"));
                    attempt.setStartedAt(rs.getTimestamp("started_at"));
                    attempt.setCompletedAt(rs.getTimestamp("completed_at"));

                    return attempt;
                }
            }
        }

        return null;
    }
    
    public List<QuizAttempt> getAttemptsByUser(int userId)
            throws SQLException {

        List<QuizAttempt> attempts = new ArrayList<>();

        String sql = "SELECT * FROM quiz_attempts "
                   + "WHERE user_id = ? ORDER BY started_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    QuizAttempt attempt = new QuizAttempt();

                    attempt.setAttemptId(rs.getInt("attempt_id"));
                    attempt.setUserId(rs.getInt("user_id"));
                    attempt.setTopicId(rs.getInt("topic_id"));
                    attempt.setTotalQuestions(rs.getInt("total_questions"));
                    attempt.setCorrectAnswers(rs.getInt("correct_answers"));
                    attempt.setScore(rs.getDouble("score"));
                    attempt.setStartedAt(rs.getTimestamp("started_at"));
                    attempt.setCompletedAt(rs.getTimestamp("completed_at"));

                    attempts.add(attempt);
                }
            }
        }

        return attempts;
    }
    
    public List<AttemptAnswer> getAnswersByAttempt(int attemptId)
            throws SQLException {

        List<AttemptAnswer> answers = new ArrayList<>();

        String sql = "SELECT * FROM attempt_answers WHERE attempt_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attemptId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    AttemptAnswer answer = new AttemptAnswer();

                    answer.setAnswerId(rs.getInt("answer_id"));
                    answer.setAttemptId(rs.getInt("attempt_id"));
                    answer.setQuestionId(rs.getInt("question_id"));
                    answer.setUserAnswer(rs.getString("user_answer"));
                    answer.setCorrect(rs.getBoolean("is_correct"));
                    answer.setSelectedOptionId(
                            rs.getInt("selected_option_id"));

                    answers.add(answer);
                }
            }
        }

        return answers;
    }
    
    public int getTotalAttempts(int userId) throws SQLException {

        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
    
    public Map<String, Double> getScoreStatistics(int userId)
            throws SQLException {

        String sql = """
                SELECT AVG(score) AS average_score,
                       MAX(score) AS highest_score
                FROM quiz_attempts
                WHERE user_id = ?
                """;

        Map<String, Double> statistics = new HashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    statistics.put("average",
                            rs.getDouble("average_score"));

                    statistics.put("highest",
                            rs.getDouble("highest_score"));
                }
            }
        }

        return statistics;
    }
    
    public int getTotalQuestionsAnswered(int userId)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM attempt_answers aa
                JOIN quiz_attempts qa
                ON aa.attempt_id = qa.attempt_id
                WHERE qa.user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
    
    public Map<String, Double> getSubjectWisePerformance(int userId)
            throws SQLException {

        String sql = """
                SELECT s.subject_name, AVG(qa.score) AS average_score
                FROM quiz_attempts qa
                JOIN topics t ON qa.topic_id = t.topic_id
                JOIN subjects s ON t.subject_id = s.subject_id
                WHERE qa.user_id = ?
                GROUP BY s.subject_id, s.subject_name
                ORDER BY average_score DESC
                """;

        Map<String, Double> performance = new LinkedHashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    performance.put(
                            rs.getString("subject_name"),
                            rs.getDouble("average_score"));
                }
            }
        }

        return performance;
    }
    public Map<String, Double> getWeakTopics(int userId)
            throws SQLException {

        String sql = """
                SELECT t.topic_name, AVG(qa.score) AS average_score
                FROM quiz_attempts qa
                JOIN topics t ON qa.topic_id = t.topic_id
                WHERE qa.user_id = ?
                GROUP BY t.topic_id, t.topic_name
                HAVING AVG(qa.score) < 60
                ORDER BY average_score ASC
                """;

        Map<String, Double> weakTopics = new LinkedHashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    weakTopics.put(
                            rs.getString("topic_name"),
                            rs.getDouble("average_score"));
                }
            }
        }

        return weakTopics;
    }
    
    public List<String> getUnpractisedTopics(int userId)
            throws SQLException {

        String sql = """
                SELECT t.topic_name
                FROM topics t
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM quiz_attempts qa
                    WHERE qa.topic_id = t.topic_id
                    AND qa.user_id = ?
                )
                ORDER BY t.topic_name
                """;

        List<String> topics = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topics.add(rs.getString("topic_name"));
                }
            }
        }

        return topics;
    }
    
    public String getRecommendedTopic(int userId)
            throws SQLException {

        Map<String, Double> weakTopics = getWeakTopics(userId);

        if (!weakTopics.isEmpty()) {
            return weakTopics.keySet().iterator().next();
        }

        List<String> unpractisedTopics = getUnpractisedTopics(userId);

        if (!unpractisedTopics.isEmpty()) {
            return unpractisedTopics.get(0);
        }

        return "All topics practised! Try another difficulty level.";
    }
}