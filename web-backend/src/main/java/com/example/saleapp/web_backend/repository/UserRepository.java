package com.example.saleapp.web_backend.repository;

import com.example.saleapp.web_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
