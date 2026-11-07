package com.prepmate.main;

import java.sql.Connection;
import com.prepmate.connection.DBConnection;

public class ConnectionTest {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("PrepMate Database Connected!");

        } catch (Exception e) {

            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}