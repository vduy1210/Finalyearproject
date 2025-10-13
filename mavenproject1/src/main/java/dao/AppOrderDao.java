package dao;

import database.DatabaseConnector;
import model.Order;
import model.OrderDetails;
import java.sql.*;
import java.util.List;

public class AppOrderDao {

    public boolean createAppOrder(Order order, List<OrderDetails> details) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        String sqlOrder = "INSERT INTO app_order (customer_id, user_id, order_date, total_amount, tax, discount, total, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";
        String sqlDetail = "INSERT INTO app_order_details (order_id, product_id, quantity, price) VALUES (?, (SELECT id FROM products WHERE name=?), ?, ?)";
        String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE name = ?";

        try {
            System.out.println("=== Starting app order creation (manual input) ===");
            System.out.println("Order details: " + order);
            System.out.println("Number of order details: " + details.size());
            
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into app_order table
            System.out.println("1. Inserting into app_order table...");
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getCustomerId());
            psOrder.setInt(2, order.getStaffId()); // staff_id sẽ được lưu vào user_id
            psOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            psOrder.setDouble(4, order.getTotalAmount());
            psOrder.setDouble(5, order.getTax());
            psOrder.setDouble(6, order.getDiscount());
            psOrder.setDouble(7, order.getTotalAmount());
            
            System.out.println("SQL App Order: " + sqlOrder);
            System.out.println("Customer ID: " + order.getCustomerId());
            System.out.println("Staff ID: " + order.getStaffId());
            System.out.println("Total Amount: " + order.getTotalAmount());
            
            int orderResult = psOrder.executeUpdate();
            System.out.println("App order insert result: " + orderResult);

            // 2. Get generated order ID
            System.out.println("2. Getting generated app order ID...");
            rs = psOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
                System.out.println("Generated app order ID: " + orderId);
            } else {
                throw new SQLException("Creating app order failed, no ID obtained.");
            }

            // 3. Insert order details into app_order_details
            System.out.println("3. Inserting app order details...");
            psDetail = conn.prepareStatement(sqlDetail);
            for (OrderDetails detail : details) {
                System.out.println("Processing detail: " + detail);
                psDetail.setInt(1, orderId);
                psDetail.setString(2, detail.getProductName());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getUnitPrice());
                psDetail.addBatch();
            }
            int[] detailResults = psDetail.executeBatch();
            System.out.println("App order details insert results: " + java.util.Arrays.toString(detailResults));

            // 4. Update product stock
            System.out.println("4. Updating product stock...");
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);
            for (OrderDetails detail : details) {
                psUpdateStock.setInt(1, detail.getQuantity());
                psUpdateStock.setString(2, detail.getProductName());
                psUpdateStock.addBatch();
            }
            int[] stockResults = psUpdateStock.executeBatch();
            System.out.println("Stock update results: " + java.util.Arrays.toString(stockResults));

            conn.commit();
            System.out.println("=== App order creation completed successfully ===");
            return true;

        } catch (SQLException e) {
            System.err.println("=== SQL Error during app order creation ===");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("=== General Error during app order creation ===");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (psUpdateStock != null) psUpdateStock.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== Reporting utilities for app orders =====
    public double getTotalRevenue(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][]{
                {"total_amount", "order_date"},
                {"total", "order_date"},
                {"total_amount", "created_at"},
                {"total", "created_at"}
        };
        for (String[] v : variants) {
            String totalCol = v[0];
            String dateCol = v[1];
            String sql = "SELECT COALESCE(SUM(" + totalCol + "), 0) AS revenue FROM app_order WHERE " + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble("revenue");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getTotalRevenue variant (" + totalCol + "," + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0.0;
    }

    public long getOrderCount(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][]{
                {"order_date"},
                {"created_at"}
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COUNT(*) AS cnt FROM app_order WHERE " + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong("cnt");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getOrderCount variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    public long getDistinctCustomerCount(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][]{
                {"order_date"},
                {"created_at"}
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COUNT(DISTINCT customer_id) AS cnt FROM app_order WHERE " + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong("cnt");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getDistinctCustomerCount variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    public long getProductsSold(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        String[][] variants = new String[][]{
                {"order_date"},
                {"created_at"}
        };
        for (String[] v : variants) {
            String dateCol = v[0];
            String sql = "SELECT COALESCE(SUM(d.quantity),0) AS qty FROM app_order_details d JOIN app_order o ON d.order_id = o.order_id WHERE o." + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong("qty");
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getProductsSold variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
                // try next variant
            }
        }
        return 0L;
    }

    /**
     * Get revenue aggregated by date (local date) for the given range.
     * Returns a map with LocalDate -> revenue (Double).
     */
    public java.util.Map<java.time.LocalDate, Double> getRevenueByDay(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Double> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[]{"order_date", "created_at"};
        String[] totalCols = new String[]{"total_amount", "total"};
        for (String dateCol : dateCols) {
            for (String totalCol : totalCols) {
                String sql = "SELECT DATE(" + dateCol + ") AS d, COALESCE(SUM(" + totalCol + "),0) AS revenue FROM app_order WHERE " + dateCol + " BETWEEN ? AND ? GROUP BY DATE(" + dateCol + ") ORDER BY DATE(" + dateCol + ")";
                try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
                    System.err.println("[AppOrderDao] SQL error in getRevenueByDay variant (" + totalCol + "," + dateCol + "): " + e.getMessage());
                    e.printStackTrace();
                    // try next variant
                }
            }
        }
        return map;
    }

    /**
     * Get order count aggregated by date (LocalDate) for the given range.
     */
    public java.util.Map<java.time.LocalDate, Long> getOrderCountByDay(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Long> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[]{"order_date", "created_at"};
        for (String dateCol : dateCols) {
            String sql = "SELECT DATE(" + dateCol + ") AS d, COUNT(*) AS cnt FROM app_order WHERE " + dateCol + " BETWEEN ? AND ? GROUP BY DATE(" + dateCol + ") ORDER BY DATE(" + dateCol + ")";
            try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date d = rs.getDate("d");
                        if (d != null) map.put(d.toLocalDate(), rs.getLong("cnt"));
                    }
                    return map;
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getOrderCountByDay variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Get distinct customer count aggregated by date (LocalDate) for the given range.
     */
    public java.util.Map<java.time.LocalDate, Long> getDistinctCustomerCountByDay(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        java.util.Map<java.time.LocalDate, Long> map = new java.util.LinkedHashMap<>();
        String[] dateCols = new String[]{"order_date", "created_at"};
        for (String dateCol : dateCols) {
            String sql = "SELECT DATE(" + dateCol + ") AS d, COUNT(DISTINCT customer_id) AS cnt FROM app_order WHERE " + dateCol + " BETWEEN ? AND ? GROUP BY DATE(" + dateCol + ") ORDER BY DATE(" + dateCol + ")";
            try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date d = rs.getDate("d");
                        if (d != null) map.put(d.toLocalDate(), rs.getLong("cnt"));
                    }
                    return map;
                }
            } catch (SQLException e) {
                System.err.println("[AppOrderDao] SQL error in getDistinctCustomerCountByDay variant (" + dateCol + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }
}
