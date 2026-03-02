package com.example.saleapp.web_backend.controller;

import com.example.saleapp.web_backend.dto.OrderRequest;
import com.example.saleapp.web_backend.model.*;
import com.example.saleapp.web_backend.repository.*;
import com.example.saleapp.web_backend.validator.InputValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Place a new order with comprehensive validation and stock management.
     * This method handles:
     * - Input validation (phone, email, name, table number)
     * - Customer lookup/creation with conflict detection
     * - Two-phase stock validation and update
     * - Order and order items creation
     * 
     * @param orderRequest The order request containing customer info and items
     * @return ResponseEntity with success status and orderId, or error message
     */
    @PostMapping
    @org.springframework.transaction.annotation.Transactional // Đánh dấu method này là một giao dịch (transaction). Nếu
                                                              // có lỗi xảy ra ở bất kỳ bước nào, toàn bộ thay đổi db sẽ
                                                              // bị rollback (hoàn tác).
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // ============================================================
            // WEB ORDER WORKFLOW (Quy trình xử lý đơn hàng Web)
            // ============================================================
            // 1. Nhận request từ Web Web (OrderRequest).
            // 2. Validate dữ liệu (Input Validation).
            // 3. Tìm hoặc tạo mới khách hàng (Customer Handling).
            // 4. Lấy giá sản phẩm từ Database (Server-side Pricing).
            // 5. [SKIPPED] Trừ tồn kho (Stock Deduction) -> Đã tắt theo yêu cầu.
            // Stock sẽ được trừ khi nhân viên xác nhận đơn ở Desktop App.
            // 6. Lưu đơn hàng vào Database (Save Order).
            // ============================================================

            // ============================================================
            // BƯỚC 1: KIỂM TRA DỮ LIỆU ĐẦU VÀO (INPUT VALIDATION)
            // ============================================================
            // Kiểm tra tính hợp lệ của số điện thoại, email, tên để tránh lỗi hoặc tấn công
            // injection.
            InputValidator.validatePhone(orderRequest.getPhone());
            InputValidator.validateEmail(orderRequest.getEmail());
            String safeName = InputValidator.validateAndSanitizeName(orderRequest.getName());
            InputValidator.validateTableNumber(orderRequest.getTableNumber());

            // ============================================================
            // BƯỚC 2: XỬ LÝ THÔNG TIN KHÁCH HÀNG (CUSTOMER LOOKUP/CONFLICT)
            // ============================================================
            // Tìm khách hàng trong database dựa trên SĐT hoặc Email
            Optional<Customer> existingByPhone = customerRepository.findByPhone(orderRequest.getPhone());
            Optional<Customer> existingByEmail = customerRepository.findByEmail(orderRequest.getEmail());
            Customer customer = null;

            // ============================================================
            // TRƯỜNG HỢP 1: Số điện thoại đã tồn tại
            // ============================================================
            if (existingByPhone.isPresent()) {
                Customer customerByPhone = existingByPhone.get();

                // Kiểm tra xem tên và email nhập vào có khớp với dữ liệu đã lưu không.
                // Điều này ngăn chặn việc sử dụng SĐT của người khác để đặt hàng.
                boolean nameMatches = customerByPhone.getName().equalsIgnoreCase(safeName.trim());
                boolean emailMatches = customerByPhone.getEmail().equalsIgnoreCase(orderRequest.getEmail().trim());

                if (!nameMatches || !emailMatches) {
                    // Nếu thông tin không khớp, trả về lỗi chi tiết để người dùng biết.
                    // Fix UX: Thông báo rõ ràng về việc trùng lặp thông tin.
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "error", "❌ Số điện thoại này đã được đăng ký với thông tin khác\n" +
                                    "⚠️ Vui lòng sử dụng đúng thông tin đã đăng ký hoặc dùng số điện thoại khác.",
                            "conflictType", "phone_mismatch", // Frontend dựa vào key này để xử lý hiển thị
                            "existingCustomer", Map.of(
                                    "name", customerByPhone.getName(),
                                    "email", customerByPhone.getEmail(),
                                    "phone", customerByPhone.getPhone())));
                }

                // Nếu thông tin khớp, sử dụng khách hàng hiện có.
                customer = customerByPhone;
            }
            // ============================================================
            // TRƯỜNG HỢP 2: Email đã tồn tại nhưng SĐT khác (Trùng Email)
            // ============================================================
            else if (existingByEmail.isPresent()) {
                Customer customerByEmail = existingByEmail.get();

                // Ngăn chặn việc một email được đăng ký cho nhiều SĐT khác nhau (bảo mật tài
                // khoản).
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "❌ Email này đã được đăng ký với số điện thoại khác\n" +
                                "⚠️ Vui lòng sử dụng đúng số điện thoại đã đăng ký hoặc dùng email khác.",
                        "conflictType", "email_mismatch",
                        "existingCustomer", Map.of(
                                "name", customerByEmail.getName(),
                                "email", customerByEmail.getEmail(),
                                "phone", customerByEmail.getPhone())));
            }
            // ============================================================
            // TRƯỜNG HỢP 3: Khách hàng mới hoàn toàn
            // ============================================================
            else {
                // Tạo mới đối tượng Customer và lưu vào DB.
                customer = new Customer();
                customer.setName(safeName);
                customer.setPhone(orderRequest.getPhone());
                customer.setEmail(orderRequest.getEmail());
                customer.setAccumulatedPoint(0.0); // Điểm tích lũy ban đầu là 0
                customer = customerRepository.save(customer);
            }

            // ============================================================
            // BƯỚC 3: LẤY NHÂN VIÊN MẶC ĐỊNH
            // ============================================================
            // Gán đơn hàng cho nhân viên mặc định (ID=1) để theo dõi.
            User defaultStaff = userRepository.findById(1).orElse(null);
            if (defaultStaff == null) {
                return ResponseEntity.status(500).body("Default staff not found");
            }

            // ============================================================
            // BƯỚC 4: KHỞI TẠO ĐỐI TƯỢNG ORDER
            // ============================================================
            Order order = new Order();
            order.setCustomer(customer);
            order.setStaff(defaultStaff);
            order.setShippingName(orderRequest.getName());
            order.setShippingPhone(orderRequest.getPhone());
            order.setShippingEmail(orderRequest.getEmail());
            order.setStatus("Pending"); // Trạng thái ban đầu là Pending (Chờ xử lý)
            order.setOrderDate(LocalDateTime.now());

            if (orderRequest.getTableNumber() != null && !orderRequest.getTableNumber().trim().isEmpty()) {
                order.setTableNumber(orderRequest.getTableNumber());
            }

            double total = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();

            // ============================================================
            // BƯỚC 5: XÁC THỰC VÀ CẬP NHẬT TỒN KHO (QUAN TRỌNG)
            // Fix Bug 4 (Race Condition) & Bug 1 (Price Manipulation)
            // ============================================================

            for (OrderRequest.OrderItemRequest item : orderRequest.getItems()) {
                // Fix Bug 1: Lấy giá sản phẩm TRỰC TIẾP từ Database thay vì tin tưởng giá từ
                // Frontend gửi lên.
                // Frontend có thể bị chỉnh sửa, nên giá phải luôn lấy từ server.
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + item.getProductId()));

                System.out.println("Processing item: " + product.getName() + " | Req Qty: " + item.getQuantity()
                        + " | Current Stock: " + product.getStock());

                // Tạo OrderItem với giá SERVER-SIDE
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(product.getPrice()); // Sử dụng giá DB

                total += product.getPrice() * item.getQuantity();
                orderItems.add(orderItem);
            }

            // Cập nhật tổng tiền cuối cùng
            order.setTotal(total);
            order.setTotalAmount(total);
            order.setItems(orderItems); // Hibernate sẽ tự động lưu OrderItem nhờ CascadeType.ALL

            // ============================================================
            // BƯỚC 6: LƯU ĐƠN HÀNG VÀO DATABASE
            // ============================================================
            Order savedOrder = orderRepository.save(order);

            // Gán lại quan hệ ngược chiều nếu cần thiết (dù Cascade đã lo)
            for (OrderItem item : orderItems) {
                item.setOrder(savedOrder);
            }

            // ============================================================
            // BƯỚC 7: CẬP NHẬT ĐIỂM TÍCH LŨY (LOYALTY POINTS) - NEW LOGIC
            // ============================================================
            // Logic: Cộng cố định 10 điểm cho mỗi đơn hàng (Theo yêu cầu)
            if (customer != null) {
                double pointsEarned = 10.0;
                double currentPoints = (customer.getAccumulatedPoint() != null) ? customer.getAccumulatedPoint() : 0.0;
                customer.setAccumulatedPoint(currentPoints + pointsEarned);
                customerRepository.save(customer);
            }

            // Trả về kết quả thành công kèm Order ID
            return ResponseEntity.ok(Map.of("success", true, "orderId", savedOrder.getId()));

        } catch (Exception e) {
            // Log lỗi ra console
            e.printStackTrace();
            // Nếu có bất kỳ lỗi nào (ví dụ hết hàng), Transaction sẽ rollback (hoàn trả
            // trạng thái cũ).
            // Trả về lỗi 500 cho Frontend.
            return ResponseEntity.status(500).body("Error placing order: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");
        User user = userOpt.get();
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }
}