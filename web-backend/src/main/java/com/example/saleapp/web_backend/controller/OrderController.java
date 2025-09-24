package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.dto.OrderRequest;
import com.example.saleapp.web_backend.model.*;
import com.example.saleapp.web_backend.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // 1) Find or create customer by phone (more unique identifier)
            Customer customer = customerRepository.findByPhone(orderRequest.getPhone())
                    .orElseGet(() -> {
                        Customer c = new Customer();
                        c.setName(orderRequest.getName());
                        c.setPhone(orderRequest.getPhone());
                        c.setEmail(orderRequest.getEmail());
                        c.setAccumulatedPoint(0.0);
                        return customerRepository.save(c);
                    });

            // 2) Update customer information if needed
            if (!customer.getName().equals(orderRequest.getName()) || 
                !customer.getEmail().equals(orderRequest.getEmail())) {
                customer.setName(orderRequest.getName());
                customer.setEmail(orderRequest.getEmail());
                customerRepository.save(customer);
            }

            // 3) Get default staff user (assuming staff ID 1 exists)
            User defaultStaff = userRepository.findById(1).orElse(null);
            if (defaultStaff == null) {
                return ResponseEntity.status(500).body("Default staff not found");
            }

            // 4) Build order
            Order order = new Order();
            order.setCustomer(customer);
            order.setStaff(defaultStaff); // Set staff_id
            order.setShippingName(orderRequest.getName());
            order.setShippingPhone(orderRequest.getPhone());
            order.setShippingEmail(orderRequest.getEmail());
            order.setStatus("Pending");
            order.setOrderDate(LocalDateTime.now());
            
            // Add table number if available
            if (orderRequest.getTableNumber() != null && !orderRequest.getTableNumber().trim().isEmpty()) {
                order.setTableNumber(orderRequest.getTableNumber());
            }

            double total = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            
            // First pass: Validate stock availability
            for (OrderRequest.OrderItemRequest item : orderRequest.getItems()) {
                Optional<Product> productOpt = productRepository.findById(item.getProductId());
                if (productOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Product not found with ID: " + item.getProductId());
                }
                Product product = productOpt.get();
                
                if (product.getStock() < item.getQuantity()) {
                    return ResponseEntity.badRequest().body(
                        "Insufficient stock for product: " + product.getName() + 
                        ". Available: " + product.getStock() + 
                        ", Requested: " + item.getQuantity()
                    );
                }
            }
            
            // Second pass: Process order and update stock
            for (OrderRequest.OrderItemRequest item : orderRequest.getItems()) {
                Optional<Product> productOpt = productRepository.findById(item.getProductId());
                if (productOpt.isEmpty()) continue;
                Product product = productOpt.get();

                // Update stock: subtract ordered quantity
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(newStock);
                productRepository.save(product); // Save updated stock

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                total += item.getPrice() * item.getQuantity();
                orderItems.add(orderItem);
            }

            order.setTotal(total);
            order.setTotalAmount(total); // Set total_amount field
            order.setItems(orderItems);
            
            // Save order first to get the order_id
            Order savedOrder = orderRepository.save(order);
            
            // Save order items (web_order_details)
            for (OrderItem item : orderItems) {
                item.setOrder(savedOrder);
            }
            // The items will be saved automatically due to CascadeType.ALL
            return ResponseEntity.ok(Map.of("success", true, "orderId", order.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error placing order: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        User user = userOpt.get();
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }
}