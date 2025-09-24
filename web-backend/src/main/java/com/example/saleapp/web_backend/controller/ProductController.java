package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.model.Product;
import com.example.saleapp.web_backend.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.nio.file.*;
import java.io.IOException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok().body(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateProductStock(@PathVariable Long id, @RequestParam int stock) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
        
        if (stock < 0) {
            return ResponseEntity.badRequest().body("Stock cannot be negative");
        }
        
        product.setStock(stock);
        productRepository.save(product);
        
        return ResponseEntity.ok().body(Map.of(
            "success", true,
            "productId", id,
            "newStock", stock,
            "productName", product.getName()
        ));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        
        // 1. Set the upload directory (relative to your project root)
        String uploadDir = "E:/Finalyearproject/web-backend/uploads/";
        try {
            // 2. Create the uploads directory if it doesn't exist
            Files.createDirectories(Paths.get(uploadDir));

            // 3. Create a unique filename
            String filename = "product_" + id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);

            // 4. Save the file to disk
            Files.write(filePath, file.getBytes());

            // 5. Update the product's imageUrl in the database
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("Product not found with ID: " + id);
            }
            product.setImageUrl("/uploads/" + filename);
            productRepository.save(product);

            // 6. Return the new image URL
            return ResponseEntity.ok().body("{\"imageUrl\": \"/uploads/" + filename + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }
}