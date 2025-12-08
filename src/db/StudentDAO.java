// db/StudentDAO.java
// Handles adding/updating students and enrolling them in classes.

package db;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Student WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student insertOrUpdateStudent(String username,
                                         String studentId,
                                         String last,
                                         String first) throws SQLException {
        Student existing = findByUsername(username);
        if (existing == null) {
            String sql = "INSERT INTO Student (username, student_id, first_name, last_name) " +
                    "VALUES (?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, studentId);
                ps.setString(3, first);
                ps.setString(4, last);
                ps.executeUpdate();
            }
            return new Student(username, studentId, first, last);
        } else {
            // Possibly update name if mismatch
            if (!existing.getFirstName().equals(first) ||
                    !existing.getLastName().equals(last)) {
                System.out.println("WARNING: Updating name for student " + username);
                String sql = "UPDATE Student SET first_name = ?, last_name = ? WHERE username = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, first);
                    ps.setString(2, last);
                    ps.setString(3, username);
                    ps.executeUpdate();
                }
            }
            return findByUsername(username);
        }
    }

    public void enrollStudentInClass(String username, int classId) throws SQLException {
        String sql = "INSERT IGNORE INTO Enrollment (class_id, username) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public List<Student> listStudentsInClass(int classId) throws SQLException {
        String sql = """
            SELECT s.*
            FROM Enrollment e
            JOIN Student s ON s.username = e.username
            WHERE e.class_id = ?
            ORDER BY s.last_name, s.first_name
        """;
        List<Student> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    public boolean isEnrolledInClass(String username, int classId) throws SQLException {
        String sql = "SELECT 1 FROM Enrollment WHERE class_id = ? AND username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public List<Student> searchStudentsInClass(int classId, String query) throws SQLException {
        String like = "%" + query.toLowerCase() + "%";
        String sql = """
            SELECT s.*
            FROM Enrollment e
            JOIN Student s ON s.username = e.username
            WHERE e.class_id = ?
              AND (LOWER(s.first_name) LIKE ? 
                   OR LOWER(s.last_name) LIKE ?
                   OR LOWER(s.username) LIKE ?)
            ORDER BY s.last_name, s.first_name
        """;
        List<Student> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        String u = rs.getString("username");
        String id = rs.getString("student_id");
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        return new Student(u, id, first, last);
    }
}
