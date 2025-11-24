package util;

import database.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One-time migration utility to hash existing plain-text passwords
 * 
 * WARNING: Run this ONCE after deploying password hashing feature
 * This will convert all existing plain-text passwords to BCrypt hashes
 */
public class PasswordMigration {
    
    public static void main(String[] args) {
        System.out.println("=== Password Migration Utility ===");
        System.out.println("This will hash all plain-text passwords in the database.");
        System.out.println("Make sure you have a database backup before proceeding!\n");
        
        migratePasswords();
    }
    
    /**
     * Migrate all plain-text passwords to BCrypt hashes
     */
    public static void migratePasswords() {
        String selectSql = "SELECT userID, password FROM users";
        String updateSql = "UPDATE users SET password = ? WHERE userID = ?";
        
        int totalUsers = 0;
        int migratedUsers = 0;
        int skippedUsers = 0;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery()) {
            
            while (rs.next()) {
                totalUsers++;
                int userId = rs.getInt("userID");
                String password = rs.getString("password");
                
                // Skip if already hashed (BCrypt hashes start with $2a$ or $2b$)
                if (password != null && password.startsWith("$2")) {
                    System.out.println("User ID " + userId + ": Already hashed, skipping");
                    skippedUsers++;
                    continue;
                }
                
                // Hash the plain-text password
                String hashedPassword = PasswordUtil.hashPassword(password);
                
                // Update database
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setInt(2, userId);
                    int updated = updateStmt.executeUpdate();
                    
                    if (updated > 0) {
                        System.out.println("User ID " + userId + ": Password hashed successfully");
                        migratedUsers++;
                    } else {
                        System.err.println("User ID " + userId + ": Failed to update");
                    }
                }
            }
            
            System.out.println("\n=== Migration Complete ===");
            System.out.println("Total users: " + totalUsers);
            System.out.println("Migrated: " + migratedUsers);
            System.out.println("Skipped (already hashed): " + skippedUsers);
            
        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
