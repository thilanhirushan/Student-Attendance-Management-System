package com.sams.sams;

import com.sams.sams.util.DBConnection;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ MySQL connection successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
