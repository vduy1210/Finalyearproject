package com.example.saleapp.web_backend.repository;

import com.example.saleapp.web_backend.model.Order;
import com.example.saleapp.web_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
} 