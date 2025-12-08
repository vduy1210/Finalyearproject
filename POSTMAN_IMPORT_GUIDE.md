# 🚀 Hướng Dẫn Import & Test API với Postman

## 📦 File đã chuẩn bị

Sử dụng file: **`SaleApp_Postman_Collection.json`**

## ✅ Cách Import vào Postman

### Bước 1: Mở Postman
- Mở ứng dụng Postman Desktop hoặc Postman Web

### Bước 2: Import Collection
1. Click nút **"Import"** (góc trên bên trái)
2. Chọn tab **"File"**
3. Click **"Choose Files"** hoặc kéo thả file `SaleApp_Postman_Collection.json`
4. Click **"Import"**

### Bước 3: Xác nhận Import thành công
Bạn sẽ thấy collection **"SaleApp API"** với 3 folders:
- 📁 1. Products API (5 requests)
- 📁 2. Orders API (7 requests)
- 📁 3. Users API (4 requests)

## 🎯 Cách Test API

### ✨ Chuẩn bị:

1. **Start Backend Server:**
```powershell
cd web-backend
./mvnw spring-boot:run
```

2. **Chờ backend khởi động xong** (khoảng 10-30 giây)
   - Xem log: `Started WebBackendApplication in X seconds`

3. **Verify backend đang chạy:**
   - Mở browser: `http://localhost:8081/api/products`
   - Nếu thấy JSON array → Backend OK ✅

---

## 📋 Test Scenarios

### 🔹 Scenario 1: Test Products API

#### 1.1. Get All Products
```
Request: GET /api/products
Expected: 200 OK, array of products
```

**Steps:**
1. Mở folder **"1. Products API"**
2. Click **"Get All Products"**
3. Click **"Send"**
4. Verify response: Status 200, có danh sách sản phẩm

#### 1.2. Get Product by ID
```
Request: GET /api/products/1
Expected: 200 OK, product object with id=1
```

#### 1.3. Update Product
```
Request: PUT /api/products/1
Body: {name, price, stock, description}
Expected: 200 OK, updated product
```

#### 1.4. Update Stock
```
Request: PUT /api/products/1/stock?stock=150
Expected: 200 OK, {success: true, newStock: 150}
```

#### 1.5. Upload Image
```
Request: POST /api/products/1/image
Body: form-data với file
Expected: 200 OK, {imageUrl: "/uploads/..."}
```

**Steps:**
1. Click **"Upload Product Image"**
2. Chọn tab **"Body"**
3. Trong row "file", click **"Select Files"**
4. Chọn file ảnh từ máy tính (jpg, png)
5. Click **"Send"**
6. Verify: Response có imageUrl

---

### 🔹 Scenario 2: Test Customer Uniqueness (Orders API)

#### 2.1. Đặt hàng lần đầu (Customer mới) ✅

**Request:** `Create Order - New Customer (Success)`

**Expected Response:**
```json
{
  "success": true,
  "orderId": 123
}
```

**Steps:**
1. Mở folder **"2. Orders API"**
2. Click **"Create Order - New Customer (Success)"**
3. Click **"Send"**
4. Verify: Status 200, có orderId
5. **Lưu ý:** Customer với phone `0901234567` và email `nguyenvana@gmail.com` đã được tạo

---

#### 2.2. Đặt hàng lần 2 với cùng thông tin ✅

**Request:** `Create Order - Existing Customer (Success)`

**Expected:** Status 200 - Sử dụng customer đã tồn tại

**Steps:**
1. Click **"Create Order - Existing Customer (Success)"**
2. Click **"Send"**
3. Verify: Status 200, có orderId mới
4. Customer không bị duplicate, sử dụng customer đã có

---

#### 2.3. Test Phone Conflict ❌

**Request:** `Create Order - Phone Conflict (Fail)`

**Body:**
```json
{
  "name": "Tran Van B",  // ← Tên khác
  "phone": "0901234567",  // ← Phone TRÙNG
  "email": "tranvanb@gmail.com",  // ← Email khác
  ...
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "conflictType": "phone_mismatch",
  "error": "Số điện thoại này đã được đăng ký với thông tin khác:...",
  "existingCustomer": {
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com",
    "phone": "0901234567"
  }
}
```

**Steps:**
1. Click **"Create Order - Phone Conflict (Fail)"**
2. Click **"Send"**
3. **Verify:**
   - ✅ Status code = 400
   - ✅ `conflictType` = "phone_mismatch"
   - ✅ `existingCustomer` hiển thị thông tin đã đăng ký
   - ✅ Order **KHÔNG** được tạo

---

#### 2.4. Test Email Conflict ❌

**Request:** `Create Order - Email Conflict (Fail)`

**Body:**
```json
{
  "name": "Nguyen Van A",
  "phone": "0987654321",  // ← Phone KHÁC
  "email": "nguyenvana@gmail.com",  // ← Email TRÙNG
  ...
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "conflictType": "email_mismatch",
  "error": "Email này đã được đăng ký với số điện thoại khác:...",
  "existingCustomer": {
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com",
    "phone": "0901234567"
  }
}
```

**Steps:**
1. Click **"Create Order - Email Conflict (Fail)"**
2. Click **"Send"**
3. **Verify:**
   - ✅ Status = 400
   - ✅ `conflictType` = "email_mismatch"
   - ✅ Hiển thị phone đã đăng ký

---

#### 2.5. Test Invalid Phone ❌

**Request:** `Create Order - Invalid Phone (Fail)`

**Body:** Phone = "123456789" (sai format)

**Expected:** 400 - "Invalid Vietnamese phone number..."

---

#### 2.6. Test Insufficient Stock ❌

**Request:** `Create Order - Insufficient Stock (Fail)`

**Body:** Quantity = 999999 (vượt quá stock)

**Expected:** 400 - "Insufficient stock for product..."

---

### 🔹 Scenario 3: Test Login API

#### 3.1. Login Success ✅
**Request:** `Login - Success`
**Expected:** 200 OK, {success: true, userName, role}

#### 3.2. Wrong Password ❌
**Request:** `Login - Wrong Password (Fail)`
**Expected:** 401 Unauthorized

#### 3.3. Email Not Found ❌
**Request:** `Login - Email Not Found (Fail)`
**Expected:** 401 Unauthorized

#### 3.4. Invalid Email Format ❌
**Request:** `Login - Invalid Email (Fail)`
**Expected:** 400 Bad Request

---

## 📊 Bảng Tổng Hợp Test Cases

| # | Test Case | Expected Result | Status Code |
|---|-----------|-----------------|-------------|
| 1 | GET All Products | Danh sách sản phẩm | 200 |
| 2 | GET Product By ID (exists) | Chi tiết sản phẩm | 200 |
| 3 | GET Product By ID (not exists) | Not Found | 404 |
| 4 | PUT Update Product | Sản phẩm đã update | 200 |
| 5 | PUT Update Stock | Stock mới | 200 |
| 6 | POST Upload Image | imageUrl mới | 200 |
| 7 | POST Create Order (New) | orderId | 200 |
| 8 | POST Create Order (Existing) | orderId | 200 |
| 9 | POST Order - Phone Conflict | Error + existingCustomer | 400 |
| 10 | POST Order - Email Conflict | Error + existingCustomer | 400 |
| 11 | POST Order - Invalid Phone | Validation error | 400 |
| 12 | POST Order - Low Stock | Insufficient stock | 400 |
| 13 | POST Login Success | userName + role | 200 |
| 14 | POST Login Wrong Password | Invalid credentials | 401 |
| 15 | POST Login Email Not Found | Invalid credentials | 401 |
| 16 | POST Login Invalid Email | Validation error | 400 |

---

## 🎬 Demo Flow - Kiểm tra đầy đủ

### Flow: Đặt hàng và validate customer uniqueness

```
Step 1: GET /api/products
        → Lấy productId để đặt hàng

Step 2: POST /api/orders (New Customer)
        Body: {name: "Nguyen Van A", phone: "0901234567", email: "nguyenvana@gmail.com"}
        → ✅ Success, orderId created
        → Customer được tạo trong DB

Step 3: POST /api/orders (Same Info)
        Body: {name: "Nguyen Van A", phone: "0901234567", email: "nguyenvana@gmail.com"}
        → ✅ Success, sử dụng customer đã có

Step 4: POST /api/orders (Phone Conflict)
        Body: {name: "Tran Van B", phone: "0901234567", email: "tranvanb@gmail.com"}
        → ❌ 400 Bad Request
        → conflictType: "phone_mismatch"
        → Hiển thị thông tin customer đã đăng ký

Step 5: POST /api/orders (Email Conflict)
        Body: {name: "Test", phone: "0987654321", email: "nguyenvana@gmail.com"}
        → ❌ 400 Bad Request
        → conflictType: "email_mismatch"
        → Hiển thị phone đã đăng ký

Step 6: POST /api/orders (Invalid Phone)
        Body: {phone: "123456789"}
        → ❌ 400 Bad Request
        → "Invalid Vietnamese phone number..."

Step 7: POST /api/orders (Insufficient Stock)
        Body: {items: [{quantity: 999999}]}
        → ❌ 400 Bad Request
        → "Insufficient stock for product..."
```

---

## 💡 Tips & Tricks

### 1. Xem Response Body
- Sau khi click **"Send"**, kéo xuống xem phần **"Response"**
- Tab **"Pretty"** để xem JSON đẹp
- Tab **"Raw"** để xem text thô

### 2. Check Status Code
- Góc trên bên phải response: **"Status: 200 OK"**
- Màu xanh = Success (200, 201)
- Màu đỏ = Error (400, 401, 404, 500)

### 3. Sửa Request Body
- Click tab **"Body"**
- Chọn **"raw"** và **"JSON"**
- Sửa trực tiếp trong editor
- Click **"Send"** để test

### 4. Thay đổi URL
- Click vào URL bar
- Thay đổi localhost thành IP: `http://192.168.1.11:8081`
- Hoặc thay đổi ID: `/api/products/2`

### 5. Save Response as Example
- Sau khi có response, click **"Save as Example"**
- Để lưu lại response mẫu cho lần sau

---

## 🔧 Troubleshooting

### ❌ Error: "Could not get response"
**Nguyên nhân:** Backend chưa chạy hoặc sai URL

**Giải pháp:**
1. Verify backend đang chạy: `http://localhost:8081/api/products`
2. Check console backend có error không
3. Restart backend: `./mvnw spring-boot:run`

---

### ❌ Error: 404 Not Found
**Nguyên nhân:** Sai URL hoặc ID không tồn tại

**Giải pháp:**
1. Check URL: `/api/products` (không có s thừa)
2. Check ID tồn tại: GET /api/products trước
3. Copy ID chính xác từ response

---

### ❌ Error: 400 Bad Request
**Nguyên nhân:** Request body sai format hoặc validation fail

**Giải pháp:**
1. Check JSON format hợp lệ (không có dấu phẩy thừa)
2. Check field names đúng (phone, email, name)
3. Đọc error message trong response

---

### ❌ Error: 500 Internal Server Error
**Nguyên nhân:** Lỗi backend

**Giải pháp:**
1. Xem console backend để xem stack trace
2. Check database connection
3. Check log files

---

## ✅ Checklist Test Hoàn Chỉnh

- [ ] Import collection thành công
- [ ] Backend đang chạy port 8081
- [ ] Test GET All Products → 200 OK
- [ ] Test GET Product By ID → 200 OK
- [ ] Test PUT Update Product → 200 OK
- [ ] Test POST Upload Image → 200 OK
- [ ] Test POST Create Order (New) → 200 OK
- [ ] Test POST Create Order (Existing) → 200 OK
- [ ] Test POST Order Phone Conflict → 400 Bad Request
- [ ] Test POST Order Email Conflict → 400 Bad Request
- [ ] Test POST Order Invalid Phone → 400 Bad Request
- [ ] Test POST Order Low Stock → 400 Bad Request
- [ ] Test POST Login Success → 200 OK
- [ ] Test POST Login Wrong Password → 401
- [ ] Test POST Login Email Not Found → 401
- [ ] Test POST Login Invalid Email → 400

---

## 🎯 Kết Luận

API của SaleApp đã implement đầy đủ:
- ✅ RESTful design principles
- ✅ Input validation (phone, email, name)
- ✅ Customer uniqueness check (phone + email)
- ✅ Error handling với descriptive messages
- ✅ Proper HTTP status codes
- ✅ Data integrity (stock management)
- ✅ Security (password encryption, input sanitization)

**File để import:** `SaleApp_Postman_Collection.json`

**Total Requests:** 16 (5 Products + 7 Orders + 4 Users)

Happy Testing! 🚀
