# TỔNG HỢP CÁC CÚ PHÁP (SYNTAX) PHẢI BIẾT
Tài liệu này tổng hợp các cú pháp quan trọng nhất trong dự án, giải thích chi tiết để bạn dễ dàng trả lời vấn đề kỹ thuật.

---

## 1. JAVA SWING (Desktop App)

### `extends JFrame`
*   **Lý thuyết:** Kế thừa (Inheritance). Class con sẽ nhận toàn bộ thuộc tính và phương thức của Class cha.
*   **Cú pháp:** `public class LoginForm extends JFrame { ... }`
*   **Tác dụng:** Biến class `LoginForm` bình thường trở thành một **cửa sổ ứng dụng** (Window) có thể đóng, mở, thu nhỏ.
*   **Ý nghĩa:** "Tôi muốn class này hoạt động như một cửa sổ Windows."

### `implements ActionListener` (hoặc `e -> { ... }`)
*   **Lý thuyết:** Triển khai Interface (Interface Implementation). Cam kết thực hiện các hàm mà Interface quy định.
*   **Cú pháp:** `button.addActionListener(e -> { System.out.println("Clicked"); });`
*   **Tác dụng:** Gắn một hành động cụ thể vào sự kiện click chuột.
*   **Ý nghĩa:** "Khi nút này bị bấm, hãy chạy đoạn code trong ngoặc nhọn." (Đây là cú pháp Lambda Expression của Java 8 cho gọn).

### `super()`
*   **Lý thuyết:** Gọi Constructor của lớp cha.
*   **Cú pháp:** `super("Title of Window");`
*   **Tác dụng:** Thiết lập tiêu đề cho cửa sổ ngay khi khởi tạo.
*   **Ý nghĩa:** "Hãy nhờ lớp cha (JFrame) dựng sẵn cái khung cửa sổ với tiêu đề này cho tôi."

### `this`
*   **Lý thuyết:** Tham chiếu đến đối tượng hiện tại (Current Instance).
*   **Cú pháp:** `this.setSize(800, 600);` hoặc `JOptionPane.showMessageDialog(this, ...)`
*   **Tác dụng:** Chỉ định rõ ràng là đang thao tác trên chính cửa sổ này.
*   **Ý nghĩa:** "Cái cửa sổ này" (chứ không phải cửa sổ khác).

### `try-with-resources`
*   **Lý thuyết:** Quản lý tài nguyên tự động (Automatic Resource Management).
*   **Cú pháp:** 
    ```java
    try (Connection conn = DatabaseConnector.getConnection()) { 
        // Code dùng kết nối 
    } catch (SQLException e) { ... }
    ```
*   **Tác dụng:** Tự động đóng kết nối (`conn.close()`) khi chạy xong hoặc gặp lỗi, không cần viết `finally`.
*   **Ý nghĩa:** "Mở kết nối dùng đi, dùng xong tự dọn dẹp nhé, đừng để rác (memory leak)."

---

## 2. SPRING BOOT (Web Backend)

### `@RestController`
*   **Lý thuyết:** Annotation (Chú thích). Đánh dấu class là một Controller chuyên xử lý RESTful requests.
*   **Cú pháp:** Đặt trên đầu class.
*   **Tác dụng:** Biến class thành nơi tiếp nhận các yêu cầu HTTP và trả về dữ liệu (thường là JSON) thay vì giao diện HTML.
*   **Ý nghĩa:** "Đây là cái máy bán hàng tự động, bỏ tiền (request) vào là nhả hàng (JSON) ra."

### `@Autowired` (hoặc Constructor Injection)
*   **Lý thuyết:** Dependency Injection (Tiêm phụ thuộc).
*   **Cú pháp:** 
    ```java
    public ProductController(ProductRepository repo) {
        this.productRepository = repo;
    }
    ```
*   **Tác dụng:** Spring tự động tìm và đưa (tiêm) đối tượng `ProductRepository` đã khởi tạo sẵn vào Controller.
*   **Ý nghĩa:** "Tôi cần cái kìm (Repository) để làm việc, Spring hãy tự đi lấy và đưa cho tôi, tôi không muốn tự đi mua (new Repository)."

### `@GetMapping`, `@PostMapping`
*   **Lý thuyết:** Định tuyến (Routing).
*   **Cú pháp:** `@GetMapping("/products")`
*   **Tác dụng:** Ánh xạ một đường dẫn URL (`/products`) với một hàm trong Java.
*   **Ý nghĩa:** "Nếu ai đó gõ địa chỉ này, hãy chạy hàm này."

### `@PathVariable` vs `@RequestParam`
*   **Lý thuyết:** Lấy tham số từ URL.
*   **Cú pháp:**
    *   `@PathVariable`: `/products/5` -> Lấy số 5. (Dùng định danh tài nguyên).
    *   `@RequestParam`: `/products?type=food` -> Lấy chữ "food". (Dùng lọc/tìm kiếm).
*   **Ý nghĩa:**
    *   `PathVariable`: "Lấy cái sản phẩm CỤ THỂ số 5."
    *   `RequestParam`: "Lấy NHỮNG sản phẩm CÓ tính chất là food."

---

## 3. REACTJS (Web Frontend)

### `const [state, setState] = useState(initialValue)`
*   **Lý thuyết:** Hook quản lý trạng thái.
*   **Cú pháp:** `const [count, setCount] = useState(0);`
*   **Tác dụng:** Khai báo biến `count` với giá trị đầu là 0. Khi muốn đổi giá trị, BẮT BUỘC phải dùng `setCount(5)`.
*   **Ý nghĩa:** "Đây là biến đặc biệt. Khi nó thay đổi, giao diện sẽ tự động vẽ lại (re-render) để hiển thị số mới."

### `useEffect(() => { ... }, [])`
*   **Lý thuyết:** Hook xử lý tác vụ phụ (Side Effects).
*   **Cú pháp:**
    ```javascript
    useEffect(() => {
        fetchData();
    }, []);
    ```
*   **Tác dụng:** Chạy đoạn code bên trong vào những thời điểm cụ thể.
    *   `[]` rỗng: Chỉ chạy 1 lần duy nhất khi trang vừa mở (giống `window.onload`).
    *   `[count]`: Chạy mỗi khi biến `count` thay đổi.
*   **Ý nghĩa:** "Khi trang web hiện lên xong xuôi, hãy làm việc này (ví dụ: gọi API lấy dữ liệu)."

### `map()`
*   **Lý thuyết:** Phương thức mảng (Array Method).
*   **Cú pháp:**
    ```javascript
    products.map(p => <div key={p.id}>{p.name}</div>)
    ```
*   **Tác dụng:** Biến đổi một danh sách dữ liệu thô thành một danh sách các thẻ HTML.
*   **Ý nghĩa:** "Với mỗi sản phẩm trong kho, hãy tạo ra một cái thẻ hiển thị tên của nó."

### `fetch()`
*   **Lý thuyết:** API của trình duyệt để gửi yêu cầu mạng (Network Request).
*   **Cú pháp:**
    ```javascript
    fetch('url')
      .then(res => res.json())
      .then(data => ...)
    ```
*   **Tác dụng:** Gửi tín hiệu đến Server để xin dữ liệu.
*   **Ý nghĩa:** "Alo Server (gọi điện), cho tôi xin danh sách món ăn. (Đợi...). Có rồi thì chuyển sang dạng JSON cho tôi đọc."

---

## 4. SQL (Database)

### `SELECT * FROM table`
*   **Lý thuyết:** Truy vấn dữ liệu.
*   **Ý nghĩa:** "Lấy tất cả mọi thứ trong bảng này ra đây."

### `JOIN`
*   **Lý thuyết:** Kết nối bảng.
*   **Cú pháp:** `SELECT * FROM orders JOIN customers ON orders.cust_id = customers.id`
*   **Tác dụng:** Ghép 2 bảng lại dựa trên một cột chung.
*   **Ý nghĩa:** "Bảng đơn hàng chỉ có ID khách, muốn biết tên khách thì phải nhìn sang bảng Khách hàng, ghép dòng tương ứng lại."

### `WHERE` vs `HAVING`
*   **Lý thuyết:** Lọc dữ liệu.
*   **Ý nghĩa:**
    *   `WHERE`: Lọc TRƯỚC khi gom nhóm (Group). Ví dụ: Lọc ra các đơn hàng ngày hôm nay.
    *   `HAVING`: Lọc SAU khi gom nhóm. Ví dụ: Lọc ra những khách hàng ĐÃ mua trên 10 triệu (phải tính tổng trước rồi mới lọc).

---

## 5. CHIẾN LƯỢC HỌC HIỂU BẢN CHẤT (LEARNING STRATEGY)

Để không học vẹt, bạn hãy áp dụng 4 bước sau cho mỗi cú pháp:

### Bước 1: Liên tưởng thực tế (Analogy)
Đừng cố nhớ định nghĩa khô khan. Hãy gắn nó với một hình ảnh đời thường.
*   *Ví dụ:* `Interface` giống như "Hợp đồng lao động". Bạn ký hợp đồng (implements) thì bạn phải làm đúng những việc đã ghi trong đó (override methods).
*   *Ví dụ:* `API` giống như "Người bồi bàn". Bạn (Frontend) không được vào bếp (Database), bạn phải gọi món qua bồi bàn (Backend API).

### Bước 2: Dịch sang tiếng Việt (Translation)
Hãy thử dịch dòng code sang một câu nói bình thường.
*   *Code:* `if (user == null) return;`
*   *Dịch:* "Nếu không thấy người dùng này đâu, thì thôi nghỉ, không làm gì nữa."

### Bước 3: Phá hoại để học (Break & Fix)
Đây là cách học nhanh nhất. Hãy thử... xóa hoặc sửa sai code xem nó báo lỗi gì.
*   *Thử:* Xóa dòng `setVisible(true)` -> Chạy thử -> Kết quả: App vẫn chạy nhưng không hiện cửa sổ.
    *   *=> Bài học:* À, thì ra hàm này là công tắc bật màn hình.
*   *Thử:* Đổi `@RestController` thành `@Controller` -> Gọi API -> Kết quả: Lỗi 404 hoặc trả về tên file thay vì JSON.
    *   *=> Bài học:* `@RestController` chuyên dùng cho API trả dữ liệu.

### Bước 4: Dạy lại cho người khác (Teach Back)
Hãy tưởng tượng bạn đang giải thích cho một người không biết gì về IT (ví dụ: em gái, bà ngoại).
*   *Thử nói:* "Cái `useState` này giống như cái bảng điểm điện tử ấy bà ạ. Mỗi lần trọng tài bấm nút (setState), số điểm trên bảng nó tự nhảy (re-render) mà không cần ai leo lên thay số thủ công."

---
**LỜI KHUYÊN CUỐI CÙNG:**
Trong buổi bảo vệ, nếu bạn quên thuật ngữ chuyên ngành, hãy dùng **Bước 1 (Liên tưởng)** để trả lời. Giáo viên thà nghe một ví dụ so sánh thông minh còn hơn nghe một định nghĩa sai.
