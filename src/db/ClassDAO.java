// db/ClassDAO.java
// Encapsulates SQL operations for the Class table.

package db;

import model.ClassInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public ClassInfo createClass(String courseNumber, String term, int section, String description)
            throws SQLException {
        String sql = "INSERT INTO Class (course_number, term, section, description) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, courseNumber);
            ps.setString(2, term);
            ps.setInt(3, section);
            ps.setString(4, description);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new ClassInfo(id, courseNumber, term, section, description);
                }
            }
        }
        return null;
    }

    public List<ClassInfo> listClassesWithStudentCount() throws SQLException {
        // List classes with # of students enrolled
        String sql = """
            SELECT c.class_id,
                   c.course_number,
                   c.term,
                   c.section,
                   c.description,
                   COUNT(e.username) AS student_count
            FROM Class c
            LEFT JOIN Enrollment e ON c.class_id = e.class_id
            GROUP BY c.class_id, c.course_number, c.term, c.section, c.description
            ORDER BY c.course_number, c.term, c.section
            """;

        List<ClassInfo> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("class_id");
                String course = rs.getString("course_number");
                String term = rs.getString("term");
                int section = rs.getInt("section");
                String desc = rs.getString("description");
                int count = rs.getInt("student_count");

                ClassInfo ci = new ClassInfo(id, course, term, section,
                        desc + " [students=" + count + "]");
                result.add(ci);
            }
        }
        return result;
    }

    public ClassInfo findSpecificClass(String courseNumber, String term, int section)
            throws SQLException {
        String sql = "SELECT * FROM Class WHERE course_number = ? AND term = ? AND section = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseNumber);
            ps.setString(2, term);
            ps.setInt(3, section);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public ClassInfo selectMostRecentByCourse(String courseNumber) throws SQLException {
        String sql = """
            SELECT *
            FROM Class
            WHERE course_number = ?
            ORDER BY term DESC, section ASC
            """;
        List<ClassInfo> classes = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapRow(rs));
                }
            }
        }

        if (classes.isEmpty()) {
            return null;
        }

        // If there is only one section in the "most recent" term, pick it.
        ClassInfo first = classes.get(0);
        String newestTerm = first.getTerm();
        long countInNewestTerm = classes.stream()
                .filter(c -> c.getTerm().equals(newestTerm))
                .count();

        if (countInNewestTerm == 1) {
            return first;
        } else {

            return null;
        }
    }

    public ClassInfo selectByCourseAndTerm(String courseNumber, String term) throws SQLException {
        String sql = "SELECT * FROM Class WHERE course_number = ? AND term = ?";
        List<ClassInfo> classes = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseNumber);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapRow(rs));
                }
            }
        }
        if (classes.size() == 1) {
            return classes.get(0);
        } else {

            return null;
        }
    }

    private ClassInfo mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("class_id");
        String course = rs.getString("course_number");
        String term = rs.getString("term");
        int section = rs.getInt("section");
        String desc = rs.getString("description");
        return new ClassInfo(id, course, term, section, desc);
    }
}
