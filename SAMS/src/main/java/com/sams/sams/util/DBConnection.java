package com.sams.sams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/attendance_db";
    private static final String USER = "root";  // replace with your MySQL username
    private static final String PASSWORD = "Thilan123"; // replace with your MySQL password

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            throw new RuntimeException(e);
        }
    }
}
