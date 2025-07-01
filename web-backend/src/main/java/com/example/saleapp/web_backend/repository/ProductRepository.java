package com.example.saleapp.web_backend.repository;

import com.example.saleapp.web_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
