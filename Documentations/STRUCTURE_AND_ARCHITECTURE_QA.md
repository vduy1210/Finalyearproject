# STRUCTURE & ARCHITECTURE Q&A (CÂU HỎI VÀ TRẢ LỜI VỀ CẤU TRÚC VÀ KIẾN TRÚC)

This document focuses exclusively on the **System Architecture, Design Patterns, and Structural Decisions** of the project. It is designed to help you answer "Why did you build it this way?" questions.
*(Tài liệu này tập trung chuyên sâu vào **Kiến trúc hệ thống, Mẫu thiết kế và Các quyết định về cấu trúc** của dự án. Nó giúp bạn trả lời các câu hỏi "Tại sao bạn lại xây dựng như thế này?".)*

---

## 🏗️ 1. SYSTEM OVERVIEW (TỔNG QUAN HỆ THỐNG)

### ❓ Q1: What is the high-level architecture of your system?
**(Kiến trúc tổng thể của hệ thống là gì?)**

**💡 Answer:**
*   **English:** My system follows a **Hybrid Architecture** combining two different stacks sharing a single database:
    1.  **Desktop App:** Follows a **2-Tier Architecture** (Client-Server), connecting directly to the MySQL Database via JDBC. It uses the **MVC (Model-View-Controller)** pattern (or more precisely, MVP) for the internal structure.
    2.  **Web Application:** Follows a **3-Tier Architecture** (Presentation - Logic - Data). The Frontend (React) communicates with the Backend (Spring Boot) via **RESTful APIs**.
*   **Tiếng Việt:** Hệ thống của em tuân theo **Kiến trúc Lai (Hybrid Architecture)**, kết hợp hai stack công nghệ khác nhau cùng chia sẻ một cơ sở dữ liệu:
    1.  **Desktop App:** Tuân theo **Kiến trúc 2 tầng (Client-Server)**, kết nối trực tiếp đến MySQL Database qua JDBC. Bên trong tuân theo mô hình **MVC** (hoặc chính xác hơn là MVP).
    2.  **Web Application:** Tuân theo **Kiến trúc 3 tầng**. Frontend (React) giao tiếp với Backend (Spring Boot) thông qua **RESTful APIs**.

### ❓ Q2: Why does the Desktop App connect directly to the Database (2-Tier)? Why not connect to the API?
**(Tại sao Desktop App kết nối trực tiếp Database? Sao không gọi qua API?)**

**💡 Answer:**
*   **English:**
    *   **Simplicity:** For a small-scale specific application (like a POS at a counter), direct JDBC connection is simpler to implement and faster (less network overhead) than going through an HTTP layer.
    *   **Legacy Context:** Many traditional desktop Swing applications use this pattern. In this project, I wanted to demonstrate direct **JDBC Transaction management**.
    *   **Trade-off:** The downside is security (exposing DB credentials) and scalability. In a real-world enterprise scenario, I would refactor it to call the backend API (3-Tier) like the Web App.
*   **Tiếng Việt:**
    *   **Sự đơn giản:** Với ứng dụng quy mô nhỏ và chuyên biệt (như máy POS tại quầy), kết nối trực tiếp JDBC dễ triển khai và nhanh hơn (ít độ trễ mạng) so với đi qua lớp HTTP.
    *   **Bối cảnh truyền thống:** Nhiều ứng dụng Swing cũ dùng mô hình này. Trong đồ án này, em muốn demo kỹ năng quản lý **Giao dịch JDBC (Transaction)** trực tiếp.
    *   **Đánh đổi:** Nhược điểm là bảo mật (lộ thông tin DB) và khó mở rộng. Trong môi trường doanh nghiệp thực tế, em sẽ refactor để nó gọi Web API (3 tầng) giống như Web App.

---

## 📐 2. DESIGN PATTERNS (MẪU THIẾT KẾ)

### ❓ Q3: How is the MVC pattern applied in your Desktop App?
**(Mô hình MVC được áp dụng thế nào trong Desktop App?)**

**💡 Answer:**
*   **English:**
    *   **Model:** The POJO classes in the `model` package (e.g., `User`, `Product`) that represent data structures.
    *   **View:** The `JFrame` and `JPanel` classes in the `view` package (e.g., `OrderPanel`) that handle the UI and user inputs.
    *   **Controller:** The logic is distributed. The **DAO (Data Access Object)** classes act as a part of the Controller by handling data logic. The event listeners (ActionListeners) in the View trigger these DAOs. This acts as a bridge between View and Model.
*   **Tiếng Việt:**
    *   **Model:** Các class POJO trong gói `model` (như `User`, `Product`) đại diện cho dữ liệu.
    *   **View:** Các class `JFrame`, `JPanel` trong gói `view` (như `OrderPanel`) xử lý giao diện và nhập liệu.
    *   **Controller:** Logic được phân tán. Các lớp **DAO** đóng vai trò xử lý logic dữ liệu. Các bộ lắng nghe sự kiện (Event Listeners) trong View sẽ gọi các DAO này. Nó đóng vai trò cầu nối giữa View và Model.

### ❓ Q4: What is the DAO Pattern and why do you use it?
**(Mẫu DAO là gì và tại sao em dùng nó?)**

**💡 Answer:**
*   **English:** **DAO (Data Access Object)** is a structural pattern that isolates the application/business layer from the persistence layer (database).
    *   **Purpose:** The UI doesn't need to know *how* to connect to SQL. It just calls `userDAO.authenticate()`. This allows me to change the database (e.g., switch to PostgreSQL) without changing the UI code.
*   **Tiếng Việt:** **DAO** là mẫu thiết kế giúp tách biệt lớp nghiệp vụ (Business Layer) khỏi lớp lưu trữ (Data Layer).
    *   **Tác dụng:** Giao diện không cần biết *làm thế nào* để kết nối SQL. Nó chỉ cần gọi `userDAO.authenticate()`. Điều này giúp em có thể đổi cơ sở dữ liệu (ví dụ sang PostgreSQL) mà không phải sửa code giao diện.

### ❓ Q5: Did you use the Singleton Pattern? Where?
**(Em có dùng Singleton Pattern không? Ở đâu?)**

**💡 Answer:**
*   **English:** Yes, I used it in the **`DatabaseConnector`** class on the Desktop App.
    *   **Why:** Creating a database connection object is "expensive" (takes resources). Singleton ensures that there is only **one global instance** responsible for managing the connection configuration, avoiding unnecessary object creation overhead.
*   **Tiếng Việt:** Có, em dùng trong lớp **`DatabaseConnector`** ở Desktop App.
    *   **Tại sao:** Việc tạo đối tượng kết nối CSDL rất "tốn kém" tài nguyên. Singleton đảm bảo chỉ có **duy nhất một instance** chịu trách nhiệm quản lý cấu hình kết nối, tránh việc tạo thừa thãi.

---

## 🌐 3. COMMUNICATION & INTEGRATION (GIAO TIẾP & TÍCH HỢP)

### ❓ Q6: How does the Web Frontend talk to the Backend? (REST API)
**(Frontend và Backend giao tiếp thế nào?)**

**💡 Answer:**
*   **English:** They communicate via **RESTful APIs** over HTTP.
    *   **Protocol:** HTTP (Hypertext Transfer Protocol).
    *   **Data Format:** **JSON** (JavaScript Object Notation).
    *   **Stateless:** Each request contains all necessary info; the server doesn't remember the previous request state.
    *   **Example:** When React needs products, it sends a `GET` request to `/api/products`. The Spring Boot backend queries the DB and responds with a JSON array.
*   **Tiếng Việt:** Chúng giao tiếp qua **RESTful APIs** trên nền giao thức HTTP.
    *   **Giao thức:** HTTP.
    *   **Định dạng dữ liệu:** **JSON**.
    *   **Phi trạng thái (Stateless):** Mỗi request chứa đủ thông tin cần thiết; server không cần nhớ trạng thái của request trước đó.
    *   **Ví dụ:** Khi React cần danh sách sản phẩm, nó gửi request `GET` đến `/api/products`. Spring Boot backend sẽ query DB và trả về chuỗi JSON.

### ❓ Q7: How do you handle "Concurrency" (Race Conditions) between Desktop and Web?
**(Em xử lý đồng bộ dữ liệu giữa Desktop và Web thế nào?)**

**💡 Answer:**
*   **English:** Since both connect to the **same physical Database**, the Database acts as the "Single Source of Truth".
    *   **Race Condition Risk:** If both Desktop and Web try to buy the last item simultaneously.
    *   **Solution:** I use **Database Transactions** (`conn.setAutoCommit(false)` in Java).
    *   **Future Improvement:** I should implement **Pessimistic Locking** (`SELECT ... FOR UPDATE`) or **Optimistic Locking** (using a `@Version` column) to prevent one user from overwriting another's data during high traffic.
*   **Tiếng Việt:** Vì cả hai cùng kết nối vào **một Database vật lý**, Database đóng vai trò là "Nguồn sự thật duy nhất" (Single Source of Truth).
    *   **Rủi ro:** Cả Desktop và Web cùng mua món cuối cùng một lúc.
    *   **Giải pháp:** Em dùng **Giao dịch Cơ sở dữ liệu (Transaction)**.
    *   **Cải tiến:** Em nên cài đặt thêm **Khóa Bi quan (Pessimistic Locking)** hoặc **Khóa Lạc quan (Optimistic Locking)** để ngăn chặn việc ghi đè dữ liệu khi có nhiều người dùng cùng lúc.

---

## 🗄️ 4. DATABASE & DATA INTEGRITY (CƠ SỞ DỮ LIỆU)

### ❓ Q8: Why did you separate `app_order` and `web_order`? Is it good practice?
**(Tại sao tách 2 bảng đơn hàng riêng? Làm vậy có tốt không?)**

**💡 Answer:**
*   **English:**
    *   **Reason:** I separated them initially because the data fields were different (Web needs shipping info, App does not). It was easier for development speed.
    *   **Self-Critique:** It violates **Normalization** principles. It makes reporting (calculating total revenue) difficult because I have to query two tables.
    *   **Better Solution:** I should use a generic `orders` table with a `type` column ('WEB' or 'APP') and a separate `shipping_info` table linked by ID.
*   **Tiếng Việt:**
    *   **Lý do:** Em tách ra ban đầu vì các trường dữ liệu khác nhau (Web cần thông tin ship, App thì không). Lúc đó làm vậy cho nhanh.
    *   **Tự nhận xét:** Nó vi phạm nguyên tắc **Chuẩn hóa**. Nó làm việc báo cáo khó khăn vì phải query 2 bảng rồi cộng lại.
    *   **Giải pháp tốt hơn:** Em nên dùng một bảng `orders` chung với cột `type` ('WEB' hoặc 'APP') và bảng `shipping_info` riêng được liên kết sang.

### ❓ Q9: How do you ensure Data Integrity when saving a complex order?
**(Làm sao đảm bảo toàn vẹn dữ liệu khi lưu một đơn hàng phức tạp?)**

**💡 Answer:**
*   **English:** I use **ACID Transactions**.
    *   **Atomicity:** Saving the `Order` info and the `OrderDetails` (list of products) must be a single unit. Either both succeed, or both fail.
    *   **Implementation:** In the DAO, I assume `setAutoCommit(false)`. If any line fails (e.g., product out of stock), I trigger `conn.rollback()` to undo everything. Only when all are safe do I call `conn.commit()`.
*   **Tiếng Việt:** Em sử dụng **Giao dịch ACID**.
    *   **Tính Nguyên tử (Atomicity):** Việc lưu thông tin `Order` và `OrderDetails` (danh sách món) phải là một khối thống nhất. Một là lưu được hết, hai là không lưu gì cả.
    *   **Thực hiện:** Trong DAO, em set `setAutoCommit(false)`. Nếu có bất kỳ lỗi nào (ví dụ hết hàng), em gọi `rollback()` để hoàn tác tất cả. Chỉ khi mọi thứ ổn thỏa em mới `commit()`.

---

## 🛡️ 5. QUALITY ATTRIBUTES (THUỘC TÍNH CHẤT LƯỢNG)

### ❓ Q10: Is your system Scalable?
**(Hệ thống có mở rộng được không?)**

**💡 Answer:**
*   **English:**
    *   **Web Tier:** Highly scalable. The Spring Boot backend is **stateless**, so we can run multiple instances behind a Load Balancer to handle more users.
    *   **Desktop Tier:** Not very scalable in its current form because of the direct DB connection limit.
    *   **Database:** A single MySQL server is a bottleneck. To scale, we would need **Read Replicas** (separate servers for reading data) or **Sharding** (splitting data across servers).
*   **Tiếng Việt:**
    *   **Web Tier:** Có khả năng mở rộng tốt. Backend Spring Boot là **stateless**, nên có thể chạy nhiều server song song phía sau Load Balancer.
    *   **Desktop Tier:** Khó mở rộng vì giới hạn kết nối trực tiếp vào DB.
    *   **Database:** Một server MySQL sẽ là điểm nghẽn. Để mở rộng, cần dùng cơ chế **Replication** (server riêng để đọc) hoặc **Sharding** (chia nhỏ dữ liệu ra nhiều server).

### ❓ Q11: How do you handle Security?
**(Em xử lý bảo mật thế nào?)**

**💡 Answer:**
*   **English:**
    1.  **Passwords:** Hashed using **BCrypt** with salt. Never stored in plain text.
    2.  **SQL Injection:** Prevented using **PreparedStatement** (JDBC) and **JPA Repositories** (Spring).
    3.  **CORS:** Configured on Spring Boot to only allow requests from the React Frontend (`localhost:3000`).
*   **Tiếng Việt:**
    1.  **Mật khẩu:** Băm bằng **BCrypt** có muối. Không bao giờ lưu dạng rõ.
    2.  **SQL Injection:** Ngăn chặn bằng cách dùng **PreparedStatement** (JDBC) và **JPA** (Spring).
    3.  **CORS:** Cấu hình trên Spring Boot để chỉ nhận request từ React Frontend (`localhost:3000`).
