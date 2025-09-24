package dao;

import database.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.user;

public class UserDAO {
    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

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
            if (rowsAffected > 0) {
                lastErrorMessage = null;
                return true;
            }
            lastErrorMessage = "No rows inserted";
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    // ===== CRUD & utilities for User Management =====
    public List<user> listUsers() {
        List<user> users = new ArrayList<>();
        String sql = "SELECT userID, userName, password, role, email FROM users ORDER BY userID DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new user(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE userName = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public boolean createUser(String username, String password, String email, String role) {
        role = role != null ? role.toLowerCase() : null;
        String sql = "INSERT INTO users (userName, password, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, role);
            int n = stmt.executeUpdate();
            if (n > 0) {
                lastErrorMessage = null;
                return true;
            }
            lastErrorMessage = "No rows inserted";
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public boolean updateUser(int userId, String username, String password, String email, String role) {
        role = role != null ? role.toLowerCase() : null;
        String sql = "UPDATE users SET userName = ?, password = ?, email = ?, role = ? WHERE userID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setInt(5, userId);
            int n = stmt.executeUpdate();
            if (n > 0) {
                lastErrorMessage = null;
                return true;
            }
            lastErrorMessage = "No rows updated";
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE userID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int n = stmt.executeUpdate();
            if (n > 0) {
                lastErrorMessage = null;
                return true;
            }
            lastErrorMessage = "No rows deleted";
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }
}