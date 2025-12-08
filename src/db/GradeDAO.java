package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GradeDAO {


// Insert or update a grade for a student and assignment.

    public void setGrade(int assignmentId, String username, double gradePoints) throws SQLException {
        String sql = """
                INSERT INTO Grade (assignment_id, username, grade_points)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE grade_points = VALUES(grade_points)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setString(2, username);
            ps.setDouble(3, gradePoints);
            ps.executeUpdate();
        }
    }
}
