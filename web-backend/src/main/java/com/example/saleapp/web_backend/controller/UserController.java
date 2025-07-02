package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.model.User;
import com.example.saleapp.web_backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody java.util.Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        System.out.println("Login request: " + email + " - " + password);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("User not found with email: " + email);
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Password mismatch for user: " + email);
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        return ResponseEntity.ok().body(
            java.util.Map.of("success", true, "userName", user.getUserName())
        );
    }
}
