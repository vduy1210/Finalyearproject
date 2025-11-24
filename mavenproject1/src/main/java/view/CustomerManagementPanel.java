package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerManagementPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.of("vi","VN"));
    
    // Stat card labels to update
    private JLabel totalCustomersValue;
    private JLabel platinumValue;
    private JLabel goldValue;
    private JLabel totalRevenueValue;
    private JLabel avgSpentValue;

    // Theme constants to match other panels
    private static final Color BACKGROUND_COLOR = new Color(0xDD, 0xE3, 0xEA);
    private static final Color WHITE = Color.WHITE;
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    private static final Color TABLE_BG = new Color(0xE5, 0xE7, 0xEB);
    private static final Color ROW_ALT = new Color(0xEF, 0xF1, 0xF5);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    // FONT_BUTTON removed (not used)

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(16,16,16,16));
        setBackground(BACKGROUND_COLOR);

        // Header: title + subtitle on left, search + add on right
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);
        JLabel title = new JLabel("Customer Management");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT);
        JLabel subtitle = new JLabel("Manage customer profiles, loyalty points, and purchase history");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(110,110,110));
        titleWrap.add(title, BorderLayout.NORTH);
        titleWrap.add(subtitle, BorderLayout.SOUTH);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);
        searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(360, 34));
        searchField.setToolTipText("Search by name, phone, or email...");
        RoundedButton addCustomer = new RoundedButton("Add Customer");
        util.UIUtils.styleActionButton(addCustomer, 140);
        addCustomer.setBackground(MAIN_COLOR); addCustomer.setForeground(WHITE);
        addCustomer.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) { showAddDialog(); } });
        rightHeader.add(searchField);
        rightHeader.add(addCustomer);

        header.add(titleWrap, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1,5,12,0));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(12,0,12,0));
        
        JPanel totalCustomersCard = createStatCard("Total Customers", "0", new Color(95,165,245));
        totalCustomersValue = getStatCardValueLabel(totalCustomersCard);
        
        JPanel platinumCard = createStatCard("Platinum", "0", new Color(148, 71, 255));
        platinumValue = getStatCardValueLabel(platinumCard);
        
        JPanel goldCard = createStatCard("Gold", "0", new Color(246, 190, 66));
        goldValue = getStatCardValueLabel(goldCard);
        
        JPanel totalRevenueCard = createStatCard("Total Revenue", "0", new Color(67, 181, 129));
        totalRevenueValue = getStatCardValueLabel(totalRevenueCard);
        
        JPanel avgSpentCard = createStatCard("Avg Spent", "0", new Color(244, 140, 82));
        avgSpentValue = getStatCardValueLabel(avgSpentCard);
        
        statsRow.add(totalCustomersCard);
        statsRow.add(platinumCard);
        statsRow.add(goldCard);
        statsRow.add(totalRevenueCard);
        statsRow.add(avgSpentCard);
        add(statsRow, BorderLayout.BEFORE_FIRST_LINE);

        model = new DefaultTableModel(new String[]{"ID","Customer","Phone","Email","Orders","Total Spent","Points","Tier","Last Visit"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setSelectionForeground(COLOR_TEXT);
        table.setBackground(TABLE_BG);

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_LABEL);
        th.setBackground(WHITE);
        th.setForeground(COLOR_TEXT);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer alt = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                return c;
            }
        };
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(alt);
        }

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(TABLE_BG);
        tablePanel.add(sp, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // add action buttons below the table (operate on the selected row)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionsPanel.setOpaque(false);
        final JButton viewBtn = new RoundedButton("View");
        final JButton editBtn = new RoundedButton("Edit");
        final JButton delBtn = new RoundedButton("Delete");
        final JButton configTiersBtn = new RoundedButton("Configure Tiers");
        util.UIUtils.styleActionButton(viewBtn, 100);
        util.UIUtils.styleActionButton(editBtn, 100);
        util.UIUtils.styleActionButton(delBtn, 100);
        util.UIUtils.styleActionButton(configTiersBtn, 150);
        viewBtn.setEnabled(false); editBtn.setEnabled(false); delBtn.setEnabled(false);
        actionsPanel.add(viewBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(delBtn);
        actionsPanel.add(Box.createHorizontalStrut(20)); // Add spacing
        actionsPanel.add(configTiersBtn);
        add(actionsPanel, BorderLayout.SOUTH);

        // wire actions
        viewBtn.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) {
            int r = table.getSelectedRow(); if (r < 0) { JOptionPane.showMessageDialog(CustomerManagementPanel.this, "Please select a customer to view.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
            int modelRow = table.convertRowIndexToModel(r);
            int id = (Integer) model.getValueAt(modelRow, 0);
            showCustomerViewDialog(id);
        } });
        editBtn.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) { showEditDialog(); } });
        delBtn.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelected(); } });
        configTiersBtn.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) { showTierConfigDialog(); } });

        searchField.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(ActionEvent e) { loadCustomers(); } });

        // enable buttons on row selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean has = table.getSelectedRow() >= 0;
                viewBtn.setEnabled(has);
                editBtn.setEnabled(has);
                delBtn.setEnabled(has);
            }
        });

        // initial load
        loadCustomers();
    }

    private void loadCustomers() {
        model.setRowCount(0);
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            // Modified query to include BOTH app_order and web_order
            String sql = "SELECT c.id, c.name, c.phone, c.email, c.accumulatedPoint, "
                    + "COUNT(DISTINCT COALESCE(ao.order_id, wo.order_id)) AS orders, "
                    + "COALESCE(SUM(COALESCE(ao.total, ao.total_amount)), 0) + COALESCE(SUM(COALESCE(wo.total, wo.total_amount)), 0) AS total_spent, "
                    + "GREATEST(MAX(ao.order_date), MAX(wo.order_date)) AS last_visit "
                    + "FROM customers c "
                    + "LEFT JOIN app_order ao ON ao.customer_id = c.id "
                    + "LEFT JOIN web_order wo ON wo.customer_id = c.id "
                    + "WHERE (c.name LIKE ? OR c.phone LIKE ? OR c.email LIKE ?) "
                    + "GROUP BY c.id, c.name, c.phone, c.email, c.accumulatedPoint ORDER BY c.name";
            String term = (searchField.getText() != null) ? searchField.getText().trim() : "";
            String like = "%" + term + "%";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                int orders = rs.getInt("orders");
                double totalSpent = rs.getDouble("total_spent");
                java.sql.Timestamp ts = rs.getTimestamp("last_visit");
                String lastVisit = (ts == null) ? "" : ts.toLocalDateTime().format(df);
                // Get actual points and calculate tier based on configuration
                float points = rs.getFloat("accumulatedPoint");
                String tier = calculateTier(points);

        Object[] row = new Object[]{
            id,
            name,
            phone,
            email,
            orders,
            currencyFormat.format(totalSpent),
            points,
            tier,
            lastVisit
        };
                model.addRow(row);
            }
            rs.close();
            ps.close();
            
            // Update statistics after loading customers
            updateStatistics();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatistics() {
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            // Get total customers
            String countSql = "SELECT COUNT(*) as total FROM customers";
            PreparedStatement psCount = conn.prepareStatement(countSql);
            ResultSet rsCount = psCount.executeQuery();
            int totalCustomers = 0;
            if (rsCount.next()) {
                totalCustomers = rsCount.getInt("total");
            }
            rsCount.close();
            psCount.close();
            
            // Count tier distribution
            int platinumCount = 0;
            int goldCount = 0;
            
            String tierSql = "SELECT accumulatedPoint FROM customers";
            PreparedStatement psTier = conn.prepareStatement(tierSql);
            ResultSet rsTier = psTier.executeQuery();
            while (rsTier.next()) {
                float points = rsTier.getFloat("accumulatedPoint");
                String tier = calculateTier(points);
                if (tier.contains("Platinum")) platinumCount++;
                else if (tier.contains("Gold")) goldCount++;
            }
            rsTier.close();
            psTier.close();
            
            // Get total revenue from BOTH app_order and web_order
            String revenueSql = "SELECT "
                    + "(COALESCE(SUM(COALESCE(ao.total, ao.total_amount)), 0) + COALESCE(SUM(COALESCE(wo.total, wo.total_amount)), 0)) as total_revenue, "
                    + "COUNT(DISTINCT COALESCE(ao.customer_id, wo.customer_id)) as customers_with_orders "
                    + "FROM customers c "
                    + "LEFT JOIN app_order ao ON ao.customer_id = c.id "
                    + "LEFT JOIN web_order wo ON wo.customer_id = c.id "
                    + "WHERE ao.customer_id IS NOT NULL OR wo.customer_id IS NOT NULL";
            PreparedStatement psRevenue = conn.prepareStatement(revenueSql);
            ResultSet rsRevenue = psRevenue.executeQuery();
            double totalRevenue = 0;
            int customersWithOrders = 0;
            if (rsRevenue.next()) {
                totalRevenue = rsRevenue.getDouble("total_revenue");
                customersWithOrders = rsRevenue.getInt("customers_with_orders");
            }
            rsRevenue.close();
            psRevenue.close();
            
            // Calculate average spent
            double avgSpent = (customersWithOrders > 0) ? (totalRevenue / customersWithOrders) : 0;
            
            // Update UI labels
            if (totalCustomersValue != null) {
                totalCustomersValue.setText(String.valueOf(totalCustomers));
            }
            if (platinumValue != null) {
                platinumValue.setText(String.valueOf(platinumCount));
            }
            if (goldValue != null) {
                goldValue.setText(String.valueOf(goldCount));
            }
            if (totalRevenueValue != null) {
                totalRevenueValue.setText(currencyFormat.format(totalRevenue));
            }
            if (avgSpentValue != null) {
                avgSpentValue.setText(currencyFormat.format(avgSpent));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createStatCard(String title, String value, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(12,12,12,12)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(COLOR_TEXT);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));
        v.setForeground(bg);
        v.setName("valueLabel"); // Tag để lấy sau
        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }
    
    private JLabel getStatCardValueLabel(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel && "valueLabel".equals(c.getName())) {
                return (JLabel) c;
            }
        }
        return null;
    }

    // actions moved to toolbar below the table

    private void showCustomerViewDialog(int customerId) {
        // basic view: show recent orders for customer
        DefaultTableModel m = new DefaultTableModel(new String[]{"Order ID","Date","Total"},0) { public boolean isCellEditable(int r,int c){return false;} };
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String sql = "SELECT o.order_id, o.order_date, COALESCE(o.total, o.total_amount) as total FROM web_order o WHERE o.customer_id = ? ORDER BY o.order_date DESC LIMIT 20";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                Object[] r = new Object[]{ rs.getInt("order_id"), rs.getTimestamp("order_date").toLocalDateTime().format(df), currencyFormat.format(rs.getDouble("total")) };
                m.addRow(r);
            }
            rs.close(); ps.close();
        } catch (SQLException ex) { ex.printStackTrace(); }

        JTable t = new JTable(m);
        t.setRowHeight(36);
        JScrollPane sp = new JScrollPane(t);
        JPanel main = new JPanel(new BorderLayout());
        main.add(sp, BorderLayout.CENTER);
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Customer #"+customerId, true);
        d.getContentPane().add(main);
        d.setSize(600,400);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void showAddDialog() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        Object[] fields = {"Name:", name, "Phone:", phone, "Email:", email};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO customers(name, phone, email) VALUES(?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name.getText().trim());
            ps.setString(2, phone.getText().trim());
            ps.setString(3, email.getText().trim());
            ps.executeUpdate();
            ps.close();
            loadCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a customer to edit.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int id = (Integer) model.getValueAt(r, 0);
        JTextField name = new JTextField((String) model.getValueAt(r, 1));
        JTextField phone = new JTextField((String) model.getValueAt(r, 2));
        JTextField email = new JTextField((String) model.getValueAt(r, 3));
        Object[] fields = {"Name:", name, "Phone:", phone, "Email:", email};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Edit Customer", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String sql = "UPDATE customers SET name = ?, phone = ?, email = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name.getText().trim());
            ps.setString(2, phone.getText().trim());
            ps.setString(3, email.getText().trim());
            ps.setInt(4, id);
            ps.executeUpdate();
            ps.close();
            loadCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int id = (Integer) model.getValueAt(r, 0);
        String customerName = (String) model.getValueAt(r, 1);
        
        // Check if customer has existing orders
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            // Check app_order
            PreparedStatement psCheckApp = conn.prepareStatement("SELECT COUNT(*) as cnt FROM app_order WHERE customer_id = ?");
            psCheckApp.setInt(1, id);
            ResultSet rsApp = psCheckApp.executeQuery();
            int appOrderCount = 0;
            if (rsApp.next()) {
                appOrderCount = rsApp.getInt("cnt");
            }
            rsApp.close();
            psCheckApp.close();
            
            // Check web_order
            PreparedStatement psCheckWeb = conn.prepareStatement("SELECT COUNT(*) as cnt FROM web_order WHERE customer_id = ?");
            psCheckWeb.setInt(1, id);
            ResultSet rsWeb = psCheckWeb.executeQuery();
            int webOrderCount = 0;
            if (rsWeb.next()) {
                webOrderCount = rsWeb.getInt("cnt");
            }
            rsWeb.close();
            psCheckWeb.close();
            
            int totalOrders = appOrderCount + webOrderCount;
            
            if (totalOrders > 0) {
                // Customer has orders - offer options
                String[] options = {"Cancel", "Remove Orders & Delete Customer", "Set Orders to NULL & Delete Customer"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    "Customer '" + customerName + "' has " + totalOrders + " order(s).\n\n" +
                    "Choose an action:\n" +
                    "- Remove Orders: Delete all orders and customer (cannot be undone)\n" +
                    "- Set to NULL: Keep orders but remove customer reference",
                    "Customer Has Orders",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                
                if (choice == 1) {
                    // Delete orders first, then customer
                    PreparedStatement psDelAppDetails = conn.prepareStatement("DELETE FROM app_order_details WHERE order_id IN (SELECT order_id FROM app_order WHERE customer_id = ?)");
                    psDelAppDetails.setInt(1, id);
                    psDelAppDetails.executeUpdate();
                    psDelAppDetails.close();
                    
                    PreparedStatement psDelApp = conn.prepareStatement("DELETE FROM app_order WHERE customer_id = ?");
                    psDelApp.setInt(1, id);
                    psDelApp.executeUpdate();
                    psDelApp.close();
                    
                    PreparedStatement psDelWebDetails = conn.prepareStatement("DELETE FROM web_order_details WHERE order_id IN (SELECT order_id FROM web_order WHERE customer_id = ?)");
                    psDelWebDetails.setInt(1, id);
                    psDelWebDetails.executeUpdate();
                    psDelWebDetails.close();
                    
                    PreparedStatement psDelWeb = conn.prepareStatement("DELETE FROM web_order WHERE customer_id = ?");
                    psDelWeb.setInt(1, id);
                    psDelWeb.executeUpdate();
                    psDelWeb.close();
                    
                    PreparedStatement psDel = conn.prepareStatement("DELETE FROM customers WHERE id = ?");
                    psDel.setInt(1, id);
                    psDel.executeUpdate();
                    psDel.close();
                    
                    JOptionPane.showMessageDialog(this, "Customer and all associated orders deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                } else if (choice == 2) {
                    // Set customer_id to NULL in orders, then delete customer
                    PreparedStatement psNullApp = conn.prepareStatement("UPDATE app_order SET customer_id = NULL WHERE customer_id = ?");
                    psNullApp.setInt(1, id);
                    psNullApp.executeUpdate();
                    psNullApp.close();
                    
                    PreparedStatement psNullWeb = conn.prepareStatement("UPDATE web_order SET customer_id = NULL WHERE customer_id = ?");
                    psNullWeb.setInt(1, id);
                    psNullWeb.executeUpdate();
                    psNullWeb.close();
                    
                    PreparedStatement psDel = conn.prepareStatement("DELETE FROM customers WHERE id = ?");
                    psDel.setInt(1, id);
                    psDel.executeUpdate();
                    psDel.close();
                    
                    JOptionPane.showMessageDialog(this, "Customer deleted. Orders preserved with NULL customer reference.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                }
                // If choice == 0 or closed, do nothing (cancelled)
            } else {
                // No orders - safe to delete directly
                int confirm = JOptionPane.showConfirmDialog(this, "Delete customer '" + customerName + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE id = ?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    ps.close();
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Tier configuration using Java Preferences
    private static final java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(CustomerManagementPanel.class);
    
    private String calculateTier(float points) {
        if (points >= prefs.getFloat("platinum_min", 10000)) return "Platinum (" + prefs.getFloat("platinum_discount", 15) + "%)";
        if (points >= prefs.getFloat("gold_min", 5000)) return "Gold (" + prefs.getFloat("gold_discount", 10) + "%)";
        if (points >= prefs.getFloat("silver_min", 1000)) return "Silver (" + prefs.getFloat("silver_discount", 5) + "%)";
        return "Bronze (" + prefs.getFloat("bronze_discount", 0) + "%)";
    }
    
    public static float getDiscountForPoints(float points) {
        if (points >= prefs.getFloat("platinum_min", 10000)) return prefs.getFloat("platinum_discount", 15);
        if (points >= prefs.getFloat("gold_min", 5000)) return prefs.getFloat("gold_discount", 10);
        if (points >= prefs.getFloat("silver_min", 1000)) return prefs.getFloat("silver_discount", 5);
        return prefs.getFloat("bronze_discount", 0);
    }

    private void showTierConfigDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 3, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Bronze
        panel.add(new JLabel("Bronze (0 - " + (prefs.getFloat("silver_min", 1000) - 1) + " pts):"));
        JTextField bronzeDiscount = new JTextField(String.valueOf(prefs.getFloat("bronze_discount", 0)));
        panel.add(bronzeDiscount);
        panel.add(new JLabel("% discount"));
        
        // Silver
        JTextField silverMin = new JTextField(String.valueOf(prefs.getFloat("silver_min", 1000)));
        JTextField silverDiscount = new JTextField(String.valueOf(prefs.getFloat("silver_discount", 5)));
        panel.add(new JLabel("Silver Min Points:"));
        panel.add(silverMin);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Silver Discount:"));
        panel.add(silverDiscount);
        panel.add(new JLabel("% discount"));
        
        // Gold
        JTextField goldMin = new JTextField(String.valueOf(prefs.getFloat("gold_min", 5000)));
        JTextField goldDiscount = new JTextField(String.valueOf(prefs.getFloat("gold_discount", 10)));
        panel.add(new JLabel("Gold Min Points:"));
        panel.add(goldMin);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Gold Discount:"));
        panel.add(goldDiscount);
        panel.add(new JLabel("% discount"));
        
        // Platinum
        JTextField platinumMin = new JTextField(String.valueOf(prefs.getFloat("platinum_min", 10000)));
        JTextField platinumDiscount = new JTextField(String.valueOf(prefs.getFloat("platinum_discount", 15)));
        panel.add(new JLabel("Platinum Min Points:"));
        panel.add(platinumMin);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Platinum Discount:"));
        panel.add(platinumDiscount);
        panel.add(new JLabel("% discount"));
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Configure Customer Tiers", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Save configurations
                prefs.putFloat("bronze_discount", Float.parseFloat(bronzeDiscount.getText().trim()));
                prefs.putFloat("silver_min", Float.parseFloat(silverMin.getText().trim()));
                prefs.putFloat("silver_discount", Float.parseFloat(silverDiscount.getText().trim()));
                prefs.putFloat("gold_min", Float.parseFloat(goldMin.getText().trim()));
                prefs.putFloat("gold_discount", Float.parseFloat(goldDiscount.getText().trim()));
                prefs.putFloat("platinum_min", Float.parseFloat(platinumMin.getText().trim()));
                prefs.putFloat("platinum_discount", Float.parseFloat(platinumDiscount.getText().trim()));
                
                prefs.flush(); // Save to disk
                
                JOptionPane.showMessageDialog(this, 
                    "Tier configuration saved successfully!\nCustomer list will be refreshed.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadCustomers(); // Refresh the table with new tier calculations
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid number format. Please enter valid numbers.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (java.util.prefs.BackingStoreException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error saving configuration: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
