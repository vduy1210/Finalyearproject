# PHÂN TÍCH CÁC HÀM QUAN TRỌNG (KEY FUNCTIONS ANALYSIS)

Tài liệu này "mổ xẻ" các hàm quan trọng nhất trong dự án theo phong cách: Lý thuyết -> Code -> Tác dụng -> Liên tưởng.

---

## 1. DESKTOP APP: Đăng nhập (`UserDAO.authenticateUser`)

### 📖 Lý thuyết (Logic Flow)
1.  Nhận `username` và `password` từ người dùng nhập.
2.  Kết nối Database, tìm dòng dữ liệu có `userName` trùng khớp.
3.  Lấy mật khẩu đã mã hóa (hashed password) từ Database ra.
4.  Dùng thuật toán BCrypt để so sánh mật khẩu nhập vào (plain) với mật khẩu mã hóa (hashed).

### 💻 Code đắt giá (Key Code)
```java
// File: dao/UserDAO.java
public boolean authenticateUser(String username, String password) {
    String sql = "SELECT password FROM users WHERE userName = ?";
    // ... (kết nối DB) ...
    if (rs.next()) {
        String hashedPassword = rs.getString("password");
        // QUAN TRỌNG: Không so sánh bằng dấu == hoặc .equals()
        return PasswordUtil.checkPassword(password, hashedPassword);
    }
    return false;
}
```

### 🎯 Tác dụng (Purpose)
Đảm bảo chỉ người có tài khoản hợp lệ mới được vào hệ thống. Bảo vệ hệ thống khỏi người lạ.

### 💡 Liên tưởng (Analogy)
Giống như **"Ổ khóa vân tay"**.
*   Database không lưu ngón tay của bạn (mật khẩu thô), mà lưu "bản đồ vân tay" (mật khẩu mã hóa).
*   Khi bạn đặt tay vào, máy sẽ quét và so sánh với bản đồ đã lưu. Nếu khớp thì mở cửa.

---

## 2. DESKTOP APP: Tạo đơn hàng (`AppOrderDao.createAppOrder`)

### 📖 Lý thuyết (Logic Flow)
1.  Tắt chế độ "tự động lưu" của Database (`setAutoCommit(false)`) để bắt đầu Giao dịch (Transaction).
2.  Lưu thông tin chung của đơn hàng vào bảng `app_order` -> Lấy được `order_id` vừa sinh ra.
3.  Dùng `order_id` đó để lưu từng món ăn vào bảng `app_order_details`.
4.  Nếu mọi thứ êm đẹp -> `commit()` (Lưu thật).
5.  Nếu có bất kỳ lỗi gì (ví dụ: đang lưu món thứ 2 thì mất điện) -> `rollback()` (Hủy hết, coi như chưa từng bán đơn này).

### 💻 Code đắt giá (Key Code)
```java
// File: dao/AppOrderDao.java
public boolean createAppOrder(Order order, List<OrderDetails> details) {
    try {
        conn.setAutoCommit(false); // 1. Bắt đầu giao dịch
        
        // ... (Lưu order và lấy ID) ...
        
        // ... (Lưu từng món ăn vào order_details) ...
        
        conn.commit(); // 2. Chốt đơn (Lưu vĩnh viễn)
        return true;
    } catch (Exception e) {
        conn.rollback(); // 3. Có biến! Hủy hết!
        return false;
    }
}
```

### 🎯 Tác dụng (Purpose)
Đảm bảo tính **Toàn vẹn dữ liệu (Data Integrity)**. Không bao giờ có chuyện "Đơn hàng đã tạo nhưng không có món nào bên trong".

### 💡 Liên tưởng (Analogy)
Giống như **"Chuyển tiền ngân hàng"**.
*   Khi bạn chuyển tiền cho A, ngân hàng phải làm 2 việc: (1) Trừ tiền của bạn, (2) Cộng tiền cho A.
*   Nếu làm xong (1) mà mất mạng, không làm được (2), ngân hàng phải `rollback` (hoàn lại tiền cho bạn), chứ không được để tiền "bốc hơi".

---

## 3. WEB BACKEND: Lấy danh sách món (`ProductController.getAllProducts`)

### 📖 Lý thuyết (Logic Flow)
1.  Frontend gọi vào đường dẫn `/api/products`.
2.  Controller nhận yêu cầu, gọi sang Repository.
3.  Repository (nhờ JPA) tự động sinh câu lệnh SQL `SELECT * FROM products`.
4.  Database trả về dữ liệu dạng bảng.
5.  JPA chuyển đổi bảng đó thành danh sách các đối tượng Java (`List<Product>`).
6.  Controller chuyển danh sách đó thành JSON và trả về cho Frontend.

### 💻 Code đắt giá (Key Code)
```java
// File: controller/ProductController.java
@GetMapping
public List<Product> getAllProducts() {
    // Chỉ 1 dòng nhưng làm tất cả các bước 3, 4, 5 ở trên
    return productRepository.findAll(); 
}
```

### 🎯 Tác dụng (Purpose)
Cung cấp thực đơn cho khách hàng xem trên web.

### 💡 Liên tưởng (Analogy)
Giống như **"Gọi món qua ứng dụng"**.
*   Khách (Frontend) bấm nút "Xem Menu".
*   Ứng dụng (Controller) chạy vào bếp (Database), lấy danh sách món, chụp ảnh lại (JSON) rồi gửi ra cho khách xem.

---

## 4. WEB FRONTEND: Quản lý giỏ hàng (`Cart.js`)

### 📖 Lý thuyết (Logic Flow)
1.  Khởi tạo giỏ hàng là một mảng rỗng `[]` trong `useState`.
2.  Khi bấm "Add to Cart":
    *   Kiểm tra xem món đó đã có trong giỏ chưa.
    *   Nếu có rồi -> Tăng số lượng lên 1.
    *   Nếu chưa -> Thêm món đó vào mảng với số lượng = 1.
3.  Cập nhật lại State -> Giao diện tự động hiển thị số lượng mới.

### 💻 Code đắt giá (Key Code)
```javascript
// File: App.js (hoặc nơi chứa logic giỏ hàng)
const addToCart = (product) => {
    setCartItems(prevItems => {
        // Tìm xem món này đã có trong giỏ chưa
        const exist = prevItems.find(x => x.id === product.id);
        if (exist) {
            // Có rồi thì tăng số lượng
            return prevItems.map(x => x.id === product.id ? {...exist, qty: exist.qty + 1} : x);
        } else {
            // Chưa có thì thêm mới
            return [...prevItems, {...product, qty: 1}];
        }
    });
};
```

### 🎯 Tác dụng (Purpose)
Giúp khách hàng gom các món muốn mua lại một chỗ để thanh toán một lần.

### 💡 Liên tưởng (Analogy)
Giống như **"Cái giỏ siêu thị"**.
*   Bạn nhặt một gói mì (Add to Cart).
*   Thấy ngon, bạn nhặt thêm gói nữa (Tăng số lượng).
*   Cái giỏ (State) sẽ giữ tất cả những gì bạn chọn cho đến khi ra quầy thu ngân (Checkout).

---

## 5. DESKTOP APP: Báo cáo doanh thu (`RevenueReportPanel.java`)

### 📖 Lý thuyết (Logic Flow)
1.  Người dùng chọn ngày bắt đầu và ngày kết thúc -> Bấm "Apply".
2.  Hệ thống kết nối Database, chạy câu lệnh `SELECT` để tính tổng tiền trong khoảng thời gian đó.
3.  Kết quả đổ vào `JTable` để hiển thị trên màn hình.
4.  Khi bấm "Export Excel", hệ thống dùng thư viện **Apache POI** để tạo một file Excel ảo trong RAM.
5.  Ghi từng dòng dữ liệu từ `JTable` vào file Excel ảo đó.
6.  Lưu file Excel ảo ra ổ cứng máy tính.

### 💻 Code đắt giá (Key Code)
```java
// File: view/RevenueReportPanel.java
private void exportExcel() {
    // Tạo file Excel ảo
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
        XSSFSheet sheet = workbook.createSheet("Revenue");
        
        // Chép dữ liệu từ bảng (JTable) vào Excel
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            Row row = sheet.createRow(r + 1);
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                Object val = tableModel.getValueAt(r, c);
                row.createCell(c).setCellValue(val.toString());
            }
        }
        // ... Lưu ra file ...
    }
}
```

### 🎯 Tác dụng (Purpose)
Giúp chủ cửa hàng nắm bắt tình hình kinh doanh và lưu trữ số liệu để tính toán sổ sách sau này.

### 💡 Liên tưởng (Analogy)
Giống như **"Sao chép danh sách lớp"**.
*   `JTable` là cái bảng điểm treo trên tường.
*   `Apache POI` là cái máy photocopy.
*   `Export Excel` là việc bạn chép lại nội dung trên bảng vào cuốn sổ tay để mang về nhà.

---

## 6. WEB BACKEND: Upload ảnh sản phẩm (`ProductController.uploadProductImage`)

### 📖 Lý thuyết (Logic Flow)
1.  Frontend gửi file ảnh lên qua form `multipart/form-data`.
2.  Backend nhận file dưới dạng đối tượng `MultipartFile`.
3.  Tạo tên file mới (để tránh trùng tên) = `UUID` + đuôi file gốc (ví dụ: `a1b2-c3d4.jpg`).
4.  Lưu file đó vào thư mục `uploads` trên ổ cứng server.
5.  Lưu **đường dẫn** (URL) của file đó vào Database (chứ không lưu cả file ảnh vào DB vì rất nặng).

### 💻 Code đắt giá (Key Code)
```java
// File: controller/ProductController.java
@PostMapping("/{id}/image")
public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    // 1. Tạo tên file ngẫu nhiên để không bị trùng
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    
    // 2. Lưu file vào ổ cứng
    Files.copy(file.getInputStream(), Paths.get("uploads").resolve(fileName));
    
    // 3. Chỉ lưu tên file vào Database
    product.setImageUrl("/uploads/" + fileName);
    productRepository.save(product);
}
```

### 🎯 Tác dụng (Purpose)
Cho phép hiển thị hình ảnh minh họa món ăn trực quan trên web.

### 💡 Liên tưởng (Analogy)
Giống như **"Làm thẻ căn cước"**.
*   Bạn nộp ảnh chân dung (Upload file).
*   Công an không dán ảnh gốc của bạn vào hồ sơ giấy (Database), mà họ lưu ảnh vào kho lưu trữ (Folder uploads), và trên hồ sơ chỉ ghi "Mã số ảnh: 12345" (Lưu đường dẫn). Khi cần xem mặt, họ tra mã số đó để tìm ảnh trong kho.

---

## 7. WEB FRONTEND: Đặt hàng (`Cart.js`)

### 📖 Lý thuyết (Logic Flow)
1.  Người dùng điền tên, số điện thoại, chọn bàn -> Bấm "Confirm".
2.  Frontend gom hết thông tin (khách hàng + danh sách món trong giỏ) thành một gói tin JSON.
3.  Dùng hàm `fetch` gửi gói tin đó đến địa chỉ `POST /api/orders` của Backend.
4.  Chờ Backend trả lời:
    *   Nếu OK (200): Xóa giỏ hàng, hiện thông báo thành công.
    *   Nếu Lỗi (400/500): Hiện thông báo lỗi đỏ lòm.

### 💻 Code đắt giá (Key Code)
```javascript
// File: components/Cart.js
async function handleOrder() {
    // 1. Chuẩn bị gói hàng (Payload)
    const payload = {
        name: name,
        phone: phone,
        items: cart.map(item => ({ id: item.id, qty: item.quantity }))
    };

    // 2. Gửi đi (Ship hàng)
    const res = await fetch('http://localhost:8081/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    // 3. Nhận phản hồi
    if (res.ok) {
        alert("Đặt hàng thành công!");
        clearCart(); // Xóa giỏ hàng
    }
}
```

### 🎯 Tác dụng (Purpose)
Chuyển đổi "ý định mua hàng" của khách (trên giao diện) thành "đơn hàng thực tế" (trong hệ thống).

### 💡 Liên tưởng (Analogy)
Giống như **"Gửi thư tay"**.
*   `payload`: Là nội dung bức thư (Tôi muốn mua gà, cá...).
*   `fetch`: Là bưu điện.
*   `POST`: Là dịch vụ gửi thư bảo đảm.
*   `res.ok`: Là giấy báo phát thành công (đã đến tay người nhận).

---

## 8. DESKTOP APP: Quản lý sản phẩm (`ProductManagerPanel.java`)

### 📖 Lý thuyết (Logic Flow)
1.  Khi mở màn hình, gọi `ProductDAO.getAllProducts()` để lấy dữ liệu -> Đổ vào `DefaultTableModel` -> Hiện lên bảng.
2.  Khi bấm "Add": Mở hộp thoại nhập -> Lưu vào DB -> Gọi lại hàm load dữ liệu để bảng tự cập nhật dòng mới.
3.  Khi bấm "Delete": Lấy ID của dòng đang chọn -> Gọi `ProductDAO.delete(id)` -> Xóa dòng đó khỏi Model -> Bảng tự biến mất dòng đó.

### 💻 Code đắt giá (Key Code)
```java
// File: view/ProductManagerPanel.java
public void loadProductData() {
    // 1. Xóa sạch bảng cũ
    tableModel.setRowCount(0);
    
    // 2. Lấy dữ liệu mới từ DB
    List<Object[]> list = ProductDAO.getAllProducts();
    
    // 3. Đổ vào bảng
    for (Object[] row : list) {
        tableModel.addRow(row);
    }
}
```

### 🎯 Tác dụng (Purpose)
Giúp chủ quán thêm món mới, sửa giá, hoặc xóa món không bán nữa. Đây là chức năng CRUD (Create, Read, Update, Delete) cơ bản nhất.

### 💡 Liên tưởng (Analogy)
Giống như **"Viết bảng thực đơn quán ăn"**.
*   `loadProductData`: Là việc bạn xóa bảng phấn cũ đi và viết lại toàn bộ thực đơn mới nhất lên đó mỗi sáng.
*   `tableModel`: Là nội dung phấn viết trên bảng.

---
**LỜI KHUYÊN:**
Học theo cách này: **Hiểu luồng đi -> Nhớ từ khóa trong code -> Hiểu tác dụng -> Liên tưởng thực tế**. Đừng cố học thuộc lòng từng dòng code.
