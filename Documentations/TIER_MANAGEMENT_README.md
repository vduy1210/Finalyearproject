# Customer Tier Management System

## Tổng quan
Hệ thống quản lý tier (hạng) khách hàng dựa trên điểm tích lũy (accumulatedPoint). Mỗi tier có mức giảm giá (discount) khác nhau.

**Đặc điểm:** Tier được tính toán ĐỘNG (dynamically) dựa trên `accumulatedPoint` - KHÔNG cần thêm cột vào bảng `customers`.

## Cấu trúc Database

### Bảng `customer_tiers` (MỚI - cần tạo)
```sql
CREATE TABLE customer_tiers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tier_name VARCHAR(50) NOT NULL UNIQUE,
    min_points FLOAT NOT NULL,
    max_points FLOAT NOT NULL,
    discount_percent FLOAT NOT NULL DEFAULT 0,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Bảng `customers` (KHÔNG thay đổi)
```sql
-- Bảng hiện tại của bạn - KHÔNG cần thêm cột gì
id int AI PK 
name varchar(100) 
phone varchar(20) 
email varchar(100) 
accumulatedPoint float
```

## Tier mặc định

| Tier     | Min Points | Max Points | Discount | Mô tả                            |
|----------|------------|------------|----------|----------------------------------|
| Bronze   | 0          | 999        | 0%       | New customers                    |
| Silver   | 1,000      | 4,999      | 5%       | 5% discount on all orders        |
| Gold     | 5,000      | 9,999      | 10%      | 10% discount on all orders       |
| Platinum | 10,000     | 999,999    | 15%      | 15% discount on all orders       |

## Cài đặt

1. **Chạy SQL script**:
   ```bash
   mysql -u username -p database_name < shopdb/create_customer_tiers_table.sql
   ```

2. **Compile Java classes**:
   - `model/CustomerTier.java`
   - `dao/CustomerTierDAO.java`
   - `view/TierConfigDialog.java`
   - `view/CustomerManagementPanel.java` (đã cập nhật)

## Chức năng

### 1. Configure Tiers (Cấu hình Tier)
- Nhấn nút **"Configure Tiers"** trong Customer Management Panel
- Xem danh sách tất cả các tier
- **Edit**: Chỉnh sửa min/max points, discount % và mô tả
- **Add**: Thêm tier mới
- **Delete**: Xóa tier

### 2. Set Tiers (Làm mới/Tính toán lại Tier)
- Nhấn nút **"Set Tiers"** để làm mới danh sách khách hàng
- Tier sẽ được tính toán tự động dựa vào `accumulatedPoint`
- So sánh với `min_points` và `max_points` của các tier trong bảng `customer_tiers`

### 3. Hiển thị trong bảng
- Cột **"Points"**: Hiển thị accumulated points của khách hàng
- Cột **"Tier"**: Hiển thị tier được tính động (Bronze, Silver, Gold, Platinum,...)

## Quy trình sử dụng

### Lần đầu setup:
1. Chạy SQL script để tạo bảng `customer_tiers` và dữ liệu mặc định:
   ```bash
   mysql -u root -p shopdb < shopdb/create_customer_tiers_table.sql
   ```
2. Mở Customer Management Panel - tier sẽ tự động hiển thị

### Khi muốn thay đổi cấu hình:
1. Nhấn **"Configure Tiers"**
2. Chỉnh sửa min/max points hoặc discount %
3. Đóng dialog - tier sẽ tự động cập nhật theo cấu hình mới

### Khi có khách hàng mới hoặc điểm thay đổi:
- Tier tự động cập nhật mỗi khi load danh sách khách hàng
- Hoặc nhấn **"Set Tiers"** để làm mới ngay lập tức

## Cách hoạt động

### Tính toán Tier động
Tier được tính trong SQL query mỗi khi load danh sách:
```sql
SELECT 
    c.id, c.name, c.accumulatedPoint,
    (SELECT t.tier_name 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) AS tier
FROM customers c
```

### Ưu điểm:
- ✅ Không cần thêm cột vào bảng `customers`
- ✅ Tier luôn chính xác với điểm hiện tại
- ✅ Không cần trigger hoặc stored procedure
- ✅ Dễ bảo trì và mở rộng

## API Classes

### CustomerTier (model)
```java
// Properties
int id
String tierName
float minPoints
float maxPoints
float discountPercent
String description
```

### CustomerTierDAO (dao)
```java
// Methods
List<CustomerTier> getAllTiers()                    // Lấy tất cả tier config
CustomerTier getTierByName(String tierName)         // Lấy tier theo tên
CustomerTier getTierByPoints(float points)          // Lấy tier dựa vào điểm
void updateTier(CustomerTier tier)                  // Cập nhật tier config
void addTier(CustomerTier tier)                     // Thêm tier mới
void deleteTier(int tierId)                         // Xóa tier
float getDiscountForCustomer(int customerId)        // Lấy discount % của khách hàng
String getTierNameForCustomer(int customerId)       // Lấy tên tier của khách hàng
```

### TierConfigDialog (view)
- Dialog để quản lý cấu hình tier
- CRUD operations cho tier
- Table view với ID, Name, Points range, Discount, Description

## Tích hợp với checkout

Để tự động áp dụng discount khi khách hàng mua hàng:

```java
// Lấy discount của khách hàng
CustomerTierDAO tierDAO = new CustomerTierDAO();
float discountPercent = tierDAO.getDiscountForCustomer(customerId);

// Áp dụng discount
float discountAmount = orderTotal * discountPercent / 100;
float finalTotal = orderTotal - discountAmount;

// Cập nhật accumulated points sau khi mua hàng
float earnedPoints = finalTotal / 1000; // Ví dụ: 1000 VND = 1 point
float newPoints = customer.getAccumulatedPoint() + earnedPoints;

// Update trong database
String sql = "UPDATE customers SET accumulatedPoint = ? WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setFloat(1, newPoints);
ps.setInt(2, customerId);
ps.executeUpdate();

// Tier sẽ tự động cập nhật khi load lại customer panel
```

## Ghi chú
- ✅ Tier được tính động - không lưu trong database
- ✅ Không cần modify bảng `customers` hiện có
- ✅ Có thể thêm nhiều tier tùy ý
- ✅ Discount được tính theo phần trăm (%)
- ⚠️ Min/Max points range không được overlap giữa các tier
- ⚠️ Nếu không có tier phù hợp, mặc định là "Bronze"
