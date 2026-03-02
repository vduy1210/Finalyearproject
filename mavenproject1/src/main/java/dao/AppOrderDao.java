package dao;

import database.DatabaseConnector;
import model.Order;
import model.OrderDetails;
import java.sql.*;
import java.util.List;

public class AppOrderDao {

    public boolean createAppOrder(Order order, List<OrderDetails> details) {
        String sqlOrder = "INSERT INTO app_order (customer_id, user_id, order_date, total_amount, tax, discount, total, status, shipping_name, shipping_phone, shipping_email, table_number) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending', ?, ?, ?, ?)";

        // Fix Bug 6: Sử dụng câu lệnh INSERT trực tiếp với Product ID để tham chiếu
        // chính xác
        String sqlDetail = "INSERT INTO app_order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        // Fix Bug 6: Cập nhật tồn kho dựa trên ID thay vì tên sản phẩm để tránh sai sót
        // nếu tên trùng hoặc đổi tên
        String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";

        // Fix Bug 3: Sử dụng try-with-resources để đảm bảo Connection luôn được đóng,
        // tránh rò rỉ kết nối (Memory/Resource Leak)
        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction (Các thay đổi chưa được lưu ngay lập tức)

            // Sử dụng RETURN_GENERATED_KEYS để lấy ID của đơn hàng vừa tạo
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                // 1. Thêm dữ liệu vào bảng app_order
                System.out.println("1. Inserting into app_order table...");
                psOrder.setInt(1, order.getCustomerId());
                psOrder.setInt(2, order.getStaffId());
                psOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
                psOrder.setDouble(4, order.getTotalAmount());
                psOrder.setDouble(5, order.getTax());
                psOrder.setDouble(6, order.getDiscount());
                psOrder.setDouble(7, order.getTotalAmount());
                psOrder.setString(8, order.getShippingName());
                psOrder.setString(9, order.getShippingPhone());
                psOrder.setString(10, order.getShippingEmail());
                psOrder.setString(11, order.getTableNumber());

                psOrder.executeUpdate();

                // 2. Lấy Order ID vừa được sinh ra
                ResultSet rs = psOrder.getGeneratedKeys();
                int orderId = -1;
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating app order failed, no ID obtained.");
                }
                rs.close();

                // 3. Thêm chi tiết đơn hàng (Batch Processing)
                try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                    for (OrderDetails detail : details) {
                        psDetail.setInt(1, orderId);
                        psDetail.setInt(2, detail.getProductId()); // Sử dụng Product ID
                        psDetail.setInt(3, detail.getQuantity());
                        psDetail.setDouble(4, detail.getUnitPrice());
                        psDetail.addBatch(); // Thêm vào lô để xử lý một lần
                    }
                    psDetail.executeBatch(); // Thực thi lô lệnh
                }

                // 4. Cập nhật điểm tích lũy cho khách hàng (Loyalty Points)
                // Logic: Cộng cố định 10 điểm cho mỗi đơn hàng (Theo yêu cầu)
                if (order.getCustomerId() > 0) {
                    try (PreparedStatement psUpdatePoints = conn.prepareStatement(
                            "UPDATE customers SET accumulatedPoint = accumulatedPoint + ? WHERE id = ?")) {
                        double pointsEarned = 10.0;
                        psUpdatePoints.setDouble(1, pointsEarned);
                        psUpdatePoints.setInt(2, order.getCustomerId());
                        psUpdatePoints.executeUpdate();
                        System.out.println(
                                "Updated loyalty points for customer " + order.getCustomerId() + ": +" + pointsEarned);
                    }
                }

                // 4. Cập nhật tồn kho sản phẩm
                try (PreparedStatement psUpdateStock = conn.prepareStatement(sqlUpdateStock)) {
                    for (OrderDetails detail : details) {
                        psUpdateStock.setInt(1, detail.getQuantity());
                        psUpdateStock.setInt(2, detail.getProductId()); // Sử dụng Product ID để update chính xác
                        psUpdateStock.addBatch();
                    }
                    psUpdateStock.executeBatch();
                }

                conn.commit(); // Hoàn tất Transaction (Lưu tất cả thay đổi vào DB)
                System.out.println("=== App order creation completed successfully ===");
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Nếu có lỗi ở bước nào, hoàn tác tất cả các thay đổi trước đó
                System.err.println("=== SQL Error, Rolled back ===");
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("=== Connection Error ===");
            e.printStackTrace();
            return false;
        }
    }

    // ===== Reporting utilities for app orders =====
    public double getTotalRevenue(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][] {
                { "total_amount", "order_date" },
                { "total", "order_date" },
                { "total_amount", "created_at" },
                { "total", "created_at" }
        };
        for (String[] v : variants) {
            String totalCol = v[0];
            String dateCol = v[1];
            String sql = "SELECT COALESCE(SUM(" + totalCol + "), 0) AS revenue FROM app_order WHERE " + dateCol
                    + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getDouble("revenue");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getTotalRevenue variant (" + totalCol + "," + dateCol
                        + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0.0;
    }

    public long getOrderCount(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][] {
                { "order_date" },
                { "created_at" }
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COUNT(*) AS cnt FROM app_order WHERE " + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getLong("cnt");
                }
            } catch (SQLException e) {
                System.err.println(
                        "[AppOrderDao] SQL error in getOrderCount variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    public long getDistinctCustomerCount(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][] {
                { "order_date" },
                { "created_at" }
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COUNT(DISTINCT customer_id) AS cnt FROM app_order WHERE " + dateCol
                    + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getLong("cnt");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getDistinctCustomerCount variant (" + dateCol + "): "
                        + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    public long getProductsSold(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][] {
                { "order_date" },
                { "created_at" }
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COALESCE(SUM(d.quantity),0) AS qty FROM app_order_details d JOIN app_order o ON d.order_id = o.order_id WHERE o."
                    + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getLong("qty");
                }
            } catch (SQLException e) {
                System.err.println(
                        "[AppOrderDao] SQL error in getProductsSold variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    public java.util.Map<java.time.LocalDate, Double> getRevenueByDay(java.time.LocalDateTime from,
            java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Double> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[] { "order_date", "created_at" };
        String[] totalCols = new String[] { "total_amount", "total" };
        for (String dateCol : dateCols) {
            for (String totalCol : totalCols) {
                String sql = "SELECT DATE(" + dateCol + ") AS d, COALESCE(SUM(" + totalCol
                        + "),0) AS revenue FROM app_order WHERE " + dateCol + " BETWEEN ? AND ? GROUP BY DATE("
                        + dateCol + ") ORDER BY DATE(" + dateCol + ")";
                try (Connection conn = DatabaseConnector.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setTimestamp(1, Timestamp.valueOf(from));
                    ps.setTimestamp(2, Timestamp.valueOf(to));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            java.sql.Date d = rs.getDate("d");
                            if (d != null) {
                                java.time.LocalDate ld = d.toLocalDate();
                                double rev = rs.getDouble("revenue");
                                map.put(ld, rev);
                            }
                        }
                        return map;
                    }
                } catch (SQLException e) {
                    System.err.println("[AppOrderDao] SQL error in getRevenueByDay variant (" + totalCol + "," + dateCol
                            + "): " + e.getMessage());
                    e.printStackTrace();
                    // try next variant
                }
            }
        }
        return map;
    }

    public java.util.Map<java.time.LocalDate, Long> getOrderCountByDay(java.time.LocalDateTime from,
            java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Long> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[] { "order_date", "created_at" };
        for (String dateCol : dateCols) {
            String sql = "SELECT DATE(" + dateCol + ") AS d, COUNT(*) AS cnt FROM app_order WHERE " + dateCol
                    + " BETWEEN ? AND ? GROUP BY DATE(" + dateCol + ") ORDER BY DATE(" + dateCol + ")";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date d = rs.getDate("d");
                        if (d != null)
                            map.put(d.toLocalDate(), rs.getLong("cnt"));
                    }
                    return map;
                }
            } catch (SQLException e) {
                System.err.println(
                        "[AppOrderDao] SQL error in getOrderCountByDay variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }

    public java.util.Map<java.time.LocalDate, Long> getDistinctCustomerCountByDay(java.time.LocalDateTime from,
            java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Long> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[] { "order_date", "created_at" };
        for (String dateCol : dateCols) {
            String sql = "SELECT DATE(" + dateCol + ") AS d, COUNT(DISTINCT customer_id) AS cnt FROM app_order WHERE "
                    + dateCol + " BETWEEN ? AND ? GROUP BY DATE(" + dateCol + ") ORDER BY DATE(" + dateCol + ")";
            try (Connection conn = DatabaseConnector.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date d = rs.getDate("d");
                        if (d != null)
                            map.put(d.toLocalDate(), rs.getLong("cnt"));
                    }
                    return map;
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getDistinctCustomerCountByDay variant (" + dateCol
                        + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }
}
