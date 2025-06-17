package dao;

import database.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT userID FROM users WHERE userName = ? AND password = ?"; // TODO: Hash password
        try (Connection conn = DatabaseConnector.getConnection(); // Sử dụng DatabaseConnector
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên có logging tốt hơn
            return false;
        }
    }

    public String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE userName = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String email, String password) {
        String sql = "INSERT INTO users (userName, email, password, role) VALUES (?, ?, ?, 'user')"; // TODO: Hash password
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Nên lưu mật khẩu đã hash
            // stmt.setString(4, "user"); // Đã set trực tiếp trong câu SQL

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Các phương thức khác liên quan đến User...
}