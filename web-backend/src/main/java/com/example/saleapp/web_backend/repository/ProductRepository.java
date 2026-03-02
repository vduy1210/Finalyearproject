package com.example.saleapp.web_backend.repository;

import com.example.saleapp.web_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int decrementStock(@org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("quantity") int quantity);
}
