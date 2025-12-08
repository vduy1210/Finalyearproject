# KỊCH BẢN THUYẾT TRÌNH - PROJECT STRUCTURE
## Script cho Slide Presentation về Cấu trúc Dự án

---

## 🎯 SLIDE 1: TITLE SLIDE
### Nội dung slide:
```
HỆ THỐNG QUẢN LÝ BÁN HÀNG
Project Structure & Architecture

Sinh viên: [Tên của bạn]
MSSV: [Mã số]
Giảng viên hướng dẫn: [Tên GV]
```

### 📝 Script thuyết trình:
> "Xin chào thầy/cô và các bạn. Em là [tên], hôm nay em xin được trình bày về **cấu trúc kiến trúc** của hệ thống Quản lý bán hàng mà em đã xây dựng. Dự án này được thiết kế theo mô hình **3-tier architecture** với 3 module độc lập nhưng kết nối chặt chẽ với nhau."

**⏱ Thời gian: 15 giây**

---

## 🎯 SLIDE 2: SYSTEM OVERVIEW (Tổng quan hệ thống)
### Nội dung slide:
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  DESKTOP APP    │     │  WEB BACKEND    │     │  WEB FRONTEND   │
│  Java Swing     │     │  Spring Boot    │     │     ReactJS     │
│  (POS System)   │     │  (REST API)     │     │  (Customer)     │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────────────────┴───────────────────────┘
                                 ↓
                        ┌─────────────────┐
                        │  MySQL Database │
                        │     (shopdb)    │
                        └─────────────────┘

👤 Người dùng:
• Desktop: Nhân viên quầy (Cashier + Admin)
• Web: Khách hàng đặt hàng online
```

### 📝 Script thuyết trình:
> "Đầu tiên, hệ thống của em bao gồm **3 thành phần chính**:
> 
> **Thứ nhất**, **Desktop Application** viết bằng **Java Swing**, đây là ứng dụng POS - Point of Sale - dành cho nhân viên tại quầy để thực hiện bán hàng, quản lý sản phẩm, và xem báo cáo thống kê.
> 
> **Thứ hai**, **Web Backend** sử dụng **Spring Boot**, đóng vai trò là một REST API Server để cung cấp dữ liệu cho phần Frontend thông qua các HTTP endpoints.
> 
> **Thứ ba**, **Web Frontend** được xây dựng bằng **ReactJS**, đây là trang web cho phép khách hàng xem menu, đặt hàng trực tuyến.
> 
> Cả 3 module này đều **kết nối đến cùng một Database MySQL** có tên là **shopdb**, đảm bảo tính nhất quán dữ liệu trong toàn bộ hệ thống."

**⏱ Thời gian: 45 giây**

---

## 🎯 SLIDE 3: DESKTOP APP STRUCTURE
### Nội dung slide:
```
DESKTOP APPLICATION (Java Swing - JDBC)
mavenproject1/

├── 📁 view/              → GUI Components
│   ├── LoginForm.java
│   ├── MainApplication.java
│   ├── OrderPanel.java
│   └── ProductManagerPanel.java
│
├── 📁 dao/               → Data Access Objects
│   ├── UserDAO.java
│   ├── ProductDAO.java
│   └── AppOrderDao.java
│
├── 📁 model/             → Java Beans
│   ├── Product.java
│   ├── Order.java
│   └── OrderDetails.java
│
├── 📁 database/          → Connection
│   └── DatabaseConnector.java (Singleton)
│
└── 📁 util/              → Utilities
    └── PasswordUtil.java (BCrypt)

🔑 Công nghệ: Java 22, Swing, JDBC, Apache POI, JFreeChart
```

### 📝 Script thuyết trình:
> "Bắt đầu với **Desktop App**, em áp dụng mô hình **MVC - Model View Controller**.
> 
> **Package VIEW** chứa tất cả các giao diện người dùng sử dụng Java Swing như `LoginForm` cho màn hình đăng nhập, `OrderPanel` cho màn hình bán hàng, và `ProductManagerPanel` cho quản lý sản phẩm.
> 
> **Package DAO** - Data Access Objects - là lớp trung gian kết nối với Database. Ở đây em sử dụng **JDBC thuần** với **PreparedStatement** để viết các câu lệnh SQL thủ công. Ví dụ `UserDAO` xử lý đăng nhập, `ProductDAO` xử lý CRUD sản phẩm, và `AppOrderDao` xử lý việc tạo đơn hàng với **Transaction management**.
> 
> **Package MODEL** chứa các Java Bean classes - đây là các POJO đơn giản với getter/setter để ánh xạ với dữ liệu từ Database.
> 
> **Package DATABASE** chứa class `DatabaseConnector` được thiết kế theo **Singleton Pattern** để tái sử dụng một kết nối duy nhất, tránh lãng phí tài nguyên.
> 
> Ngoài ra, em còn sử dụng thêm thư viện **Apache POI** để export báo cáo Excel và **JFreeChart** để vẽ biểu đồ thống kê doanh thu."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 4: DESKTOP APP - TRANSACTION EXAMPLE
### Nội dung slide:
```java
// AppOrderDao.java - Tạo đơn hàng với Transaction
public boolean createAppOrder(Order order, List<OrderDetails> details) {
    try (Connection conn = DatabaseConnector.getConnection()) {
        
        conn.setAutoCommit(false);  // 🔒 Bắt đầu Transaction
        
        // Bước 1: Insert vào bảng app_order
        String insertOrder = "INSERT INTO app_order(...) VALUES(...)";
        PreparedStatement ps = conn.prepareStatement(insertOrder, 
                               Statement.RETURN_GENERATED_KEYS);
        ps.executeUpdate();
        
        // Bước 2: Lấy order_id vừa tạo
        ResultSet rs = ps.getGeneratedKeys();
        int orderId = rs.getInt(1);
        
        // Bước 3: Insert từng món vào app_order_details
        for (OrderDetails item : details) {
            String insertDetail = "INSERT INTO app_order_details...";
            // ... thực hiện insert
        }
        
        conn.commit();  // ✅ Lưu tất cả
        return true;
        
    } catch (SQLException e) {
        conn.rollback();  // ❌ Có lỗi -> Hủy toàn bộ
        return false;
    }
}
```

### 📝 Script thuyết trình:
> "Điểm đặc biệt trong Desktop App là em áp dụng **Database Transaction** để đảm bảo tính toàn vẹn dữ liệu.
> 
> Như các thầy cô thấy trong đoạn code này, khi tạo một đơn hàng, hệ thống phải thực hiện **2 bước**: Đầu tiên là lưu thông tin đơn hàng vào bảng `app_order`, sau đó lưu từng món ăn vào bảng `app_order_details`.
> 
> Em sử dụng `setAutoCommit(false)` để **bắt đầu Transaction**. Nếu cả 2 bước đều thành công, em gọi `commit()` để **lưu vĩnh viễn**. Nhưng nếu có bất kỳ lỗi nào xảy ra ở bước nào, ví dụ mất mạng hoặc lỗi SQL, hệ thống sẽ gọi `rollback()` để **hủy toàn bộ**, đảm bảo không có đơn hàng nào bị thiếu món.
> 
> Đây chính là cơ chế **ACID** trong Database - đảm bảo Atomicity, tức là 'all or nothing' - hoặc làm hết hoặc không làm gì cả."

**⏱ Thời gian: 50 giây**

---

## 🎯 SLIDE 5: WEB BACKEND STRUCTURE
### Nội dung slide:
```
WEB BACKEND (Spring Boot - JPA)
web-backend/

├── 📁 controller/           → REST API Endpoints
│   ├── ProductController.java
│   ├── OrderController.java
│   └── UserController.java
│
├── 📁 repository/           → JPA Interfaces
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   └── CustomerRepository.java
│
├── 📁 model/                → JPA Entities
│   ├── Product.java         (@Entity, @Table)
│   ├── Order.java
│   └── OrderItem.java
│
├── 📁 dto/                  → Data Transfer Objects
│   └── OrderRequest.java
│
├── 📁 config/               → Configuration
│   └── CorsConfig.java      (Allow Frontend)
│
└── 📄 application.properties → Database config

🔑 Công nghệ: Spring Boot 3.5.3, Spring Data JPA, MySQL
```

### 📝 Script thuyết trình:
> "Chuyển sang **Web Backend**, đây là phần em áp dụng **Spring Boot Framework** theo kiến trúc **RESTful API**.
> 
> **Package CONTROLLER** chứa các REST endpoints. Ví dụ `ProductController` có annotation `@GetMapping` để xử lý request GET lấy danh sách sản phẩm, `@PostMapping` để tạo đơn hàng mới. Mỗi Controller này sẽ trả về dữ liệu dưới dạng **JSON** cho Frontend.
> 
> **Package REPOSITORY** sử dụng **Spring Data JPA**. Điểm đặc biệt ở đây là em **không cần viết SQL thủ công**. Em chỉ cần tạo một interface kế thừa `JpaRepository`, và Spring sẽ **tự động sinh ra** các phương thức như `findAll()`, `findById()`, `save()`, `delete()` cho em.
> 
> **Package MODEL** chứa các Entity class được đánh dấu bằng annotation `@Entity`. JPA sẽ tự động map các class này với các bảng trong Database.
> 
> **Package DTO** - Data Transfer Objects - là các class trung gian dùng để nhận dữ liệu từ Frontend. Ví dụ `OrderRequest` có cấu trúc đúng format mà Frontend gửi lên.
> 
> Cuối cùng là **CorsConfig** để cho phép Frontend ở domain khác (localhost:3000) có thể gọi API ở backend (localhost:8081)."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 6: WEB BACKEND - REST API EXAMPLE
### Nội dung slide:
```java
// ProductController.java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    // GET /api/products → Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // GET /api/products/5 → Lấy sản phẩm có ID = 5
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // PATCH /api/products/5/stock?stock=100 → Cập nhật kho
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id, 
            @RequestParam int stock) {
        // ... logic cập nhật
    }
}
```

### 📝 Script thuyết trình:
> "Để minh họa rõ hơn, đây là một ví dụ về REST API trong `ProductController`.
> 
> **Annotation `@RestController`** đánh dấu class này là một Controller chuyên xử lý REST request và tự động chuyển đổi kết quả thành JSON.
> 
> **`@GetMapping`** định nghĩa một endpoint GET. Khi Frontend gọi `GET /api/products`, hàm `getAllProducts()` sẽ được thực thi và trả về danh sách tất cả sản phẩm.
> 
> **`@PathVariable`** dùng để lấy tham số từ URL. Ví dụ khi gọi `/api/products/5`, số `5` sẽ được truyền vào biến `id`.
> 
> **`@RequestParam`** dùng để lấy query parameter. Ví dụ `/api/products/5/stock?stock=100`, giá trị `100` sẽ được gán vào biến `stock`.
> 
> Nhờ **Dependency Injection** của Spring, em chỉ cần khai báo `ProductRepository`, Spring sẽ tự động tạo instance và inject vào. Em không cần phải `new ProductRepository()` thủ công."

**⏱ Thời gian: 50 giây**

---

## 🎯 SLIDE 7: WEB FRONTEND STRUCTURE
### Nội dung slide:
```
WEB FRONTEND (ReactJS - SPA)
web-frontend/

├── 📁 src/
│   ├── 📁 components/          → UI Components
│   │   ├── Navbar.js           (Thanh điều hướng)
│   │   ├── Menu.js             (Danh sách món ăn)
│   │   ├── Cart.js             (Giỏ hàng)
│   │   ├── OrderHistory.js     (Lịch sử đơn)
│   │   ├── Login.js            (Đăng nhập)
│   │   ├── Orders.js           (Quản lý đơn - Staff)
│   │   ├── ProductManagerPanel.js
│   │   ├── Notification.js     (Toast notification)
│   │   └── RequireAuth.js      (Protected routes)
│   │
│   ├── 📁 pages/
│   │   └── App.js              (Main page)
│   │
│   ├── 📁 services/
│   │   └── api.js              (API calls wrapper)
│   │
│   ├── 📁 styles/              → CSS files
│   │
│   ├── routes.js               (React Router config)
│   └── index.js                (Entry point)
│
└── 📄 package.json             (Dependencies)

🔑 Công nghệ: React 19.1, React Router 7.9, Material-UI 7.2
```

### 📝 Script thuyết trình:
> "Phần **Web Frontend** được xây dựng bằng **ReactJS**, đây là một **SPA - Single Page Application**, nghĩa là toàn bộ website chỉ load một lần, sau đó mọi thao tác đều được xử lý bằng JavaScript mà không cần reload lại trang.
> 
> **Folder COMPONENTS** chứa các React component có thể tái sử dụng. Ví dụ `Menu.js` hiển thị danh sách món ăn, `Cart.js` quản lý giỏ hàng, `OrderHistory.js` hiển thị lịch sử đơn hàng của khách.
> 
> Đặc biệt, em có component `RequireAuth` để **bảo vệ các route chỉ dành cho nhân viên**. Nếu người dùng chưa đăng nhập mà cố truy cập `/orders` hoặc `/products`, họ sẽ bị redirect về trang login.
> 
> **Folder SERVICES** chứa file `api.js` - đây là nơi em đóng gói các hàm gọi API bằng `fetch()`. Thay vì viết `fetch('http://localhost:8081/api/products')` nhiều lần, em tạo hàm `getProducts()` để tái sử dụng.
> 
> File **routes.js** định nghĩa các đường dẫn của website sử dụng **React Router**, ví dụ `/` là trang menu, `/cart` là giỏ hàng, `/orders` là quản lý đơn hàng.
> 
> Về UI, em sử dụng thư viện **Material-UI** để có các component đẹp mắt và responsive như Button, Dialog, Table."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 8: WEB FRONTEND - REACT EXAMPLE
### Nội dung slide:
```javascript
// Menu.js - Hiển thị danh sách món ăn
import React, { useState, useEffect } from 'react';

function Menu() {
    // 1. Khai báo state để lưu danh sách sản phẩm
    const [products, setProducts] = useState([]);
    
    // 2. useEffect: Gọi API khi component được mount
    useEffect(() => {
        fetch('http://localhost:8081/api/products')
            .then(res => res.json())
            .then(data => setProducts(data))
            .catch(err => console.error(err));
    }, []); // [] = chỉ chạy 1 lần
    
    // 3. Render UI: Dùng map() để duyệt qua từng sản phẩm
    return (
        <div>
            {products.map(product => (
                <div key={product.id}>
                    <h3>{product.name}</h3>
                    <p>Giá: {product.price} VNĐ</p>
                    <button onClick={() => addToCart(product)}>
                        Thêm vào giỏ
                    </button>
                </div>
            ))}
        </div>
    );
}
```

### 📝 Script thuyết trình:
> "Đây là một ví dụ điển hình về cách React hoạt động trong component `Menu.js`.
> 
> **Đầu tiên**, em sử dụng **useState hook** để khai báo một state tên là `products` với giá trị khởi tạo là một mảng rỗng. Đây là nơi lưu trữ danh sách món ăn sau khi lấy từ API.
> 
> **Thứ hai**, **useEffect hook** với dependency array rỗng `[]` sẽ chạy **đúng 1 lần** khi component được mount - tức là khi trang web vừa mở xong. Trong đó, em gọi API bằng `fetch()` để lấy danh sách sản phẩm, sau đó dùng `setProducts()` để cập nhật state.
> 
> **Điểm quan trọng**: Khi `setProducts()` được gọi, React sẽ **tự động re-render** component, và giao diện sẽ cập nhật để hiển thị dữ liệu mới mà không cần reload trang.
> 
> **Thứ ba**, trong phần return, em sử dụng **method `map()`** để duyệt qua từng phần tử trong mảng `products` và tạo ra một thẻ `div` tương ứng. Mỗi thẻ hiển thị tên, giá, và một nút 'Thêm vào giỏ'.
> 
> Đây chính là tư tưởng **Declarative Programming** của React - em chỉ cần mô tả UI trông như thế nào, React sẽ tự lo phần cập nhật DOM."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 9: DATA FLOW (Luồng dữ liệu)
### Nội dung slide:
```
LUỒNG DỮ LIỆU HOÀN CHỈNH (End-to-End)
Ví dụ: Khách hàng đặt món ăn

┌─────────────────────────────────────────────────────────┐
│ 1. USER ACTION (Web Frontend)                           │
│    - Khách chọn món → Click "Đặt hàng"                  │
│    - Component: Cart.js                                 │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 2. API CALL (HTTP POST)                                 │
│    fetch('http://localhost:8081/api/orders', {         │
│        method: 'POST',                                  │
│        body: JSON.stringify(orderData)                  │
│    })                                                   │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 3. BACKEND CONTROLLER (Spring Boot)                     │
│    @PostMapping("/api/orders")                          │
│    public ResponseEntity<?> createOrder(...)            │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 4. REPOSITORY + JPA                                     │
│    orderRepository.save(order)                          │
│    → JPA tự động chuyển thành SQL:                      │
│      INSERT INTO web_order (...)                        │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 5. DATABASE (MySQL)                                     │
│    - Lưu vào bảng web_order                             │
│    - Lưu chi tiết vào web_order_details                 │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 6. RESPONSE (JSON)                                      │
│    { "success": true, "orderId": 123 }                  │
└───────────────────────┬─────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 7. UPDATE UI (React)                                    │
│    - Hiển thị thông báo "Đặt hàng thành công!"         │
│    - Reset giỏ hàng (setCart([]))                       │
│    - Re-render component                                │
└─────────────────────────────────────────────────────────┘
```

### 📝 Script thuyết trình:
> "Để thầy cô thấy rõ sự kết nối giữa 3 module, em xin trình bày một **luồng dữ liệu hoàn chỉnh** từ đầu đến cuối.
> 
> **Bước 1**: Khách hàng trên Web Frontend chọn món ăn, click nút 'Đặt hàng'.
> 
> **Bước 2**: Component `Cart.js` gọi API bằng `fetch()`, gửi một HTTP POST request kèm dữ liệu đơn hàng dưới dạng JSON đến Backend.
> 
> **Bước 3**: Backend nhận request tại Controller, hàm được đánh dấu `@PostMapping` sẽ được thực thi.
> 
> **Bước 4**: Controller gọi xuống Repository, JPA **tự động chuyển đổi** đối tượng Java thành câu lệnh SQL INSERT và thực thi.
> 
> **Bước 5**: Database MySQL lưu dữ liệu vào bảng `web_order` và `web_order_details`.
> 
> **Bước 6**: Backend trả về response dưới dạng JSON cho Frontend, kèm theo mã đơn hàng vừa tạo.
> 
> **Bước 7**: Frontend nhận được response, sử dụng `setState` để cập nhật UI - hiển thị thông báo thành công, reset giỏ hàng về rỗng, và component tự động re-render.
> 
> Toàn bộ quá trình này diễn ra trong vòng chưa đầy 1 giây, mang lại trải nghiệm mượt mà cho người dùng."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 10: DATABASE SCHEMA
### Nội dung slide:
```
DATABASE DESIGN (MySQL - shopdb)

┌─────────────────┐         ┌─────────────────┐
│     users       │         │   customers     │
├─────────────────┤         ├─────────────────┤
│ PK: userId      │         │ PK: customerId  │
│     userName    │         │     name        │
│     password    │         │     phone       │
│     email       │         │     tierId      │◄─┐
│     role        │         └─────────────────┘   │
└─────────────────┘                               │
                                                  │
┌─────────────────┐                               │
│    products     │                               │
├─────────────────┤         ┌─────────────────┐   │
│ PK: productId   │         │ customer_tier   │   │
│     name        │         ├─────────────────┤   │
│     price       │         │ PK: tierId      │───┘
│     stock       │         │     tierName    │
│     image       │         │     discount    │
│     isDeleted   │         │     minSpending │
└────────┬────────┘         └─────────────────┘
         │
         │ FK
         │
    ┌────┴────────┬──────────────┐
    │             │              │
┌───▼──────────┐ ┌▼─────────────┐ ┌▼──────────────┐
│  app_order   │ │  web_order   │ │               │
├──────────────┤ ├──────────────┤ │               │
│ PK: orderId  │ │ PK: orderId  │ │               │
│     userId   │ │  customerId  │ │               │
│     date     │ │     date     │ │               │
│     total    │ │     total    │ │               │
└──────┬───────┘ └──────┬───────┘ │               │
       │                │         │               │
       │ 1:N            │ 1:N     │               │
       │                │         │               │
┌──────▼──────────┐ ┌───▼─────────────┐           │
│app_order_details│ │web_order_details│           │
├─────────────────┤ ├─────────────────┤           │
│ PK: detailId    │ │ PK: detailId    │           │
│ FK: orderId     │ │ FK: orderId     │           │
│ FK: productId   │ │ FK: productId   │           │
│     quantity    │ │     quantity    │           │
│     price       │ │     price       │           │
└─────────────────┘ └─────────────────┘           │
```

### 📝 Script thuyết trình:
> "Về mặt Database, em thiết kế schema gồm **8 bảng chính**.
> 
> **Bảng `users`** lưu thông tin nhân viên với mật khẩu đã mã hóa bằng BCrypt.
> 
> **Bảng `customers`** lưu thông tin khách hàng, có quan hệ với bảng `customer_tier` để quản lý hạng thành viên và mức giảm giá tương ứng.
> 
> **Bảng `products`** lưu thông tin sản phẩm với cột `isDeleted` để soft delete - em không xóa vật lý mà chỉ đánh dấu là đã xóa.
> 
> **Bảng `app_order` và `app_order_details`** lưu đơn hàng từ Desktop App. Đây là quan hệ **1-to-many** - một đơn hàng có nhiều chi tiết.
> 
> Tương tự, **bảng `web_order` và `web_order_details`** lưu đơn hàng từ Web. Em tách riêng 2 loại đơn để dễ phân biệt nguồn gốc và tiện cho việc báo cáo thống kê.
> 
> Tất cả các bảng đều có **Foreign Key** để đảm bảo **Referential Integrity** - không thể xóa một sản phẩm nếu nó đang tồn tại trong đơn hàng."

**⏱ Thời gian: 50 giây**

---

## 🎯 SLIDE 11: TECHNOLOGY COMPARISON
### Nội dung slide:
```
SO SÁNH CÔNG NGHỆ GIỮA 3 MODULE

┌────────────────┬───────────────┬──────────────┬──────────────┐
│     Tiêu chí   │  Desktop App  │ Web Backend  │ Web Frontend │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Ngôn ngữ       │     Java      │     Java     │  JavaScript  │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Framework      │  Java Swing   │ Spring Boot  │    ReactJS   │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Database Access│ JDBC (Manual) │  JPA (Auto)  │     N/A      │
│                │ PreparedStmt  │  Repository  │  (via API)   │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ UI Technology  │ JFrame/JPanel │    No UI     │ HTML+CSS+JSX │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Pattern        │     MVC       │  3-Layer     │  Component   │
│                │               │ Architecture │    Based     │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Deployment     │  .jar file    │  .war/.jar   │ Static files │
│                │ (Standalone)  │   (Server)   │   (CDN)      │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Người dùng     │ Nhân viên     │     N/A      │ Khách hàng   │
│                │    quầy       │  (Backend)   │              │
├────────────────┼───────────────┼──────────────┼──────────────┤
│ Port           │     N/A       │    8081      │ 3000 (dev)   │
└────────────────┴───────────────┴──────────────┴──────────────┘

LÝ DO CHỌN CÔNG NGHỆ:
✅ Desktop: Swing - Gọn nhẹ, không cần server
✅ Backend: Spring Boot - Tiêu chuẩn enterprise, dễ mở rộng
✅ Frontend: React - SPA hiện đại, UX mượt mà
```

### 📝 Script thuyết trình:
> "Slide này em muốn so sánh công nghệ giữa 3 module để thầy cô thấy sự khác biệt.
> 
> **Về Database Access**: Desktop App em dùng **JDBC thuần** với PreparedStatement, phải viết SQL thủ công, trong khi Backend dùng **JPA** - mọi thứ đều tự động, không cần viết SQL. Frontend thì không truy cập trực tiếp Database mà phải thông qua Backend API.
> 
> **Về Pattern**: Desktop theo **MVC**, Backend theo **3-Layer Architecture**, còn Frontend theo **Component-based** của React.
> 
> **Về Deployment**: Desktop được đóng gói thành file `.jar` chạy độc lập không cần server. Backend deploy lên Tomcat server. Frontend sau khi build ra static files có thể host trên bất kỳ CDN nào.
> 
> **Lý do chọn công nghệ**:
> - **Swing** cho Desktop vì gọn nhẹ, không cần cài đặt phức tạp, phù hợp cho ứng dụng tại quầy.
> - **Spring Boot** vì đây là tiêu chuẩn enterprise trong Java, có sẵn rất nhiều tính năng và dễ mở rộng.
> - **ReactJS** vì đây là thư viện phổ biến nhất hiện nay cho SPA, cho phép xây dựng UI tương tác mượt mà."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 12: KEY FEATURES (Tính năng nổi bật)
### Nội dung slide:
```
CÁC TÍNH NĂNG KỸ THUẬT NỔI BẬT

🔒 1. SECURITY (Bảo mật)
   ✓ BCrypt password hashing (không lưu plain text)
   ✓ PreparedStatement (chống SQL Injection)
   ✓ CORS Configuration (kiểm soát cross-origin)
   ✓ Role-based access (Admin/Staff/Customer)

💾 2. DATA INTEGRITY (Toàn vẹn dữ liệu)
   ✓ Database Transaction (ACID)
   ✓ Foreign Key Constraints
   ✓ Soft Delete (isDeleted flag)
   ✓ Two-phase commit pattern

⚡ 3. PERFORMANCE (Hiệu năng)
   ✓ Singleton Database Connection (Desktop)
   ✓ JPA Second-level Cache (Backend)
   ✓ React Virtual DOM (Frontend)
   ✓ Lazy Loading images

🎨 4. USER EXPERIENCE (Trải nghiệm người dùng)
   ✓ Realtime notification system
   ✓ Auto-refresh order list
   ✓ Responsive design (Material-UI)
   ✓ Form validation

📊 5. REPORTING (Báo cáo)
   ✓ Export Excel (Apache POI)
   ✓ Revenue charts (JFreeChart)
   ✓ Dashboard analytics
   ✓ Customer tier management
```

### 📝 Script thuyết trình:
> "Em xin nêu một số **điểm kỹ thuật nổi bật** của dự án.
> 
> **Về bảo mật**: Em áp dụng **BCrypt** để mã hóa mật khẩu, không bao giờ lưu password dạng plain text. Sử dụng **PreparedStatement** thay vì ghép chuỗi SQL để chống SQL Injection. Có **CORS Configuration** để kiểm soát việc Frontend gọi Backend. Và có phân quyền rõ ràng cho Admin, Staff, và Customer.
> 
> **Về toàn vẹn dữ liệu**: Em dùng **Database Transaction** với commit/rollback đảm bảo tính ACID. Có **Foreign Key Constraints** để không thể xóa dữ liệu đang được tham chiếu. Áp dụng **Soft Delete** - đánh dấu `isDeleted` thay vì xóa vật lý để có thể khôi phục.
> 
> **Về hiệu năng**: Desktop dùng **Singleton Pattern** cho Database Connection. Backend có JPA Cache. Frontend dùng **Virtual DOM** của React để update UI hiệu quả.
> 
> **Về trải nghiệm người dùng**: Có hệ thống **notification realtime**, đơn hàng tự động refresh, giao diện responsive, và validation form đầy đủ.
> 
> **Về báo cáo**: Tích hợp **Apache POI** để export Excel, **JFreeChart** vẽ biểu đồ doanh thu, và có dashboard phân tích."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 13: CHALLENGES & SOLUTIONS (Thách thức & Giải pháp)
### Nội dung slide:
```
THÁCH THỨC GØP PHẢI VÀ CÁCH GIẢI QUYẾT

❌ THÁCH THỨC 1: Đồng bộ dữ liệu giữa Desktop và Web
   ✅ GIẢI PHÁP:
      → Dùng chung 1 Database
      → Tách bảng app_order vs web_order để tránh conflict
      → Desktop có thể xem đơn web qua WebOrderDao

❌ THÁCH THỨC 2: Transaction rollback khi tạo đơn
   ✅ GIẢI PHÁP:
      → Dùng try-catch với conn.rollback()
      → Test kỹ các trường hợp lỗi
      → Log đầy đủ để debug

❌ THÁCH THỨC 3: CORS error khi Frontend gọi Backend
   ✅ GIẢI PHÁP:
      → Tạo CorsConfig.java
      → @CrossOrigin annotation trên Controller
      → Allow credentials và specific origins

❌ THÁCH THỨC 4: State management phức tạp trong React
   ✅ GIẢI PHÁP:
      → Sử dụng useState cho local state
      → Context API cho global state (Notification)
      → Component composition để chia nhỏ logic

❌ THÁCH THỨC 5: Image upload không hiển thị
   ✅ GIẢI PHÁP:
      → Lưu path tương đối trong DB
      → Config static resource handler
      → Đồng bộ folder uploads/ giữa Desktop và Backend
```

### 📝 Script thuyết trình:
> "Trong quá trình phát triển, em đã gặp một số **thách thức kỹ thuật** và đây là cách em giải quyết.
> 
> **Thách thức đầu tiên**: Làm sao đồng bộ dữ liệu giữa Desktop và Web khi cùng lúc có nhiều người dùng? Em giải quyết bằng cách cả 2 đều dùng chung một Database, nhưng **tách riêng bảng đơn hàng** để tránh conflict. Desktop vẫn có thể xem đơn web thông qua `WebOrderDao`.
> 
> **Thách thức thứ hai**: Xử lý Transaction rollback khi tạo đơn bị lỗi giữa chừng. Em dùng **try-catch** kết hợp `rollback()`, và test kỹ các trường hợp edge case như mất mạng, sản phẩm hết hàng.
> 
> **Thách thức thứ ba**: CORS error - Frontend không gọi được Backend vì khác domain. Em tạo class `CorsConfig` và dùng annotation `@CrossOrigin` để cho phép request từ localhost:3000.
> 
> **Thách thức thứ tư**: State management trong React rất phức tạp khi có nhiều component cần chia sẻ dữ liệu. Em dùng **Context API** cho phần Notification, còn lại dùng `useState` và truyền props.
> 
> **Thách thức cuối**: Ảnh upload từ Desktop không hiển thị trên Web. Em phải đồng bộ folder `uploads/` và config **static resource handler** trong Spring Boot."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 14: DEMO ARCHITECTURE (Kiến trúc tổng thể)
### Nội dung slide:
```
KIẾN TRÚC TỔNG THỂ (COMPLETE ARCHITECTURE)

                    ┌─────────────────────┐
                    │    END USERS        │
                    └──────────┬──────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
        ┌───────▼────────┐          ┌────────▼────────┐
        │  NHÂN VIÊN     │          │   KHÁCH HÀNG    │
        │   (Staff)      │          │   (Customer)    │
        └───────┬────────┘          └────────┬────────┘
                │                            │
                │                            │
    ┌───────────▼──────────┐     ┌──────────▼──────────┐
    │   DESKTOP APP        │     │   WEB FRONTEND      │
    │   (Java Swing)       │     │   (ReactJS)         │
    │   Port: N/A          │     │   Port: 3000        │
    └───────────┬──────────┘     └──────────┬──────────┘
                │                           │
                │ JDBC                      │ HTTP/REST
                │                           │
                │                 ┌─────────▼──────────┐
                │                 │   WEB BACKEND      │
                │                 │   (Spring Boot)    │
                │                 │   Port: 8081       │
                │                 └─────────┬──────────┘
                │                           │
                │                           │ JPA/Hibernate
                │                           │
                └──────────┬────────────────┘
                           │
                    ┌──────▼───────┐
                    │   DATABASE   │
                    │    (MySQL)   │
                    │   Port: 3306 │
                    └──────────────┘

DEPLOYMENT ENVIRONMENT:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
• Database: MySQL Server (Cloud/Local)
• Backend: Tomcat/Embedded Server
• Frontend: Nginx/Apache (Static hosting)
• Desktop: Windows/Mac/Linux (JRE required)
```

### 📝 Script thuyết trình:
> "Slide này tóm tắt **kiến trúc tổng thể** của toàn bộ hệ thống.
> 
> Ở tầng người dùng, hệ thống phục vụ 2 nhóm: **Nhân viên** dùng Desktop App và **Khách hàng** dùng Web.
> 
> Desktop App kết nối trực tiếp xuống Database qua **JDBC**, phù hợp cho môi trường mạng nội bộ tại quán, đảm bảo tốc độ xử lý nhanh.
> 
> Web Frontend gọi vào Backend thông qua **HTTP REST API**. Backend sau đó sử dụng **JPA/Hibernate** để tương tác với Database, tự động map giữa Object và Relational Database.
> 
> Về deployment, Database có thể chạy trên Cloud hoặc Local Server. Backend deploy trên Tomcat. Frontend sau khi build có thể host trên Nginx hoặc CDN. Desktop đóng gói thành file .jar chỉ cần có JRE là chạy được.
> 
> Kiến trúc này đảm bảo **tính mở rộng cao** - nếu sau này cần thêm Mobile App, chỉ cần gọi vào Backend API hiện có mà không cần sửa Database hay logic."

**⏱ Thời gian: 60 giây**

---

## 🎯 SLIDE 15: CONCLUSION (Kết luận)
### Nội dung slide:
```
KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

✅ ĐÃ HOÀN THÀNH:
   • Xây dựng hệ thống hoàn chỉnh 3 module
   • Áp dụng đúng design pattern (MVC, Singleton, Repository)
   • Đảm bảo bảo mật và toàn vẹn dữ liệu
   • Giao diện thân thiện, responsive
   • Có báo cáo, thống kê, export Excel

🚀 HƯỚNG PHÁT TRIỂN:
   1. Thêm Mobile App (React Native/Flutter)
   2. Tích hợp Payment Gateway (VNPay, Momo)
   3. WebSocket cho realtime order notification
   4. Microservices architecture (tách service)
   5. CI/CD pipeline (Jenkins, Docker)
   6. Unit Test coverage > 80%
   7. API Documentation (Swagger)
   8. Cloud deployment (AWS, Azure)

📚 KẾT QUẢ HỌC TẬP:
   ✓ Thành thạo Java (Swing + Spring Boot)
   ✓ Hiểu rõ Database design và Transaction
   ✓ Nắm vững ReactJS và SPA architecture
   ✓ Biết cách kết nối Frontend-Backend-Database
   ✓ Áp dụng được các best practices

CẢM ƠN THẦY CÔ ĐÃ LẮNG NGHE! 🙏
```

### 📝 Script thuyết trình:
> "Để kết thúc phần trình bày về cấu trúc dự án, em xin tóm tắt những gì đã làm được và hướng phát triển.
> 
> Em đã hoàn thành một hệ thống **3-tier** với đầy đủ các tính năng: Desktop cho nhân viên, Web cho khách hàng, REST API làm cầu nối. Áp dụng đúng các **design pattern** như MVC, Singleton, Repository Pattern. Đảm bảo **bảo mật** với BCrypt và PreparedStatement. Có **Transaction management** đầy đủ. Giao diện thân thiện và có đầy đủ tính năng báo cáo.
> 
> **Hướng phát triển**: Em có thể thêm Mobile App bằng React Native, tích hợp cổng thanh toán VNPay, dùng WebSocket cho notification realtime, chuyển sang Microservices, setup CI/CD, viết Unit Test đầy đủ, tạo API Documentation bằng Swagger, và deploy lên Cloud.
> 
> **Kết quả học tập**: Qua dự án này, em đã thành thạo cả Java Swing lẫn Spring Boot, hiểu rõ Database design, nắm vững ReactJS, và quan trọng nhất là biết cách **kết nối các tầng của một hệ thống thực tế**.
> 
> Em xin chân thành cảm ơn thầy cô đã lắng nghe. Em sẵn sàng trả lời các câu hỏi của thầy cô ạ!"

**⏱ Thời gian: 50 giây**

---

## 📊 TỔNG KẾT

### Tổng thời gian: **~13 phút**
### Số slide: **15 slides**

### 💡 LỜI KHUYÊN KHI THUYẾT TRÌNH:

1. **Giữ tốc độ ổn định**: Không nói quá nhanh, dừng nghỉ giữa các câu.

2. **Nhìn vào thầy cô**: Không nhìn xuống bàn hay quay lưng vào màn hình.

3. **Dùng con trỏ/bút laser**: Khi giải thích sơ đồ, hãy chỉ rõ từng phần.

4. **Chuẩn bị demo**: Nếu được hỏi, sẵn sàng mở code hoặc chạy ứng dụng thật.

5. **Tự tin nhưng khiêm tốn**: Nói "Em đã áp dụng..." thay vì "Em rất giỏi...".

6. **Dự đoán câu hỏi**: 
   - "Tại sao không dùng MongoDB?" → Vì dữ liệu có quan hệ chặt chẽ, SQL phù hợp hơn.
   - "Tại sao tách app_order và web_order?" → Để dễ phân biệt nguồn gốc và báo cáo.
   - "Em test thế nào?" → Em có Unit Test cho Backend, manual test cho Desktop và Web.

7. **Backup plan**: Chuẩn bị video demo phòng trường hợp laptop lỗi.

---

## 🎬 BONUS: CÂU HỎI DỰ ĐOÁN & ĐÁP ÁN

### ❓ "Em giải thích Singleton Pattern trong DatabaseConnector?"
> "Dạ, Singleton Pattern đảm bảo chỉ có **một instance duy nhất** của class DatabaseConnector trong toàn bộ ứng dụng. Em implement bằng cách:
> 1. Khai báo constructor là `private` để không ai có thể `new` từ bên ngoài.
> 2. Tạo một biến static `instance` lưu trữ đối tượng duy nhất.
> 3. Cung cấp method `getInstance()` để trả về instance đó.
> 
> Lợi ích là **tiết kiệm tài nguyên** - không tạo nhiều kết nối Database không cần thiết, và dễ quản lý việc đóng kết nối."

### ❓ "Tại sao em không dùng ORM cho Desktop App?"
> "Dạ, em có cân nhắc giữa JPA và JDBC. Em chọn **JDBC thuần** cho Desktop vì:
> 1. Desktop App không cần quá phức tạp, số lượng bảng không nhiều.
> 2. JDBC cho phép em **kiểm soát tuyệt đối** câu lệnh SQL, dễ optimize performance.
> 3. **Transaction management** với JDBC rõ ràng hơn với `setAutoCommit`, `commit`, `rollback`.
> 4. Ứng dụng nhẹ hơn, không cần load thêm Hibernate framework.
> 
> Còn Backend em dùng JPA vì Spring Boot tích hợp sẵn và cần tính scalability cao hơn."

### ❓ "Em xử lý concurrency như thế nào khi nhiều người cùng sửa một sản phẩm?"
> "Dạ, em có nghĩ đến vấn đề này. Hiện tại em chưa implement Optimistic/Pessimistic Locking, nhưng em đã có biện pháp:
> 1. Trong Desktop, chỉ có Admin mới được sửa sản phẩm, và thường chỉ 1 admin.
> 2. Web chỉ cho phép **đọc** sản phẩm, không cho khách hàng sửa.
> 3. Database có **Transaction Isolation Level** mặc định của MySQL là REPEATABLE READ.
> 
> Nếu phát triển tiếp, em sẽ thêm **version field** trong bảng products và implement Optimistic Locking của JPA."

---

**🎓 CHÚC BẠN BẢO VỆ THÀNH CÔNG!**
