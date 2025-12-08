# 📊 FLOW HOẠT ĐỘNG CỦA STAFF VÀ ADMIN

## 🔐 1. FLOW ĐĂNG NHẬP (Login Flow)

```mermaid
graph TD
    A[👤 User Opens Application] --> B[LoginForm.java]
    B --> C{Enter Username & Password}
    C --> D[Click Sign In Button]
    D --> E[UserDAO.authenticateUser]
    E --> F{Check Password with BCrypt}
    F -->|Invalid| G[Show Error Dialog]
    F -->|Valid| H[UserDAO.getUserRole]
    H --> I{Role?}
    I -->|STAFF| J[MainApplication - STAFF Mode]
    I -->|ADMIN| K[MainApplication - ADMIN Mode]
    G --> C
```

### 📁 Files Involved:
- **`view/LoginForm.java`** - Giao diện đăng nhập
- **`dao/UserDAO.java`** - Xác thực người dùng
- **`util/PasswordUtil.java`** - Mã hóa BCrypt
- **`database/DatabaseConnector.java`** - Kết nối database

---

## 👨‍💼 2. FLOW HOẠT ĐỘNG CỦA STAFF

### 🎯 2.1. Dashboard Panel (Màn hình chính)

```mermaid
graph TD
    A[MainApplication Constructor] --> B[Create Sidebar Menu]
    B --> C[Load DashboardPanel]
    C --> D[Display Today's Stats]
    D --> E[Auto Refresh Every 5 seconds]
    
    D --> F[📊 Total Sales Today]
    D --> G[📦 Total Orders Today]
    D --> H[👥 Total Customers Today]
    D --> I[📈 Products Sold Today]
    
    E --> J[refreshTodayStats]
    J --> K[Query AppOrderDao]
    K --> L[Update UI Components]
```

**Files:** `view/DashboardPanel.java`, `dao/AppOrderDao.java`

---

### 🛒 2.2. Order Management Flow

```mermaid
graph TD
    A[Staff clicks Order Menu] --> B[OrderPanel.java]
    B --> C[Load Product List]
    C --> D[Display Product Table]
    
    D --> E{Staff Action}
    
    E -->|Search Product| F[Filter Products]
    E -->|Select Product| G[Add to Cart]
    
    G --> H[Enter Customer Phone]
    H --> I{Customer Exists?}
    I -->|Yes| J[Load Customer Info & Discount]
    I -->|No| K[Create New Customer]
    
    J --> L[Apply Tier Discount]
    K --> L
    
    L --> M[Select Table Number]
    M --> N[Calculate Total]
    N --> O[Click Confirm Order]
    
    O --> P[Save to app_order table]
    P --> Q[Save to app_order_details table]
    Q --> R[Update Product Stock]
    R --> S[Show Success Message]
    S --> T[Clear Cart]
```

**Files:**
- `view/OrderPanel.java`
- `dao/GetProduct.java`
- `dao/AppOrderDao.java`
- `model/Order.java`
- `model/OrderDetails.java`

---

### 📦 2.3. Product Management Flow

```mermaid
graph TD
    A[Staff clicks Product Management] --> B[ProductManagerPanel.java]
    B --> C[Load Product Data]
    C --> D[Display Product Table]
    
    D --> E{Staff Action}
    
    E -->|Search| F[Filter by Name/Price/Stock]
    E -->|Add New| G[ProductDialog - Create Mode]
    E -->|Edit| H[ProductDialog - Edit Mode]
    E -->|Delete| I[Confirm Delete]
    
    G --> J[Enter Product Details]
    H --> J
    J --> K{Validate Input}
    K -->|Invalid| L[Show Error]
    K -->|Valid| M[ProductDAO.save/update]
    
    I --> N[ProductDAO.deleteById]
    
    M --> O[Reload Product Table]
    N --> O
```

**Files:**
- `view/ProductManagerPanel.java`
- `view/ProductDialog.java`
- `dao/ProductDAO.java`
- `model/Product.java`

---

### 📋 2.4. Order Confirmation Flow

```mermaid
graph TD
    A[Staff clicks Order Confirmation] --> B[OrderConfirmationPanel.java]
    B --> C[Load Pending Orders]
    C --> D[Display Orders with Status]
    
    D --> E{Staff Action}
    
    E -->|Filter by Status| F[Filter Orders List]
    E -->|Select Order| G[View Order Details]
    E -->|Confirm Payment| H[Update Payment Status]
    
    H --> I{Status Change}
    I -->|Pending → Paid| J[Update to 'Paid']
    I -->|Paid → Completed| K[Update to 'Completed']
    
    J --> L[Save to Database]
    K --> L
    L --> M[Reload Orders List]
```

**Files:**
- `view/OrderConfirmationPanel.java`
- `dao/AppOrderDao.java`

---

### 💰 2.5. Revenue Today Flow

```mermaid
graph TD
    A[Staff clicks Revenue Today] --> B[RevenueTodayPanel.java]
    B --> C[Query Today's Revenue]
    C --> D[Calculate Stats]
    
    D --> E[📊 Total Revenue]
    D --> F[📦 Total Orders]
    D --> G[💵 Average Order Value]
    D --> H[👥 Unique Customers]
    
    C --> I[Load Order Details Table]
    I --> J[Display Order List]
    J --> K[Show Order Items]
```

**Files:**
- `view/RevenueTodayPanel.java`
- `dao/AppOrderDao.java`

---

### 👥 2.6. Customer Management Flow

```mermaid
graph TD
    A[Staff clicks Customers] --> B[CustomerManagementPanel.java]
    B --> C[Load Customer List]
    C --> D[Display with Tier Info]
    
    D --> E{Staff Action}
    
    E -->|Search| F[Filter Customers]
    E -->|View Details| G[CustomerInfoDialog]
    E -->|Edit| H[Update Customer Info]
    
    G --> I[Display Customer Stats]
    I --> J[Total Orders]
    I --> K[Total Spent]
    I --> L[Current Tier & Discount]
    
    H --> M[Save to Database]
    M --> N[Reload Customer List]
```

**Files:**
- `view/CustomerManagementPanel.java`
- `view/CustomerInfoDialog.java`
- `dao/CustomerDao.java`

---

## 👨‍💻 3. FLOW HOẠT ĐỘNG CỦA ADMIN

### Admin có TẤT CẢ quyền của Staff + các quyền bổ sung:

```mermaid
graph TD
    A[Admin Login] --> B[MainApplication - ADMIN Mode]
    B --> C[Show Additional Menu Items]
    
    C --> D[All Staff Menus]
    C --> E[👥 User Management]
    C --> F[📊 Revenue Report]
    
    E --> G[ADMIN ONLY SECTION]
    F --> G
```

---

### 👥 3.1. User Management Flow (ADMIN ONLY)

```mermaid
graph TD
    A[Admin clicks User Management] --> B[UserManagementPanel.java]
    B --> C[Load All Users]
    C --> D[Display User Table]
    
    D --> E{Admin Action}
    
    E -->|Add User| F[Enter New User Details]
    E -->|Edit User| G[Modify User Info]
    E -->|Delete User| H[Confirm Delete]
    E -->|Search User| I[Filter User List]
    
    F --> J{Validate Username & Email}
    J -->|Duplicate| K[Show Error]
    J -->|Valid| L[Hash Password with BCrypt]
    
    L --> M[UserDAO.registerUser]
    G --> N[UserDAO.updateUser]
    H --> O[UserDAO.deleteUserById]
    
    M --> P[Reload User Table]
    N --> P
    O --> P
```

**Files:**
- `view/UserManagementPanel.java`
- `dao/UserDAO.java`
- `model/user.java`
- `util/PasswordUtil.java`

---

### 📊 3.2. Revenue Report Flow (ADMIN ONLY)

```mermaid
graph TD
    A[Admin clicks Revenue Report] --> B[RevenueReportPanel.java]
    B --> C[Set Default Date Range - Today]
    
    C --> D{Admin Action}
    
    D -->|Select Date Range| E[Pick From & To Date]
    D -->|Quick Filter| F[Today/Yesterday/This Week/This Month]
    
    E --> G[Load Data for Date Range]
    F --> G
    
    G --> H[Query Database]
    H --> I[Calculate Statistics]
    
    I --> J[📊 Total Revenue]
    I --> K[📦 Total Orders]
    I --> L[💵 Average Order]
    
    I --> M[Display Detailed Table]
    M --> N[Order ID, Date, Amount, Payment Status]
    
    G --> O{Export Action}
    O -->|Excel| P[Export to .xlsx]
    O -->|Print| Q[Print Report]
    O -->|PDF| R[Export to PDF]
```

**Files:**
- `view/RevenueReportPanel.java`
- `dao/AppOrderDao.java`
- Uses Apache POI for Excel export

---

## 🔄 4. SYSTEM COMPONENTS & INTERACTIONS

```mermaid
graph TB
    subgraph "Presentation Layer"
        A[LoginForm]
        B[MainApplication]
        C[DashboardPanel]
        D[OrderPanel]
        E[ProductManagerPanel]
        F[OrderConfirmationPanel]
        G[RevenueTodayPanel]
        H[CustomerManagementPanel]
        I[UserManagementPanel - ADMIN]
        J[RevenueReportPanel - ADMIN]
    end
    
    subgraph "Business Logic Layer"
        K[UserDAO]
        L[ProductDAO]
        M[AppOrderDao]
        N[CustomerDao]
        O[GetProduct]
    end
    
    subgraph "Data Layer"
        P[DatabaseConnector]
        Q[(MySQL Database)]
    end
    
    subgraph "Utility Layer"
        R[PasswordUtil - BCrypt]
        S[UIUtils]
    end
    
    A --> K
    B --> C
    B --> D
    B --> E
    B --> F
    B --> G
    B --> H
    B --> I
    B --> J
    
    D --> M
    D --> N
    D --> O
    E --> L
    F --> M
    G --> M
    H --> N
    I --> K
    J --> M
    
    K --> P
    L --> P
    M --> P
    N --> P
    O --> P
    
    P --> Q
    
    K --> R
    A --> R
```

---

## 📊 5. DATABASE SCHEMA INTERACTIONS

### Tables Used:

| Table | Used By | Purpose |
|-------|---------|---------|
| `users` | UserDAO | Authentication, User Management |
| `products` | ProductDAO | Product Management |
| `app_order` | AppOrderDao | Order Management |
| `app_order_details` | AppOrderDao | Order Items |
| `customers` | CustomerDao | Customer Management |
| `customer_tiers` | CustomerDao | Loyalty Program |

---

## 🔐 6. AUTHENTICATION & AUTHORIZATION

```mermaid
graph TD
    A[User Login] --> B[UserDAO.authenticateUser]
    B --> C[Query users table]
    C --> D[Get hashed password]
    D --> E[PasswordUtil.checkPassword]
    E --> F{BCrypt Verify}
    
    F -->|Success| G[UserDAO.getUserRole]
    F -->|Fail| H[Login Failed]
    
    G --> I{Role?}
    I -->|STAFF| J[Limited Menu Access]
    I -->|ADMIN| K[Full Menu Access]
    
    J --> L[Dashboard, Order, Products, Revenue Today, Customers, Order Confirmation]
    K --> M[All Staff Menus + User Management + Revenue Report]
```

---

## 🎯 7. KEY DIFFERENCES: STAFF vs ADMIN

### 👨‍💼 STAFF Permissions:
✅ View Dashboard  
✅ Create & Manage Orders  
✅ Manage Products  
✅ Confirm Orders  
✅ View Today's Revenue  
✅ Manage Customers  
❌ User Management  
❌ Full Revenue Reports with Date Ranges  

### 👨‍💻 ADMIN Permissions:
✅ **All Staff Permissions**  
✅ **User Management** - Create, Edit, Delete Users  
✅ **Revenue Report** - View historical data with date filters  
✅ **Export Reports** - Excel, PDF, Print  

---

## 🔄 8. AUTO-REFRESH MECHANISMS

### DashboardPanel:
- Auto-refresh every **5 seconds**
- Updates: Sales, Orders, Customers, Products Sold
- Uses SwingWorker for background queries

### OrderPanel:
- Real-time product stock updates
- Customer discount calculation on phone input
- Cart total recalculation on quantity change

### OrderConfirmationPanel:
- Manual refresh on status update
- Filter by payment status (Pending, Paid, Completed)

---

## 📱 9. UI NAVIGATION FLOW

```mermaid
stateDiagram-v2
    [*] --> LoginForm
    LoginForm --> MainApplication: Successful Login
    
    MainApplication --> Dashboard
    MainApplication --> ProductManagement
    MainApplication --> Order
    MainApplication --> OrderConfirmation
    MainApplication --> RevenueToday
    MainApplication --> Customers
    
    state ADMIN_ONLY {
        MainApplication --> UserManagement
        MainApplication --> RevenueReport
    }
    
    Dashboard --> [*]: Logout
    ProductManagement --> [*]: Logout
    Order --> [*]: Logout
    OrderConfirmation --> [*]: Logout
    RevenueToday --> [*]: Logout
    Customers --> [*]: Logout
    UserManagement --> [*]: Logout
    RevenueReport --> [*]: Logout
```

---

## 🛠️ 10. TECHNOLOGY STACK

### Frontend (Desktop):
- **Java Swing** - UI Framework
- **JFreeChart** - Charts & Graphs
- **GridBagLayout, BorderLayout** - Layouts

### Backend:
- **JDBC** - Database Connectivity
- **BCrypt** - Password Hashing
- **Apache POI** - Excel Export

### Database:
- **MySQL** - RDBMS

### Design Patterns:
- **DAO Pattern** - Data Access
- **MVC Pattern** - Architecture
- **Singleton** - Database Connector

---

## 📝 NOTES:

1. **Password Security**: Tất cả mật khẩu được hash bằng BCrypt trước khi lưu vào database
2. **Role-Based Access**: Menu items được hiển thị dựa trên role của user
3. **Real-time Updates**: Dashboard tự động refresh để cập nhật dữ liệu mới nhất
4. **Customer Loyalty**: Hệ thống tiers tự động tính discount dựa trên tổng chi tiêu
5. **Validation**: Tất cả input đều được validate trước khi lưu vào database

---

## 🚀 GETTING STARTED

### For Staff:
1. Login với username và password
2. Xem Dashboard để có overview
3. Tạo đơn hàng mới trong Order Panel
4. Xác nhận thanh toán trong Order Confirmation
5. Xem doanh thu ngày trong Revenue Today

### For Admin:
1. Login với admin account
2. Có tất cả quyền của Staff
3. Quản lý users trong User Management
4. Xem báo cáo chi tiết trong Revenue Report
5. Export dữ liệu ra Excel/PDF

---

**Tạo bởi:** GitHub Copilot  
**Ngày:** December 5, 2025  
**Version:** 1.0
