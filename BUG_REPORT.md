# 🐛 BUG REPORT & SECURITY AUDIT

Sau khi rà soát mã nguồn (code review), tôi phát hiện các vấn đề sau, được sắp xếp theo mức độ nghiêm trọng.

## 🔴 MỨC ĐỘ: NGHIÊM TRỌNG (CRITICAL)

### 1. Lỗ hổng thao tác giá (Price Manipulation Vulnerability)
*   **Vị trí:** `OrderController.java` (Backend) và `Cart.js` (Frontend).
*   **Mô tả:** Frontend tự tính tổng tiền và gửi giá (`price`) của từng món lên Server. Server (`OrderController`) nhận giá này và lưu thẳng vào Database mà không kiểm tra lại.
*   **Hậu quả:** Hacker có thể dùng Postman hoặc sửa code JS để gửi request mua hàng với giá **0 đồng**.
*   **Khắc phục:** Backend **TUYỆT ĐỐI KHÔNG** tin giá từ Client. Backend phải lấy `productId` từ request, sau đó tự query vào bảng `products` để lấy giá chính xác.

### 2. Lộ thông tin nhạy cảm (Hardcoded Credentials)
*   **Vị trí:** `DatabaseConnector.java`
*   **Mô tả:** Mật khẩu Database (`root`/`123456`) đang được viết cứng (hardcode) ngay trong file Java.
*   **Hậu quả:** Nếu ai đó có được file `.class` hoặc source code (ví dụ up lên GitHub), họ sẽ biết ngay mật khẩu Database của bạn.
*   **Khắc phục:** Sử dụng **Biến môi trường (Environment Variables)** hoặc file cấu hình bên ngoài (ví dụ `application.properties` và không up file đó lên git).

### 3. Rò rỉ tài nguyên (Resource Leak)
*   **Vị trí:** `dao/AppOrderDao.java` (Hàm `createAppOrder`)
*   **Mô tả:** Trong khối `try-catch`, bạn mở kết nối (`Connection`, `PreparedStatement`) nhưng **không có khối `finally`** để đóng chúng. Dù bạn có gọi `commit()` hay `rollback()`, kết nối vẫn dính ở đó.
*   **Hậu quả:** Sau một thời gian chạy, Database sẽ báo lỗi "Too many connections" và ứng dụng sẽ bị sập (Crash).
*   **Khắc phục:** Sử dụng `try-with-resources` như các hàm khác trong cùng file.

---

## 🟡 MỨC ĐỘ: TRUNG BÌNH (MEDIUM)

### 4. Vấn đề tranh chấp dữ liệu (Race Condition)
*   **Vị trí:** `OrderController.java`
*   **Mô tả:** Logic hiện tại: (1) Kiểm tra tồn kho (`if stock < quality`) -> (2) Nếu đủ thì trừ kho (`stock = stock - quantity`).
*   **Hậu quả:** Giữa lúc (1) và (2), nếu có một request khác chen vào, cả 2 đơn hàng đều thấy "còn hàng" và cùng trừ kho, dẫn đến Tồn kho Âm.
*   **Khắc phục:** Sử dụng câu lệnh SQL nguyên tử: `UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?`.

### 5. Hiệu năng kết nối (Connection Performance)
*   **Vị trí:** `DatabaseConnector.java`
*   **Mô tả:** Mỗi lần gọi `getConnection()` là một lần mở kết nối mới tới MySQL.
*   **Hậu quả:** Việc mở kết nối rất tốn thời gian (TCP handshake, auth...). Nếu có 100 người dùng cùng lúc, server sẽ rất chậm.
*   **Khắc phục:** Sử dụng **HikariCP** (Connection Pool) để tái sử dụng các kết nối có sẵn.

---

## 🟢 MỨC ĐỘ: THẤP (LOW) / CODE SMELL

### 6. Logic tìm kiếm mong manh (Fragile Logic)
*   **Vị trí:** `AppOrderDao.java`
*   **Mô tả:** `UPDATE products ... WHERE name = ?`. Tìm và cập nhật sản phẩm theo **Tên**.
*   **Rủi ro:** Nếu sau này bạn đổi tên sản phẩm, hoặc lỡ có 2 sản phẩm trùng tên, logic này sẽ chạy sai. Luôn luôn nên dùng **ID** (Primary Key).

### 7. Bảo mật Frontend (XSS)
*   **Vị trí:** `Login.js`
*   **Mô tả:** Lưu token và role vào `localStorage`.
*   **Rủi ro:** Nếu trang web bị dính mã độc XSS (Cross-Site Scripting), hacker có thể đánh cắp token này và giả danh người dùng.
*   **Khắc phục:** (Nâng cao) Lưu token trong `HttpOnly Cookie`.
