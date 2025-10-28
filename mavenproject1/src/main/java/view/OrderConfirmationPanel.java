package view;

import dao.WebOrderDao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import javax.swing.Timer;



public class OrderConfirmationPanel extends JPanel {
    // --- Constants and variables ---
    // Light theme to match redesigned panels
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Gray tinted white
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    private static final Color TABLE_BG = new Color(0xE5, 0xE7, 0xEB);
    private static final Color ROW_ALT = new Color(0xEF, 0xF1, 0xF5);

    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);

    private JTable orderTable;
    private JTable orderDetailsTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel orderDetailsModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private NumberFormat currencyFormat;
    
    // Auto-refresh and highlighting variables
    private Timer refreshTimer;
    private Set<Integer> newOrderIds;
    private Set<Integer> lastSeenOrderIds;
    
    // Notification for new orders
    private JLabel notificationLabel;
    private Timer notifyHideTimer;
    
    // DAO instances
    private WebOrderDao webOrderDao;
    


    public OrderConfirmationPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        
        // Initialize DAO
        webOrderDao = new WebOrderDao();
        
        // Initialize auto-refresh and highlighting variables
    newOrderIds = new HashSet<>();
    lastSeenOrderIds = new HashSet<>();
        
        // Initialize auto-refresh timer (5 seconds)
        refreshTimer = new Timer(5000, new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                // Do not interrupt user while searching/filtering
                if (!isFilterActive()) {
                    refreshAccordingToState();
                    highlightNewOrders();
                }
            }
        });
        refreshTimer.start();
        
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setOpaque(false);

        JLabel searchLabel = createLabel("Search:");
        searchField = new JTextField(20);
        searchField.setFont(FONT_LABEL);

        JLabel filterLabel = createLabel("Filter by:");
        filterComboBox = new JComboBox<>(new String[]{"All Orders", "Today", "This Week", "This Month"});
        filterComboBox.setFont(FONT_LABEL);

        JButton searchButton = createButton("🔍 Search");
        JButton refreshButton = createButton("🔄 Refresh");

        searchButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { performSearch(); }
        });
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { refreshAccordingToState(); }
        });

        left.add(searchLabel);
        left.add(searchField);
        left.add(filterLabel);
        left.add(filterComboBox);
        left.add(searchButton);
        left.add(refreshButton);

    notificationLabel = new JLabel("");
    notificationLabel.setFont(FONT_LABEL);
    notificationLabel.setForeground(MAIN_COLOR);
        notificationLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        notificationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        right.setOpaque(false);
        right.add(notificationLabel);

        panel.add(left, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

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
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        orderTable.setRowHeight(44);
        orderTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadOrderDetails();
            }
        });
        
        // Set custom renderer for status column (column index 6)
        orderTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Header styling and zebra rows for orders table
        JTableHeader oth = orderTable.getTableHeader();
        oth.setFont(FONT_TITLE);
        oth.setBackground(WHITE);
        oth.setForeground(COLOR_TEXT);
        oth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer ordersAltRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                return c;
            }
        };
        for (int i = 0; i < orderModel.getColumnCount(); i++) {
            if (i != 6) { // keep custom renderer for status column
                orderTable.getColumnModel().getColumn(i).setCellRenderer(ordersAltRenderer);
            }
        }

        orderTable.setShowGrid(false);
        orderTable.setIntercellSpacing(new Dimension(0, 0));
        orderTable.setSelectionBackground(new Color(232, 244, 253));
        orderTable.setSelectionForeground(COLOR_TEXT);
        orderTable.setBackground(TABLE_BG);

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        scroll.getViewport().setBackground(TABLE_BG);
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
        orderDetailsTable.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        orderDetailsTable.setRowHeight(44);
        orderDetailsTable.setShowGrid(false);
        orderDetailsTable.setIntercellSpacing(new Dimension(0, 0));
        orderDetailsTable.setSelectionBackground(new Color(232, 244, 253));
        orderDetailsTable.setSelectionForeground(COLOR_TEXT);
        orderDetailsTable.setBackground(TABLE_BG);

        JTableHeader dth = orderDetailsTable.getTableHeader();
        dth.setFont(FONT_TITLE);
        dth.setBackground(WHITE);
        dth.setForeground(COLOR_TEXT);
        dth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer detailsAltRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                return c;
            }
        };
        for (int i = 0; i < orderDetailsModel.getColumnCount(); i++) {
            orderDetailsTable.getColumnModel().getColumn(i).setCellRenderer(detailsAltRenderer);
        }

        JScrollPane scroll = new JScrollPane(orderDetailsTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        scroll.getViewport().setBackground(TABLE_BG);
        return scroll;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setOpaque(false);

    JButton cancelOrderButton = createButton("Cancel");
    // icons removed per user preference
    JButton doneButton = createButton("Complete");
    // icons removed per user preference
    JButton printButton = createButton("Print");
    // icons removed per user preference
    JButton deleteButton = createButton("Delete");
    // icons removed per user preference
    JButton mergeButton = createButton("Merge");
    // icons removed per user preference
    JButton gotoOrderButton = createButton("Open");
    // icons removed per user preference

    // Removed Confirm (Processing) action - replaced by Open which loads the order into the cart
    cancelOrderButton.setToolTipText("Cancel selected order");
    cancelOrderButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { updateOrderStatus("Cancelled"); }});

    doneButton.setToolTipText("Mark selected order as completed");
    doneButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { updateOrderStatus("Completed"); }});

    printButton.setToolTipText("Print receipt for selected order");
    printButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { printOrder(); }});

    deleteButton.setToolTipText("Delete selected order");
    deleteButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { deleteOrder(); }});

    mergeButton.setToolTipText("Merge selected orders into one");
    mergeButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { mergeSelectedOrders(); }});

    gotoOrderButton.setToolTipText("Open selected order in Order panel");
    gotoOrderButton.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(java.awt.event.ActionEvent e) { goToOrderWithCart(); }});

        panel.add(cancelOrderButton);
        panel.add(doneButton);
        panel.add(printButton);
        panel.add(deleteButton);
        panel.add(mergeButton);
        panel.add(gotoOrderButton);

        return panel;
    }

    private void loadOrders() {
        // Load fresh from DB and detect brand-new orders compared to lastSeenOrderIds
        orderModel.setRowCount(0);
        newOrderIds.clear();
        Set<Integer> currentIds = new HashSet<>();

        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "SELECT o.order_id, COALESCE(o.table_number, 'N/A') AS table_number, " +
                         "COALESCE(o.shipping_name, c.name) AS customer_name, " +
                         "COALESCE(o.shipping_phone, c.phone) AS phone, o.order_date, " +
                         "COALESCE(o.total, o.total_amount) as total_display, o.status " +
                         "FROM web_order o LEFT JOIN customers c ON o.customer_id = c.id " +
                         "ORDER BY o.order_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int detectedNew = 0;
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                currentIds.add(orderId);

                // Check if this is a newly seen order compared to last load
                if (!lastSeenOrderIds.contains(orderId)) {
                    newOrderIds.add(orderId);
                    detectedNew++;
                }
                
                Object[] row = {
                    orderId,
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

            // Update lastSeenOrderIds to current snapshot to avoid repeated notifications
            lastSeenOrderIds.clear();
            lastSeenOrderIds.addAll(currentIds);

            if (detectedNew > 0) {
                showNewOrdersNotification(detectedNew);
            }
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
            String sql = "SELECT p.name as product_name, od.quantity, od.price, (od.quantity * od.price) as subtotal FROM web_order_details od " +
                         "JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
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
            sql.append("o.status FROM web_order o LEFT JOIN customers c ON o.customer_id = c.id ");

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
                int orderId = rs.getInt("order_id");
                Object[] row = {
                    orderId,
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
            
            // Repaint to show highlighting
            orderTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching orders: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAccordingToState() {
        if (isFilterActive()) {
            performSearch();
        } else {
            loadOrders();
        }
    }

    private boolean isFilterActive() {
        String term = searchField.getText() != null ? searchField.getText().trim() : "";
        Object filter = filterComboBox.getSelectedItem();
        return (term.length() > 0) || (filter != null && !"All Orders".equals(filter.toString()));
    }
    
    private void highlightNewOrders() {
        // Force table repaint to show new highlighting
        orderTable.repaint();
    }

    private void showNewOrdersNotification(int count) {
        // Update label text and make it visible
        notificationLabel.setText("🆕 New orders: " + count);
        notificationLabel.setVisible(true);
        // Play a short beep
        Toolkit.getDefaultToolkit().beep();

        // Auto-hide after 5 seconds; reset timer if already running
        if (notifyHideTimer != null && notifyHideTimer.isRunning()) {
            notifyHideTimer.stop();
        }
        notifyHideTimer = new Timer(5000, new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                notificationLabel.setText("");
                notificationLabel.setVisible(false);
            }
        });
        notifyHideTimer.setRepeats(false);
        notifyHideTimer.start();
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
        
        boolean success = webOrderDao.updateWebOrderStatus(orderId, newStatus);
        if (success) {
            JOptionPane.showMessageDialog(this, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOrders();
            orderTable.repaint(); // Repaint to show updated colors
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Removed unused exportReport()

    private void printOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to print", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (Integer) orderModel.getValueAt(selectedRow, 0);
        
        try {
            // Get order details from database
            Connection conn = database.DatabaseConnector.getConnection();
            
            // Get order information
            String orderSql = "SELECT o.*, c.name as customer_name, c.phone as customer_phone " +
                           "FROM web_order o LEFT JOIN customers c ON o.customer_id = c.id " +
                           "WHERE o.order_id = ?";
            PreparedStatement orderPs = conn.prepareStatement(orderSql);
            orderPs.setInt(1, orderId);
            ResultSet orderRs = orderPs.executeQuery();
            
            if (!orderRs.next()) {
                JOptionPane.showMessageDialog(this, "Order not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get order details
            String detailsSql = "SELECT p.name as product_name, od.quantity, od.price, (od.quantity * od.price) as subtotal " +
                              "FROM web_order_details od JOIN products p ON od.product_id = p.id " +
                              "WHERE od.order_id = ?";
            PreparedStatement detailsPs = conn.prepareStatement(detailsSql);
            detailsPs.setInt(1, orderId);
            ResultSet detailsRs = detailsPs.executeQuery();
            
            // Create receipt content
            StringBuilder receipt = new StringBuilder();
            receipt.append("========================================\n");
            receipt.append("           ORDER RECEIPT\n");
            receipt.append("========================================\n");
            receipt.append("Order ID: #").append(orderId).append("\n");
            receipt.append("Date: ").append(orderRs.getTimestamp("order_date").toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
            receipt.append("Customer: ").append(orderRs.getString("customer_name")).append("\n");
            receipt.append("Phone: ").append(orderRs.getString("customer_phone")).append("\n");
            receipt.append("Table: ").append(orderRs.getString("table_number")).append("\n");
            receipt.append("Status: ").append(orderRs.getString("status")).append("\n");
            receipt.append("----------------------------------------\n");
            receipt.append("ITEMS:\n");
            
            double total = 0.0;
            while (detailsRs.next()) {
                String productName = detailsRs.getString("product_name");
                int quantity = detailsRs.getInt("quantity");
                double price = detailsRs.getDouble("price");
                double subtotal = detailsRs.getDouble("subtotal");
                total += subtotal;
                
                receipt.append(String.format("%-20s %3d x %8.0f = %10.0f\n", 
                    productName.length() > 20 ? productName.substring(0, 17) + "..." : productName,
                    quantity, price, subtotal));
            }
            
            receipt.append("----------------------------------------\n");
            receipt.append(String.format("Subtotal: %25.0f VND\n", total));
            receipt.append(String.format("Discount: %25.0f VND\n", orderRs.getDouble("discount")));
            receipt.append(String.format("Tax: %30.0f VND\n", orderRs.getDouble("tax")));
            receipt.append("----------------------------------------\n");
            receipt.append(String.format("TOTAL: %28.0f VND\n", orderRs.getDouble("total")));
            receipt.append("========================================\n");
            receipt.append("Thank you for your order!\n");
            receipt.append("========================================\n");
            
            // Show receipt in a dialog
            JTextArea receiptArea = new JTextArea(receipt.toString());
            receiptArea.setFont(new Font("Courier New", Font.PLAIN, 12));
            receiptArea.setEditable(false);
            receiptArea.setBackground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            // Create print button
            JButton printButton = new RoundedButton("🖨️ Print Receipt");
            util.UIUtils.styleActionButton(printButton, 160);
            printButton.addActionListener(new java.awt.event.ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        receiptArea.print();
                        JOptionPane.showMessageDialog(OrderConfirmationPanel.this, "Receipt sent to printer!", 
                            "Print Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(OrderConfirmationPanel.this, "Print error: " + ex.getMessage(), 
                            "Print Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // Create export button
            JButton exportButton = new RoundedButton("💾 Export as Text");
            util.UIUtils.styleActionButton(exportButton, 160);
            exportButton.addActionListener(new java.awt.event.ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new java.io.File("receipt_" + orderId + ".txt"));
                    if (fileChooser.showSaveDialog(OrderConfirmationPanel.this) == JFileChooser.APPROVE_OPTION) {
                        try {
                            java.io.File file = fileChooser.getSelectedFile();
                            java.nio.file.Files.write(file.toPath(), receipt.toString().getBytes());
                            JOptionPane.showMessageDialog(OrderConfirmationPanel.this, "Receipt exported to: " + file.getAbsolutePath(), 
                                "Export Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(OrderConfirmationPanel.this, "Export error: " + ex.getMessage(), 
                                "Export Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            
            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(printButton);
            buttonPanel.add(exportButton);
            
            // Create main panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Show receipt dialog
            JDialog receiptDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Order Receipt #" + orderId, true);
            receiptDialog.add(mainPanel);
            receiptDialog.pack();
            receiptDialog.setLocationRelativeTo(this);
            receiptDialog.setVisible(true);
            
            // Close database connections
            detailsRs.close();
            detailsPs.close();
            orderRs.close();
            orderPs.close();
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private void mergeSelectedOrders() {
        int[] selectedRows = orderTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length < 2) {
            JOptionPane.showMessageDialog(this, "Please select at least two orders to merge.", 
                "Need Multiple Selections", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Collect order IDs
        List<Integer> orderIds = new ArrayList<>();
        for (int rowIndex : selectedRows) {
            orderIds.add((Integer) orderModel.getValueAt(rowIndex, 0));
        }

        int targetOrderId = orderIds.get(0);

        // Validate same table and status Pending
        String targetTable = null;
        String targetStatus = null;
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            try {
                for (Integer oid : orderIds) {
                    String q = "SELECT table_number, status FROM web_order WHERE order_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(q)) {
                        ps.setInt(1, oid);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                String tn = rs.getString("table_number");
                                String st = rs.getString("status");
                                if (targetTable == null) targetTable = tn;
                                if (targetStatus == null) targetStatus = st;
                                if (!String.valueOf(targetTable).equals(String.valueOf(tn))) {
                                    JOptionPane.showMessageDialog(this, "Orders must have the same table to merge.", 
                                        "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                                    rs.close();
                                    conn.close();
                                    return;
                                }
                                if (!"Pending".equalsIgnoreCase(st)) {
                                    JOptionPane.showMessageDialog(this, "Only Pending orders can be merged.", 
                                        "Invalid Status", JOptionPane.ERROR_MESSAGE);
                                    rs.close();
                                    conn.close();
                                    return;
                                }
                            }
                        }
                    }
                }
            } finally {
                // keep connection open for next step
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during validation: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Merge these orders into order #" + targetOrderId + ": " + orderIds + "?", 
            "Confirm Merge", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Execute merge inside a transaction
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            try {
                // For each source order (exclude target)
                for (int i = 1; i < orderIds.size(); i++) {
                    int sourceOrderId = orderIds.get(i);

                    // Move/aggregate details
                    String fetchDetails = "SELECT product_id, quantity, price FROM web_order_details WHERE order_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(fetchDetails)) {
                        ps.setInt(1, sourceOrderId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                int productId = rs.getInt("product_id");
                                int quantity = rs.getInt("quantity");
                                double price = rs.getDouble("price");

                                // Try to update existing row with same product and price
                                String updateExisting = "UPDATE web_order_details SET quantity = quantity + ? WHERE order_id = ? AND product_id = ? AND price = ?";
                                try (PreparedStatement up = conn.prepareStatement(updateExisting)) {
                                    up.setInt(1, quantity);
                                    up.setInt(2, targetOrderId);
                                    up.setInt(3, productId);
                                    up.setDouble(4, price);
                                    int updated = up.executeUpdate();
                                    if (updated == 0) {
                                        // Insert new row into target
                                        String insert = "INSERT INTO web_order_details(order_id, product_id, quantity, price) VALUES(?, ?, ?, ?)";
                                        try (PreparedStatement ins = conn.prepareStatement(insert)) {
                                            ins.setInt(1, targetOrderId);
                                            ins.setInt(2, productId);
                                            ins.setInt(3, quantity);
                                            ins.setDouble(4, price);
                                            ins.executeUpdate();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Delete source order details and order
                    try (PreparedStatement delDet = conn.prepareStatement("DELETE FROM web_order_details WHERE order_id = ?")) {
                        delDet.setInt(1, sourceOrderId);
                        delDet.executeUpdate();
                    }
                    try (PreparedStatement delOrd = conn.prepareStatement("DELETE FROM web_order WHERE order_id = ?")) {
                        delOrd.setInt(1, sourceOrderId);
                        delOrd.executeUpdate();
                    }
                }

                // Recalculate total for target order
                double newTotal = 0.0;
                try (PreparedStatement sumPs = conn.prepareStatement("SELECT SUM(quantity * price) AS total FROM web_order_details WHERE order_id = ?")) {
                    sumPs.setInt(1, targetOrderId);
                    try (ResultSet rs = sumPs.executeQuery()) {
                        if (rs.next()) {
                            newTotal = rs.getDouble("total");
                        }
                    }
                }

                try (PreparedStatement updOrd = conn.prepareStatement("UPDATE web_order SET total = ?, total_amount = ?, order_date = NOW() WHERE order_id = ?")) {
                    updOrd.setDouble(1, newTotal);
                    updOrd.setDouble(2, newTotal);
                    updOrd.setInt(3, targetOrderId);
                    updOrd.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }

            JOptionPane.showMessageDialog(this, "Orders merged successfully into order #" + targetOrderId + ".", 
                "Merge Complete", JOptionPane.INFORMATION_MESSAGE);
            loadOrders();
            orderTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during merge: " + e.getMessage(), 
                "Merge Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Removed unused viewReceipt()

    private void goToOrderWithCart() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int orderId = (Integer) orderModel.getValueAt(selectedRow, 0);

        // Find parent frame and switch to Order panel, loading the cart
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainApplication) {
            MainApplication app = (MainApplication) window;
            app.showOrderWithOrderId(orderId);
        } else if (window instanceof JFrame) {
            // Try to find MainApplication via parent chain if wrapped
            JFrame frame = (JFrame) window;
            if (frame instanceof MainApplication) {
                ((MainApplication) frame).showOrderWithOrderId(orderId);
            } else {
                JOptionPane.showMessageDialog(this, "Cannot navigate to Order panel in this context.", 
                    "Navigation Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot navigate to Order panel.", 
                "Navigation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Removed unused createTitledBorder()

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_TEXT);
        return label;
    }

    private JButton createButton(String text) {
    JButton button = new RoundedButton(text);
    util.UIUtils.styleActionButton(button, 140);
    button.setFont(FONT_BUTTON);
    button.setBackground(MAIN_COLOR);
    button.setForeground(WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(10, 20, 10, 20)));
        return button;
    }
    
    // Method to stop timer when panel is closed
    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
    
    // Custom cell renderer for status column with color highlighting
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Zebra background when not selected
            if (!isSelected) {
                c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
            }

            if (value != null) {
                String status = value.toString();
                int modelRow = table.convertRowIndexToModel(row);
                int orderId = (Integer) table.getModel().getValueAt(modelRow, 0);

                // Text color by status (uses constants to keep theme consistent)
                if ("Pending".equalsIgnoreCase(status)) {
                    c.setForeground(MAIN_COLOR);
                } else if ("Processing".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
                    c.setForeground(SUCCESS_COLOR);
                } else if ("Cancelled".equalsIgnoreCase(status) || "Canceled".equalsIgnoreCase(status)) {
                    c.setForeground(DANGER_COLOR);
                } else {
                    c.setForeground(COLOR_TEXT);
                }

                // Prepend new icon if this is a newly arrived order
                String displayText = status;
                if (newOrderIds.contains(orderId)) {
                    displayText = "🆕 " + displayText;
                }
                ((JLabel) c).setText(displayText);
            }
            
            return c;
        }
    }

} 