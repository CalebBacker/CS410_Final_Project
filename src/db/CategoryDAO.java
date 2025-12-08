package db;

import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void addCategory(int classId, String name, double weight) throws SQLException {
        String sql = "INSERT INTO Category (class_id, name, weight) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, name);
            ps.setDouble(3, weight);
            ps.executeUpdate();
        }
    }

    public List<Category> listCategories(int classId) throws SQLException {
        String sql = """
                SELECT category_id, class_id, name, weight
                FROM Category
                WHERE class_id = ?
                ORDER BY name
                """;
        List<Category> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Category(
                            rs.getInt("category_id"),
                            rs.getInt("class_id"),
                            rs.getString("name"),
                            rs.getDouble("weight")
                    ));
                }
            }
        }
        return result;
    }

    public Category findByName(int classId, String name) throws SQLException {
        String sql = """
                SELECT category_id, class_id, name, weight
                FROM Category
                WHERE class_id = ? AND name = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("category_id"),
                            rs.getInt("class_id"),
                            rs.getString("name"),
                            rs.getDouble("weight")
                    );
                }
            }
        }
        return null;
    }
}
