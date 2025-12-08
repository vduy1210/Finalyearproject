# BỘ CÂU HỎI VÀ TRẢ LỜI PHẢN BIỆN ĐỒ ÁN (Dành cho Giáo viên Khó tính)

Tài liệu này tổng hợp các câu hỏi "xoáy" về mặt kỹ thuật, kiến trúc và logic mà hội đồng phản biện có thể đặt ra, kèm theo câu trả lời chi tiết để bạn chuẩn bị.

---

## 1. KIẾN TRÚC HỆ THỐNG (SYSTEM ARCHITECTURE)

### ❓ Câu hỏi 1: Tại sao lại tách bảng `app_order` và `web_order` riêng biệt?
**Giáo viên hỏi:** *"Tôi thấy em thiết kế 2 bảng `app_order` (đơn tại quầy) và `web_order` (đơn online) riêng biệt. Tại sao lại làm như vậy? Việc này khiến việc báo cáo doanh thu tổng hợp rất khó khăn và code bị lặp lại (duplicate code)."*

**💡 Trả lời chi tiết:**
*   **Lý do (Thực tế):** Em tách ra để dễ quản lý trong giai đoạn đầu, vì đơn online có thêm các trường thông tin giao hàng (`shipping_address`, `shipping_phone`) mà đơn tại quầy không cần. Ngoài ra, quy trình xử lý trạng thái của 2 loại đơn này cũng hơi khác nhau (Online cần duyệt -> giao -> hoàn thành; Tại quầy thì thường là hoàn thành ngay).
*   **Nhận diện vấn đề:** Em đồng ý rằng về mặt thiết kế lâu dài, đây là một điểm yếu. Nó vi phạm nguyên tắc thiết kế cơ sở dữ liệu (Normalization). Khi muốn tính tổng doanh thu, em phải query 2 lần rồi cộng lại, rất kém hiệu quả.
*   **Giải pháp (Nếu được làm lại):** Em sẽ gộp thành một bảng `orders` duy nhất.
    *   Thêm cột `order_source` (ENUM: 'APP', 'WEB') để phân loại.
    *   Các thông tin giao hàng sẽ tách sang bảng `shipping_info` (liên kết 1-1 với `orders`) hoặc để null nếu là đơn tại quầy.
    *   Như vậy việc query báo cáo sẽ chỉ cần `SELECT SUM(total) FROM orders`, nhanh và chính xác hơn nhiều.

### ❓ Câu hỏi 2: Vấn đề đồng bộ dữ liệu (Concurrency) khi nhiều người cùng thao tác?
**Giáo viên hỏi:** *"Desktop App và Web Backend cùng kết nối vào một Database. Nếu nhân viên A đang sửa sản phẩm trên App, nhân viên B mua hàng trên Web cùng lúc thì liệu có xảy ra lỗi sai lệch tồn kho (Race Condition) không?"*

**💡 Trả lời chi tiết:**
*   **Giải thích:** Đây là vấn đề tranh chấp tài nguyên (Concurrency Control). Nếu không xử lý kỹ, tồn kho có thể bị trừ sai (ví dụ: còn 1 cái, 2 người cùng mua được).
*   **Hiện trạng:** Trong code hiện tại, em đang sử dụng Transaction (`conn.setAutoCommit(false)`) khi tạo đơn hàng. Điều này giúp đảm bảo tính toàn vẹn dữ liệu trong một giao dịch (hoặc thành công hết, hoặc thất bại hết).
*   **Giải pháp nâng cao:** Tuy nhiên, để chặn triệt để Race Condition, em cần áp dụng cơ chế **Locking**:
    *   **Pessimistic Locking:** Khoá dòng dữ liệu (`SELECT ... FOR UPDATE`) khi có người đang thao tác.
    *   **Optimistic Locking:** Thêm cột `version` vào bảng `products`. Trước khi update sẽ kiểm tra xem `version` có thay đổi không. Nếu có người khác đã sửa rồi thì báo lỗi yêu cầu thử lại.

### ❓ Câu hỏi 3: Tại sao đặt tên package là `dao`, `model` trơ trọi như vậy?
**Giáo viên hỏi:** *"Cấu trúc package của em trong `mavenproject1` là `dao`, `model`, `view`. Đây không phải là chuẩn naming convention của Java (thường là `com.company.project.module`). Tại sao lại đặt tên thiếu chuyên nghiệp như vậy?"*

**💡 Trả lời chi tiết:**
*   **Thừa nhận:** Dạ, đây là thiếu sót của em trong quá trình khởi tạo dự án ban đầu. Em đã tập trung quá nhiều vào logic chức năng mà lơ là quy chuẩn đặt tên.
*   **Khắc phục:** Đúng chuẩn phải là `com.saleapp.core.dao`, `com.saleapp.core.model`. Việc phân chia package theo domain (ví dụ `com.saleapp.order`, `com.saleapp.product`) sẽ giúp code dễ bảo trì hơn là chia theo layer (`dao`, `model`) khi dự án lớn lên. Em sẽ refactor lại cấu trúc này trong phiên bản tới.

---

## 2. CƠ SỞ DỮ LIỆU (DATABASE DESIGN)

### ❓ Câu hỏi 4: Tại sao bảng `customers` có 2 cột `accumulatedPoint` và `accumulated_point`?
**Giáo viên hỏi:** *"Dữ liệu bị dư thừa (Redundancy). Em có 2 cột lưu điểm tích lũy với tên gần giống nhau. Em đang dùng cột nào? Tại sao không xóa cột kia đi?"*

**💡 Trả lời chi tiết:**
*   **Nguyên nhân:** Đây là lỗi trong quá trình migrate hoặc update cơ sở dữ liệu. Có thể lúc đầu em đặt tên kiểu camelCase (`accumulatedPoint`), sau đó đổi sang snake_case (`accumulated_point`) cho đúng chuẩn SQL nhưng quên xóa cột cũ.
*   **Hậu quả:** Gây nhầm lẫn cho người lập trình sau này và tốn dung lượng lưu trữ. Nguy hiểm hơn là dữ liệu có thể không nhất quán (cột này update, cột kia không).
*   **Khắc phục:** Em sẽ kiểm tra lại code xem đang map vào cột nào, sau đó viết script `ALTER TABLE customers DROP COLUMN ...` để xóa cột thừa đi ngay lập tức.

### ❓ Câu hỏi 5: Code phải "đoán" tên cột trong Database?
**Giáo viên hỏi:** *"Trong hàm báo cáo, em dùng vòng lặp để thử query với `total_amount` rồi đến `total`. Tại sao Database lại không nhất quán về tên cột như vậy?"*

**💡 Trả lời chi tiết:**
*   **Thừa nhận:** Dạ đúng là Database của em đang thiếu sự nhất quán trong quy ước đặt tên (Naming Convention). Bảng `app_order` dùng `total`, bảng khác có thể lại dùng `total_amount`.
*   **Giải pháp:** Để khắc phục, em cần chuẩn hóa lại toàn bộ Database:
    *   Tất cả các cột tiền tệ thống nhất là `total_amount`.
    *   Tất cả các cột ngày tạo thống nhất là `created_at`.
    *   Việc xử lý logic "thử sai" trong code Java (`try-catch` để query) là một "bad smell" (code xấu), làm giảm hiệu năng và che giấu lỗi thiết kế.

---

## 3. CHẤT LƯỢNG MÃ NGUỒN (CODE QUALITY)

### ❓ Câu hỏi 6: Tại sao dùng JDBC thuần và Hardcode SQL mà không dùng Hibernate/JPA?
**Giáo viên hỏi:** *"Thời đại này rồi sao còn viết `INSERT INTO ...` thủ công trong code Java? Vừa dài dòng, vừa dễ lỗi SQL Injection, vừa khó bảo trì. Tại sao không dùng Framework?"*

**💡 Trả lời chi tiết:**
*   **Lý do:** Em chọn sử dụng JDBC thuần cho đồ án này vì em muốn hiểu rõ bản chất cách Java giao tiếp với Database ở mức thấp (low-level) trước khi phụ thuộc vào các "Magic Framework" như Hibernate. Nó giúp em kiểm soát chính xác từng câu lệnh SQL được thực thi và tối ưu hóa performance ở những chỗ cần thiết.
*   **Về SQL Injection:** Em đã sử dụng `PreparedStatement` (dấu `?`) thay vì nối chuỗi (`+`), nên vấn đề SQL Injection đã được giải quyết triệt để.
*   **Hướng phát triển:** Tuy nhiên, em đồng ý là với dự án doanh nghiệp, việc dùng JPA/Hibernate sẽ tăng tốc độ phát triển và giảm lỗi. Em sẽ áp dụng nó cho các dự án sau.

### ❓ Câu hỏi 7: Lỗi quản lý tài nguyên trong khối `finally`?
**Giáo viên hỏi:** *"Trong khối `finally`, em đóng connection từng cái một. Nếu cái đầu tiên bị lỗi, mấy cái sau sẽ không được đóng, gây rò rỉ bộ nhớ (Memory Leak). Em giải thích sao?"*

**💡 Trả lời chi tiết:**
*   **Phân tích lỗi:** Dạ thầy nhận xét rất chính xác. Cách viết `try { close() } catch` lồng nhau trong `finally` là cách viết cũ và rất dễ sai sót.
*   **Giải pháp:** Từ Java 7 trở đi đã có `try-with-resources`. Em nên viết lại code như sau để Java tự động đóng kết nối dù có lỗi xảy ra hay không:
    ```java
    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Code xử lý
    } catch (SQLException e) {
        // Xử lý lỗi
    }
    ```

### ❓ Câu hỏi 8: Logic tìm sản phẩm theo Tên (`WHERE name=?`)?
**Giáo viên hỏi:** *"Em tìm ID sản phẩm bằng tên (`SELECT id FROM products WHERE name=?`). Nếu lỡ nhập sai chính tả một chút hoặc có 2 sản phẩm trùng tên thì sao?"*

**💡 Trả lời chi tiết:**
*   **Rủi ro:** Tìm kiếm theo tên (String) rất mong manh. Chỉ cần thừa một dấu cách là không tìm thấy. Và tên sản phẩm không phải là duy nhất (Unique Key).
*   **Giải pháp:** Trong hệ thống bán hàng thực tế, người ta luôn dùng **Mã vạch (Barcode)** hoặc **SKU (Stock Keeping Unit)** để tìm sản phẩm.
    *   Em sẽ thêm cột `sku` hoặc `barcode` vào bảng `products`.
    *   Khi quét mã hoặc nhập liệu, hệ thống sẽ tìm theo mã này (`WHERE sku=?`), đảm bảo chính xác tuyệt đối 100%.

---

## 4. QUY TRÌNH NGHIỆP VỤ (PROCESS & FLOW)

### ❓ Câu hỏi 9: Xử lý tồn kho âm?
**Giáo viên hỏi:** *"Em trừ tồn kho bằng lệnh `UPDATE ... SET stock = stock - ?`. Nếu kho đang còn 5 mà khách mua 10 thì kho thành -5 à? Em có chặn việc này không?"*

**💡 Trả lời chi tiết:**
*   **Thiếu sót:** Hiện tại trong Database em chưa set constraint `UNSIGNED` hoặc `CHECK (stock >= 0)` cho cột stock.
*   **Logic đúng:** Quy trình đúng phải là:
    1.  **Check:** Query xem `stock` hiện tại có >= số lượng mua không.
    2.  **Lock & Update:** Nếu đủ thì mới thực hiện trừ kho.
    3.  **Thông báo:** Nếu không đủ, phải báo lỗi "Hết hàng" cho nhân viên ngay lập tức.
*   **Khắc phục:** Em sẽ bổ sung logic kiểm tra này vào `AppOrderDao` trước khi thực hiện lệnh `UPDATE`.

### ❓ Câu hỏi 10: "Offline Mode" hoạt động như thế nào?
**Giáo viên hỏi:** *"Em nói ứng dụng có Offline Mode, nhưng kiến trúc lại là Client kết nối trực tiếp Server Database. Mất mạng thì kết nối kiểu gì?"*

**💡 Trả lời chi tiết:**
*   **Đính chính:** Có thể em đã dùng từ "Offline Mode" chưa chính xác. Ứng dụng của em là Desktop App chạy trong mạng LAN (Local Area Network).
*   **Trường hợp mất Internet:** Nếu Server Database đặt tại quán (Local Server), thì mất Internet vẫn bán hàng được bình thường, chỉ không đồng bộ được đơn Online thôi.
*   **Nếu Server trên Cloud:** Thì đúng là mất mạng sẽ không bán được. Để hỗ trợ Offline thực sự, em cần sử dụng **SQLite** lưu dữ liệu tạm trên máy tính tiền. Khi có mạng lại, App sẽ chạy một tiếng trình nền (Background Service) để đồng bộ (Sync) dữ liệu từ SQLite lên MySQL Server.

---
**LỜI KHUYÊN CUỐI CÙNG KHI TRẢ LỜI:**
1.  **Thái độ:** Luôn giữ thái độ cầu thị, lắng nghe. Đừng cãi tay đôi với giáo viên.
2.  **Thừa nhận & Giải pháp:** Nếu sai, hãy nói: *"Dạ, em thừa nhận đây là thiếu sót. Giải pháp khắc phục là..."*. Điều này cho thấy em hiểu vấn đề chứ không phải làm bừa.
3.  **Tự tin vào những gì mình làm được:** Dù có lỗi, nhưng hãy nhấn mạnh vào những chức năng đã chạy tốt (ví dụ: tính tiền nhanh, giao diện dễ dùng).
