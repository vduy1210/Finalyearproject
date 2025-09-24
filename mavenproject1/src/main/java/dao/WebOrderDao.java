package dao;

import database.DatabaseConnector;
import model.Order;
import model.OrderDetails;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WebOrderDao {

    public boolean createWebOrder(Order order, List<OrderDetails> details) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rs = null;

        String sqlOrder = "INSERT INTO web_order (customer_id, staff_id, order_date, total_amount, tax, discount, total, status, shipping_name, shipping_phone, shipping_email, table_number) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending', ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO web_order_details (order_id, product_id, quantity, price) VALUES (?, (SELECT id FROM products WHERE name=?), ?, ?)";

        try {
            System.out.println("=== Starting web order creation ===");
            System.out.println("Order details: " + order);
            System.out.println("Number of order details: " + details.size());
            
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into web_order table
            System.out.println("1. Inserting into web_order table...");
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
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
            
            System.out.println("SQL Web Order: " + sqlOrder);
            System.out.println("Customer ID: " + order.getCustomerId());
            System.out.println("Staff ID: " + order.getStaffId());
            System.out.println("Total Amount: " + order.getTotalAmount());
            
            int orderResult = psOrder.executeUpdate();
            System.out.println("Web order insert result: " + orderResult);

            // 2. Get generated order ID
            System.out.println("2. Getting generated web order ID...");
            rs = psOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
                System.out.println("Generated web order ID: " + orderId);
            } else {
                throw new SQLException("Creating web order failed, no ID obtained.");
            }

            // 3. Insert order details into web_order_details
            System.out.println("3. Inserting web order details...");
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
            System.out.println("Web order details insert results: " + java.util.Arrays.toString(detailResults));

            // 4. Skip stock update for web orders (reservations only)
            System.out.println("4. Skipping stock update for web orders (reservations only)...");

            conn.commit();
            System.out.println("=== Web order creation completed successfully ===");
            return true;

        } catch (SQLException e) {
            System.err.println("=== SQL Error during web order creation ===");
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
            System.err.println("=== General Error during web order creation ===");
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
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== Reporting utilities for web orders =====
    public double getTotalRevenue(LocalDateTime from, LocalDateTime to) {
        // Try multiple schema variants
        String[][] variants = new String[][]{
                {"total", "order_date"},
                {"total", "created_at"},
                {"total_amount", "order_date"},
                {"total_amount", "created_at"},
                {"total", "orderDate"}
        };
        for (String[] v : variants) {
            String totalCol = v[0];
            String dateCol = v[1];
            String sql = "SELECT COALESCE(SUM(" + totalCol + "), 0) AS revenue FROM web_order WHERE " + dateCol + " BETWEEN ? AND ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("revenue");
                    }
                }
            } catch (SQLException e) {
                // try next variant
            }
        }
        return 0.0;
    }

    public List<Order> listWebOrders(LocalDateTime from, LocalDateTime to) {
        // Try multiple schema variants, alias to stable names
        String[][] variants = new String[][]{
                {"order_id", "order_date", "total"},
                {"order_id", "created_at", "total"},
                {"order_id", "order_date", "total_amount"},
                {"order_id", "created_at", "total_amount"}
        };
        for (String[] v : variants) {
            String idCol = v[0];
            String dateCol = v[1];
            String totalCol = v[2];
            String sql = "SELECT " + idCol + " AS oid, customer_id, staff_id, " + dateCol + " AS odate, " + totalCol + " AS ototal, tax, discount " +
                    "FROM web_order WHERE " + dateCol + " BETWEEN ? AND ? ORDER BY " + dateCol;
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(from));
                ps.setTimestamp(2, Timestamp.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    List<Order> orders = new ArrayList<>();
                    while (rs.next()) {
                        Order o = new Order();
                        o.setOrderId(rs.getInt("oid"));
                        o.setCustomerId(rs.getInt("customer_id"));
                        o.setStaffId(rs.getInt("staff_id"));
                        Timestamp ts = rs.getTimestamp("odate");
                        o.setOrderDate(ts != null ? ts.toLocalDateTime() : null);
                        o.setTotalAmount(rs.getDouble("ototal"));
                        o.setTax(rs.getDouble("tax"));
                        o.setDiscount(rs.getDouble("discount"));
                        orders.add(o);
                    }
                    if (!orders.isEmpty()) return orders;
                }
            } catch (SQLException e) {
                // try next variant
            }
        }
        return new ArrayList<>();
    }

    public boolean updateWebOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE web_order SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWebOrder(int orderId) {
        String sql = "DELETE FROM web_order WHERE order_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
