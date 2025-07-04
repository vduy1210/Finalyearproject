package view;

import dao.GetProduct;
import dao.OrderDao;
import model.Product;
import model.Order;
import model.OrderDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;
import view.CustomerInfoDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
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
    // --- Các hằng số và biến ---
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);

    private static final Font FONT_BUTTON = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_LABEL = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 16);

    private JTable productTable;
    private JTable cartTable;
    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;
    private JTextField totalTextField;
    private JTextField customerPhoneField;
    private JTextField discountField;
    private JSpinner quantitySpinner;
    private NumberFormat currencyFormat;

    public OrderPanel() {
        // ... Hàm khởi tạo và bố cục (giữ nguyên) ...
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
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

    private JScrollPane createProductListPanelFromDatabase() {
        String[] columns = {"Product Name", "Price"};
        productModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        loadProducts();

        productTable = new JTable(productModel);
        productTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        productTable.setRowHeight(28);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(createTitledBorder(" List of Products "));
        scroll.getViewport().setBackground(WHITE);
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

        JButton addButton = new JButton("Add to cart");
        addButton.setFont(FONT_BUTTON);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(12, 20, 12, 20));

        addButton.addActionListener(e -> handleAddToCart());

        JButton removeButton = new JButton("Remove from cart");
        removeButton.setFont(FONT_BUTTON);
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeButton.setBackground(DANGER_COLOR);
        removeButton.setForeground(WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(new EmptyBorder(12, 20, 12, 20));

        removeButton.addActionListener(e -> handleRemoveFromCart());

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
        cartTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        cartTable.setRowHeight(28);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(createTitledBorder(" Cart "));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15; panel.add(createLabel("Customer Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35; customerPhoneField = new JTextField(); panel.add(customerPhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15; panel.add(createLabel("Discount:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35; discountField = new JTextField("0"); 
        discountField.addCaretListener(e -> updateTotal()); // Auto-update total when discount changes
        panel.add(discountField, gbc);

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
        JButton exportReceiptButton = createButton("Export Receipt");
        JButton cancelButton = createButton("Cancel Order");
        JButton refreshButton = createButton("🔄 Refresh Products");

        // Add event listeners
        createOrderButton.addActionListener(e -> handleCreateOrder());
        exportReceiptButton.addActionListener(e -> handleExportReceipt());
        cancelButton.addActionListener(e -> handleCancelOrder());
        refreshButton.addActionListener(e -> refreshProductList());

        panel.add(createOrderButton);
        panel.add(exportReceiptButton);
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

        // Apply discount if any
        double discount = 0.0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException e) {
            discount = 0.0;
        }

        double finalTotal = subtotalSum - discount;
        totalTextField.setText(currencyFormat.format(finalTotal));
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

        // Validate cart is not empty
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Please add products.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get discount value
        double discount = 0.0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount value!", "Invalid Discount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate final total
        double subtotal = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            subtotal += (Double) cartModel.getValueAt(i, 2);
        }
        double finalTotal = subtotal - discount;

        // Confirm order
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Create order for phone: " + customerPhone + "?\nTotal: " + currencyFormat.format(finalTotal), 
            "Confirm Order", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get or create customer
                int customerId = getOrCreateCustomer(customerPhone);
                if (customerId == -1) {
                    JOptionPane.showMessageDialog(this, "Error creating customer!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get current staff ID (assuming staff ID 1 for now)
                int staffId = getCurrentStaffId();

                // Create order object
                Order order = new Order();
                order.setCustomerId(customerId);
                order.setStaffId(staffId);
                order.setOrderDate(LocalDateTime.now());
                order.setTotalAmount(finalTotal);
                order.setTax(0.0); // No tax for now
                order.setDiscount(discount);

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
                OrderDao orderDao = new OrderDao();
                boolean success = orderDao.createOrder(order, orderDetails);

                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Order created successfully!\nOrder Total: " + currencyFormat.format(finalTotal), 
                        "Order Created", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear the form
                    cartModel.setRowCount(0);
                    customerPhoneField.setText("");
                    discountField.setText("0");
                    totalTextField.setText("0");
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

    private void handleExportReceipt() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in cart to export!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: Implement receipt generation and export
        JOptionPane.showMessageDialog(this, "Receipt export functionality will be implemented here!", 
            "Export Receipt", JOptionPane.INFORMATION_MESSAGE);
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
            discountField.setText("0");
            totalTextField.setText("0");
            JOptionPane.showMessageDialog(this, "Order cancelled successfully!", 
                "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Database Helper Methods ---
    
    private int getOrCreateCustomer(String customerPhone) {
        Connection conn = null;
        PreparedStatement findStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        ResultSet generatedKeys = null;
        try {
            conn = database.DatabaseConnector.getConnection();
            // Try to find existing customer by phone
            String findSql = "SELECT id, name, accumulatedPoint FROM customers WHERE phone = ?";
            findStmt = conn.prepareStatement(findSql);
            findStmt.setString(1, customerPhone);
            rs = findStmt.executeQuery();
            if (rs.next()) {
                int customerId = rs.getInt("id");
                String customerName = rs.getString("name");
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
                return customerId;
            }
            // New customer: show dialog
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CustomerInfoDialog dialog = new CustomerInfoDialog(parentFrame, customerPhone);
            dialog.setVisible(true);
            if (!dialog.isConfirmed()) {
                return -1; // User cancelled
            }
            String customerName = dialog.getCustomerName();
            String customerEmail = dialog.getCustomerEmail();
            // Insert new customer with 10 points
            String insertSql = "INSERT INTO customers (name, phone, email, accumulatedPoint) VALUES (?, ?, ?, 10)";
            insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, customerName);
            insertStmt.setString(2, customerPhone);
            insertStmt.setString(3, customerEmail);
            int rowsAffected = insertStmt.executeUpdate();
            generatedKeys = insertStmt.getGeneratedKeys();
            int customerId = -1;
            if (generatedKeys.next()) {
                customerId = generatedKeys.getInt(1);
                JOptionPane.showMessageDialog(this,
                    "New customer created successfully!\nName: " + customerName + "\nInitial points: 10",
                    "New Customer", JOptionPane.INFORMATION_MESSAGE);
            }
            return customerId;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
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
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MAIN_COLOR, 2),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_TITLE,
                LIGHT_TEXT
        );
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
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        return button;
    }
}
