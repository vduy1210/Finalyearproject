package com.example.saleapp.web_backend.validator;

import java.util.regex.Pattern;

/**
 * Input Validator
 * 
 * Provides validation and sanitization for user inputs to prevent
 * security vulnerabilities (XSS, SQL injection, invalid data)
 */
public class InputValidator {
    
    // Vietnamese phone number pattern: 09, 03, 07, 08, 05 + 8 digits
    private static final Pattern VIETNAM_PHONE_PATTERN = 
        Pattern.compile("^(09|03|07|08|05)\\d{8}$");
    
    // Basic email pattern (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Dangerous characters for SQL injection and XSS
    private static final Pattern DANGEROUS_CHARS = 
        Pattern.compile("[<>\"';\\\\]");
    
    /**
     * Validate Vietnamese phone number format
     * 
     * @param phone Phone number to validate
     * @throws IllegalArgumentException if phone format is invalid
     */
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        String cleanPhone = phone.trim();
        if (!VIETNAM_PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException(
                "Invalid Vietnamese phone number format. " +
                "Expected format: 09/03/07/08/05 followed by 8 digits (e.g., 0901234567)"
            );
        }
    }
    
    /**
     * Validate email format
     * 
     * @param email Email address to validate
     * @throws IllegalArgumentException if email format is invalid
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        String cleanEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException(
                "Invalid email format. Expected format: user@example.com"
            );
        }
    }
    
    /**
     * Sanitize text input by removing dangerous characters
     * Prevents XSS and SQL injection attacks
     * 
     * @param input Text to sanitize
     * @return Sanitized text with dangerous characters removed
     */
    public static String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        
        // Remove dangerous characters
        sanitized = DANGEROUS_CHARS.matcher(sanitized).replaceAll("");
        
        // Trim whitespace
        return sanitized.trim();
    }
    
    /**
     * Validate and sanitize customer name
     * 
     * @param name Customer name to validate
     * @return Sanitized name
     * @throws IllegalArgumentException if name is invalid
     */
    public static String validateAndSanitizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        String sanitized = sanitizeText(name);
        
        if (sanitized.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        
        if (sanitized.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
        
        return sanitized;
    }
    
    /**
     * Validate table number format
     * 
     * @param tableNumber Table number to validate
     * @throws IllegalArgumentException if table number is invalid
     */
    public static void validateTableNumber(String tableNumber) {
        if (tableNumber != null && !tableNumber.trim().isEmpty()) {
            String cleaned = tableNumber.trim();
            if (cleaned.length() > 10) {
                throw new IllegalArgumentException("Table number cannot exceed 10 characters");
            }
            // Remove dangerous characters
            if (DANGEROUS_CHARS.matcher(cleaned).find()) {
                throw new IllegalArgumentException("Table number contains invalid characters");
            }
        }
    }
}
