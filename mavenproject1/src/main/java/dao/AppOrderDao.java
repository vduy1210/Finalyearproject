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
}
