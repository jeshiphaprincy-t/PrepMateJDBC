package com.prepmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.Topic;

public class TopicDAO {

    // Check whether topic already exists
    public boolean topicExists(int subjectId, String topicName)
            throws SQLException {

        String sql = "SELECT topic_id FROM topics "
                   + "WHERE subject_id = ? AND topic_name = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, subjectId);
            pst.setString(2, topicName.trim());

            try (ResultSet rs = pst.executeQuery()) {

                return rs.next();
            }
        }
    }

    // Add a new topic
    public boolean addTopic(Topic topic) throws SQLException {

        if (topicExists(topic.getSubjectId(), topic.getTopicName())) {
            return false;
        }

        String sql = "INSERT INTO topics (subject_id, topic_name) "
                   + "VALUES (?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, topic.getSubjectId());
            pst.setString(2, topic.getTopicName().trim());

            return pst.executeUpdate() > 0;
        }
    }

    // Retrieve topics belonging to a subject
    public List<Topic> getTopicsBySubject(int subjectId)
            throws SQLException {

        List<Topic> topics = new ArrayList<>();

        String sql = "SELECT * FROM topics WHERE subject_id = ? "
                   + "ORDER BY topic_name ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, subjectId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Topic topic = new Topic();

                    topic.setTopicId(rs.getInt("topic_id"));
                    topic.setSubjectId(rs.getInt("subject_id"));
                    topic.setTopicName(rs.getString("topic_name"));

                    topics.add(topic);
                }
            }
        }

        return topics;
    }
}