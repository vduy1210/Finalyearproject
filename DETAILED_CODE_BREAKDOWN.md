# Chi Tiết Các Function Chính & Bug Fixes

Tài liệu này giải thích chi tiết các thay đổi trong code để sửa các lỗi nghiêm trọng (Bug 1, 3, 4, 6) và giải thích ý nghĩa của từng cú pháp quan trọng.

---

## 1. Backend: Xử lý Đặt hàng An toàn & Atomic Update (OrderController.java)

### Mục tiêu:
- **Fix Bug 1 (Price Manipulation):** Ngăn chặn người dùng sửa giá từ Frontend.
- **Fix Bug 4 (Race Condition):** Ngăn chặn bán quá số lượng tồn kho khi nhiều người mua cùng lúc.

### Function: `placeOrder`
Vị trí: `web-backend/.../OrderController.java`

| Dòng Code (Tóm tắt) | Ý nghĩa & Giải thích Syntax |
| :--- | :--- |
| `@Transactional` | **Transaction Management:** Đảm bảo tính nguyên vẹn dữ liệu. Nếu có lỗi (VD: hết hàng giữa chừng), toàn bộ các thay đổi trước đó (như tạo Customer, lưu Order) sẽ bị hủy bỏ (Rollback). |
| `productRepository.findById(id)` | **Server-side Validation:** Luôn lấy thông tin sản phẩm (đặc biệt là **GÍA**) từ Database hiện tại, không tin tưởng dữ liệu giá gửi từ Frontend. |
| `productRepository.decrementStock(id, qty)` | **Atomic Update:** Thực hiện trừ tồn kho ngay trong 1 câu lệnh SQL (`UPDATE products SET stock = stock - ? ...`). Database sẽ tự động khóa dòng này, đảm bảo không có 2 người cùng trừ kho về âm. |
| `if (rowsUpdated == 0)` | **Concurrency Check:** Nếu kết quả update trả về 0, nghĩa là điều kiện `stock >= quantity` không thỏa mãn. Tức là kho không đủ hàng. |
| `throw new RuntimeException(...)` | **Trigger Rollback:** Ném lỗi để `@Transactional` bắt được và thực hiện hoàn tác (rollback) giao dịch. |

### Logic Flow:
1.  **Validate Input:** Kiểm tra SĐT, Email, Tên.
2.  **Customer Check:** Tìm hoặc tạo Customer mới. Nếu trùng SĐT/Email thì báo lỗi cụ thể.
3.  **Stock & Price Check (Vòng lặp từng sản phẩm):**
    *   Lấy sản phẩm từ DB.
    *   Trừ tồn kho (Atomic). Nếu thất bại -> **LỖI NGAY LẬP TỨC**.
    *   Lấy giá từ DB -> Thêm vào đơn hàng.
4.  **Save Order:** Lưu đơn hàng hoàn chỉnh xuống DB.

---

## 2. Desktop App: Quản lý Kết nối & Tài nguyên (AppOrderDao.java)

### Mục tiêu:
- **Fix Bug 3 (Resource Leak):** Tránh việc kết nối Database không được đóng, gây treo ứng dụng sau một thời gian.
- **Fix Bug 6 (Fragile Logic):** Sử dụng ID thay vì Tên để thao tác dữ liệu.

### Function: `createAppOrder`
Vị trí: `mavenproject1/.../dao/AppOrderDao.java`

| Dòng Code (Tóm tắt) | Ý nghĩa & Giải thích Syntax |
| :--- | :--- |
| `try (Connection conn = ...)` | **Try-with-resources:** Cú pháp của Java 7+. Đảm bảo biến `conn` sẽ TỰ ĐỘNG được đóng (`close()`) khi khối try kết thúc, dù có lỗi hay không. Thay thế cho `finally { conn.close(); }` dễ quên. |
| `conn.setAutoCommit(false)` | **Manual Transaction logic:** Tắt chế độ tự động lưu để ta có thể kiểm soát việc `commit` hoặc `rollback` bằng tay. |
| `Statement.RETURN_GENERATED_KEYS` | **Retrieve ID:** Yêu cầu JDBC trả về ID tự tăng (Primary Key) của dòng vừa insert. Cần thiết để lưu vào bảng chi tiết `app_order_details`. |
| `ps.addBatch()` / `ps.executeBatch()` | **Batch Processing:** Gom nhiều lệnh Insert lại và gửi một lần duy nhất xuống Database. Tăng hiệu năng so với việc Insert từng dòng. |
| `conn.rollback()` | **Error Handling:** Nếu có bất kỳ lỗi SQL nào xảy ra, lệnh này sẽ đưa Database về trạng thái trước khi bắt đầu transaction. |

### Logic Flow:
1.  Mở kết nối (Try-with-resources).
2.  Bắt đầu Transaction.
3.  Insert bảng `app_order` -> Lấy `order_id` vừa tạo.
4.  Insert bảng `app_order_details` (dùng `order_id` ở trên + `product_id`).
5.  Update kho (`products`) dựa trên `product_id`.
6.  `commit()` nếu thành công, `rollback()` nếu thất bại.

---

## 3. Desktop App: Sử dụng ID trong Giao diện (OrderPanel.java)

### Mục tiêu:
- **Fix Bug 6:** Tránh lỗi khi sản phẩm trùng tên hoặc đổi tên.
- **Improvement:** Truyền đúng ID xuống DAO.

### Function: `handleAddToCart` & `handleCreateOrder`
Vị trí: `mavenproject1/.../view/OrderPanel.java`

| Đoạn Code | Ý nghĩa |
| :--- | :--- |
| `loadedProducts.get(row).getId()` | **Data Integrity:** Lấy ID từ object gốc đã load từ DB, thay vì lấy text từ giao diện. |
| `cartModel.addRow(..., productId)` | **Hidden Data:** Thêm ID vào bảng giỏ hàng (Table Model) ở cột thứ 4. |
| `getColumn(3).setMinWidth(0)` | **UI Hiding:** Ẩn cột chứa ID đi để người dùng không thấy (nhưng code vẫn đọc được). |
| `(Integer) cartModel.getValueAt(i, 3)` | **Retrieval:** Khi tạo đơn hàng, lấy ID ẩn này ra để gửi xuống `AppOrderDao`. |

### Logic Flow:
1.  **Load Products:** Tải danh sách Product (gồm cả ID) vào bộ nhớ `loadedProducts`.
2.  **Add to Cart:** Khi chọn, tìm ID tương ứng từ `loadedProducts` -> Thêm vào cột ẩn của `cartTable`.
3.  **Create Order:** Duyệt qua `cartTable`, lấy ID từ cột ẩn -> Tạo `OrderDetails` với đúng ID -> Gọi `AppOrderDao`.
