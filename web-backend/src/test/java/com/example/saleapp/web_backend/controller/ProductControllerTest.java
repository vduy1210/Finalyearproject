package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.config.SecurityConfig;
import com.example.saleapp.web_backend.model.Product;
import com.example.saleapp.web_backend.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@DisplayName("Product Controller Tests")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private List<Product> productList;

    @BeforeEach
    public void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Pizza");
        product1.setPrice(15.99);
        product1.setStock(50);
        product1.setDescription("Delicious pizza");
        product1.setImageUrl("/uploads/pizza.jpg");

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Burger");
        product2.setPrice(9.99);
        product2.setStock(30);
        product2.setDescription("Tasty burger");
        product2.setImageUrl("/uploads/burger.jpg");

        productList = Arrays.asList(product1, product2);
    }

    @Test
    @DisplayName("Test get all products")
    public void testGetAllProducts() throws Exception {
        // Arrange
        when(productRepository.findAll()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[1].name").value("Burger"));
    }

    @Test
    @DisplayName("Test get product by ID - success")
    public void testGetProductById_Success() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.price").value(15.99))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    @DisplayName("Test get product by ID - not found")
    public void testGetProductById_NotFound() throws Exception {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test update product - success")
    public void testUpdateProduct_Success() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Pepperoni Pizza");
        updatedProduct.setPrice(17.99);
        updatedProduct.setStock(45);
        updatedProduct.setDescription("Spicy pepperoni pizza");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Test update product - not found")
    public void testUpdateProduct_NotFound() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Test Product");

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test update product stock - success")
    public void testUpdateProductStock_Success() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act & Assert
        mockMvc.perform(put("/api/products/1/stock")
                .param("stock", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.newStock").value(100));
    }

    @Test
    @DisplayName("Test update product stock - negative stock")
    public void testUpdateProductStock_NegativeStock() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(put("/api/products/1/stock")
                .param("stock", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Stock cannot be negative"));
    }

    @Test
    @DisplayName("Test update product stock - product not found")
    public void testUpdateProductStock_NotFound() throws Exception {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/999/stock")
                .param("stock", "100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test upload product image - success")
    public void testUploadProductImage_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act & Assert
        mockMvc.perform(multipart("/api/products/1/image")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").exists());
    }

    @Test
    @DisplayName("Test upload product image - empty file")
    public void testUploadProductImage_EmptyFile() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/products/1/image")
                .file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test upload product image - product not found")
    public void testUploadProductImage_ProductNotFound() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(multipart("/api/products/999/image")
                .file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test check product image endpoint")
    public void testCheckProductImage() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/products/1/check-image"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Pizza"))
                .andExpect(jsonPath("$.imageUrl").value("/uploads/pizza.jpg"));
    }

    @Test
    @DisplayName("Test get upload directory status")
    public void testTestUploadDirectory() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/test-upload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadDir").exists())
                .andExpect(jsonPath("$.directoryExists").exists());
    }
}
