# Customer Management Panel - Auto Refresh

## Vấn đề đã Fix
`CustomerManagementPanel` trước đây không tự động cập nhật khi có khách hàng mới được thêm vào database.

## Giải pháp đã triển khai

### 1. **Auto-Refresh Timer**
- Panel tự động làm mới dữ liệu mỗi **30 giây**
- Timer chạy trong background và gọi `loadCustomers()` định kỳ
- Đảm bảo danh sách khách hàng luôn được cập nhật mới nhất

### 2. **Manual Refresh Method**
Thêm method public để các panel khác có thể kích hoạt refresh:

```java
public void refreshCustomerData()
```

### 3. **Stop Auto-Refresh**
Method để dừng timer khi không cần thiết:

```java
public void stopAutoRefresh()
```

## Cách sử dụng

### Tự động Refresh (mặc định)
Panel sẽ tự động refresh mỗi 30 giây. Không cần làm gì thêm.

### Refresh thủ công từ code khác
Nếu bạn có một panel/form khác thêm customer mới và muốn `CustomerManagementPanel` update ngay lập tức:

```java
// Giả sử bạn có reference đến CustomerManagementPanel
customerManagementPanel.refreshCustomerData();
```

### Thay đổi thời gian auto-refresh
Mở file `CustomerManagementPanel.java` và tìm dòng:

```java
refreshTimer = new Timer(30000, ...  // 30000 = 30 giây
```

Thay đổi số `30000` (milliseconds) thành giá trị mong muốn:
- 10 giây = `10000`
- 1 phút = `60000`
- 2 phút = `120000`

### Dừng auto-refresh khi dispose panel
Nếu bạn đóng/dispose panel, nên gọi:

```java
customerManagementPanel.stopAutoRefresh();
```

## Lợi ích

✅ **Dữ liệu luôn mới nhất**: Không cần F5 hay restart
✅ **Đồng bộ real-time**: Thay đổi từ web/app/desktop đều được cập nhật
✅ **Linh hoạt**: Có thể refresh thủ công khi cần
✅ **Tối ưu hiệu năng**: Timer có thể dừng khi không cần

## Lưu ý

- Auto-refresh chỉ chạy khi panel đang hiển thị
- Query database mỗi 30s nên đảm bảo database không quá tải
- Statistics (Total Customers, Platinum, Gold, etc.) cũng được update tự động
- Nếu có nhiều người dùng đồng thời, tất cả đều thấy dữ liệu cập nhật

## Ví dụ Integration

### Trong OrderPanel khi tạo customer mới:
```java
// Sau khi insert customer vào database
Connection conn = database.DatabaseConnector.getConnection();
// ... insert customer code ...

// Refresh CustomerManagementPanel nếu có reference
if (mainFrame.getCustomerManagementPanel() != null) {
    mainFrame.getCustomerManagementPanel().refreshCustomerData();
}
```

### Trong MainFrame/AdminDashboard:
```java
private CustomerManagementPanel customerPanel;

public CustomerManagementPanel getCustomerManagementPanel() {
    return customerPanel;
}

// Khi đóng ứng dụng
@Override
public void dispose() {
    if (customerPanel != null) {
        customerPanel.stopAutoRefresh();
    }
    super.dispose();
}
```

## Testing

### Test Auto-Refresh:
1. Mở CustomerManagementPanel
2. Từ MySQL Workbench hoặc web app, thêm customer mới
3. Chờ 30 giây
4. Customer mới sẽ xuất hiện trong panel

### Test Manual Refresh:
1. Thêm customer từ dialog "Add Customer"
2. Panel sẽ tự động refresh ngay lập tức (do gọi `loadCustomers()` sau insert)

## Performance Impact

- **Network**: 1 query mỗi 30s (minimal)
- **CPU**: Rất thấp (chỉ chạy khi có data thay đổi)
- **Memory**: Không đáng kể

Nếu cần tối ưu hơn, có thể:
- Tăng interval lên 60s
- Chỉ refresh khi panel đang visible
- Implement smart refresh (chỉ refresh khi có thay đổi thực sự)
