# Hướng Dẫn Nhanh - Customer Tier System

## 🚀 Bước 1: Chạy SQL Script

```bash
# Kết nối vào MySQL
mysql -u root -p

# Chọn database
USE shopdb;

# Chạy script tạo bảng tier
SOURCE E:/Final/Finalyearproject/shopdb/create_customer_tiers_table.sql;

# Kiểm tra
SELECT * FROM customer_tiers;
```

Kết quả mong đợi: 4 tier mặc định (Bronze, Silver, Gold, Platinum)

## 🎯 Bước 2: Compile Java Classes

Các file mới đã tạo:
- ✅ `model/CustomerTier.java`
- ✅ `dao/CustomerTierDAO.java`  
- ✅ `view/TierConfigDialog.java`
- ✅ `view/CustomerManagementPanel.java` (đã cập nhật)

Compile trong IDE hoặc command line:
```bash
cd e:\Final\Finalyearproject\mavenproject1
javac -d target/classes -cp "lib/*" src/main/java/model/CustomerTier.java
javac -d target/classes -cp "lib/*" src/main/java/dao/CustomerTierDAO.java
javac -d target/classes -cp "lib/*" src/main/java/view/TierConfigDialog.java
```

## 🧪 Bước 3: Test với Sample Data (Optional)

```sql
-- Thêm điểm test cho khách hàng
UPDATE customers SET accumulatedPoint = 15000 WHERE id = 1;  -- Platinum
UPDATE customers SET accumulatedPoint = 7000 WHERE id = 2;   -- Gold
UPDATE customers SET accumulatedPoint = 2500 WHERE id = 3;   -- Silver
UPDATE customers SET accumulatedPoint = 500 WHERE id = 4;    -- Bronze

-- Kiểm tra tier được tính đúng
SELECT 
    c.id, c.name, c.accumulatedPoint,
    (SELECT t.tier_name FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points LIMIT 1) as tier,
    (SELECT t.discount_percent FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points LIMIT 1) as discount
FROM customers c;
```

## 💡 Bước 4: Sử dụng

### Trong Customer Management Panel:

1. **Xem tier của khách hàng:**
   - Mở Customer Management Panel
   - Cột "Points" hiển thị điểm tích lũy
   - Cột "Tier" hiển thị hạng (Bronze/Silver/Gold/Platinum)

2. **Cấu hình tier:**
   - Nhấn nút **"Configure Tiers"**
   - Edit/Add/Delete tier theo ý muốn
   - Ví dụ: Thay đổi Silver từ 1000-4999 thành 1000-2999

3. **Làm mới tier:**
   - Nhấn nút **"Set Tiers"** để reload danh sách
   - Tier tự động cập nhật theo config mới

## 📊 Kiểm tra kết quả

```sql
-- Thống kê khách hàng theo tier
SELECT 
    t.tier_name,
    t.discount_percent,
    COUNT(c.id) as total_customers
FROM customer_tiers t
LEFT JOIN customers c 
    ON c.accumulatedPoint >= t.min_points 
    AND c.accumulatedPoint <= t.max_points
GROUP BY t.tier_name, t.discount_percent
ORDER BY t.min_points;
```

## ❓ Troubleshooting

### Lỗi: "Unknown column 'c.tier'"
- ✅ ĐÃ FIX: Không dùng cột `tier` trong bảng customers nữa
- Tier được tính động trong SQL query

### Tier không hiển thị đúng:
1. Kiểm tra bảng `customer_tiers` đã có dữ liệu chưa
2. Kiểm tra min_points và max_points không overlap
3. Kiểm tra accumulatedPoint của customer có giá trị hợp lệ

### Discount không áp dụng:
- Sử dụng method `getDiscountForCustomer(customerId)` từ `CustomerTierDAO`
- Tích hợp vào checkout process (xem README chính)

## 🎉 Hoàn thành!

Hệ thống tier đã sẵn sàng sử dụng với:
- ✅ 4 tier mặc định (Bronze, Silver, Gold, Platinum)
- ✅ Discount 0%, 5%, 10%, 15%
- ✅ Tính toán động không cần thêm cột database
- ✅ UI quản lý tier đầy đủ
- ✅ Có thể customize theo nhu cầu

Xem thêm chi tiết trong `TIER_MANAGEMENT_README.md`
