package com.sams.sams.dao;

import com.sams.sams.model.Attendance;
import com.sams.sams.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Save a single attendance record to the database
    public boolean addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id, date, present) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attendance.getStudentId());
            stmt.setDate(2, java.sql.Date.valueOf(attendance.getDate()));
            stmt.setBoolean(3, attendance.isPresent());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ View attendance for a given date
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE date = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("present")
                );
                list.add(attendance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    // Get all attendance records (needed for report)
    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Attendance a = new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("present")
                );
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int getPresentCountByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND present = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getTotalSessionsByCourse(int courseId) {
        String sql = "SELECT COUNT(DISTINCT date) " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.id " +
                "WHERE s.course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
