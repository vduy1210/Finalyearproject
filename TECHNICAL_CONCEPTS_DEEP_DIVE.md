# TECHNICAL CONCEPTS DEEP DIVE (GIẢI THÍCH CHUYÊN SÂU CÁC THUẬT NGỮ KỸ THUẬT)

This document provides detailed explanations of the technical keywords mentioned in the Architecture Q&A. Each concept is broken down into: **Definition**, **Role in Project**, and **Real-world Analogy**.
*(Tài liệu này giải thích chi tiết các từ khóa kỹ thuật đã nhắc đến trong phần Hỏi-Đáp Kiến trúc. Mỗi khái niệm được chia thành: **Định nghĩa**, **Vai trò trong dự án**, và **Liên tưởng thực tế**.)*

---

## 1. RESTful API (Representational State Transfer)

### 📖 Khái niệm (Definition)
**RESTful API** is a set of rules (architectural style) for building web services where clients and servers communicate over HTTP. It treats everything as a **Resource** (e.g., User, Product) identified by a URL.
*(**RESTful API** là một tập hợp các quy tắc (phong cách kiến trúc) để xây dựng các dịch vụ web. Nó coi mọi thứ là một **Tài nguyên** (ví dụ: Người dùng, Sản phẩm) và định danh chúng bằng một đường dẫn URL.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **Location:** Defined in the **Spring Boot Backend** (specifically in `Controller` classes like `ProductController`, `OrderController`).
*   **Action:** It acts as the **Gateway** or **Receptionist** for the Web Frontend.
    *   When the React App wants to show products, it cannot query the Database directly. It MUST ask the REST API via `GET /api/products`.
    *   The API accepts the request, gets data, and replies.
*(**Vị trí:** Được định nghĩa trong **Spring Boot Backend** (cụ thể là các lớp `Controller`).
**Hành động:** Nó đóng vai trò là **Cổng giao tiếp** hoặc **Lễ tân** cho Web Frontend. Khi React App muốn hiện sản phẩm, nó không thể tự mò vào Database. Nó BUỘC CHỊU phải hỏi REST API qua lệnh `GET /api/products`.)*

### 💡 Liên tưởng (Analogy)
**"Menu đồ uống ở quán cafe"**
*   **Resource:** "Cà phê đen", "Bạc xỉu" (các món trong menu).
*   **URL:** Tên gọi chính xác để gọi món.
*   **GET:** Bạn nói "Cho tôi một ly cafe" (Lấy thông tin).
*   **POST:** Bạn nói "Làm cho tôi món mới này" (Tạo mới).
*   **REST API:** Là tờ Menu và quy tắc gọi món. Bạn không thể tự chui vào quầy pha chế (Database) để lấy cafe, bạn phải gọi qua nhân viên (API).

---

## 2. HTTP (HyperText Transfer Protocol)

### 📖 Khái niệm (Definition)
**HTTP** is the underlying protocol of the World Wide Web. It defines how messages are formatted and transmitted. It's strictly **Request-Response**: Client asks, Server answers.
*(**HTTP** là giao thức nền tảng của World Wide Web. Nó quy định cách các tin nhắn được định dạng và truyền đi. Nó hoạt động theo cơ chế **Hỏi-Đáp**: Client hỏi, Server trả lời.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **Context:** It is the **Language** that your React App (Client) and Spring Boot (Server) use to talk to each other.
*   **Usage:** Every time you see `fetch('http://...')` in your Javascript code, you are sending an HTTP packet.
*   **Methods used:**
    *   `GET`: Fetch data (e.g., load menu).
    *   `POST`: Submit data (e.g., place order).
    *   `PUT`: Update data (e.g., update price).
*(**Bối cảnh:** Nó là **Ngôn ngữ** mà React App và Spring Boot dùng để nói chuyện với nhau.
**Sử dụng:** Mỗi khi bạn thấy lệnh `fetch` trong code JS, là bạn đang gửi một gói tin HTTP. Các phương thức dùng trong dự án: GET (lấy), POST (gửi), PUT (sửa).)*

### 💡 Liên tưởng (Analogy)
**"Dịch vụ gửi thư bưu điện"**
*   **HTTP Request:** Là bức thư bạn gửi đi (bên trong có ghi rõ gửi cho ai, nội dung gì).
*   **HTTP Response:** Là thư hồi âm từ người nhận.
*   **Giao thức:** Là quy định chung như "phải dán tem góc phải", "ghi mã bưu chính". Nếu không tuân thủ quy định (giao thức), thư sẽ không đến nơi.

---

## 3. JDBC (Java Database Connectivity)

### 📖 Khái niệm (Definition)
**JDBC** is a standard Java API that allows Java programs to connect to database management systems (like MySQL, PostgreSQL). It executes SQL processing operations.
*(**JDBC** là một API chuẩn của Java cho phép các chương trình Java kết nối với các hệ quản trị cơ sở dữ liệu. Nó thực thi các câu lệnh SQL.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **Location:** Used extensively in the **Desktop Application** (in `dao` package, e.g., `UserDAO.java`).
*   **Action:** It builds the direct "pipe" to the MySQL Database.
    *   `DriverManager.getConnection()`: Opens the pipe.
    *   `PreparedStatement`: Sends SQL commands through the pipe.
    *   `ResultSet`: Receives the data coming back.
*(**Vị trí:** Dùng chủ yếu trong **Desktop App**.
**Hành động:** Nó xây dựng cái "đường ống" nối thẳng tới MySQL Database. `DriverManager` mở ống, `PreparedStatement` đẩy lệnh SQL qua ống, `ResultSet` hứng dữ liệu chảy về.)*

### 💡 Liên tưởng (Analogy)
**"Dây cáp nối máy chiếu"**
*   Java App là cái Laptop. Database là cái Máy chiếu.
*   **JDBC** chính là sợi dây cáp HDMI/VGA nối hai thiết bị đó lại để truyền hình ảnh (dữ liệu). Không có dây cáp (Driver), laptop không thể điều khiển máy chiếu.

---

## 4. MVC (Model - View - Controller)

### 📖 Khái niệm (Definition)
**MVC** is a design pattern used to decouple user-interface (View), data (Model), and application logic (Controller).
*(**MVC** là mẫu thiết kế giúp tách biệt giao diện, dữ liệu và logic điều khiển.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **In Web Backend (Spring Boot):**
    *   **Model:** `Product.java` (Entity).
    *   **View:** The JSON response (Spring constructs the view data).
    *   **Controller:** `ProductController.java` (Receives request `GET /products`, asks Service/Repository, returns JSON).
*   **In Desktop App (Swing):**
    *   **Model:** `Product` class.
    *   **View:** `ProductManagerPanel.java` (JPanel, Buttons, TextFields).
    *   **Controller:** The logic inside `ActionListener` (Button clicks) that calls `ProductDAO`.
*(**Trong Web Backend:** Cực kỳ rõ ràng. Controller nhận lệnh, xử lý và trả về dữ liệu.
**Trong Desktop App:** View là các màn hình. Khi bấm nút, một đoạn code (Controller logic) sẽ chạy để gọi xuống Database (thông qua DAO) và cập nhật lại màn hình.)*

### 💡 Liên tưởng (Analogy)
**"Nhà hàng"**
*   **View:** Cuốn Menu và không gian bàn ăn (Những gì khách thấy).
*   **Controller:** Người bồi bàn (Nhận yêu cầu từ khách, chuyển xuống bếp).
*   **Model:** Nguyên liệu trong kho bếp (Dữ liệu thô).

---

## 5. TRANSACTION (ACID)

### 📖 Khái niệm (Definition)
**Transaction** is a sequence of operations performed as a single logical unit of work. It must adhere to **ACID** properties (Atomicity, Consistency, Isolation, Durability).
*(**Giao dịch** là một chuỗi các hành động được thực hiện như một đơn vị công việc duy nhất. Nó phải tuân thủ tính chất **ACID**.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **Location:** `AppOrderDao.java` (via `conn.setAutoCommit(false)`) and Spring Boot `@Transactional`.
*   **Critical Moment:** When placing an order.
    *   Step 1: Save Order Header.
    *   Step 2: Save Order Details (Items).
    *   Step 3: Deduct Stock.
    *   **Role:** If Step 3 fails (e.g., not enough stock), Transaction ensures Step 1 and 2 are **undone** (Rolled back). Never leave "ghost orders" in the DB.
*(**Vị trí:** Trong chức năng Đặt hàng.
**Khoảnh khắc quan trọng:** Khi tạo đơn. Nếu bước trừ kho bị lỗi, Giao dịch sẽ đảm bảo bước "lưu đơn hàng" trước đó bị **hủy bỏ** (Rollback) ngay lập tức. Đảm bảo dữ liệu không bao giờ bị "dở dang".)*

### 💡 Liên tưởng (Analogy)
**"Máy bán nước tự động"**
*   Bạn nhét tiền vào -> Chọn nước -> Máy kẹt không nhả nước.
*   **Transaction:** Máy phải **trả lại tiền** cho bạn.
*   Nếu không có Transaction: Máy nuốt tiền và không đưa nước (Mất tính toàn vẹn).

---

## 6. SINGLETON PATTERN

### 📖 Khái niệm (Definition)
**Singleton** is a design pattern that ensures a class has only one instance and provides a global point of access to it.
*(**Singleton** là mẫu thiết kế đảm bảo một class chỉ có duy nhất một đối tượng (instance) được tạo ra và cung cấp một điểm truy cập toàn cục.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **Location:** `DatabaseConnector.java` used in Desktop App.
*   **Purpose:**
    *   We don't want to define the Database URL, Username, Password in 100 different files.
    *   We want ONE central place to manage connections.
    *   `DatabaseConnector.getConnection()` always uses that single configuration.
*(**Vị trí:** `DatabaseConnector.java`.
**Tác dụng:** Chúng ta không muốn khai báo username/password DB ở 100 chỗ khác nhau. Singleton gom nó về 1 chỗ. Khi cần kết nối, cả ứng dụng đều gọi đến đúng 1 người quản lý này thôi.)*

---

## 7. CORS (Cross-Origin Resource Sharing)

### 📖 Khái niệm (Definition)
**CORS** is a security feature implemented by browsers that restricts web pages from making requests to a different domain than the one that served the web page.
*(**CORS** là cơ chế bảo mật của trình duyệt, chặn trang web gọi dữ liệu từ một domain khác với domain của chính nó.)*

### 🛠️ Vai trò trong Project (Role in Project)
*   **The Conflict:**
    *   Your React App runs on `localhost:3000`.
    *   Your Spring Boot API runs on `localhost:8081`.
    *   Browser thinks: "Different ports? Could be a hacker! Block it!"
*   **The Fix:** You configured CORS in Spring Boot (`.allowedOrigins("http://localhost:3000")`) to tell the browser: "It's okay, I (Server 8081) trust this guy (Client 3000)."
*(**Xung đột:** React chạy port 3000, Backend chạy port 8081. Trình duyệt coi đây là 2 "nhà" khác nhau nên chặn không cho nói chuyện để bảo mật.
**Giải pháp:** Bạn cấu hình CORS trong Spring Boot để Server 8081 xác nhận: "Không sao, tôi tin tưởng thằng 3000 này, cho nó vào đi".)*

