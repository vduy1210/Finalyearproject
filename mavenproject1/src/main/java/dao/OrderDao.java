package dao;

import database.DatabaseConnector;
import model.Order;
import model.OrderDetails;
import java.sql.*;
import java.util.List;

public class OrderDao {

    public boolean createOrder(Order order, List<OrderDetails> details) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        String sqlOrder = "INSERT INTO orders (customer_id, staff_id, order_date, total_amount, tax, discount) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, (SELECT id FROM products WHERE name=?), ?, ?)";
        String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE name = ?";

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into orders table
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getCustomerId());
            psOrder.setInt(2, order.getStaffId());
            psOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            psOrder.setDouble(4, order.getTotalAmount());
            psOrder.setDouble(5, order.getTax());
            psOrder.setDouble(6, order.getDiscount());
            psOrder.executeUpdate();

            // 2. Get generated order ID
            rs = psOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            // 3. Insert order details
            psDetail = conn.prepareStatement(sqlDetail);
            for (OrderDetails detail : details) {
                psDetail.setInt(1, orderId);
                psDetail.setString(2, detail.getProductName());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getUnitPrice());
                psDetail.addBatch();
            }
            psDetail.executeBatch();

            // 4. Update product stock
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);
            for (OrderDetails detail : details) {
                psUpdateStock.setInt(1, detail.getQuantity());
                psUpdateStock.setString(2, detail.getProductName());
                psUpdateStock.addBatch();
            }
            psUpdateStock.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
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
}