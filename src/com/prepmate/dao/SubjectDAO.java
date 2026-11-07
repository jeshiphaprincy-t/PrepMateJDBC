package com.prepmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.prepmate.connection.DBConnection;
import com.prepmate.model.Subject;

public class SubjectDAO {

    public List<Subject> getAllSubjects() throws SQLException {

        List<Subject> subjects = new ArrayList<>();

        String sql = "SELECT * FROM subjects";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                Subject subject = new Subject();

                subject.setSubjectId(rs.getInt("subject_id"));
                subject.setSubjectName(rs.getString("subject_name"));

                subjects.add(subject);
            }
        }

        return subjects;
    }
    
    public boolean addSubject(String subjectName) throws SQLException {

        if (subjectExists(subjectName)) {
            return false;
        }

        String sql = "INSERT INTO subjects (subject_name) VALUES (?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, subjectName.trim());

            return pst.executeUpdate() > 0;
        }
    }
    
    public boolean subjectExists(String subjectName) throws SQLException {

        String sql = "SELECT subject_id FROM subjects WHERE subject_name = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, subjectName.trim());

            try (ResultSet rs = pst.executeQuery()) {

                return rs.next();
            }
        }
    }
}