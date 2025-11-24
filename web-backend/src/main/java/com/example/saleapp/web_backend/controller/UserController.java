package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.model.User;
import com.example.saleapp.web_backend.repository.UserRepository;
import com.example.saleapp.web_backend.validator.InputValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody java.util.Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        
        // Validate email format
        try {
            InputValidator.validateEmail(email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        
        // Validate password is not empty
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Password cannot be empty");
        }
        
        System.out.println("Login request: " + email + " - " + password);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("User not found with email: " + email);
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        // Use BCrypt password verification
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Password mismatch for user: " + email);
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        return ResponseEntity.ok().body(
            java.util.Map.of(
                "success", true,
                "userName", user.getUserName(),
                "role", user.getRole()
            )
        );
    }
}
