package com.example.saleapp.web_backend.integration;

import com.example.saleapp.web_backend.dto.OrderRequest;
import com.example.saleapp.web_backend.model.User;
import com.example.saleapp.web_backend.model.Product;
import com.example.saleapp.web_backend.model.Customer;
import com.example.saleapp.web_backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled("Integration tests disabled due to H2 database compatibility issues with MySQL foreign key constraints")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests - Complete Application Flow")
public class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static User testStaff;
    private static Product testProduct;

    @BeforeEach
    public void setUp() {
        // Create test staff user if not exists
        if (testStaff == null) {
            Optional<User> existingStaff = userRepository.findById(1);
            if (existingStaff.isPresent()) {
                testStaff = existingStaff.get();
            } else {
                testStaff = new User();
                testStaff.setUserName("teststatus");
                testStaff.setEmail("teststaff@example.com");
                testStaff.setPassword("password123");
                testStaff.setRole("staff");
                testStaff = userRepository.save(testStaff);
            }
        }

        // Create test product if not exists
        if (testProduct == null) {
            testProduct = new Product();
            testProduct.setName("Integration Test Pizza");
            testProduct.setPrice(19.99);
            testProduct.setStock(100);
            testProduct.setDescription("Test pizza for integration testing");
            testProduct = productRepository.save(testProduct);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Integration Test 1: User Login Flow")
    public void testUserLoginFlow() throws Exception {
        // Create test user for login
        User loginUser = new User();
        loginUser.setUserName("integrationuser");
        loginUser.setEmail("integration@example.com");
        loginUser.setPassword("testpass123");
        loginUser.setRole("customer");
        userRepository.save(loginUser);

        // Test login with correct credentials
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "integration@example.com");
        loginRequest.put("password", "testpass123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userName").value("integrationuser"))
                .andExpect(jsonPath("$.role").value("customer"));
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test 2: Product Management Flow")
    public void testProductManagementFlow() throws Exception {
        // 1. Get all products
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 2. Get specific product
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Pizza"));

        // 3. Update product
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Pizza");
        updatedProduct.setPrice(24.99);
        updatedProduct.setStock(150);
        updatedProduct.setDescription("Updated description");

        mockMvc.perform(put("/api/products/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk());

        // 4. Update stock
        mockMvc.perform(put("/api/products/" + testProduct.getId() + "/stock")
                .param("stock", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newStock").value(200));
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test 3: Complete Order Flow")
    @Transactional
    public void testCompleteOrderFlow() throws Exception {
        // Ensure product has sufficient stock
        testProduct.setStock(100);
        productRepository.save(testProduct);

        // 1. Create order request
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("Integration Test Customer");
        orderRequest.setPhone("1112223333");
        orderRequest.setEmail("integration.customer@example.com");
        orderRequest.setTableNumber("T99");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(testProduct.getId());
        item.setQuantity(5);
        item.setPrice(testProduct.getPrice());

        orderRequest.setItems(Arrays.asList(item));

        // 2. Place order
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").exists());

        // 3. Verify stock was reduced
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(95));

        // 4. Verify customer was created
        Optional<Customer> customer = customerRepository.findByPhone("1112223333");
        Assertions.assertTrue(customer.isPresent());
        Assertions.assertEquals("Integration Test Customer", customer.get().getName());
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test 4: Order with Insufficient Stock")
    public void testOrderWithInsufficientStock() throws Exception {
        // Set low stock
        testProduct.setStock(2);
        productRepository.save(testProduct);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("Test Customer");
        orderRequest.setPhone("4445556666");
        orderRequest.setEmail("test@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(testProduct.getId());
        item.setQuantity(10); // More than available
        item.setPrice(testProduct.getPrice());

        orderRequest.setItems(Arrays.asList(item));

        // Should fail due to insufficient stock
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Insufficient stock")));
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test 5: Multiple Products in Single Order")
    @Transactional
    public void testMultipleProductsOrder() throws Exception {
        // Create additional products
        Product product2 = new Product();
        product2.setName("Test Burger");
        product2.setPrice(12.99);
        product2.setStock(50);
        product2 = productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Test Salad");
        product3.setPrice(8.99);
        product3.setStock(30);
        product3 = productRepository.save(product3);

        // Ensure main product has stock
        testProduct.setStock(100);
        productRepository.save(testProduct);

        // Create order with multiple items
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("Multi Item Customer");
        orderRequest.setPhone("7778889999");
        orderRequest.setEmail("multi@example.com");

        OrderRequest.OrderItemRequest item1 = new OrderRequest.OrderItemRequest();
        item1.setProductId(testProduct.getId());
        item1.setQuantity(2);
        item1.setPrice(testProduct.getPrice());

        OrderRequest.OrderItemRequest item2 = new OrderRequest.OrderItemRequest();
        item2.setProductId(product2.getId());
        item2.setQuantity(3);
        item2.setPrice(product2.getPrice());

        OrderRequest.OrderItemRequest item3 = new OrderRequest.OrderItemRequest();
        item3.setProductId(product3.getId());
        item3.setQuantity(1);
        item3.setPrice(product3.getPrice());

        orderRequest.setItems(Arrays.asList(item1, item2, item3));

        // Place order
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify all stocks were reduced
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(98));

        mockMvc.perform(get("/api/products/" + product2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(47));

        mockMvc.perform(get("/api/products/" + product3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(29));
    }

    @Test
    @Order(6)
    @DisplayName("Integration Test 6: Existing Customer Order")
    @Transactional
    public void testExistingCustomerOrder() throws Exception {
        // Create existing customer
        Customer existingCustomer = new Customer();
        existingCustomer.setName("Existing Customer");
        existingCustomer.setPhone("5554443333");
        existingCustomer.setEmail("existing@example.com");
        existingCustomer.setAccumulatedPoint(100.0);
        customerRepository.save(existingCustomer);

        testProduct.setStock(100);
        productRepository.save(testProduct);

        // Place order with same phone
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("Updated Name");
        orderRequest.setPhone("5554443333");
        orderRequest.setEmail("updated@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(testProduct.getId());
        item.setQuantity(1);
        item.setPrice(testProduct.getPrice());

        orderRequest.setItems(Arrays.asList(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify customer info was updated
        Optional<Customer> updatedCustomer = customerRepository.findByPhone("5554443333");
        Assertions.assertTrue(updatedCustomer.isPresent());
        Assertions.assertEquals("Updated Name", updatedCustomer.get().getName());
        Assertions.assertEquals("updated@example.com", updatedCustomer.get().getEmail());
    }
}
