# Thông Tin Chi Tiết Cho Vẽ Sequence Diagram

## 📋 Tổng Quan Kiến Trúc Hệ Thống

### Hệ Thống Gồm 2 Phần:

#### 1. **Desktop Application (mavenproject1)** - Java Swing
- **Presentation Layer**: View classes (JFrame, JPanel)
- **Business Logic Layer**: DAO classes 
- **Data Access Layer**: DatabaseConnector
- **Architecture Pattern**: MVC (Model-View-Controller)

#### 2. **Web Application (web-backend + web-frontend)**
- **Backend**: Spring Boot REST API
- **Frontend**: React.js
- **Architecture**: 3-tier với Controller → Repository (JPA) → Database

---

## 🏗️ KIẾN TRÚC LAYERED CHO DESKTOP APP

### Layer 1: Presentation (View)
```
view/
├── LoginForm.java          - Form đăng nhập
├── MainApplication.java    - Main frame chứa menu và panels
├── OrderPanel.java         - Quản lý đơn hàng (desktop)
├── ProductManagerPanel.java - Quản lý sản phẩm
├── UserManagementPanel.java - Quản lý user
├── CustomerManagementPanel.java - Quản lý khách hàng & tier
├── TierConfigDialog.java   - Cấu hình customer tier
└── RevenueReportPanel.java - Báo cáo doanh thu
```

### Layer 2: Business Logic (DAO)
```
dao/
├── UserDAO.java            - CRUD users, authentication
├── ProductDAO.java         - CRUD products
├── OrderDao.java           - Delegate to WebOrderDao/AppOrderDao
├── WebOrderDao.java        - Web orders (from web frontend)
├── AppOrderDao.java        - Desktop orders (manual input)
├── CustomerTierDAO.java    - Tier management
└── GetProduct.java         - Helper for product operations
```

### Layer 3: Data Model
```
model/
├── user.java              - User entity
├── customer.java          - Customer entity
├── Product.java           - Product entity
├── Order.java             - Order entity
├── OrderDetails.java      - Order line items
└── CustomerTier.java      - Customer tier config
```

### Layer 4: Database
```
database/
└── DatabaseConnector.java - MySQL connection manager
```

---

## 🌐 KIẾN TRÚC WEB BACKEND (Spring Boot)

### Layer 1: Controller (REST API)
```
controller/
├── UserController.java    - /api/users/** endpoints
├── ProductController.java - /api/products/** endpoints  
└── OrderController.java   - /api/orders/** endpoints
```

### Layer 2: Repository (JPA/Hibernate)
```
repository/
├── UserRepository.java        - JpaRepository<User, Integer>
├── ProductRepository.java     - JpaRepository<Product, Long>
├── OrderRepository.java       - JpaRepository<Order, Long>
├── OrderItemRepository.java   - JpaRepository<OrderItem, Long>
└── CustomerRepository.java    - JpaRepository<Customer, Long>
```

### Layer 3: Model (Entity)
```
model/
├── User.java              - @Entity for users table
├── Product.java           - @Entity for products table
├── Order.java             - @Entity for web_order table
├── OrderItem.java         - @Entity for web_order_details table
└── Customer.java          - @Entity for customers table
```

### Layer 4: DTO
```
dto/
└── OrderRequest.java      - Request payload cho place order
```

---

## 📊 DATABASE SCHEMA

### Tables:
1. **users** - Lưu thông tin người dùng (admin, staff, user)
   - Columns: userID, userName, password, email, role
   
2. **customers** - Thông tin khách hàng
   - Columns: id, name, phone, email, accumulatedPoint
   
3. **products** - Danh mục sản phẩm
   - Columns: id, name, price, stock, imageUrl, description, created_at
   
4. **web_order** - Đơn hàng từ web
   - Columns: order_id, customer_id, staff_id, user_id, order_date, total_amount, total, status, shipping_name, shipping_phone, shipping_email, table_number
   
5. **web_order_details** - Chi tiết đơn hàng web
   - Columns: detail_id, order_id, product_id, quantity, price
   
6. **app_order** - Đơn hàng từ desktop app
   - Columns: order_id, customer_id, user_id, order_date, total_amount, tax, discount, total, status, shipping_name, shipping_phone, shipping_email, table_number
   
7. **app_order_details** - Chi tiết đơn hàng desktop
   - Columns: detail_id, order_id, product_id, quantity, price
   
8. **customer_tiers** - Cấu hình bậc khách hàng
   - Columns: id, tier_name, min_points, max_points, discount_percent, description

---

## 🔄 CÁC USE CASE CHÍNH VÀ LUỒNG XỬ LÝ

---

### USE CASE 1: ĐĂNG NHẬP (Desktop App)

**Actor**: Staff/Admin

**Luồng chính**:
1. User mở LoginForm
2. User nhập username và password
3. User click "Sign In" button
4. LoginForm gọi `UserDAO.authenticateUser(username, password)`
5. UserDAO thực hiện query: `SELECT userID FROM users WHERE userName=? AND password=?`
6. DatabaseConnector cung cấp Connection
7. Nếu tìm thấy user:
   - LoginForm gọi `UserDAO.getUserRole(username)`
   - UserDAO query: `SELECT role FROM users WHERE userName=?`
   - LoginForm tạo MainApplication(username, role)
   - LoginForm đóng và MainApplication hiển thị
8. Nếu không tìm thấy: hiển thị error message

**Messages/Methods**:
- `LoginForm.actionPerformed()` → (internal event handler)
- `UserDAO.authenticateUser(String, String): boolean`
- `UserDAO.getUserRole(String): String`
- `DatabaseConnector.getConnection(): Connection`
- `PreparedStatement.executeQuery(): ResultSet`
- `ResultSet.next(): boolean`
- `MainApplication(String username, String role)` (constructor)
- `LoginForm.dispose()`

**Điều kiện**:
- if (username.isEmpty() || password.isEmpty()) → show validation error
- if (authenticateUser returns false) → show login failed
- if (authenticateUser returns true) → proceed to get role

**Xử lý ngoại lệ**:
- SQLException → catch và print stack trace, return false

---

### USE CASE 2: ĐĂNG NHẬP (Web App - React + Spring Boot)

**Actor**: Customer

**Luồng chính**:
1. User truy cập Login page (React)
2. User nhập email và password
3. User click "Login" button
4. React gọi `authService.login(email, password)`
5. authService gửi POST request đến `/api/users/login`
6. UserController.loginUser() nhận request
7. UserController gọi `userRepository.findByEmail(email)`
8. JPA Hibernate execute query: `SELECT * FROM users WHERE email=?`
9. UserController kiểm tra password
10. Nếu hợp lệ: return ResponseEntity với user info (userName, role)
11. React lưu user info vào localStorage
12. React redirect đến trang chủ

**Messages/Methods**:
- `authService.login(email, password): Promise`
- `POST /api/users/login` (HTTP Request)
- `UserController.loginUser(Map<String, String>): ResponseEntity<?>`
- `userRepository.findByEmail(String): User`
- `User.getPassword(): String`
- `User.getUserName(): String`
- `User.getRole(): String`
- `ResponseEntity.ok(Object): ResponseEntity`
- `ResponseEntity.status(401): ResponseEntity.BodyBuilder`

**Điều kiện**:
- if (user == null) → return 401 "Invalid email or password"
- if (!password.equals(user.getPassword())) → return 401
- else → return 200 with user info

**Return values**:
- Success: `{"success": true, "userName": "...", "role": "..."}`
- Failure: 401 status with error message

---

### USE CASE 3: QUẢN LÝ SẢN PHẨM - Thêm Sản Phẩm (Desktop)

**Actor**: Admin/Staff

**Luồng chính**:
1. User mở ProductManagerPanel
2. User nhập thông tin sản phẩm (name, price, stock)
3. User click "Add Product" button
4. ProductManagerPanel gọi `ProductDAO.addProduct(name, price, stock)`
5. ProductDAO prepare SQL: `INSERT INTO products (name, price, stock) VALUES (?, ?, ?)`
6. ProductDAO gọi DatabaseConnector.getConnection()
7. ProductDAO execute PreparedStatement
8. Database insert record và return affected rows
9. Nếu > 0: return true
10. ProductManagerPanel refresh table và hiển thị success message

**Messages/Methods**:
- `ProductManagerPanel.actionPerformed()` (button click)
- `ProductDAO.addProduct(String, double, int): boolean`
- `DatabaseConnector.getConnection(): Connection`
- `Connection.prepareStatement(String): PreparedStatement`
- `PreparedStatement.setString(int, String)`
- `PreparedStatement.setDouble(int, double)`
- `PreparedStatement.setInt(int, int)`
- `PreparedStatement.executeUpdate(): int`
- `ProductManagerPanel.loadProducts()` (refresh table)

**Điều kiện**:
- Validate: name not empty, price > 0, stock >= 0
- if (executeUpdate() > 0) → success
- else → failure

**Xử lý ngoại lệ**:
- SQLException → catch, printStackTrace, return false

---

### USE CASE 4: QUẢN LÝ SẢN PHẨM - Cập Nhật Stock (Web API)

**Actor**: Admin (via Web Frontend)

**Luồng chính**:
1. Admin chọn product và nhập stock mới
2. React gọi API: `PUT /api/products/{id}/stock?stock={newStock}`
3. ProductController.updateProductStock(id, stock) nhận request
4. ProductController gọi `productRepository.findById(id)`
5. JPA execute: `SELECT * FROM products WHERE id=?`
6. ProductController validate stock >= 0
7. ProductController gọi `product.setStock(newStock)`
8. ProductController gọi `productRepository.save(product)`
9. JPA execute: `UPDATE products SET stock=? WHERE id=?`
10. Return ResponseEntity 200 với thông tin updated

**Messages/Methods**:
- `PUT /api/products/{id}/stock` (HTTP Request)
- `ProductController.updateProductStock(Long, int): ResponseEntity<?>`
- `productRepository.findById(Long): Optional<Product>`
- `Product.setStock(int): void`
- `productRepository.save(Product): Product`
- `ResponseEntity.ok(Object): ResponseEntity`
- `ResponseEntity.badRequest(): ResponseEntity.BodyBuilder`
- `ResponseEntity.status(404): ResponseEntity.BodyBuilder`

**Điều kiện**:
- if (product == null) → return 404 "Product not found"
- if (stock < 0) → return 400 "Stock cannot be negative"
- else → update and return 200

**Return values**:
- Success: `{"success": true, "productId": id, "newStock": stock, "productName": "..."}`

---

### USE CASE 5: TẠO ĐON HÀNG (Desktop App - Manual Order Entry)

**Actor**: Staff

**Luồng chính**:
1. Staff mở OrderPanel
2. Staff chọn products từ danh sách
3. Staff nhập quantity và click "Add to Cart"
4. Staff nhập thông tin customer (phone)
5. OrderPanel tự động lookup customer từ DB by phone
6. OrderPanel gọi `CustomerTierDAO.getTierByPoints(points)`
7. CustomerTierDAO return discount percent
8. Staff click "Place Order"
9. OrderPanel tạo Order object và List<OrderDetails>
10. OrderPanel gọi `AppOrderDao.createAppOrder(order, details)`
11. AppOrderDao begin transaction
12. AppOrderDao execute: `INSERT INTO app_order (...) VALUES (...)`
13. AppOrderDao get generated order_id
14. AppOrderDao execute batch: `INSERT INTO app_order_details (...) VALUES (...)`
15. AppOrderDao execute batch: `UPDATE products SET stock = stock - ? WHERE name = ?`
16. AppOrderDao commit transaction
17. Return true
18. OrderPanel hiển thị success và clear cart

**Messages/Methods**:
- `OrderPanel.actionPerformed()` (Place Order button)
- `OrderPanel.lookupCustomerByPhone(String): CustomerInfo`
- `CustomerTierDAO.getTierByPoints(float): CustomerTier`
- `Order(...)` (constructor)
- `OrderDetails(...)` (constructor)
- `AppOrderDao.createAppOrder(Order, List<OrderDetails>): boolean`
- `Connection.setAutoCommit(false)`
- `PreparedStatement.executeUpdate(): int`
- `PreparedStatement.getGeneratedKeys(): ResultSet`
- `PreparedStatement.addBatch()`
- `PreparedStatement.executeBatch(): int[]`
- `Connection.commit()`

**Điều kiện (loops/conditions)**:
- Loop: for each OrderDetails in list → addBatch()
- if (rs.next()) → get generated ID, else throw SQLException
- Try-catch: if SQLException → rollback transaction

**Xử lý ngoại lệ**:
- SQLException → rollback, printStackTrace, return false
- Exception → rollback, printStackTrace, return false

---

### USE CASE 6: ĐẶT HÀNG (Web App - Customer Order)

**Actor**: Customer

**Luồng chính**:
1. Customer browse products trên web
2. Customer add products vào cart (local state)
3. Customer nhập thông tin: name, phone, email, (table number optional)
4. Customer click "Place Order"
5. React gọi `POST /api/orders` với OrderRequest body
6. OrderController.placeOrder(orderRequest) nhận request
7. OrderController gọi `customerRepository.findByPhone(phone)`
8. Nếu không tìm thấy: create new Customer
9. OrderController gọi `customerRepository.save(customer)`
10. OrderController gọi `userRepository.findById(1)` (default staff)
11. OrderController tạo Order object
12. OrderController loop qua items:
    - Validate stock: `productRepository.findById(productId)`
    - Check: `if (product.getStock() < quantity)` → return 400 error
13. OrderController loop qua items lần 2:
    - Update stock: `product.setStock(newStock)`
    - `productRepository.save(product)`
    - Tạo OrderItem objects
14. OrderController set Order.items
15. OrderController gọi `orderRepository.save(order)`
16. JPA cascade save OrderItems
17. Return 200 với orderId

**Messages/Methods**:
- `POST /api/orders` (HTTP Request)
- `OrderController.placeOrder(OrderRequest): ResponseEntity<?>`
- `customerRepository.findByPhone(String): Optional<Customer>`
- `Customer()` (constructor)
- `customerRepository.save(Customer): Customer`
- `userRepository.findById(Integer): Optional<User>`
- `Order()` (constructor)
- `productRepository.findById(Long): Optional<Product>`
- `Product.getStock(): int`
- `Product.setStock(int): void`
- `productRepository.save(Product): Product`
- `OrderItem()` (constructor)
- `Order.setItems(List<OrderItem>)`
- `orderRepository.save(Order): Order`
- `ResponseEntity.ok(Object): ResponseEntity`
- `ResponseEntity.badRequest(): ResponseEntity.BodyBuilder`

**Điều kiện/Loops**:
- Loop 1: Validate stock for all items
  - if (product not found) → return 400
  - if (stock < quantity) → return 400 with error message
- Loop 2: Process order and update stock
  - Update each product's stock
  - Create OrderItem for each

**Xử lý ngoại lệ**:
- Exception e → catch, printStackTrace, return 500 "Error placing order"

**Return values**:
- Success: `{"success": true, "orderId": 123}`
- Failure: 400/500 with error message

---

### USE CASE 7: QUẢN LÝ CUSTOMER TIER - Xem Danh Sách Tier

**Actor**: Admin

**Luồng chính**:
1. Admin mở TierConfigDialog
2. TierConfigDialog gọi `loadTiers()`
3. loadTiers() gọi `CustomerTierDAO.getAllTiers()`
4. CustomerTierDAO execute: `SELECT * FROM customer_tiers ORDER BY min_points ASC`
5. DatabaseConnector provide Connection
6. Loop qua ResultSet:
   - Tạo CustomerTier objects
   - Add vào List
7. Return List<CustomerTier>
8. TierConfigDialog loop qua list và add rows vào JTable

**Messages/Methods**:
- `TierConfigDialog()` (constructor)
- `TierConfigDialog.loadTiers(): void`
- `CustomerTierDAO.getAllTiers(): List<CustomerTier>`
- `DatabaseConnector.getConnection(): Connection`
- `PreparedStatement.executeQuery(): ResultSet`
- Loop: `while (rs.next())`
  - `CustomerTier(...)` (constructor)
  - `List.add(CustomerTier)`
- `DefaultTableModel.addRow(Object[])`

---

### USE CASE 8: QUẢN LÝ CUSTOMER TIER - Cập Nhật Tier

**Actor**: Admin

**Luồng chính**:
1. Admin chọn tier từ table
2. Admin click "Edit" button
3. TierConfigDialog gọi `editSelectedTier()`
4. Show dialog với current values
5. Admin nhập new values (min_points, max_points, discount_percent, description)
6. Admin click "Save"
7. TierConfigDialog tạo CustomerTier object với updated values
8. TierConfigDialog gọi `CustomerTierDAO.updateTier(tier)`
9. CustomerTierDAO execute: `UPDATE customer_tiers SET ... WHERE id=?`
10. PreparedStatement set parameters và executeUpdate()
11. TierConfigDialog gọi `loadTiers()` để refresh table

**Messages/Methods**:
- `TierConfigDialog.editSelectedTier(): void`
- `JTable.getSelectedRow(): int`
- `DefaultTableModel.getValueAt(int, int): Object`
- `JOptionPane.showInputDialog(...)`: String`
- `CustomerTier(...)` (constructor)
- `CustomerTierDAO.updateTier(CustomerTier): void`
- `Connection.prepareStatement(String): PreparedStatement`
- `PreparedStatement.setFloat(int, float)`
- `PreparedStatement.setString(int, String)`
- `PreparedStatement.setInt(int, int)`
- `PreparedStatement.executeUpdate(): int`
- `TierConfigDialog.loadTiers()` (refresh)

**Điều kiện**:
- if (selectedRow < 0) → show error "Please select a tier"
- Validate: min_points < max_points

**Xử lý ngoại lệ**:
- SQLException → catch, showMessageDialog error

---

### USE CASE 9: UPLOAD HÌNH ẢNH SẢN PHẨM (Web API)

**Actor**: Admin (via Web Frontend)

**Luồng chính**:
1. Admin chọn product
2. Admin chọn image file
3. React gọi `POST /api/products/{id}/image` với MultipartFile
4. ProductController.uploadProductImage(id, file) nhận request
5. ProductController validate: `if (file.isEmpty())` → return 400
6. ProductController tạo upload directory: `Files.createDirectories(uploadPath)`
7. ProductController generate unique filename
8. ProductController save file: `Files.write(filePath, file.getBytes())`
9. ProductController gọi `productRepository.findById(id)`
10. ProductController gọi `product.setImageUrl("/uploads/" + filename)`
11. ProductController gọi `productRepository.save(product)`
12. Return 200 với imageUrl

**Messages/Methods**:
- `POST /api/products/{id}/image` (HTTP Multipart Request)
- `ProductController.uploadProductImage(Long, MultipartFile): ResponseEntity<?>`
- `MultipartFile.isEmpty(): boolean`
- `Paths.get(String): Path`
- `Files.createDirectories(Path): Path`
- `MultipartFile.getOriginalFilename(): String`
- `MultipartFile.getBytes(): byte[]`
- `Files.write(Path, byte[]): Path`
- `productRepository.findById(Long): Optional<Product>`
- `Product.setImageUrl(String): void`
- `productRepository.save(Product): Product`
- `ResponseEntity.ok(Object): ResponseEntity`

**Điều kiện**:
- if (file.isEmpty()) → return 400
- if (product == null) → return 404

**Xử lý ngoại lệ**:
- IOException → catch, printStackTrace, return 500
- Exception → catch, printStackTrace, return 500

---

### USE CASE 10: BÁO CÁO DOANH THU (Desktop App)

**Actor**: Admin/Staff

**Luồng chính**:
1. User mở RevenueReportPanel
2. User chọn date range (from, to)
3. User click "Generate Report"
4. RevenueReportPanel tạo LocalDateTime objects
5. RevenueReportPanel gọi các methods:
   - `AppOrderDao.getTotalRevenue(from, to)`
   - `AppOrderDao.getOrderCount(from, to)`
   - `AppOrderDao.getDistinctCustomerCount(from, to)`
   - `AppOrderDao.getProductsSold(from, to)`
6. Each method tries multiple SQL variants (schema variations)
7. AppOrderDao execute queries với date parameters
8. Return aggregated values
9. RevenueReportPanel display trong UI (labels, charts)
10. User có thể export to Excel hoặc print

**Messages/Methods**:
- `RevenueReportPanel.generateReport(): void`
- `LocalDateTime.of(...)`: LocalDateTime`
- `AppOrderDao.getTotalRevenue(LocalDateTime, LocalDateTime): double`
- `AppOrderDao.getOrderCount(LocalDateTime, LocalDateTime): long`
- `AppOrderDao.getDistinctCustomerCount(LocalDateTime, LocalDateTime): long`
- `AppOrderDao.getProductsSold(LocalDateTime, LocalDateTime): long`
- `Connection.prepareStatement(String): PreparedStatement`
- `PreparedStatement.setTimestamp(int, Timestamp)`
- `PreparedStatement.executeQuery(): ResultSet`
- `ResultSet.getDouble(String): double`
- `ResultSet.getLong(String): long`
- `JLabel.setText(String)` (update UI)

**Loops**:
- Loop qua SQL variants nếu query fail
- Try multiple column names (total_amount vs total, order_date vs created_at)

**Xử lý ngoại lệ**:
- SQLException → log error, try next variant
- If all variants fail → return 0/empty

---

## 🔗 TỔNG HỢP DEPENDENCIES GIỮA CÁC COMPONENTS

### Desktop App Dependencies:

```
LoginForm 
  └─→ UserDAO 
       └─→ DatabaseConnector → MySQL

MainApplication 
  ├─→ OrderPanel
  ├─→ ProductManagerPanel
  ├─→ UserManagementPanel
  └─→ RevenueReportPanel

OrderPanel
  ├─→ GetProduct
  ├─→ AppOrderDao
  └─→ CustomerTierDAO

ProductManagerPanel
  └─→ ProductDAO

UserManagementPanel
  └─→ UserDAO

CustomerManagementPanel
  └─→ CustomerTierDAO

TierConfigDialog
  └─→ CustomerTierDAO

RevenueReportPanel
  ├─→ AppOrderDao
  └─→ WebOrderDao (via OrderDao)
```

### Web Backend Dependencies:

```
UserController
  └─→ UserRepository → JPA → MySQL

ProductController
  └─→ ProductRepository → JPA → MySQL

OrderController
  ├─→ OrderRepository → JPA → MySQL
  ├─→ CustomerRepository → JPA → MySQL
  ├─→ ProductRepository
  └─→ UserRepository
```

---

## 📝 MESSAGE TYPES VÀ NOTATION

### Synchronous Messages (mũi tên đặc):
- Method calls: `objectA.method(params)`
- Return values: nét đứt `<<return>>`

### Asynchronous Messages (mũi tên rỗng):
- HTTP Requests: `POST /api/endpoint`
- Event handlers: `actionPerformed()`

### Return Messages (nét đứt):
- `return boolean`
- `return List<Object>`
- `ResponseEntity<?>`

---

## 🎯 CÁC FRAGMENTS TRONG SEQUENCE DIAGRAM

### alt (alternative):
```
alt user found
  authenticate success
  get user role
  open main application
else user not found
  show error message
end
```

### loop:
```
loop for each item in cart
  create OrderDetail
  add to batch
end
```

### opt (optional):
```
opt if discount available
  apply customer tier discount
end
```

### par (parallel):
```
par
  update product stock
and
  insert order details
end
```

---

## 💡 LƯU Ý KHI VẼ SEQUENCE DIAGRAM

1. **Participants (từ trái sang phải)**:
   - Actor
   - Boundary (UI/Form/Controller)
   - Control (DAO/Service)
   - Entity (Model/Repository)
   - Database

2. **Activation boxes**: Hiển thị khi object đang xử lý

3. **Self-calls**: Khi object gọi method của chính nó

4. **Creation**: Use `<<create>>` stereotype

5. **Destruction**: Use X at end of lifeline

6. **Notes**: Thêm notes cho business logic quan trọng

---

## 🔍 EXAMPLE SEQUENCE DIAGRAM STRUCTURE

### USE CASE: Login (Desktop)

```
Actor: Staff
Boundary: LoginForm
Control: UserDAO
Entity: DatabaseConnector
Database: MySQL

Staff → LoginForm: nhập username, password
Staff → LoginForm: click "Sign In"
activate LoginForm
  LoginForm → UserDAO: authenticateUser(username, password)
  activate UserDAO
    UserDAO → DatabaseConnector: getConnection()
    activate DatabaseConnector
      DatabaseConnector → MySQL: connect
      DatabaseConnector ← MySQL: Connection
    deactivate DatabaseConnector
    UserDAO → MySQL: executeQuery("SELECT userID FROM users...")
    alt user found
      UserDAO ← MySQL: ResultSet (user exists)
      LoginForm ← UserDAO: return true
      LoginForm → UserDAO: getUserRole(username)
      UserDAO → MySQL: executeQuery("SELECT role...")
      UserDAO ← MySQL: ResultSet (role)
      LoginForm ← UserDAO: return "admin"
      LoginForm → MainApplication: <<create>>(username, role)
      LoginForm → LoginForm: dispose()
      Staff ← LoginForm: show MainApplication
    else user not found
      LoginForm ← UserDAO: return false
      Staff ← LoginForm: show error "Invalid credentials"
    end
  deactivate UserDAO
deactivate LoginForm
```

---

## 📚 TÀI LIỆU THAM KHẢO CODE

### Desktop App:
- **View Layer**: `e:\Final\Finalyearproject\mavenproject1\src\main\java\view\`
- **DAO Layer**: `e:\Final\Finalyearproject\mavenproject1\src\main\java\dao\`
- **Model Layer**: `e:\Final\Finalyearproject\mavenproject1\src\main\java\model\`

### Web Backend:
- **Controller**: `e:\Final\Finalyearproject\web-backend\src\main\java\com\example\saleapp\web_backend\controller\`
- **Repository**: `e:\Final\Finalyearproject\web-backend\src\main\java\com\example\saleapp\web_backend\repository\`
- **Model**: `e:\Final\Finalyearproject\web-backend\src\main\java\com\example\saleapp\web_backend\model\`

---

## ✅ CHECKLIST ĐỂ VẼ MỘT SEQUENCE DIAGRAM HOÀN CHỈNH

- [ ] Xác định Use Case cụ thể
- [ ] List tất cả Actors
- [ ] List tất cả Objects/Components tham gia
- [ ] Xác định thứ tự messages theo timeline
- [ ] Xác định loại message (sync/async/return)
- [ ] Thêm activation boxes
- [ ] Thêm fragments (alt/loop/opt) nếu có
- [ ] Ghi chú parameters quan trọng
- [ ] Ghi chú return values
- [ ] Thêm error handling (alt/opt)
- [ ] Review lại luồng có logic không

---

## 🎨 TOOLS KHUYẾN NGHỊ

1. **PlantUML** - Text-based, dễ version control
2. **Lucidchart** - Online, collaboration
3. **Draw.io** - Free, desktop + online
4. **Enterprise Architect** - Professional
5. **Visual Paradigm** - Full UML suite

---

**Tài liệu này cung cấp đầy đủ thông tin cần thiết để vẽ sequence diagram cho mọi use case trong hệ thống Sales Management!** 🎯
