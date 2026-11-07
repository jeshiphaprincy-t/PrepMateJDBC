package com.prepmate.main;

import java.sql.SQLException;
import java.util.List;

import com.prepmate.dao.SubjectDAO;
import com.prepmate.model.Subject;

public class SubjectTest {

    public static void main(String[] args) {

        SubjectDAO subjectDAO = new SubjectDAO();

        try {

            List<Subject> subjects = subjectDAO.getAllSubjects();

            System.out.println("===== PREPMATE SUBJECTS =====");

            for (Subject subject : subjects) {

                System.out.println(
                    subject.getSubjectId() + " - " +
                    subject.getSubjectName()
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}