package view;

import dao.GetProduct;
import dao.AppOrderDao;
import model.Product;
import model.Order;
import model.OrderDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;


import javax.swing.*;
import util.UIUtils;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderPanel extends JPanel {
    public interface OrderListener {
        void onOrderCreated();
    }

    private OrderListener orderListener;
    
    // Helper class to store customer information
    private static class CustomerInfo {
        int id;
        String name;
        String phone;
        String email;
        
        CustomerInfo(int id, String name, String phone, String email) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
        }
    }
    
    // --- Các hằng số và biến ---
    // Light theme to match the modernized UI elsewhere
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Gray tinted white
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    private static final Color TABLE_BG = new Color(0xE5, 0xE7, 0xEB);
    private static final Color ROW_ALT = new Color(0xEF, 0xF1, 0xF5);
    private static final Color LIGHT_TEXT = new Color(108, 117, 125);

    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);

    private JTable productTable;
    private JTable cartTable;
    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;
    private JTextField totalTextField;
    private JTextField customerPhoneField;
    private JComboBox<String> tableNumberCombo;
    private JSpinner quantitySpinner;
    private NumberFormat currencyFormat;
    private double currentDiscountPercent = 0.0; // Store customer discount %

    public OrderPanel() {
        // ... Hàm khởi tạo và bố cục (giữ nguyên) ...
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        GridBagConstraints layout = new GridBagConstraints();
        layout.insets = new Insets(5, 5, 5, 5);

        layout.gridx = 0; layout.gridy = 0;
        layout.weightx = 0.45; layout.weighty = 0.9;
        layout.fill = GridBagConstraints.BOTH;
        add(createProductListPanelFromDatabase(), layout);

        layout.gridx = 1;
        layout.weightx = 0.1;
        layout.fill = GridBagConstraints.VERTICAL;
        add(createAddToCartPanel(), layout);

        layout.gridx = 2;
        layout.weightx = 0.45;
        layout.fill = GridBagConstraints.BOTH;
        add(createCartPanel(), layout);

        layout.gridx = 0; layout.gridy = 1; layout.gridwidth = 3;
        layout.weighty = 0;
        layout.fill = GridBagConstraints.HORIZONTAL;
        add(createOrderInfoPanel(), layout);

        layout.gridy = 2;
        add(createActionButtons(), layout);
    }

    public void setOrderListener(OrderListener listener) {
        this.orderListener = listener;
    }

    // Load an existing order's items into the cart by orderId
    public void loadOrderIntoCart(int orderId) {
        loadOrderIntoCart(orderId, null);
    }
    
    // Load an existing order's items and table number into the cart
    public void loadOrderIntoCart(int orderId, String tableNumber) {
        // Clear current cart
        if (cartModel != null) {
            cartModel.setRowCount(0);
        }
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "SELECT p.name AS product_name, od.quantity, od.price FROM web_order_details od " +
                         "JOIN products p ON od.product_id = p.id WHERE od.order_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String productName = rs.getString("product_name");
                        int quantity = rs.getInt("quantity");
                        double price = rs.getDouble("price");
                        double subtotal = quantity * price;
                        cartModel.addRow(new Object[]{productName, quantity, subtotal});
                    }
                }
            }

            // Load customer phone and table number if present on order
            String phoneSql = "SELECT COALESCE(shipping_phone, c.phone) AS phone, o.table_number " +
                              "FROM web_order o " +
                              "LEFT JOIN customers c ON o.customer_id = c.id WHERE o.order_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(phoneSql)) {
                ps2.setInt(1, orderId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        String phone = rs2.getString("phone");
                        if (phone != null && customerPhoneField != null) {
                            customerPhoneField.setText(phone);
                        }
                        
                        // Set table number if available from database or parameter
                        String dbTableNumber = rs2.getString("table_number");
                        String tableToSet = (tableNumber != null) ? tableNumber : dbTableNumber;
                        
                        if (tableToSet != null && tableNumberCombo != null) {
                            // Find and select the table in combo box
                            for (int i = 0; i < tableNumberCombo.getItemCount(); i++) {
                                if (tableNumberCombo.getItemAt(i).equals(tableToSet)) {
                                    tableNumberCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order into cart: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        updateTotal();
    }

    private JScrollPane createProductListPanelFromDatabase() {
        String[] columns = {"Product Name", "Price"};
        productModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        loadProducts();

    productTable = new JTable(productModel);
    productTable.setFont(new Font("Segoe UI", Font.PLAIN, 17));
    productTable.setRowHeight(44);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setShowGrid(false);
        productTable.setIntercellSpacing(new Dimension(0, 0));
        productTable.setSelectionBackground(new Color(232, 244, 253));
        productTable.setSelectionForeground(COLOR_TEXT);
        productTable.setBackground(TABLE_BG);

        // Header styling and zebra rows
        JTableHeader pth = productTable.getTableHeader();
    pth.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pth.setBackground(WHITE);
        pth.setForeground(COLOR_TEXT);
        pth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer pAltRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                return c;
            }
        };
        for (int i = 0; i < productModel.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(pAltRenderer);
        }

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        scroll.getViewport().setBackground(TABLE_BG);
        return scroll;
    }

    private JPanel createAddToCartPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Quantity:");
        label.setForeground(LIGHT_TEXT);
        label.setFont(FONT_LABEL);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(FONT_LABEL);
        quantitySpinner.setMaximumSize(new Dimension(80, 40));

    JButton addButton = new RoundedButton("Add");
    addButton.setFont(FONT_BUTTON);
    addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    addButton.setBackground(SUCCESS_COLOR);
    addButton.setForeground(WHITE);
    addButton.setFocusPainted(false);
    addButton.setBorder(new EmptyBorder(12, 20, 12, 20));
    UIUtils.styleActionButton(addButton, 160);

    // icons removed per user preference
        addButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleAddToCart(); }
        });
    JButton removeButton = new RoundedButton("Remove");
    removeButton.setFont(FONT_BUTTON);
    removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    removeButton.setBackground(DANGER_COLOR);
    removeButton.setForeground(WHITE);
    removeButton.setFocusPainted(false);
    removeButton.setBorder(new EmptyBorder(12, 20, 12, 20));
    UIUtils.styleActionButton(removeButton, 160);

    // icons removed per user preference
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleRemoveFromCart(); }
        });

        panel.add(Box.createVerticalGlue());
        panel.add(label);
        panel.add(quantitySpinner);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(addButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(removeButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JScrollPane createCartPanel() {
        String[] columns = {"Item", "Quantity", "Subtotal"};
        cartModel = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

    cartTable = new JTable(cartModel);
    cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 17));
    cartTable.setRowHeight(44);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 0));
        cartTable.setSelectionBackground(new Color(232, 244, 253));
        cartTable.setSelectionForeground(COLOR_TEXT);
        cartTable.setBackground(TABLE_BG);

        JTableHeader cth = cartTable.getTableHeader();
    cth.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cth.setBackground(WHITE);
        cth.setForeground(COLOR_TEXT);
        cth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer cAltRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                return c;
            }
        };
        for (int i = 0; i < cartModel.getColumnCount(); i++) {
            cartTable.getColumnModel().getColumn(i).setCellRenderer(cAltRenderer);
        }

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        scroll.getViewport().setBackground(TABLE_BG);
        return scroll;
    }

    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15; panel.add(createLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35; 
        customerPhoneField = new JTextField(); 
        customerPhoneField.addCaretListener(new javax.swing.event.CaretListener() {
            @Override public void caretUpdate(javax.swing.event.CaretEvent e) { 
                updateDiscountFromCustomer(); 
            }
        });
        panel.add(customerPhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15; panel.add(createLabel("Table:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35; 
        // Create table numbers 1-20
        String[] tableNumbers = new String[21];
        tableNumbers[0] = "Select Table";
        for (int i = 1; i <= 20; i++) {
            tableNumbers[i] = "Table " + i;
        }
        tableNumberCombo = new JComboBox<>(tableNumbers);
        panel.add(tableNumberCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15; panel.add(createLabel("Total:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35; totalTextField = new JTextField("0"); totalTextField.setEditable(false); panel.add(totalTextField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.15; panel.add(createLabel("Date:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35; JTextField dateField = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date())); dateField.setEditable(false); panel.add(dateField, gbc);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
    panel.setOpaque(false);

        JButton createOrderButton = createButton("Create Order");
        JButton printOrderButton = createButton("Print Order");
        JButton cancelButton = createButton("Cancel Order");
        JButton refreshButton = createButton("Refresh Products");

        // Add event listeners
        createOrderButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleCreateOrder(); }
        });
        printOrderButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handlePrintOrder(); }
        });
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleCancelOrder(); }
        });
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { refreshProductList(); }
        });

        panel.add(createOrderButton);
        panel.add(printOrderButton);
        panel.add(cancelButton);
        panel.add(refreshButton);

        return panel;
    }

    // --- Các hàm logic ---

    private void handleAddToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để thêm!", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productName = (String) productTable.getValueAt(selectedRow, 0);
        double price = (Double) productTable.getValueAt(selectedRow, 1);
        int quantityToAdd = (Integer) quantitySpinner.getValue();

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (productName.equals(cartModel.getValueAt(i, 0))) {
                int currentQuantity = (Integer) cartModel.getValueAt(i, 1);
                int newQuantity = currentQuantity + quantityToAdd;
                double newSubtotal = newQuantity * price;

                cartModel.setValueAt(newQuantity, i, 1);
                cartModel.setValueAt(newSubtotal, i, 2);

                updateTotal();
                return;
            }
        }

        double subtotal = price * quantityToAdd;
        Object[] newRowData = {productName, quantityToAdd, subtotal};
        cartModel.addRow(newRowData);

        updateTotal();
    }

    // THAY ĐỔI: Cập nhật logic của hàm xóa khỏi giỏ hàng
    private void handleRemoveFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trong giỏ hàng để xóa!", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantityToRemove = (Integer) quantitySpinner.getValue();
        int currentQuantity = (Integer) cartModel.getValueAt(selectedRow, 1);

        int newQuantity = currentQuantity - quantityToRemove;

        if (newQuantity > 0) {
            // Nếu số lượng còn lại > 0, chỉ cập nhật lại
            double subtotal = (Double) cartModel.getValueAt(selectedRow, 2);
            double price = subtotal / currentQuantity; // Tính lại giá đơn vị
            double newSubtotal = newQuantity * price;

            cartModel.setValueAt(newQuantity, selectedRow, 1);
            cartModel.setValueAt(newSubtotal, selectedRow, 2);
        } else {
            // Nếu số lượng <= 0, xóa hoàn toàn dòng đó
            cartModel.removeRow(selectedRow);
        }

        // Cập nhật lại tổng tiền
        updateTotal();
    }

    private void updateTotal() {
        double subtotalSum = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            subtotalSum += (Double) cartModel.getValueAt(i, 2);
        }

        // Apply discount percentage based on customer tier
        double discountAmount = subtotalSum * (currentDiscountPercent / 100.0);
        double finalTotal = subtotalSum - discountAmount;
        totalTextField.setText(currencyFormat.format(finalTotal));
    }
    
    private void updateDiscountFromCustomer() {
        String customerPhone = customerPhoneField.getText().trim();
        if (customerPhone.isEmpty() || !customerPhone.matches("\\d+")) {
            currentDiscountPercent = 0.0;
            updateTotal();
            return;
        }
        
        // Check if customer exists and get their discount
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            String sql = "SELECT accumulatedPoint FROM customers WHERE phone = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerPhone);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                float points = rs.getFloat("accumulatedPoint");
                currentDiscountPercent = CustomerManagementPanel.getDiscountForPoints(points);
            } else {
                currentDiscountPercent = 0.0;
            }
            
            rs.close();
            ps.close();
            conn.close();
            updateTotal();
        } catch (SQLException e) {
            currentDiscountPercent = 0.0;
            updateTotal();
        }
    }

    public void loadProducts() {
        if (productModel != null) {
            productModel.setRowCount(0);
        }

        List<Product> products = GetProduct.getAllProducts();
        for (Product p : products) {
            productModel.addRow(new Object[]{p.getName(), p.getPrice()});
        }
    }

    public void refreshProductList() {
        loadProducts();
        JOptionPane.showMessageDialog(this, "Product list has been refreshed.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Missing Action Handlers ---
    
    private void handleCreateOrder() {
        // Validate customer phone
        String customerPhone = customerPhoneField.getText().trim();
        if (customerPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer phone number!", "Missing Customer Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Basic phone number validation (optional)
        if (!customerPhone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (numbers only)!", "Invalid Phone Number", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate table selection
        if (tableNumberCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a table number!", "Missing Table Number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate cart is not empty
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Please add products.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate final total with discount
        double subtotal = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            subtotal += (Double) cartModel.getValueAt(i, 2);
        }
        double discountAmount = subtotal * (currentDiscountPercent / 100.0);
        double finalTotal = subtotal - discountAmount;
        
        String tableNumber = (String) tableNumberCombo.getSelectedItem();

        // Confirm order with table and discount info
        String confirmMessage = String.format(
            "Create order:\n" +
            "Customer: %s\n" +
            "Table: %s\n" +
            "Discount: %.1f%%\n" +
            "Subtotal: %s\n" +
            "Discount Amount: %s\n" +
            "Total: %s",
            customerPhone, tableNumber, currentDiscountPercent,
            currencyFormat.format(subtotal),
            currencyFormat.format(discountAmount),
            currencyFormat.format(finalTotal)
        );
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage, 
            "Confirm Order", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get or create customer and retrieve full info
                CustomerInfo customerInfo = getOrCreateCustomer(customerPhone);
                if (customerInfo == null) {
                    JOptionPane.showMessageDialog(this, "Error creating customer!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get current staff ID (assuming staff ID 1 for now)
                int staffId = getCurrentStaffId();
                
                // Get table number
                String selectedTable = (String) tableNumberCombo.getSelectedItem();

                // Create order object
                Order order = new Order();
                order.setCustomerId(customerInfo.id);
                order.setStaffId(staffId);
                order.setOrderDate(LocalDateTime.now());
                order.setTotalAmount(finalTotal);
                order.setTax(0.0); // No tax for now
                order.setDiscount(discountAmount);
                order.setTableNumber(selectedTable);
                
                // Set shipping information from customer data
                order.setShippingName(customerInfo.name);
                order.setShippingPhone(customerInfo.phone);
                order.setShippingEmail(customerInfo.email);

                // Create order details list
                ArrayList<OrderDetails> orderDetails = new ArrayList<>();
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    String productName = (String) cartModel.getValueAt(i, 0);
                    int quantity = (Integer) cartModel.getValueAt(i, 1);
                    double subtotalItem = (Double) cartModel.getValueAt(i, 2);
                    double unitPrice = subtotalItem / quantity;

                    OrderDetails detail = new OrderDetails();
                    detail.setProductName(productName);
                    detail.setQuantity(quantity);
                    detail.setUnitPrice(unitPrice);
                    orderDetails.add(detail);
                }

                // Save order to database
                            AppOrderDao appOrderDao = new AppOrderDao();
            boolean success = appOrderDao.createAppOrder(order, orderDetails);

                if (success) {
                    String successMessage = String.format(
                        "Order created successfully!\n" +
                        "Table: %s\n" +
                        "Discount: %.1f%%\n" +
                        "Total: %s",
                        tableNumber, currentDiscountPercent,
                        currencyFormat.format(finalTotal)
                    );
                    JOptionPane.showMessageDialog(this, 
                        successMessage, 
                        "Order Created", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear the form
                    cartModel.setRowCount(0);
                    customerPhoneField.setText("");
                    tableNumberCombo.setSelectedIndex(0);
                    totalTextField.setText("0");
                    currentDiscountPercent = 0.0;

                    // Notify listener so other panels can refresh
                    if (orderListener != null) {
                        orderListener.onOrderCreated();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create order!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creating order: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePrintOrder() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in cart to print!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate customer phone
        String customerPhone = customerPhoneField.getText().trim();
        if (customerPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer phone number!", "Missing Customer Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate table selection
        if (tableNumberCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a table number!", "Missing Table Number", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tableNumber = (String) tableNumberCombo.getSelectedItem();
        
        // Calculate totals
        double subtotal = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            subtotal += (Double) cartModel.getValueAt(i, 2);
        }
        double discountAmount = subtotal * (currentDiscountPercent / 100.0);
        double finalTotal = subtotal - discountAmount;

        // Create printable content
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("           ORDER RECEIPT\n");
        receipt.append("=====================================\n");
        receipt.append("Customer: ").append(customerPhone).append("\n");
        receipt.append("Table: ").append(tableNumber).append("\n");
        receipt.append("Date: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
        receipt.append("-------------------------------------\n");
        
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String product = (String) cartModel.getValueAt(i, 0);
            int quantity = (Integer) cartModel.getValueAt(i, 1);
            double itemTotal = (Double) cartModel.getValueAt(i, 2);
            receipt.append(String.format("%-20s x%d\n", product, quantity));
            receipt.append(String.format("                    %s\n", currencyFormat.format(itemTotal)));
        }
        
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("Subtotal:           %s\n", currencyFormat.format(subtotal)));
        receipt.append(String.format("Discount (%.1f%%):    -%s\n", currentDiscountPercent, currencyFormat.format(discountAmount)));
        receipt.append("=====================================\n");
        receipt.append(String.format("TOTAL:              %s\n", currencyFormat.format(finalTotal)));
        receipt.append("=====================================\n");

        // Display print dialog
        JTextArea textArea = new JTextArea(receipt.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        try {
            boolean printed = textArea.print();
            if (printed) {
                JOptionPane.showMessageDialog(this, "Order printed successfully!", "Print Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error printing order: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelOrder() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!", "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this order?", 
            "Cancel Order", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            cartModel.setRowCount(0);
            customerPhoneField.setText("");
            tableNumberCombo.setSelectedIndex(0);
            totalTextField.setText("0");
            currentDiscountPercent = 0.0;
            JOptionPane.showMessageDialog(this, "Order cancelled successfully!", 
                "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Database Helper Methods ---
    
    private CustomerInfo getOrCreateCustomer(String customerPhone) {
        Connection conn = null;
        PreparedStatement findStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        ResultSet generatedKeys = null;
        try {
            conn = database.DatabaseConnector.getConnection();
            // Try to find existing customer by phone
            String findSql = "SELECT id, name, email, accumulatedPoint FROM customers WHERE phone = ?";
            findStmt = conn.prepareStatement(findSql);
            findStmt.setString(1, customerPhone);
            rs = findStmt.executeQuery();
            if (rs.next()) {
                int customerId = rs.getInt("id");
                String customerName = rs.getString("name");
                String customerEmail = rs.getString("email");
                float currentPoints = rs.getFloat("accumulatedPoint");
                // Add 10 points for existing customer
                String updateSql = "UPDATE customers SET accumulatedPoint = accumulatedPoint + 10 WHERE id = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, customerId);
                int updateResult = updateStmt.executeUpdate();
                if (updateResult > 0) {
                    float newPoints = currentPoints + 10;
                    JOptionPane.showMessageDialog(this,
                        "Welcome back, " + customerName + "!\n+10 points added to your account.\nTotal points: " + newPoints,
                        "Existing Customer", JOptionPane.INFORMATION_MESSAGE);
                }
                return new CustomerInfo(customerId, customerName, customerPhone, customerEmail);
            }
            // New customer: show dialog
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CustomerInfoDialog dialog = new CustomerInfoDialog(parentFrame, customerPhone);
            dialog.setVisible(true);
            if (!dialog.isConfirmed()) {
                return null; // User cancelled
            }
            String customerName = dialog.getCustomerName();
            String customerEmail = dialog.getCustomerEmail();
            // Insert new customer with 10 points
            String insertSql = "INSERT INTO customers (name, phone, email, accumulatedPoint) VALUES (?, ?, ?, 10)";
            insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, customerName);
            insertStmt.setString(2, customerPhone);
            insertStmt.setString(3, customerEmail);
            insertStmt.executeUpdate();
            generatedKeys = insertStmt.getGeneratedKeys();
            int customerId = -1;
            if (generatedKeys.next()) {
                customerId = generatedKeys.getInt(1);
                JOptionPane.showMessageDialog(this,
                    "New customer created successfully!\nName: " + customerName + "\nInitial points: 10",
                    "New Customer", JOptionPane.INFORMATION_MESSAGE);
                return new CustomerInfo(customerId, customerName, customerPhone, customerEmail);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (generatedKeys != null) generatedKeys.close();
                if (findStmt != null) findStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private int getCurrentStaffId() {
        // For now, return a default staff ID (1)
        // In a real application, this would get the logged-in user's ID
        return 1;
    }

    // --- Các hàm tiện ích (giữ nguyên) ---
    // Removed unused createTitledBorder method to avoid unused warning

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_TEXT);
        return label;
    }

    private JButton createButton(String text) {
    JButton button = new RoundedButton(text);
    button.setFont(FONT_BUTTON);
    button.setBackground(MAIN_COLOR);
    button.setForeground(WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(10, 20, 10, 20)));
        return button;
    }
}
