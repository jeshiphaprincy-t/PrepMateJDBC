package com.prepmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.QuestionOption;

public class QuestionOptionDAO {

    public boolean addOption(QuestionOption option) {

        String sql = "INSERT INTO question_options "
                + "(question_id, option_label, option_text, is_correct) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, option.getQuestionId());
            ps.setString(2, option.getOptionLabel());
            ps.setString(3, option.getOptionText());
            ps.setBoolean(4, option.isCorrect());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<QuestionOption> getOptionsByQuestion(int questionId) {

        List<QuestionOption> options = new ArrayList<>();

        String sql = "SELECT * FROM question_options "
                + "WHERE question_id = ? ORDER BY option_label";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    QuestionOption option = new QuestionOption();

                    option.setOptionId(rs.getInt("option_id"));
                    option.setQuestionId(rs.getInt("question_id"));
                    option.setOptionLabel(rs.getString("option_label"));
                    option.setOptionText(rs.getString("option_text"));
                    option.setCorrect(rs.getBoolean("is_correct"));

                    options.add(option);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return options;
    }
}