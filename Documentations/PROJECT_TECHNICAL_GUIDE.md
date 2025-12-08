# HƯỚNG DẪN KỸ THUẬT & GIẢI THÍCH CODE CHI TIẾT (BẢN ĐẦY ĐỦ)

Tài liệu này giải thích chi tiết **từng dòng code**, từng hàm và ý nghĩa của chúng. Dùng tài liệu này để trả lời khi giáo viên chỉ vào bất kỳ dòng nào và hỏi "Dòng này làm gì?".

---

## 1. TỔNG QUAN HỆ THỐNG (SYSTEM OVERVIEW)

Hệ thống bao gồm 3 thành phần chính hoạt động cùng nhau:

| Thành phần | Công nghệ | Vai trò | Thư mục |
| :--- | :--- | :--- | :--- |
| **Desktop App** | Java Swing (JDBC) | Ứng dụng quản lý tại quầy (POS). | `mavenproject1` |
| **Web Backend** | Spring Boot (JPA) | Server cung cấp API cho Web. | `web-backend` |
| **Web Frontend** | ReactJS | Trang web đặt hàng cho khách. | `web-frontend` |

---

## 2. DESKTOP APPLICATION (Java Swing)

### A. Giao diện Đăng nhập (`LoginForm.java`)

```java
// File: view/LoginForm.java

// 1. Kế thừa JFrame: Biến class này thành một cửa sổ Windows
public class LoginForm extends JFrame {
    
    // Khai báo biến để dùng được ở nhiều nơi trong class
    private JTextField usernameField;     // Ô nhập văn bản thường (hiện chữ)
    private JPasswordField passwordField; // Ô nhập mật khẩu (hiện dấu chấm tròn)

    public LoginForm() {
        // --- CẤU HÌNH CỬA SỔ ---
        setTitle("Sales Management System"); // Đặt tiêu đề thanh trên cùng của cửa sổ
        setSize(1300, 750);                  // Đặt kích thước: Rộng 1300px, Cao 750px
        
        // setLocationRelativeTo(null): Căn giữa cửa sổ màn hình khi mở lên
        setLocationRelativeTo(null); 
        
        // setDefaultCloseOperation(EXIT_ON_CLOSE): Khi bấm nút X đỏ, tắt hẳn chương trình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setLayout(BorderLayout): Chia cửa sổ thành 5 vùng (Đông, Tây, Nam, Bắc, Giữa)
        setLayout(new BorderLayout()); 

        // Thêm panel chính vào vùng giữa (CENTER) của cửa sổ
        add(createMainPanel(), BorderLayout.CENTER);
    }

    // --- XỬ LÝ SỰ KIỆN (QUAN TRỌNG) ---
    private void setupEventHandlers(JButton loginButton) {
        UserDAO userDao = new UserDAO(); // Khởi tạo lớp DAO để kết nối Database
        
        // addActionListener: Lắng nghe sự kiện khi người dùng CLICK chuột vào nút
        loginButton.addActionListener(e -> {
            // .getText(): Lấy nội dung người dùng đã nhập trong ô
            String username = usernameField.getText();
            
            // .getPassword(): Lấy mật khẩu (trả về mảng ký tự char[]) -> đổi sang String
            String password = new String(passwordField.getPassword());

            // Gọi hàm kiểm tra đăng nhập trong DAO
            if (userDao.authenticateUser(username, password)) {
                // --- NẾU ĐĂNG NHẬP THÀNH CÔNG ---
                
                // 1. Tạo cửa sổ chính (MainApplication)
                MainApplication mainApp = new MainApplication(username, role);
                
                // 2. setVisible(true): Làm cho cửa sổ chính HIỆN lên màn hình
                mainApp.setVisible(true);
                
                // 3. dispose(): Đóng và HỦY cửa sổ Đăng nhập hiện tại để giải phóng Ram
                dispose(); 
            } else {
                // --- NẾU ĐĂNG NHẬP THẤT BẠI ---
                
                // JOptionPane.showMessageDialog: Hiện một hộp thoại thông báo nhỏ
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        });
    }
}
```

### B. Màn hình Bán hàng (`OrderPanel.java`)

```java
// File: view/OrderPanel.java

public class OrderPanel extends JPanel {
    // DefaultTableModel: Là "model" chứa dữ liệu của bảng (các dòng, cột)
    private DefaultTableModel cartModel; 
    
    // JTable: Là cái bảng hiển thị giao diện cho người dùng xem
    private JTable cartTable;

    // --- HÀM THÊM VÀO GIỎ HÀNG ---
    private void handleAddToCart() {
        // .getSelectedRow(): Lấy chỉ số dòng mà người dùng đang bôi đen (chọn)
        // Nếu chưa chọn dòng nào thì trả về -1
        int selectedRow = productTable.getSelectedRow();
        
        if (selectedRow == -1) return; // Chưa chọn thì thoát, không làm gì cả

        // .getValueAt(row, col): Lấy giá trị tại dòng row, cột col
        // Cột 0 là Tên, Cột 1 là Giá
        String productName = (String) productTable.getValueAt(selectedRow, 0);
        double price = (Double) productTable.getValueAt(selectedRow, 1);
        
        // .getValue(): Lấy số lượng từ ô xoay (Spinner)
        int quantity = (Integer) quantitySpinner.getValue();

        // Tính tổng tiền cho món này
        double subtotal = price * quantity;

        // .addRow(): Thêm một dòng dữ liệu mới vào bảng giỏ hàng
        // Object[]{...}: Tạo một mảng đối tượng chứa 3 thông tin
        cartModel.addRow(new Object[]{productName, quantity, subtotal});

        // Gọi hàm cập nhật lại ô Tổng tiền bên dưới
        updateTotal();
    }

    // --- HÀM THANH TOÁN (TẠO ORDER) ---
    private void handleCreateOrder() {
        // 1. Tạo đối tượng Order (để chứa thông tin chung)
        Order order = new Order();
        order.setTotalAmount(finalTotal); // Lưu tổng tiền
        order.setOrderDate(LocalDateTime.now()); // Lưu thời gian hiện tại (ngay lúc bấm nút)

        // 2. Tạo danh sách chi tiết (để chứa từng món ăn)
        List<OrderDetails> details = new ArrayList<>();
        
        // Duyệt qua từng dòng trong bảng giỏ hàng (cartModel)
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            // Lấy dữ liệu từng dòng ra
            String name = (String) cartModel.getValueAt(i, 0);
            int qty = (Integer) cartModel.getValueAt(i, 1);
            
            // Tạo đối tượng chi tiết và thêm vào danh sách
            OrderDetails item = new OrderDetails(name, qty);
            details.add(item);
        }

        // 3. Gọi DAO để lưu tất cả vào Database
        AppOrderDao dao = new AppOrderDao();
        
        // Hàm createAppOrder sẽ thực hiện câu lệnh INSERT INTO trong SQL
        boolean success = dao.createAppOrder(order, details);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Tạo đơn hàng thành công!");
            cartModel.setRowCount(0); // Xóa trắng bảng giỏ hàng để bán đơn mới
        }
    }
}
```

---

## 3. WEB BACKEND (Spring Boot)

### A. Controller (`ProductController.java`)

```java
// File: controller/ProductController.java

// @RestController: Báo cho Spring Boot biết class này chuyên xử lý các yêu cầu API (JSON)
// Thay vì trả về giao diện HTML, nó sẽ trả về dữ liệu thô
@RestController 

// @RequestMapping: Định nghĩa "địa chỉ nhà" chung cho class này
// Mọi API trong này đều bắt đầu bằng /api/products
@RequestMapping("/api/products") 
public class ProductController {

    // Khai báo Repository để nhờ nó lấy dữ liệu từ DB
    private final ProductRepository productRepository;

    // --- API 1: LẤY DANH SÁCH SẢN PHẨM ---
    // @GetMapping: Xử lý yêu cầu HTTP GET (khi trình duyệt truy cập vào link)
    // Link đầy đủ: http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAllProducts() {
        // .findAll(): Hàm có sẵn của JPA, tự động sinh câu lệnh "SELECT * FROM products"
        return productRepository.findAll(); 
    }

    // --- API 2: CẬP NHẬT TỒN KHO ---
    // @PutMapping: Xử lý yêu cầu HTTP PUT (dùng để cập nhật dữ liệu)
    // {id}: Là tham số động trên URL (ví dụ: /api/products/5/stock)
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,      // Lấy số 5 từ URL gán vào biến id
            @RequestParam int stock     // Lấy ?stock=10 từ URL gán vào biến stock
    ) {
        // .findById(id): Tìm sản phẩm có ID là 5
        // .orElse(null): Nếu không thấy thì trả về null
        Product product = productRepository.findById(id).orElse(null);
        
        if (product == null) {
            // ResponseEntity.notFound(): Trả về mã lỗi 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // Cập nhật giá trị mới vào đối tượng Java
        product.setStock(stock); 
        
        // .save(): Lưu ngược lại vào Database (tự sinh lệnh UPDATE products SET stock=... WHERE id=...)
        productRepository.save(product); 

        // ResponseEntity.ok(): Trả về mã thành công 200 OK kèm thông báo
        return ResponseEntity.ok("Stock updated");
    }
}
```

---

## 4. WEB FRONTEND (ReactJS)

### A. Hiển thị Menu (`Menu.js`)

```javascript
// File: components/Menu.js

// props ({ addToCart }): Nhận hàm addToCart từ cha truyền xuống để dùng khi bấm nút
function Menu({ addToCart }) {
  
  // useState: Khai báo "bộ nhớ" cho component
  // products: Biến chứa danh sách sản phẩm
  // setProducts: Hàm dùng để cập nhật biến products (khi gọi hàm này, giao diện sẽ tự vẽ lại)
  // []: Giá trị khởi tạo là mảng rỗng
  const [products, setProducts] = useState([]);

  // useEffect: Hàm này sẽ chạy TỰ ĐỘNG 1 lần duy nhất khi trang web vừa tải xong
  useEffect(() => {
    // fetch(): Gọi API của Backend để lấy dữ liệu
    fetch("http://localhost:8081/api/products")
      .then(res => res.json()) // Khi có kết quả, chuyển nó thành dạng JSON
      .then(data => {
          // data chính là danh sách sản phẩm lấy từ Server
          setProducts(data); // Cập nhật vào state -> Giao diện tự động hiện danh sách ra
      });
  }, []); // [] rỗng nghĩa là chỉ chạy 1 lần lúc đầu

  return (
    <div style={containerStyle}>
      <h2>Product Menu</h2>
      
      {/* .map(): Vòng lặp trong React. 
          Nó duyệt qua từng sản phẩm trong mảng products và tạo ra giao diện cho sản phẩm đó */}
      {products.map((product) => (
        // key={product.id}: Bắt buộc phải có ID duy nhất để React quản lý hiệu năng
        <div key={product.id} style={productCardStyle}>
          
          {/* Hiển thị ảnh: src là đường dẫn ảnh */}
          <img src={product.imageUrl} alt={product.name} />
          
          <div>
            {/* Hiển thị tên và giá */}
            <h3>{product.name}</h3>
            <p>Price: {product.price}₫</p>
          </div>

          {/* Sự kiện onClick: Khi bấm nút thì gọi hàm addToCart và truyền sản phẩm vào */}
          <button onClick={() => addToCart(product)}>
            Add to Cart
          </button>
          
        </div>
      ))}
    </div>
  );
}
```

```

### B. Màn hình Bán hàng (`OrderPanel.java`)

```java
// File: view/OrderPanel.java

public class OrderPanel extends JPanel {
    // DefaultTableModel: Là "model" chứa dữ liệu của bảng (các dòng, cột)
    private DefaultTableModel cartModel; 
    
    // JTable: Là cái bảng hiển thị giao diện cho người dùng xem
    private JTable cartTable;

    // --- HÀM THÊM VÀO GIỎ HÀNG ---
    private void handleAddToCart() {
        // .getSelectedRow(): Lấy chỉ số dòng mà người dùng đang bôi đen (chọn)
        // Nếu chưa chọn dòng nào thì trả về -1
        int selectedRow = productTable.getSelectedRow();
        
        if (selectedRow == -1) return; // Chưa chọn thì thoát, không làm gì cả

        // .getValueAt(row, col): Lấy giá trị tại dòng row, cột col
        // Cột 0 là Tên, Cột 1 là Giá
        String productName = (String) productTable.getValueAt(selectedRow, 0);
        double price = (Double) productTable.getValueAt(selectedRow, 1);
        
        // .getValue(): Lấy số lượng từ ô xoay (Spinner)
        int quantity = (Integer) quantitySpinner.getValue();

        // Tính tổng tiền cho món này
        double subtotal = price * quantity;

        // .addRow(): Thêm một dòng dữ liệu mới vào bảng giỏ hàng
        // Object[]{...}: Tạo một mảng đối tượng chứa 3 thông tin
        cartModel.addRow(new Object[]{productName, quantity, subtotal});

        // Gọi hàm cập nhật lại ô Tổng tiền bên dưới
        updateTotal();
    }

    // --- HÀM THANH TOÁN (TẠO ORDER) ---
    private void handleCreateOrder() {
        // 1. Tạo đối tượng Order (để chứa thông tin chung)
        Order order = new Order();
        order.setTotalAmount(finalTotal); // Lưu tổng tiền
        order.setOrderDate(LocalDateTime.now()); // Lưu thời gian hiện tại (ngay lúc bấm nút)

        // 2. Tạo danh sách chi tiết (để chứa từng món ăn)
        List<OrderDetails> details = new ArrayList<>();
        
        // Duyệt qua từng dòng trong bảng giỏ hàng (cartModel)
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            // Lấy dữ liệu từng dòng ra
            String name = (String) cartModel.getValueAt(i, 0);
            int qty = (Integer) cartModel.getValueAt(i, 1);
            
            // Tạo đối tượng chi tiết và thêm vào danh sách
            OrderDetails item = new OrderDetails(name, qty);
            details.add(item);
        }

        // 3. Gọi DAO để lưu tất cả vào Database
        AppOrderDao dao = new AppOrderDao();
        
        // Hàm createAppOrder sẽ thực hiện câu lệnh INSERT INTO trong SQL
        boolean success = dao.createAppOrder(order, details);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Tạo đơn hàng thành công!");
            cartModel.setRowCount(0); // Xóa trắng bảng giỏ hàng để bán đơn mới
        }
    }
}
```

---

## 3. WEB BACKEND (Spring Boot)

### A. Controller (`ProductController.java`)

```java
// File: controller/ProductController.java

// @RestController: Báo cho Spring Boot biết class này chuyên xử lý các yêu cầu API (JSON)
// Thay vì trả về giao diện HTML, nó sẽ trả về dữ liệu thô
@RestController 

// @RequestMapping: Định nghĩa "địa chỉ nhà" chung cho class này
// Mọi API trong này đều bắt đầu bằng /api/products
@RequestMapping("/api/products") 
public class ProductController {

    // Khai báo Repository để nhờ nó lấy dữ liệu từ DB
    private final ProductRepository productRepository;

    // --- API 1: LẤY DANH SÁCH SẢN PHẨM ---
    // @GetMapping: Xử lý yêu cầu HTTP GET (khi trình duyệt truy cập vào link)
    // Link đầy đủ: http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAllProducts() {
        // .findAll(): Hàm có sẵn của JPA, tự động sinh câu lệnh "SELECT * FROM products"
        return productRepository.findAll(); 
    }

    // --- API 2: CẬP NHẬT TỒN KHO ---
    // @PutMapping: Xử lý yêu cầu HTTP PUT (dùng để cập nhật dữ liệu)
    // {id}: Là tham số động trên URL (ví dụ: /api/products/5/stock)
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,      // Lấy số 5 từ URL gán vào biến id
            @RequestParam int stock     // Lấy ?stock=10 từ URL gán vào biến stock
    ) {
        // .findById(id): Tìm sản phẩm có ID là 5
        // .orElse(null): Nếu không thấy thì trả về null
        Product product = productRepository.findById(id).orElse(null);
        
        if (product == null) {
            // ResponseEntity.notFound(): Trả về mã lỗi 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // Cập nhật giá trị mới vào đối tượng Java
        product.setStock(stock); 
        
        // .save(): Lưu ngược lại vào Database (tự sinh lệnh UPDATE products SET stock=... WHERE id=...)
        productRepository.save(product); 

        // ResponseEntity.ok(): Trả về mã thành công 200 OK kèm thông báo
        return ResponseEntity.ok("Stock updated");
    }
}
```

---

## 4. WEB FRONTEND (ReactJS)

### A. Hiển thị Menu (`Menu.js`)

```javascript
// File: components/Menu.js

// props ({ addToCart }): Nhận hàm addToCart từ cha truyền xuống để dùng khi bấm nút
function Menu({ addToCart }) {
  
  // useState: Khai báo "bộ nhớ" cho component
  // products: Biến chứa danh sách sản phẩm
  // setProducts: Hàm dùng để cập nhật biến products (khi gọi hàm này, giao diện sẽ tự vẽ lại)
  // []: Giá trị khởi tạo là mảng rỗng
  const [products, setProducts] = useState([]);

  // useEffect: Hàm này sẽ chạy TỰ ĐỘNG 1 lần duy nhất khi trang web vừa tải xong
  useEffect(() => {
    // fetch(): Gọi API của Backend để lấy dữ liệu
    fetch("http://localhost:8081/api/products")
      .then(res => res.json()) // Khi có kết quả, chuyển nó thành dạng JSON
      .then(data => {
          // data chính là danh sách sản phẩm lấy từ Server
          setProducts(data); // Cập nhật vào state -> Giao diện tự động hiện danh sách ra
      });
  }, []); // [] rỗng nghĩa là chỉ chạy 1 lần lúc đầu

  return (
    <div style={containerStyle}>
      <h2>Product Menu</h2>
      
      {/* .map(): Vòng lặp trong React. 
          Nó duyệt qua từng sản phẩm trong mảng products và tạo ra giao diện cho sản phẩm đó */}
      {products.map((product) => (
        // key={product.id}: Bắt buộc phải có ID duy nhất để React quản lý hiệu năng
        <div key={product.id} style={productCardStyle}>
          
          {/* Hiển thị ảnh: src là đường dẫn ảnh */}
          <img src={product.imageUrl} alt={product.name} />
          
          <div>
            {/* Hiển thị tên và giá */}
            <h3>{product.name}</h3>
            <p>Price: {product.price}₫</p>
          </div>

          {/* Sự kiện onClick: Khi bấm nút thì gọi hàm addToCart và truyền sản phẩm vào */}
          <button onClick={() => addToCart(product)}>
            Add to Cart
          </button>
          
        </div>
      ))}
    </div>
  );
}
```

---

## 5. BẢO MẬT & TIỆN ÍCH (SECURITY & UTILITIES)

Phần này cực kỳ quan trọng, giáo viên thường hỏi về cách bạn bảo vệ mật khẩu người dùng.

### A. Mã hóa Mật khẩu (BCrypt)

Hệ thống sử dụng thuật toán **BCrypt** để mã hóa mật khẩu trước khi lưu vào Database. Tuyệt đối không lưu mật khẩu dạng văn bản thô (plain text).

#### 1. Desktop App (`PasswordUtil.java`)
Class tiện ích dùng để mã hóa và kiểm tra mật khẩu.

```java
// File: util/PasswordUtil.java

public class PasswordUtil {
    
    // Hàm mã hóa mật khẩu (Dùng khi Đăng ký/Tạo user)
    public static String hashPassword(String plainPassword) {
        // BCrypt.gensalt(12): Tạo "muối" ngẫu nhiên với độ khó 12
        // BCrypt.hashpw: Trộn mật khẩu + muối -> Chuỗi mã hóa (60 ký tự)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    
    // Hàm kiểm tra mật khẩu (Dùng khi Đăng nhập)
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // BCrypt.checkpw: So sánh mật khẩu nhập vào với chuỗi mã hóa trong DB
        // Nó tự động tách "muối" từ hashedPassword ra để tính toán lại
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

#### 2. Web Backend (`SecurityConfig.java`)
Cấu hình bảo mật cho Spring Boot.

```java
// File: config/SecurityConfig.java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Khai báo Bean PasswordEncoder để Spring Security sử dụng chung
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCrypt với độ mạnh 12 (2^12 vòng lặp)
        return new BCryptPasswordEncoder(12);
    }

    // Cấu hình bộ lọc bảo mật (Security Filter Chain)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì đây là REST API (Stateless)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Cho phép tất cả request (vì đang demo, thực tế sẽ chặn)
            );
        return http.build();
    }
}
```

### B. Kết nối Cơ sở dữ liệu (`DatabaseConnector.java`)

Sử dụng mẫu thiết kế **Singleton** (hoặc Static Factory) để quản lý kết nối.

```java
// File: database/DatabaseConnector.java

public class DatabaseConnector {
    // Thông tin kết nối (Hardcode - Điểm trừ nhẹ, nên đưa vào file config)
    private static final String URL = "jdbc:mysql://localhost:3306/shopdb";
    private static final String USER = "root";
    private static final String PASS = "123456";

    // Hàm lấy kết nối
    public static Connection getConnection() throws SQLException {
        // DriverManager: Trình quản lý driver của Java
        // Nó sẽ tìm driver MySQL và mở kết nối tới Database
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
```

---

---

## 6. BÁO CÁO & XUẤT EXCEL (REPORTING & EXPORT)

Chức năng báo cáo doanh thu sử dụng thư viện **Apache POI** để xuất file Excel.

### A. Xuất Excel (`RevenueReportPanel.java`)

```java
// File: view/RevenueReportPanel.java

private void exportExcel() {
    // 1. Tạo hộp thoại chọn nơi lưu file
    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("revenue_report.xlsx"));
    
    // 2. Tạo Workbook (File Excel ảo trong RAM)
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
        XSSFSheet sheet = workbook.createSheet("Revenue Report");

        // 3. Tạo dòng tiêu đề (Header)
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Order ID");
        header.createCell(1).setCellValue("Date");
        // ... (Tạo các cột khác)

        // 4. Duyệt qua bảng dữ liệu (JTable) và ghi vào Excel
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            Row row = sheet.createRow(r + 1); // Dòng dữ liệu bắt đầu từ 1
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                Object val = tableModel.getValueAt(r, c);
                // Ghi giá trị vào ô
                row.createCell(c).setCellValue(val.toString());
            }
        }

        // 5. Lưu file ra ổ cứng
        try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {
            workbook.write(fos);
        }
        JOptionPane.showMessageDialog(this, "Xuất file thành công!");
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}
```

---

## 7. TÓM TẮT QUY TRÌNH HOẠT ĐỘNG (FLOW)

Ví dụ quy trình **Khách đặt hàng Online**:

1.  **Frontend (`Menu.js`):** Khách chọn món -> Bấm "Add to Cart".
2.  **Frontend (`Cart.js`):** Khách bấm "Checkout" -> Gửi `POST` request chứa thông tin đơn hàng về Backend.
3.  **Backend (`OrderController`):** Nhận request -> Lưu đơn hàng vào bảng `web_order` trong Database.
4.  **Database:** Dữ liệu đơn hàng mới xuất hiện.
5.  **Desktop App:** Nhân viên bấm nút "Refresh" (hoặc tự động) -> App query Database -> Thấy đơn mới -> Chế biến -> Giao hàng.

---
**TỔNG KẾT CÁC TỪ KHÓA QUAN TRỌNG:**

1.  **`setVisible(true)`**: Lệnh của Java Swing để **hiện** cửa sổ lên màn hình. Nếu là `false` thì cửa sổ sẽ ẩn đi (nhưng vẫn chạy ngầm).
2.  **`dispose()`**: Lệnh **đóng và hủy** cửa sổ hoàn toàn, giải phóng bộ nhớ RAM. Dùng khi chuyển từ màn hình Login sang màn hình Chính.
3.  **`ActionListener`**: "Người nghe". Dùng để gắn vào nút bấm, khi nút được bấm thì nó sẽ báo cho code biết để chạy hàm xử lý.
4.  **`DefaultTableModel`**: "Cái lõi" của bảng dữ liệu. Muốn thêm/sửa/xóa dòng trong bảng JTable, ta phải thao tác với cái Model này.
5.  **`@RestController`**: Biển hiệu báo rằng "Đây là nơi cung cấp dữ liệu JSON", không phải nơi trả về giao diện HTML.
6.  **`fetch()`**: Hàm của Javascript dùng để "gọi điện" sang Backend xin dữ liệu.
7.  **`useState`**: "Bộ nhớ tạm" của React. Khi biến trong useState thay đổi, giao diện web sẽ tự động chớp và cập nhật lại nội dung mới.
8.  **`BCrypt`**: Thuật toán băm mật khẩu một chiều, giúp bảo mật thông tin người dùng (không thể dịch ngược lại mật khẩu gốc).
9.  **`Apache POI`**: Thư viện Java dùng để đọc/ghi file Microsoft Office (Excel, Word).

---

## 8. CÁC KHÁI NIỆM & ĐỊNH NGHĨA QUAN TRỌNG (KEY CONCEPTS)

Phần này giải thích các thuật ngữ chuyên ngành để bạn trả lời câu hỏi "Tại sao dùng cái này?".

### 1. REST API là gì?
*   **Định nghĩa:** REST (Representational State Transfer) là một chuẩn thiết kế để các hệ thống máy tính giao tiếp với nhau qua mạng. API (Application Programming Interface) là cổng giao tiếp.
*   **Cách dùng:** Trong dự án này, Backend (Spring Boot) tạo ra các đường dẫn (như `/api/products`) trả về dữ liệu dạng JSON. Frontend (React) gọi các đường dẫn này để lấy dữ liệu.
*   **Tại sao dùng?**
    *   **Tách biệt:** Giúp Backend và Frontend phát triển độc lập.
    *   **Đa nền tảng:** Một Backend có thể phục vụ cho cả Web, Mobile App, và Desktop App cùng lúc.

### 2. JDBC (Java Database Connectivity)
*   **Định nghĩa:** Là thư viện chuẩn của Java để kết nối trực tiếp với Database.
*   **Cách dùng:** Trong Desktop App (`AppOrderDao.java`), ta dùng JDBC để viết câu lệnh SQL thủ công (`SELECT`, `INSERT`).
*   **Tại sao dùng?**
    *   **Hiệu năng cao:** Vì nó chạy trực tiếp, ít lớp trung gian.
    *   **Dễ hiểu:** Giúp người mới học hiểu rõ bản chất của việc kết nối Database.

### 3. JPA (Java Persistence API) & Hibernate
*   **Định nghĩa:** Là công nghệ ORM (Object-Relational Mapping), giúp biến các bảng trong Database thành các Class trong Java (và ngược lại).
*   **Cách dùng:** Trong Web Backend (`ProductRepository`), ta không cần viết SQL. JPA tự động sinh SQL dựa trên tên hàm (ví dụ `findAll()` -> `SELECT *`).
*   **Tại sao dùng?**
    *   **Nhanh gọn:** Giảm 80% lượng code phải viết so với JDBC.
    *   **Dễ bảo trì:** Khi sửa tên cột trong DB, chỉ cần sửa trong Class Java là xong.

### 4. React Component
*   **Định nghĩa:** Là các khối xây dựng nên giao diện Web. Mỗi Component là một phần nhỏ (như nút bấm, menu, ô nhập liệu).
*   **Cách dùng:** `Menu.js` là một component hiển thị danh sách món. `Cart.js` là component hiển thị giỏ hàng.
*   **Tại sao dùng?**
    *   **Tái sử dụng:** Viết một lần, dùng nhiều nơi (ví dụ nút "Mua ngay" dùng ở 10 chỗ khác nhau).
    *   **Dễ quản lý:** Chia nhỏ giao diện phức tạp thành các phần nhỏ dễ sửa lỗi.

### 5. State (trong React)
*   **Định nghĩa:** Là "bộ nhớ" của một Component. Khi State thay đổi, giao diện tự động cập nhật.
*   **Cách dùng:** `const [products, setProducts] = useState([])`. Khi gọi `setProducts(...)`, danh sách món ăn trên màn hình sẽ thay đổi ngay lập tức.
*   **Tại sao dùng?** Để tạo ra giao diện động, phản hồi tức thì với hành động của người dùng mà không cần tải lại trang (Reload).

### 6. Singleton Pattern
*   **Định nghĩa:** Là mẫu thiết kế đảm bảo một Class chỉ có DUY NHẤT một thể hiện (instance) trong suốt quá trình chạy.
*   **Cách dùng:** Class `DatabaseConnector` dùng Singleton để đảm bảo chỉ tạo ra một kết nối Database duy nhất.
*   **Tại sao dùng?** Tiết kiệm tài nguyên hệ thống (RAM, CPU), tránh việc mở quá nhiều kết nối làm sập Database.

---
**Lưu ý:** Khi bảo vệ, hãy mở sẵn các file code này. Khi giáo viên hỏi chức năng nào, bạn mở đúng file đó và giải thích theo luồng logic như trên. Chúc bạn thành công!
```
