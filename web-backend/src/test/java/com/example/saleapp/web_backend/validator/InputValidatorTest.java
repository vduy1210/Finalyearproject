package com.example.saleapp.web_backend.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputValidator utility class
 * Tests Vietnamese phone validation, email validation, and text sanitization
 */
@DisplayName("InputValidator Unit Tests")
class InputValidatorTest {

    // ============================================================
    // VIETNAMESE PHONE NUMBER VALIDATION TESTS
    // ============================================================
    
    @Test
    @DisplayName("Should accept valid Vietnamese phone number starting with 09")
    void testValidPhone_09() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("0901234567"));
    }
    
    @Test
    @DisplayName("Should accept valid Vietnamese phone number starting with 03")
    void testValidPhone_03() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("0312345678"));
    }
    
    @Test
    @DisplayName("Should accept valid Vietnamese phone number starting with 07")
    void testValidPhone_07() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("0712345678"));
    }
    
    @Test
    @DisplayName("Should accept valid Vietnamese phone number starting with 08")
    void testValidPhone_08() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("0812345678"));
    }
    
    @Test
    @DisplayName("Should accept valid Vietnamese phone number starting with 05")
    void testValidPhone_05() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("0512345678"));
    }
    
    @Test
    @DisplayName("Should reject phone number with less than 10 digits")
    void testInvalidPhone_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone("090123456");
        });
        assertTrue(exception.getMessage().contains("Invalid Vietnamese phone number format"));
    }
    
    @Test
    @DisplayName("Should reject phone number with more than 10 digits")
    void testInvalidPhone_TooLong() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone("09012345678");
        });
        assertTrue(exception.getMessage().contains("Invalid Vietnamese phone number format"));
    }
    
    @Test
    @DisplayName("Should reject phone number with invalid prefix")
    void testInvalidPhone_InvalidPrefix() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone("0112345678");
        });
        assertTrue(exception.getMessage().contains("Invalid Vietnamese phone number format"));
    }
    
    @Test
    @DisplayName("Should reject null phone number")
    void testInvalidPhone_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone(null);
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }
    
    @Test
    @DisplayName("Should reject empty phone number")
    void testInvalidPhone_Empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone("");
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }
    
    @Test
    @DisplayName("Should reject phone number with whitespace only")
    void testInvalidPhone_WhitespaceOnly() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validatePhone("   ");
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }

    // ============================================================
    // EMAIL VALIDATION TESTS
    // ============================================================
    
    @Test
    @DisplayName("Should accept valid email with standard format")
    void testValidEmail_Standard() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("user@example.com"));
    }
    
    @Test
    @DisplayName("Should accept valid email with subdomain")
    void testValidEmail_Subdomain() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("user@mail.example.com"));
    }
    
    @Test
    @DisplayName("Should accept valid email with plus sign")
    void testValidEmail_PlusSign() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("user+tag@example.com"));
    }
    
    @Test
    @DisplayName("Should accept valid email with dots")
    void testValidEmail_Dots() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("first.last@example.com"));
    }
    
    @Test
    @DisplayName("Should accept valid email with underscore")
    void testValidEmail_Underscore() {
        assertDoesNotThrow(() -> InputValidator.validateEmail("user_name@example.com"));
    }
    
    @Test
    @DisplayName("Should reject email without @ symbol")
    void testInvalidEmail_NoAtSymbol() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateEmail("userexample.com");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }
    
    @Test
    @DisplayName("Should reject email without domain")
    void testInvalidEmail_NoDomain() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateEmail("user@");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }
    
    @Test
    @DisplayName("Should reject email without top-level domain")
    void testInvalidEmail_NoTLD() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateEmail("user@example");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }
    
    @Test
    @DisplayName("Should reject null email")
    void testInvalidEmail_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateEmail(null);
        });
        assertTrue(exception.getMessage().contains("Email cannot be empty"));
    }
    
    @Test
    @DisplayName("Should reject empty email")
    void testInvalidEmail_Empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateEmail("");
        });
        assertTrue(exception.getMessage().contains("Email cannot be empty"));
    }

    // ============================================================
    // TEXT SANITIZATION TESTS
    // ============================================================
    
    @Test
    @DisplayName("Should remove HTML tags and dangerous characters")
    void testSanitize_RemoveHTMLTags() {
        String input = "<script>alert('XSS')</script>Hello";
        String result = InputValidator.sanitizeText(input);
        // Step 1: Remove tags <script></script> -> alert('XSS')Hello
        // Step 2: Remove dangerous chars (') -> alert(XSS)Hello
        assertEquals("alert(XSS)Hello", result);
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("'"));
    }
    
    @Test
    @DisplayName("Should remove dangerous characters")
    void testSanitize_RemoveDangerousChars() {
        String input = "Hello'World\"Test;";
        String result = InputValidator.sanitizeText(input);
        assertFalse(result.contains("'"));
        assertFalse(result.contains("\""));
        assertFalse(result.contains(";"));
    }
    
    @Test
    @DisplayName("Should remove angle brackets")
    void testSanitize_RemoveAngleBrackets() {
        String input = "Hello<World>Test";
        String result = InputValidator.sanitizeText(input);
        assertFalse(result.contains("<"));
        assertFalse(result.contains(">"));
    }
    
    @Test
    @DisplayName("Should trim whitespace")
    void testSanitize_TrimWhitespace() {
        String input = "  Hello World  ";
        String result = InputValidator.sanitizeText(input);
        assertEquals("Hello World", result);
    }
    
    @Test
    @DisplayName("Should handle null input")
    void testSanitize_Null() {
        String result = InputValidator.sanitizeText(null);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should preserve normal text")
    void testSanitize_NormalText() {
        String input = "Hello World 123";
        String result = InputValidator.sanitizeText(input);
        assertEquals("Hello World 123", result);
    }

    // ============================================================
    // NAME VALIDATION TESTS
    // ============================================================
    
    @Test
    @DisplayName("Should accept valid name")
    void testValidName_Normal() {
        String result = InputValidator.validateAndSanitizeName("John Doe");
        assertEquals("John Doe", result);
    }
    
    @Test
    @DisplayName("Should accept Vietnamese name with accents")
    void testValidName_Vietnamese() {
        String result = InputValidator.validateAndSanitizeName("Nguyễn Văn A");
        assertEquals("Nguyễn Văn A", result);
    }
    
    @Test
    @DisplayName("Should sanitize name with dangerous characters")
    void testValidName_Sanitize() {
        String result = InputValidator.validateAndSanitizeName("John<script>alert('xss')</script>Doe");
        assertFalse(result.contains("<"));
        assertFalse(result.contains(">"));
    }
    
    @Test
    @DisplayName("Should reject name that is too short")
    void testInvalidName_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateAndSanitizeName("A");
        });
        assertTrue(exception.getMessage().contains("at least 2 characters"));
    }
    
    @Test
    @DisplayName("Should reject name that is too long")
    void testInvalidName_TooLong() {
        String longName = "A".repeat(101);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateAndSanitizeName(longName);
        });
        assertTrue(exception.getMessage().contains("cannot exceed 100 characters"));
    }
    
    @Test
    @DisplayName("Should reject null name")
    void testInvalidName_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateAndSanitizeName(null);
        });
        assertTrue(exception.getMessage().contains("Name cannot be empty"));
    }
    
    @Test
    @DisplayName("Should reject empty name")
    void testInvalidName_Empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateAndSanitizeName("");
        });
        assertTrue(exception.getMessage().contains("Name cannot be empty"));
    }

    // ============================================================
    // TABLE NUMBER VALIDATION TESTS
    // ============================================================
    
    @Test
    @DisplayName("Should accept valid table number")
    void testValidTableNumber() {
        assertDoesNotThrow(() -> InputValidator.validateTableNumber("Table 1"));
    }
    
    @Test
    @DisplayName("Should accept null table number (optional field)")
    void testValidTableNumber_Null() {
        assertDoesNotThrow(() -> InputValidator.validateTableNumber(null));
    }
    
    @Test
    @DisplayName("Should accept empty table number (optional field)")
    void testValidTableNumber_Empty() {
        assertDoesNotThrow(() -> InputValidator.validateTableNumber(""));
    }
    
    @Test
    @DisplayName("Should reject table number that is too long")
    void testInvalidTableNumber_TooLong() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateTableNumber("Table Number 12345");
        });
        assertTrue(exception.getMessage().contains("cannot exceed 10 characters"));
    }
    
    @Test
    @DisplayName("Should reject table number with dangerous characters")
    void testInvalidTableNumber_DangerousChars() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            InputValidator.validateTableNumber("Table<1>");
        });
        assertTrue(exception.getMessage().contains("invalid characters"));
    }
}
