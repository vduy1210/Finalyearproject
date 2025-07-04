package com.example.saleapp.web_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
    private Integer userID;

    @Column(length = 50, nullable = false, unique = true)
    private String userName;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String role;

    // Getters and setters
    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
