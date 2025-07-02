package com.example.saleapp.web_backend.repository;

import com.example.saleapp.web_backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
} 