# EXAMINER QUESTIONS & ANSWERS (CÂU HỎI & TRẢ LỜI PHẢN BIỆN)

This document categorizes questions based on the examiner's focus: **Syntax (Code details)** vs. **Structure (Architecture)**.
*(Tài liệu này phân loại câu hỏi dựa trên sự chú trọng của giáo viên: **Cú pháp (Chi tiết code)** vs. **Cấu trúc (Kiến trúc hệ thống)**.)*

---

## 👤 EXAMINER 1: THE SYNTAX EXPERT (GIÁO VIÊN CHÚ TRỌNG CÚ PHÁP)
*Focus: Java Core, JavaScript ES6, Keywords, Error Handling, Memory Management.*
*(Trọng tâm: Java Core, JavaScript ES6, Từ khóa, Xử lý lỗi, Quản lý bộ nhớ.)*

### ❓ Q1: `try-with-resources` in Java
**Question:** *"I see you use `try (Connection conn = ...)` in your DAO files. How is this different from a normal `try-catch-finally` block? What happens if you don't use it?"*
*(Tôi thấy em dùng `try (Connection conn = ...)` trong các file DAO. Nó khác gì với khối `try-catch-finally` thông thường? Chuyện gì xảy ra nếu em không dùng nó?)*

**💡 Answer:**
*   **English:** This is **Try-with-resources** (introduced in Java 7). It ensures that resources implementing `AutoCloseable` (like `Connection`, `ResultSet`) are **automatically closed** at the end of the block, even if an exception occurs. If I used a normal `try-catch`, I would have to manually call `.close()` in the `finally` block, which is verbose and prone to memory leaks if forgotten.
*   **Tiếng Việt:** Đây là cú pháp **Try-with-resources** (có từ Java 7). Nó đảm bảo các tài nguyên (như kết nối Database) sẽ **tự động được đóng** khi chạy xong khối lệnh, ngay cả khi có lỗi xảy ra. Nếu dùng `try-catch` thường, em phải tự gọi hàm `.close()` trong khối `finally`, vừa dài dòng vừa dễ quên gây rò rỉ bộ nhớ.

### ❓ Q2: `async/await` in JavaScript
**Question:** *"In `Cart.js`, you use `async function handleOrder()` and `await fetch(...)`. Can you explain how this works? Is JavaScript multi-threaded?"*
*(Trong `Cart.js`, em dùng `async` và `await`. Giải thích cơ chế hoạt động? JavaScript có phải đa luồng không?)*

**💡 Answer:**
*   **English:** JavaScript is **single-threaded**. `async/await` is syntactic sugar for **Promises**. When `await fetch(...)` is called, the execution pauses **non-blockingly**, allowing the browser to handle other tasks (like UI updates) while waiting for the server response. Once the response returns, the code resumes.
*   **Tiếng Việt:** JavaScript là **đơn luồng (single-threaded)**. `async/await` là cú pháp viết gọn của **Promise**. Khi gọi `await fetch(...)`, việc thực thi sẽ tạm dừng (nhưng không làm treo giao diện), trình duyệt vẫn làm việc khác được trong lúc đợi server trả lời. Khi có kết quả, code mới chạy tiếp.

### ❓ Q3: `map()` vs `forEach()` in React
**Question:** *"Why do you use `cart.map(...)` to render the list of items instead of `cart.forEach(...)`?"*
*(Tại sao em dùng `map` để hiển thị danh sách món ăn mà không dùng `forEach`?)*

**💡 Answer:**
*   **English:** React needs a **return value** to render elements. `map()` returns a **new array** of JSX elements, which React can render. `forEach()` returns `undefined`, so it renders nothing.
*   **Tiếng Việt:** React cần một **giá trị trả về** để hiển thị. Hàm `map()` trả về một **mảng mới** chứa các phần tử giao diện (JSX), nên React hiển thị được. Còn `forEach()` trả về `undefined` (không có gì), nên sẽ không hiện gì cả.

### ❓ Q4: `@Autowired` & Dependency Injection
**Question:** *"In `ProductController`, you use `@Autowired` (or constructor injection). What does it actually do behind the scenes?"*
*(Trong Controller, em dùng `@Autowired`. Thực chất nó làm gì bên dưới?)*

**💡 Answer:**
*   **English:** This is **Dependency Injection (DI)**. When the application starts, Spring's **IoC Container** creates an instance of `ProductRepository` (a Bean) and "injects" it into the Controller. I don't need to write `new ProductRepository()` manually. This promotes loose coupling.
*   **Tiếng Việt:** Đây là **Tiêm phụ thuộc (DI)**. Khi ứng dụng chạy, **IoC Container** của Spring sẽ tạo sẵn một đối tượng `ProductRepository` (gọi là Bean) và "tiêm" (gắn) nó vào Controller. Em không cần phải tự viết `new ProductRepository()`. Giúp code lỏng lẻo, dễ bảo trì hơn.

### ❓ Q5: `PreparedStatement` vs `Statement`
**Question:** *"Why do you use `PreparedStatement` everywhere? Is it just for SQL Injection prevention?"*
*(Tại sao chỗ nào em cũng dùng `PreparedStatement`? Có phải chỉ để chống hack SQL Injection không?)*

**💡 Answer:**
*   **English:** Besides preventing SQL Injection (by treating parameters as data, not executable code), `PreparedStatement` is also **faster** for repeated queries because the database can **pre-compile** and cache the execution plan.
*   **Tiếng Việt:** Ngoài việc chống SQL Injection (do nó coi tham số là dữ liệu chứ không phải mã lệnh), `PreparedStatement` còn **nhanh hơn** khi chạy nhiều lần, vì Database có thể **biên dịch trước** (pre-compile) câu lệnh đó.

---

## 🏗️ EXAMINER 2: THE STRUCTURE EXPERT (GIÁO VIÊN CHÚ TRỌNG CẤU TRÚC)
*Focus: Architecture Patterns (MVC), Database Design, API Design, Scalability.*
*(Trọng tâm: Mô hình kiến trúc (MVC), Thiết kế CSDL, Thiết kế API, Khả năng mở rộng.)*

### ❓ Q1: MVC Pattern Implementation
**Question:** *"Show me where M, V, and C are in your project. Is your Desktop App truly MVC?"*
*(Chỉ cho tôi xem M, V, C nằm đâu trong dự án. App Desktop của em có thực sự là MVC không?)*

**💡 Answer:**
*   **English:**
    *   **Model:** Classes in `model` package (User, Product) representing data.
    *   **View:** Classes in `view` package (JFrame, JPanel) handling UI.
    *   **Controller:** In my Desktop App, the logic is partly inside `DAO` (Data Access) and event listeners in `View`. Strictly speaking, it's more like **MVP (Model-View-Presenter)** or a simplified MVC where View calls DAO directly. In the Web Backend, it's a strict MVC (Controller handles requests, calls Service/Repository).
*   **Tiếng Việt:**
    *   **Model:** Các class trong gói `model` (User, Product) chứa dữ liệu.
    *   **View:** Các class trong gói `view` (JFrame, JPanel) lo giao diện.
    *   **Controller:** Trong App Desktop, logic nằm trong `DAO` và các sự kiện nút bấm. Chính xác thì nó giống mô hình **MVP** hơn, hoặc MVC đơn giản hóa nơi View gọi trực tiếp DAO. Còn bên Web Backend thì đúng chuẩn MVC (Controller nhận request, gọi Repository).

### ❓ Q2: Database Normalization (Chuẩn hóa CSDL)
**Question:** *"Why do you store `total_amount` in the `orders` table? Can't you just calculate it by summing up `order_details`? Isn't this redundant?"*
*(Tại sao em lưu cột `total_amount` trong bảng đơn hàng? Sao không tính tổng từ bảng chi tiết? Có phải dư thừa không?)*

**💡 Answer:**
*   **English:** You are right, it is technically redundant (denormalization). However, I store it for **Performance**. Calculating the sum of millions of order details every time I need a revenue report is very slow. Storing the total allows for instant reporting queries (`SELECT SUM(total_amount)...`).
*   **Tiếng Việt:** Thầy nói đúng, về lý thuyết là dư thừa. Tuy nhiên, em lưu nó vì **Hiệu năng (Performance)**. Việc tính tổng lại từ hàng triệu dòng chi tiết mỗi khi xem báo cáo sẽ rất chậm. Lưu sẵn tổng tiền giúp truy vấn báo cáo tức thì.

### ❓ Q3: REST API Principles
**Question:** *"Is your Web Backend a true RESTful API? What makes it RESTful?"*
*(Backend của em có phải chuẩn RESTful API không? Cái gì làm nên tính RESTful của nó?)*

**💡 Answer:**
*   **English:** Yes, it follows REST principles:
    1.  **Stateless:** No session data stored on server between requests.
    2.  **Resource-based URLs:** `/api/products` (nouns, not verbs).
    3.  **HTTP Methods:** Uses `GET` for reading, `POST` for creating, `PUT` for updating.
    4.  **JSON:** Uses JSON for data exchange.
*   **Tiếng Việt:** Dạ có, nó tuân thủ nguyên tắc REST:
    1.  **Stateless:** Server không lưu trạng thái phiên làm việc.
    2.  **URL dựa trên tài nguyên:** `/api/products` (danh từ, không phải động từ).
    3.  **Phương thức HTTP:** Dùng `GET` để lấy, `POST` để tạo, `PUT` để sửa.
    4.  **JSON:** Dùng JSON để trao đổi dữ liệu.

### ❓ Q4: Scalability (Khả năng mở rộng)
**Question:** *"If your shop expands to 100 branches, how will your system handle it? Will the Desktop App still connect directly to the DB?"*
*(Nếu quán mở rộng ra 100 chi nhánh, hệ thống chịu nổi không? App Desktop vẫn kết nối trực tiếp vào Database à?)*

**💡 Answer:**
*   **English:** Currently, the Desktop App connects directly (2-tier architecture), which is bad for 100 branches due to security and connection limits.
    *   **Solution:** I would switch to **3-tier architecture**. The Desktop App should call the **Web API** instead of the Database. The API can then be load-balanced across multiple servers to handle the traffic.
*   **Tiếng Việt:** Hiện tại App kết nối trực tiếp (kiến trúc 2 tầng), không tốt cho 100 chi nhánh vì lý do bảo mật và giới hạn kết nối.
    *   **Giải pháp:** Em sẽ chuyển sang **kiến trúc 3 tầng**. App Desktop sẽ gọi về **Web API** thay vì chọc thẳng vào Database. API có thể được cân bằng tải (Load Balancing) trên nhiều server để chịu tải lớn.

### ❓ Q5: Security (Bảo mật)
**Question:** *"How do you secure user passwords? What if the database is leaked?"*
*(Em bảo mật mật khẩu kiểu gì? Nếu lộ database thì sao?)*

**💡 Answer:**
*   **English:** I use **BCrypt** hashing. It's a one-way hashing algorithm with "salt". Even if hackers get the database, they only see random strings (hashes) and cannot reverse them to find the real passwords. I never store plain text passwords.
*   **Tiếng Việt:** Em dùng thuật toán băm **BCrypt**. Đây là hàm băm một chiều có thêm "muối" (salt). Dù hacker có lấy được database, họ chỉ thấy các chuỗi ký tự ngẫu nhiên và không thể dịch ngược lại thành mật khẩu gốc. Em tuyệt đối không lưu mật khẩu thô.
