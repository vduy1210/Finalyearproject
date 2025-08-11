package view;

import dao.OrderDao;
import model.Order;
import model.OrderDetails;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderConfirmationPanel extends JPanel {
    // --- Constants and variables ---
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);

    private static final Font FONT_BUTTON = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_LABEL = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 16);

    private JTable orderTable;
    private JTable orderDetailsTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel orderDetailsModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private NumberFormat currencyFormat;

    public OrderConfirmationPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        GridBagConstraints layout = new GridBagConstraints();
        layout.insets = new Insets(5, 5, 5, 5);

        // --- Search and Filter Panel ---
        layout.gridx = 0; layout.gridy = 0;
        layout.weightx = 1.0; layout.weighty = 0.05;
        layout.fill = GridBagConstraints.HORIZONTAL;
        add(createSearchFilterPanel(), layout);

        // --- Orders Table ---
        layout.gridy = 1;
        layout.gridx = 0;
        layout.weightx = 0.25; layout.weighty = 0.9;
        layout.fill = GridBagConstraints.BOTH;
        add(createOrderTablePanel(), layout);

        // --- Order Details Panel ---
        layout.gridx = 1; layout.gridy = 1;
        layout.weightx = 0.75;
        add(createOrderDetailsPanel(), layout);

        // --- Action Buttons ---
        layout.gridx = 0; layout.gridy = 2;
        layout.gridwidth = 2;
        layout.weightx = 1.0; layout.weighty = 0.05;
        layout.fill = GridBagConstraints.HORIZONTAL;
        add(createActionButtons(), layout);

        loadOrders();
    }

    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel searchLabel = createLabel("Search:");
        searchField = new JTextField(20);
        searchField.setFont(FONT_LABEL);

        JLabel filterLabel = createLabel("Filter by:");
        filterComboBox = new JComboBox<>(new String[]{"All Orders", "Today", "This Week", "This Month"});
        filterComboBox.setFont(FONT_LABEL);

        JButton searchButton = createButton("🔍 Search");
        JButton refreshButton = createButton("🔄 Refresh");

        searchButton.addActionListener(e -> performSearch());
        refreshButton.addActionListener(e -> refreshData());

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(filterLabel);
        panel.add(filterComboBox);
        panel.add(searchButton);
        panel.add(refreshButton);

        return panel;
    }

    private JScrollPane createOrderTablePanel() {
        String[] columns = {"Order ID", "Table", "Customer Name", "Phone", "Date", "Total", "Status"};
        orderModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        orderTable.setRowHeight(28);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadOrderDetails();
            }
        });

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(createTitledBorder(" Order Confirmation "));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JScrollPane createOrderDetailsPanel() {
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        orderDetailsModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        orderDetailsTable = new JTable(orderDetailsModel);
        orderDetailsTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        orderDetailsTable.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(orderDetailsTable);
        scroll.setBorder(createTitledBorder(" Order Details "));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setOpaque(false);

        JButton confirmButton = createButton("✅ Confirm (Processing)");
        JButton cancelOrderButton = createButton("✖ Cancel Order");
        JButton exportButton = createButton("📊 Export Report");
        JButton printButton = createButton("🖨️ Print Order");
        JButton deleteButton = createButton("🗑️ Delete Order");
        JButton viewReceiptButton = createButton("📄 View Receipt");

        confirmButton.addActionListener(e -> updateOrderStatus("Processing"));
        cancelOrderButton.addActionListener(e -> updateOrderStatus("Cancelled"));
        exportButton.addActionListener(e -> exportReport());
        printButton.addActionListener(e -> printOrder());
        deleteButton.addActionListener(e -> deleteOrder());
        viewReceiptButton.addActionListener(e -> viewReceipt());

        panel.add(confirmButton);
        panel.add(cancelOrderButton);
        panel.add(exportButton);
        panel.add(printButton);
        panel.add(deleteButton);
        panel.add(viewReceiptButton);

        return panel;
    }

    private void loadOrders() {
        orderModel.setRowCount(0);
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "SELECT o.order_id, COALESCE(o.table_number, 'N/A') AS table_number, " +
                         "COALESCE(o.shipping_name, c.name) AS customer_name, " +
                         "COALESCE(o.shipping_phone, c.phone) AS phone, o.order_date, " +
                         "COALESCE(o.total, o.total_amount) as total_display, o.status " +
                         "FROM orders o LEFT JOIN customers c ON o.customer_id = c.id " +
                         "ORDER BY o.order_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("table_number"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getTimestamp("order_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    currencyFormat.format(rs.getDouble("total_display")),
                    rs.getString("status")
                };
                orderModel.addRow(row);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderDetails() {
        orderDetailsModel.setRowCount(0);
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int orderId = (Integer) orderModel.getValueAt(selectedRow, 0);
        
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "SELECT p.name as product_name, oi.quantity, oi.price, (oi.quantity * oi.price) as subtotal FROM order_items oi " +
                         "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ? " +
                         "UNION ALL " +
                         "SELECT p.name as product_name, od.quantity, od.price, (od.quantity * od.price) as subtotal FROM order_details od " +
                         "JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.setInt(2, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    currencyFormat.format(rs.getDouble("price")),
                    currencyFormat.format(rs.getDouble("subtotal"))
                };
                orderDetailsModel.addRow(row);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String filter = (String) filterComboBox.getSelectedItem();

        orderModel.setRowCount(0);
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT o.order_id, COALESCE(o.table_number, 'N/A') AS table_number, ");
            sql.append("COALESCE(o.shipping_name, c.name) AS customer_name, ");
            sql.append("COALESCE(o.shipping_phone, c.phone) AS phone, o.order_date, COALESCE(o.total, o.total_amount) as total_display, ");
            sql.append("o.status FROM orders o LEFT JOIN customers c ON o.customer_id = c.id ");

            List<Object> params = new ArrayList<>();
            boolean hasSearch = !searchTerm.isEmpty();

            if (hasSearch) {
                sql.append("WHERE COALESCE(o.shipping_name, c.name) LIKE ? OR COALESCE(o.shipping_phone, c.phone) LIKE ? OR o.order_id LIKE ? OR o.table_number LIKE ? ");
                params.add("%" + searchTerm + "%");
                params.add("%" + searchTerm + "%");
                params.add("%" + searchTerm + "%");
                params.add("%" + searchTerm + "%");
            }

            if ("Today".equals(filter)) {
                sql.append(hasSearch ? "AND " : "WHERE ");
                sql.append("DATE(o.order_date) = CURRENT_DATE ");
            } else if ("This Week".equals(filter)) {
                sql.append(hasSearch ? "AND " : "WHERE ");
                sql.append("YEARWEEK(o.order_date) = YEARWEEK(CURRENT_DATE) ");
            } else if ("This Month".equals(filter)) {
                sql.append(hasSearch ? "AND " : "WHERE ");
                sql.append("YEAR(o.order_date) = YEAR(CURRENT_DATE) AND MONTH(o.order_date) = MONTH(CURRENT_DATE) ");
            }

            sql.append("ORDER BY o.order_date DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("table_number"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getTimestamp("order_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    currencyFormat.format(rs.getDouble("total_display")),
                    rs.getString("status")
                };
                orderModel.addRow(row);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching orders: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        loadOrders();
        searchField.setText("");
        filterComboBox.setSelectedIndex(0);
    }

    private void updateOrderStatus(String newStatus) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int orderId = (Integer) orderModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Update status of order #" + orderId + " to '" + newStatus + "'?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int affected = ps.executeUpdate();
            ps.close();
            conn.close();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this, "Export functionality will be implemented here", 
            "Export Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to print", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Print functionality will be implemented here", 
            "Print Order", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (Integer) orderModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete order #" + orderId + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement delete functionality
            JOptionPane.showMessageDialog(this, "Delete functionality will be implemented here", 
                "Delete Order", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewReceipt() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view receipt", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Receipt view functionality will be implemented here", 
            "View Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(FONT_TITLE);
        border.setTitleColor(LIGHT_TEXT);
        return border;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(LIGHT_TEXT);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(MAIN_COLOR);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }
} 