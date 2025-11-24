package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password Utility for BCrypt hashing
 * 
 * Provides secure password hashing and verification using BCrypt algorithm
 * with 12 rounds (2^12 = 4096 iterations) for enhanced security
 */
public class PasswordUtil {
    
    // BCrypt work factor (12 rounds = 4096 iterations)
    private static final int BCRYPT_ROUNDS = 12;
    
    /**
     * Hash a plain-text password using BCrypt
     * 
     * @param plainPassword The plain-text password to hash
     * @return BCrypt hashed password (60 characters)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verify a plain-text password against a BCrypt hash
     * 
     * @param plainPassword The plain-text password to check
     * @param hashedPassword The BCrypt hashed password to verify against
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }
}
