package db;

import model.Assignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public void addAssignment(int classId, int categoryId,
                              String name, String description, double points) throws SQLException {
        String sql = """
                INSERT INTO Assignment (class_id, category_id, name, description, points)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setInt(2, categoryId);
            ps.setString(3, name);
            ps.setString(4, description);
            ps.setDouble(5, points);
            ps.executeUpdate();
        }
    }

    public Assignment findByName(int classId, String name) throws SQLException {
        String sql = """
                SELECT assignment_id, class_id, category_id, name, description, points
                FROM Assignment
                WHERE class_id = ? AND name = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Assignment(
                            rs.getInt("assignment_id"),
                            rs.getInt("class_id"),
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("points")
                    );
                }
            }
        }
        return null;
    }

    public List<Assignment> listAssignmentsByClass(int classId) throws SQLException {
        String sql = """
                SELECT assignment_id, class_id, category_id, name, description, points
                FROM Assignment
                WHERE class_id = ?
                ORDER BY category_id, name
                """;
        List<Assignment> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Assignment(
                            rs.getInt("assignment_id"),
                            rs.getInt("class_id"),
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("points")
                    ));
                }
            }
        }
        return result;
    }
}
