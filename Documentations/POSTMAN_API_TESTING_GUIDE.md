# 📮 Hướng Dẫn Test API với Postman

## 🎯 Mục đích
Tài liệu này hướng dẫn cách sử dụng Postman để test API theo chuẩn RESTful API Design (Section 5.5)

## 📋 Danh sách API Endpoints

### **Base URL**
- Development: `http://localhost:8081`
- Mobile/Network: `http://192.168.1.11:8081`

---

## 1️⃣ PRODUCT APIs (`/api/products`)

### 1.1. GET - Lấy tất cả sản phẩm

**Endpoint:** `GET /api/products`

**Postman Setup:**
```
Method: GET
URL: http://localhost:8081/api/products
Headers: (không cần)
Body: (không cần)
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Coca Cola",
    "price": 15000.0,
    "stock": 100,
    "description": "Nước ngọt có gas",
    "imageUrl": "/uploads/product_1_coca.jpg"
  },
  {
    "id": 2,
    "name": "Pepsi",
    "price": 14000.0,
    "stock": 80,
    "description": "Nước ngọt có gas",
    "imageUrl": "/uploads/product_2_pepsi.jpg"
  }
]
```

**Test Cases:**
- ✅ Status code = 200
- ✅ Response body là array
- ✅ Mỗi product có đầy đủ fields: id, name, price, stock, description, imageUrl

---

### 1.2. GET - Lấy sản phẩm theo ID

**Endpoint:** `GET /api/products/{id}`

**Postman Setup:**
```
Method: GET
URL: http://localhost:8081/api/products/1
Headers: (không cần)
Body: (không cần)
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "name": "Coca Cola",
  "price": 15000.0,
  "stock": 100,
  "description": "Nước ngọt có gas",
  "imageUrl": "/uploads/product_1_coca.jpg"
}
```

**Test Cases:**
- ✅ Status code = 200 (nếu ID tồn tại)
- ✅ Status code = 404 (nếu ID không tồn tại)
- ✅ Response body có đầy đủ thông tin product

**Ví dụ lỗi (404 Not Found):**
```
Method: GET
URL: http://localhost:8081/api/products/9999
Response: 404 Not Found (empty body)
```

---

### 1.3. PUT - Cập nhật thông tin sản phẩm

**Endpoint:** `PUT /api/products/{id}`

**Postman Setup:**
```
Method: PUT
URL: http://localhost:8081/api/products/1
Headers:
  Content-Type: application/json
Body (raw JSON):
```

```json
{
  "name": "Coca Cola 330ml",
  "price": 16000,
  "stock": 95,
  "description": "Nước ngọt có gas, lon 330ml"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "name": "Coca Cola 330ml",
  "price": 16000.0,
  "stock": 95,
  "description": "Nước ngọt có gas, lon 330ml",
  "imageUrl": "/uploads/product_1_coca.jpg"
}
```

**Test Cases:**
- ✅ Status code = 200 (update thành công)
- ✅ Status code = 404 (ID không tồn tại)
- ✅ Giá trị được update chính xác
- ✅ imageUrl không bị thay đổi (update riêng)

---

### 1.4. PUT - Cập nhật stock sản phẩm

**Endpoint:** `PUT /api/products/{id}/stock`

**Postman Setup:**
```
Method: PUT
URL: http://localhost:8081/api/products/1/stock?stock=150
Headers: (không cần)
Body: (không cần)
Params:
  stock: 150
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "productId": 1,
  "newStock": 150,
  "productName": "Coca Cola"
}
```

**Test Cases:**
- ✅ Status code = 200 (update thành công)
- ✅ Status code = 400 (stock âm: `?stock=-10`)
- ✅ Status code = 404 (ID không tồn tại)

---

### 1.5. POST - Upload ảnh sản phẩm

**Endpoint:** `POST /api/products/{id}/image`

**Postman Setup:**
```
Method: POST
URL: http://localhost:8081/api/products/1/image
Headers: (Postman tự động set)
Body: form-data
  Key: file (type: File)
  Value: [Chọn file ảnh từ máy tính]
```

**Expected Response (200 OK):**
```json
{
  "imageUrl": "/uploads/product_1_1732512345678_coca.jpg"
}
```

**Test Cases:**
- ✅ Status code = 200 (upload thành công)
- ✅ Status code = 400 (không chọn file)
- ✅ Status code = 404 (product ID không tồn tại)
- ✅ File được lưu vào thư mục `uploads/`
- ✅ Database được update với imageUrl mới

---

### 1.6. GET - Kiểm tra ảnh sản phẩm

**Endpoint:** `GET /api/products/{id}/check-image`

**Postman Setup:**
```
Method: GET
URL: http://localhost:8081/api/products/1/check-image
```

**Expected Response:**
```json
{
  "productId": 1,
  "productName": "Coca Cola",
  "imageUrl": "/uploads/product_1_coca.jpg",
  "filename": "uploads/product_1_coca.jpg",
  "filePath": "uploads\\product_1_coca.jpg",
  "fileExists": true,
  "message": "Image file exists"
}
```

---

## 2️⃣ ORDER APIs (`/api/orders`)

### 2.1. POST - Tạo đơn hàng mới

**Endpoint:** `POST /api/orders`

**Postman Setup:**
```
Method: POST
URL: http://localhost:8081/api/orders
Headers:
  Content-Type: application/json
Body (raw JSON):
```

```json
{
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "nguyenvana@gmail.com",
  "tableNumber": "Table 5",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 15000
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 14000
    }
  ]
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "orderId": 123
}
```

**Test Cases:**

#### ✅ **Test Case 1: Đơn hàng mới hợp lệ**
- Sử dụng phone/email chưa tồn tại
- Status code = 200
- Response có orderId

#### ❌ **Test Case 2: Phone đã tồn tại với thông tin khác**
```json
{
  "name": "Tran Van B",
  "phone": "0901234567",  // Phone đã tồn tại
  "email": "tranvanb@gmail.com",
  "tableNumber": "Table 3",
  "items": [...]
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "error": "Số điện thoại này đã được đăng ký với thông tin khác:\n• Tên: Nguyen Van A\n• Email: nguyenvana@gmail.com\n• SĐT: 0901234567\n\nVui lòng sử dụng đúng thông tin đã đăng ký hoặc dùng số điện thoại khác.",
  "conflictType": "phone_mismatch",
  "existingCustomer": {
    "name": "Nguyen Van A",
    "email": "nguyenvana@gmail.com",
    "phone": "0901234567"
  }
}
```

#### ❌ **Test Case 3: Email đã tồn tại với phone khác**
```json
{
  "name": "Nguyen Van A",
  "phone": "0987654321",  // Phone mới
  "email": "nguyenvana@gmail.com",  // Email đã tồn tại
  "tableNumber": "Table 3",
  "items": [...]
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "error": "Email này đã được đăng ký với số điện thoại khác:...",
  "conflictType": "email_mismatch",
  "existingCustomer": {...}
}
```

#### ❌ **Test Case 4: Validation lỗi**

**4.1. Phone không hợp lệ:**
```json
{
  "phone": "123456789",  // Sai format
  "name": "Test",
  "email": "test@gmail.com",
  "tableNumber": "Table 1",
  "items": [...]
}
```
Response: 400 - "Invalid Vietnamese phone number..."

**4.2. Email không hợp lệ:**
```json
{
  "phone": "0901234567",
  "name": "Test",
  "email": "invalid-email",  // Sai format
  "tableNumber": "Table 1",
  "items": [...]
}
```
Response: 400 - "Invalid email format..."

**4.3. Tên quá ngắn:**
```json
{
  "phone": "0901234567",
  "name": "A",  // < 2 ký tự
  "email": "test@gmail.com",
  "tableNumber": "Table 1",
  "items": [...]
}
```
Response: 400 - "Name must be at least 2 characters long"

**4.4. Stock không đủ:**
```json
{
  "phone": "0901234567",
  "name": "Test User",
  "email": "test@gmail.com",
  "tableNumber": "Table 1",
  "items": [
    {
      "productId": 1,
      "quantity": 999999,  // Vượt quá stock
      "price": 15000
    }
  ]
}
```
Response: 400 - "Insufficient stock for product..."

---

### 2.2. GET - Lấy đơn hàng theo User ID

**Endpoint:** `GET /api/orders/user/{userId}`

**Postman Setup:**
```
Method: GET
URL: http://localhost:8081/api/orders/user/1
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 123,
    "customer": {
      "id": 1,
      "name": "Nguyen Van A",
      "phone": "0901234567",
      "email": "nguyenvana@gmail.com"
    },
    "staff": {
      "id": 1,
      "userName": "admin"
    },
    "orderDate": "2025-11-25T14:30:00",
    "status": "Pending",
    "total": 44000.0,
    "tableNumber": "Table 5",
    "items": [...]
  }
]
```

---

## 3️⃣ USER APIs (`/api/users`)

### 3.1. POST - Đăng nhập

**Endpoint:** `POST /api/users/login`

**Postman Setup:**
```
Method: POST
URL: http://localhost:8081/api/users/login
Headers:
  Content-Type: application/json
Body (raw JSON):
```

```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "userName": "admin",
  "role": "admin"
}
```

**Test Cases:**

#### ✅ **Test Case 1: Login thành công**
- Email và password đúng
- Status code = 200
- Response có userName và role

#### ❌ **Test Case 2: Sai password**
```json
{
  "email": "admin@example.com",
  "password": "wrongpassword"
}
```
Response: 401 - "Invalid email or password"

#### ❌ **Test Case 3: Email không tồn tại**
```json
{
  "email": "notfound@example.com",
  "password": "admin123"
}
```
Response: 401 - "Invalid email or password"

#### ❌ **Test Case 4: Email không hợp lệ**
```json
{
  "email": "invalid-email",
  "password": "admin123"
}
```
Response: 400 - "Invalid email format..."

#### ❌ **Test Case 5: Password trống**
```json
{
  "email": "admin@example.com",
  "password": ""
}
```
Response: 400 - "Password cannot be empty"

---

## 🔧 Cấu hình Postman Collection

### Bước 1: Tạo Environment

1. Click vào **Environments** tab
2. Click **Create Environment**
3. Tên: `SaleApp Development`
4. Thêm variables:

```
Variable       | Initial Value              | Current Value
-----------------------------------------------------------------------------
base_url       | http://localhost:8081      | http://localhost:8081
base_url_mobile| http://192.168.1.11:8081   | http://192.168.1.11:8081
```

### Bước 2: Tạo Collection

1. Click **New** > **Collection**
2. Tên: `SaleApp REST API`
3. Thêm các folder:
   - 📁 Products
   - 📁 Orders
   - 📁 Users

### Bước 3: Thêm Requests

#### Trong folder **Products**:
- GET All Products
- GET Product By ID
- PUT Update Product
- PUT Update Stock
- POST Upload Image
- GET Check Image

#### Trong folder **Orders**:
- POST Create Order
- GET Orders By User

#### Trong folder **Users**:
- POST Login

### Bước 4: Sử dụng Variables

Thay vì hardcode URL, sử dụng:
```
{{base_url}}/api/products
{{base_url}}/api/orders
{{base_url}}/api/users/login
```

---

## 📊 Test Scripts (Postman Tests)

### Thêm vào tab "Tests" của mỗi request:

#### GET All Products
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});

pm.test("Products have required fields", function () {
    var jsonData = pm.response.json();
    if (jsonData.length > 0) {
        pm.expect(jsonData[0]).to.have.property('id');
        pm.expect(jsonData[0]).to.have.property('name');
        pm.expect(jsonData[0]).to.have.property('price');
        pm.expect(jsonData[0]).to.have.property('stock');
    }
});
```

#### POST Create Order (Success)
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has orderId", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('orderId');
    pm.expect(jsonData.orderId).to.be.a('number');
});

pm.test("Success is true", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
});
```

#### POST Create Order (Phone Conflict)
```javascript
pm.test("Status code is 400", function () {
    pm.response.to.have.status(400);
});

pm.test("Response has conflictType", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('conflictType');
    pm.expect(jsonData.conflictType).to.equal('phone_mismatch');
});

pm.test("Response has existing customer info", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('existingCustomer');
    pm.expect(jsonData.existingCustomer).to.have.property('name');
    pm.expect(jsonData.existingCustomer).to.have.property('phone');
    pm.expect(jsonData.existingCustomer).to.have.property('email');
});
```

#### POST Login (Success)
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has user info", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
    pm.expect(jsonData).to.have.property('userName');
    pm.expect(jsonData).to.have.property('role');
});
```

---

## 🎬 Demo Flow - Kịch bản Test Đầy Đủ

### Scenario: Khách hàng đặt hàng lần đầu và lần thứ 2

#### **Step 1:** Xem danh sách sản phẩm
```
GET {{base_url}}/api/products
→ Lấy danh sách, chọn productId để đặt hàng
```

#### **Step 2:** Đặt hàng lần đầu (Customer mới)
```
POST {{base_url}}/api/orders
Body:
{
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "nguyenvana@gmail.com",
  "tableNumber": "Table 5",
  "items": [
    {"productId": 1, "quantity": 2, "price": 15000}
  ]
}
→ Kỳ vọng: 200 OK, orderId được tạo
→ Customer mới được tạo trong database
```

#### **Step 3:** Kiểm tra stock đã giảm
```
GET {{base_url}}/api/products/1
→ Kỳ vọng: stock đã giảm đi 2
```

#### **Step 4:** Đặt hàng lần 2 với đúng thông tin
```
POST {{base_url}}/api/orders
Body:
{
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "nguyenvana@gmail.com",
  "tableNumber": "Table 3",
  "items": [
    {"productId": 2, "quantity": 1, "price": 14000}
  ]
}
→ Kỳ vọng: 200 OK, sử dụng customer đã tồn tại
```

#### **Step 5:** Thử đặt hàng với phone trùng nhưng thông tin khác
```
POST {{base_url}}/api/orders
Body:
{
  "name": "Tran Van B",  // Tên khác
  "phone": "0901234567",  // Phone trùng
  "email": "tranvanb@gmail.com",  // Email khác
  "tableNumber": "Table 1",
  "items": [
    {"productId": 1, "quantity": 1, "price": 15000}
  ]
}
→ Kỳ vọng: 400 Bad Request
→ conflictType: "phone_mismatch"
→ Hiển thị thông tin customer đã đăng ký
```

#### **Step 6:** Thử đặt hàng với email trùng nhưng phone khác
```
POST {{base_url}}/api/orders
Body:
{
  "name": "Nguyen Van A",
  "phone": "0987654321",  // Phone khác
  "email": "nguyenvana@gmail.com",  // Email trùng
  "tableNumber": "Table 2",
  "items": [
    {"productId": 1, "quantity": 1, "price": 15000}
  ]
}
→ Kỳ vọng: 400 Bad Request
→ conflictType: "email_mismatch"
→ Hiển thị thông tin customer đã đăng ký
```

---

## 📝 Checklist Test API

### Products API
- [ ] GET /api/products - Lấy tất cả sản phẩm
- [ ] GET /api/products/{id} - Lấy sản phẩm theo ID (có và không tồn tại)
- [ ] PUT /api/products/{id} - Update thông tin sản phẩm
- [ ] PUT /api/products/{id}/stock - Update stock
- [ ] POST /api/products/{id}/image - Upload ảnh
- [ ] GET /api/products/{id}/check-image - Kiểm tra ảnh

### Orders API
- [ ] POST /api/orders - Đặt hàng thành công (customer mới)
- [ ] POST /api/orders - Đặt hàng thành công (customer đã tồn tại)
- [ ] POST /api/orders - Lỗi phone conflict
- [ ] POST /api/orders - Lỗi email conflict
- [ ] POST /api/orders - Validation errors (phone, email, name)
- [ ] POST /api/orders - Lỗi stock không đủ
- [ ] GET /api/orders/user/{userId} - Lấy đơn hàng theo user

### Users API
- [ ] POST /api/users/login - Login thành công
- [ ] POST /api/users/login - Sai password
- [ ] POST /api/users/login - Email không tồn tại
- [ ] POST /api/users/login - Email không hợp lệ
- [ ] POST /api/users/login - Password trống

---

## 🚀 Quick Start

1. **Khởi động Backend:**
```powershell
cd web-backend
./mvnw spring-boot:run
```

2. **Mở Postman**

3. **Import Collection** (optional):
   - File > Import
   - Chọn file `SaleApp_API_Collection.json` (nếu có)

4. **Tạo Environment:**
   - base_url: `http://localhost:8081`

5. **Bắt đầu Test:**
   - Chạy từng request theo thứ tự trong Demo Flow

---

## 📖 API Design Best Practices (Đã áp dụng)

### ✅ RESTful Principles
- ✅ Sử dụng HTTP methods đúng: GET, POST, PUT, DELETE
- ✅ URL semantic: `/api/products`, `/api/orders`, `/api/users`
- ✅ Status codes chuẩn: 200, 400, 401, 404, 500

### ✅ Request/Response Format
- ✅ JSON format cho request và response
- ✅ Content-Type: application/json
- ✅ Consistent field naming (camelCase)

### ✅ Error Handling
- ✅ Descriptive error messages
- ✅ Error codes/types (conflictType)
- ✅ Validation errors với details

### ✅ Security
- ✅ Input validation (phone, email, name)
- ✅ SQL injection prevention
- ✅ XSS prevention (sanitization)
- ✅ BCrypt password hashing

### ✅ Data Integrity
- ✅ Customer uniqueness check (phone + email)
- ✅ Stock validation before order
- ✅ Transaction handling
- ✅ Foreign key relationships

---

## 🎯 Kết luận

Hệ thống API đã được thiết kế theo chuẩn RESTful với:
- ✅ **Validation đầy đủ** trên mọi input
- ✅ **Error handling chi tiết** với messages rõ ràng
- ✅ **Data consistency** với customer uniqueness checks
- ✅ **Security** với input sanitization và password encryption
- ✅ **Proper HTTP status codes** và response format

Sử dụng Postman để test toàn bộ flow và verify API hoạt động đúng theo spec! 🚀
