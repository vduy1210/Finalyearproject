package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.model.Product;
import com.example.saleapp.web_backend.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
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

    @GetMapping("/test-upload")
    public ResponseEntity<?> testUploadDirectory() {
        try {
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            
            // Create directory if it doesn't exist
            Files.createDirectories(uploadPath);
            
            // List existing files
            List<String> existingFiles = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        existingFiles.add(file.getFileName().toString());
                    }
                }
            }
            
            return ResponseEntity.ok().body(Map.of(
                "uploadDir", uploadDir,
                "absolutePath", uploadPath.toAbsolutePath().toString(),
                "directoryExists", Files.exists(uploadPath),
                "directoryWritable", Files.isWritable(uploadPath),
                "existingFiles", existingFiles,
                "fileCount", existingFiles.size(),
                "message", "Upload directory test completed"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error testing upload directory: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/check-image")
    public ResponseEntity<?> checkProductImage(@PathVariable Long id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            String uploadDir = "uploads/";
            String imageUrl = product.getImageUrl();
            
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.ok().body(Map.of(
                    "productId", id,
                    "productName", product.getName(),
                    "imageUrl", "No image URL",
                    "fileExists", false,
                    "message", "Product has no image URL"
                ));
            }
            
            // Remove leading slash if present
            String filename = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            Path filePath = Paths.get(uploadDir, filename);
            boolean fileExists = Files.exists(filePath);
            
            return ResponseEntity.ok().body(Map.of(
                "productId", id,
                "productName", product.getName(),
                "imageUrl", imageUrl,
                "filename", filename,
                "filePath", filePath.toString(),
                "fileExists", fileExists,
                "message", fileExists ? "Image file exists" : "Image file NOT found"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking image: " + e.getMessage());
        }
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
        String uploadDir = "uploads/";
        try {
            // 2. Create the uploads directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            
            // Debug: Log the upload directory
            System.out.println("Upload directory: " + uploadPath.toAbsolutePath());
            System.out.println("Directory exists: " + Files.exists(uploadPath));
            System.out.println("Directory is writable: " + Files.isWritable(uploadPath));

            // 3. Create a unique filename
            String filename = "product_" + id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);

            // 4. Save the file to disk
            System.out.println("Saving file to: " + filePath.toAbsolutePath());
            Files.write(filePath, file.getBytes());
            System.out.println("File saved successfully. File exists: " + Files.exists(filePath));
            System.out.println("File size: " + Files.size(filePath) + " bytes");

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