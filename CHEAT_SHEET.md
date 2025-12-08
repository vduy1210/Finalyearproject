# BÍ KÍP PHÒNG THÂN (CHEAT SHEET)
*Tài liệu này dùng để in ra hoặc mở sẵn trên màn hình khi bảo vệ. Ngắn gọn, súc tích, cứu cánh.*

---

## 1. SƠ ĐỒ LUỒNG DỮ LIỆU (DATA FLOW)

Hãy nhớ câu thần chú: **"Khách đặt -> Web gửi -> Server lưu -> App hiện"**.

1.  **Frontend (React):** Khách chọn món -> Bấm "Đặt hàng" -> Gói dữ liệu thành JSON.
2.  **API (Spring Boot):** Nhận JSON -> Kiểm tra hợp lệ -> Chuyển sang JPA.
3.  **Database (MySQL):** JPA lưu dữ liệu vào bảng `web_order`.
4.  **Desktop App (Java Swing):** Nhân viên bấm "Refresh" -> JDBC chạy `SELECT` -> Lấy đơn về -> Hiển thị lên bảng.

---

## 2. 5 DÒNG CODE "SINH TỬ" (PHẢI NHỚ)

Nếu bị hỏi "Chỗ này code thế nào?", hãy chỉ vào đúng dòng này:

1.  **Kết nối Database (Desktop):**
    `Connection conn = DriverManager.getConnection(url, user, pass);`
    *(Chìa khóa mở cửa kho dữ liệu)*

2.  **Gửi đơn hàng (Frontend):**
    `fetch('http://.../api/orders', { method: 'POST', body: ... })`
    *(Shipper mang gói tin đi gửi)*

3.  **Nhận đơn hàng (Backend):**
    `@PostMapping("/orders") public ResponseEntity<?> createOrder(...)`
    *(Cổng bảo vệ nhận hàng)*

4.  **Bảo mật mật khẩu:**
    `BCrypt.checkpw(plainPassword, hashedPassword)`
    *(Máy soi vân tay, không so sánh trực tiếp)*

5.  **Giao dịch an toàn (Transaction):**
    `conn.setAutoCommit(false); ... conn.commit();`
    *(Chế độ "Làm xong hết mới được lưu", tránh lỗi nửa vời)*

---

## 3. 5 CÂU TRẢ LỜI "CỨU CÁNH" (KHI BỊ HỎI BÍ)

Khi gặp câu hỏi khó, đừng im lặng. Hãy dùng các mẫu câu này để "lái" sang hướng mình biết:

**Q: Tại sao em không dùng công nghệ X, Y, Z (hiện đại hơn)?**
> **A:** "Dạ, mục tiêu của đồ án là **nắm vững nền tảng** (Core Foundation). Em chọn JDBC/Swing để hiểu rõ bản chất cách hoạt động bên dưới trước khi dùng các Framework tự động hóa hoàn toàn ạ."

**Q: Hệ thống này có chịu tải được 1 triệu người dùng không?**
> **A:** "Dạ, hiện tại hệ thống thiết kế cho quy mô **vừa và nhỏ** (SME). Để mở rộng lên quy mô lớn, em sẽ cần nâng cấp Database (Sharding) và dùng cơ chế Caching (Redis) ạ. Đó là hướng phát triển tiếp theo của em."

**Q: Nếu đang lưu dữ liệu mà mất điện thì sao?**
> **A:** "Dạ, em có sử dụng cơ chế **Transaction** (`setAutoCommit(false)`). Nếu chưa chạy lệnh `commit()` mà mất điện, Database sẽ tự động **Rollback** (hoàn tác), đảm bảo dữ liệu không bị lỗi ạ."

**Q: Tại sao lại lưu ảnh vào folder mà không lưu vào Database?**
> **A:** "Dạ, lưu file ảnh trực tiếp vào DB sẽ làm DB rất **nặng và chậm** (Performance issue). Lưu đường dẫn (Path) giúp DB nhẹ hơn và việc backup dữ liệu cũng dễ dàng hơn ạ."

**Q: Em có test kỹ chưa?**
> **A:** "Dạ, em đã thực hiện **Unit Test** cho các hàm quan trọng và **Manual Test** (test tay) theo các kịch bản người dùng thực tế (User Scenarios) để đảm bảo các luồng chính hoạt động ổn định ạ."

---
**CHÚC BẠN BÌNH TĨNH VÀ TỰ TIN!**
