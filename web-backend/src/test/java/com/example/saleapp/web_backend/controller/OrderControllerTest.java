package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.config.SecurityConfig;
import com.example.saleapp.web_backend.dto.OrderRequest;
import com.example.saleapp.web_backend.model.*;
import com.example.saleapp.web_backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
@DisplayName("Order Controller Tests")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomerRepository customerRepository;

    private User testUser;
    private Customer testCustomer;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    public void setUp() {
        // Setup test user (staff)
        testUser = new User();
        testUser.setUserID(1);
        testUser.setUserName("staff1");
        testUser.setEmail("staff@example.com");
        testUser.setRole("staff");

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setPhone("0901234567");
        testCustomer.setEmail("john@example.com");
        testCustomer.setAccumulatedPoint(0.0);

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Pizza");
        testProduct.setPrice(15.99);
        testProduct.setStock(50);
        testProduct.setDescription("Delicious pizza");

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setStaff(testUser);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setTotal(31.98);
        testOrder.setTotalAmount(31.98);
        testOrder.setStatus("Pending");
        testOrder.setShippingName("John Doe");
        testOrder.setShippingPhone("0901234567");
        testOrder.setShippingEmail("john@example.com");
    }

    @Test
    @DisplayName("Test place order - success")
    public void testPlaceOrder_Success() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john@example.com");
        orderRequest.setTableNumber("T5");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("1234567890")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    @DisplayName("Test place order - product not found")
    public void testPlaceOrder_ProductNotFound() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(999L);
        item.setQuantity(2);
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Product not found")));
    }

    @Test
    @DisplayName("Test place order - insufficient stock")
    public void testPlaceOrder_InsufficientStock() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(100); // More than available stock (50)
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Insufficient stock")));
    }

    @Test
    @DisplayName("Test place order - with table number")
    public void testPlaceOrder_WithTableNumber() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john@example.com");
        orderRequest.setTableNumber("T10");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Test place order - existing customer")
    public void testPlaceOrder_ExistingCustomer() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe Updated");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john.new@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Test place order - default staff not found")
    public void testPlaceOrder_DefaultStaffNotFound() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("John Doe");
        orderRequest.setPhone("0901234567");
        orderRequest.setEmail("john@example.com");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);
        item.setPrice(15.99);

        orderRequest.setItems(Arrays.asList(item));

        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Default staff not found"));
    }

    @Test
    @DisplayName("Test get orders by user - success")
    public void testGetOrdersByUser_Success() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser(testUser)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Test get orders by user - user not found")
    public void testGetOrdersByUser_UserNotFound() throws Exception {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/orders/user/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Test place order with multiple items")
    public void testPlaceOrder_MultipleItems() throws Exception {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Burger");
        product2.setPrice(9.99);
        product2.setStock(30);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setName("Jane Smith");
        orderRequest.setPhone("0987654321");
        orderRequest.setEmail("jane@example.com");

        OrderRequest.OrderItemRequest item1 = new OrderRequest.OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setPrice(15.99);

        OrderRequest.OrderItemRequest item2 = new OrderRequest.OrderItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(3);
        item2.setPrice(9.99);

        orderRequest.setItems(Arrays.asList(item1, item2));

        when(customerRepository.findByPhone("0987654321")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
