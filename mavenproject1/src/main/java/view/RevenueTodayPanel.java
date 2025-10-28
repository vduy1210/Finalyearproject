package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
// removed unused date tick imports after switching to category chart
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
// unused imports removed after switching to time series chart
// time series imports removed; switching to categorical hourly bars

public class RevenueTodayPanel extends JPanel {
    // Light theme
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Gray tinted white
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    private static final Color TABLE_BG = new Color(0xE5, 0xE7, 0xEB);
    private static final Color ROW_ALT = new Color(0xEF, 0xF1, 0xF5);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_CARD = new Font("Segoe UI", Font.BOLD, 18);

    private JLabel revenueLabel;
    private JLabel totalUnitsLabel;
    private JLabel kindsLabel;
    private DefaultTableModel ordersModel;
    private NumberFormat currencyFormat;
    private JPanel chartPanel;
    private JTable ordersTable;

    // Store product sales for chart
    private final Map<String, Integer> productSales = new LinkedHashMap<>();

    public RevenueTodayPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

        // Top summary cards + refresh
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(createCard("Total Revenue Today", "0", SUCCESS_COLOR, true));
        summaryPanel.add(createCard("Total Units Sold", "0", MAIN_COLOR, false));
        summaryPanel.add(createCard("Kinds of Products Sold", "0", COLOR_TEXT, false));

        JButton refreshButton = createPrimaryButton("🔄 Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTodayStatsAndChart();
                loadTodayAppOrders();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(summaryPanel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: chart + orders table
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Chart wrapper with header
        JLabel chartHeader = new JLabel("Units Sold by Product (Today)");
        chartHeader.setFont(FONT_TITLE);
        chartHeader.setForeground(COLOR_TEXT);
        chartHeader.setBorder(new EmptyBorder(0, 0, 8, 0));
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        JPanel chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.setOpaque(false);
        chartWrapper.add(chartHeader, BorderLayout.NORTH);
        chartWrapper.add(chartPanel, BorderLayout.CENTER);
        centerPanel.add(chartWrapper);

        // Orders table
        ordersModel = new DefaultTableModel(new String[]{"Order ID", "Customer", "Total", "Time"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        ordersTable.setRowHeight(44);
        ordersTable.setShowGrid(false);
        ordersTable.setIntercellSpacing(new Dimension(0, 0));
        ordersTable.setSelectionBackground(new Color(232, 244, 253));
        ordersTable.setSelectionForeground(COLOR_TEXT);
        ordersTable.setBackground(TABLE_BG);

        JTableHeader th = ordersTable.getTableHeader();
        th.setFont(FONT_TITLE);
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
        for (int i = 0; i < ordersModel.getColumnCount(); i++) {
            ordersTable.getColumnModel().getColumn(i).setCellRenderer(alt);
        }

        // When user double-clicks a row or presses Enter, open the order in Order panel
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed()) {
                    evt.consume();
                    openSelectedOrder();
                }
            }
        });
        ordersTable.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    evt.consume();
                    openSelectedOrder();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(ordersTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        tableScroll.getViewport().setBackground(TABLE_BG);

        JLabel tableHeader = new JLabel("Today's App Orders (Manual Input)");
        tableHeader.setFont(FONT_TITLE);
        tableHeader.setForeground(COLOR_TEXT);
        tableHeader.setBorder(new EmptyBorder(0, 0, 8, 0));
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.add(tableHeader, BorderLayout.NORTH);
        tableWrapper.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(tableWrapper);

        add(centerPanel, BorderLayout.CENTER);

        // Load data for default period (Today)
        loadTodayStatsAndChart();
        loadTodayAppOrders();
    }

    private JPanel createCard(String title, String value, Color valueColor, boolean isRevenue) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(16, 20, 16, 20)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_LABEL);
        titleLabel.setForeground(COLOR_TEXT);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_CARD);
        valueLabel.setForeground(valueColor);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        if (isRevenue) this.revenueLabel = valueLabel;
        else if (title.contains("Units")) this.totalUnitsLabel = valueLabel;
        else this.kindsLabel = valueLabel;
        return card;
    }

    private void loadStatsAndChart(String period) {
        productSales.clear();
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String where = buildWhereClauseForPeriod("a.order_date", period);
            String sql = "SELECT COALESCE(SUM(ad.quantity * ad.price),0) as revenue, COALESCE(SUM(ad.quantity),0) as total_units, COUNT(DISTINCT ad.product_id) as kinds " +
                    "FROM app_order_details ad JOIN app_order a ON ad.order_id = a.order_id WHERE " + where;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                revenueLabel.setText(currencyFormat.format(rs.getDouble("revenue")));
                totalUnitsLabel.setText(String.valueOf(rs.getInt("total_units")));
                kindsLabel.setText(String.valueOf(rs.getInt("kinds")));
            }
            rs.close();
            ps.close();

            String chartSql = "SELECT p.name, SUM(ad.quantity) as total_sold FROM app_order_details ad " +
                    "JOIN products p ON ad.product_id = p.id " +
                    "JOIN app_order a ON ad.order_id = a.order_id " +
                    "WHERE " + where + " GROUP BY p.name ORDER BY total_sold DESC";
            ps = conn.prepareStatement(chartSql);
            rs = ps.executeQuery();
            while (rs.next()) {
                productSales.put(rs.getString("name"), rs.getInt("total_sold"));
            }
            rs.close();
            ps.close();
            // Render hourly revenue bar chart for the selected period
            String whereForHours = buildWhereClauseForPeriod("a.order_date", period);
            renderHourlyBarChart(whereForHours);
        } catch (SQLException e) {
            e.printStackTrace();
            revenueLabel.setText("Error");
            totalUnitsLabel.setText("Error");
            kindsLabel.setText("Error");
        }
    }
    private void renderHourlyBarChart(String whereClause) {
        // Prepare hourly revenue map (0..23)
        double[] hours = new double[24];
        for (int i = 0; i < 24; i++) hours[i] = 0.0;
        String sql = "SELECT HOUR(a.order_date) as hr, COALESCE(SUM(ad.quantity * ad.price),0) as revenue "
                + "FROM app_order_details ad JOIN app_order a ON ad.order_id = a.order_id WHERE " + whereClause + " GROUP BY hr ORDER BY hr";
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int h = rs.getInt("hr");
                if (h >= 0 && h < 24) hours[h] = rs.getDouble("revenue");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Build category dataset with hour labels
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int h = 0; h < 24; h++) {
            String label = String.format("%02dh", h);
            dataset.addValue(hours[h], "Revenue", label);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Hour",
                "Revenue",
                dataset);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 225, 231));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAIN_COLOR);

        // Range axis (revenue) - format as currency
        try {
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setNumberFormatOverride(currencyFormat);
            rangeAxis.setAutoRangeIncludesZero(true);
        } catch (ClassCastException ex) {
            // ignore
        }

        // Category axis tweak: rotate labels slightly if needed
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        ChartPanel cp = new ChartPanel(chart);
        cp.setPopupMenu(null);
        cp.setMouseWheelEnabled(true);
        cp.setBackground(WHITE);

        chartPanel.removeAll();
        chartPanel.add(cp, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void loadAppOrders(String period) {
        ordersModel.setRowCount(0);
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String where = buildWhereClauseForPeriod("o.order_date", period);
            String sql = "SELECT o.order_id, c.name as customer_name, o.total_amount, o.order_date FROM app_order o JOIN customers c ON o.customer_id = c.id WHERE " + where + " ORDER BY o.order_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            while (rs.next()) {
                ordersModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        currencyFormat.format(rs.getDouble("total_amount")),
                        rs.getTimestamp("order_date").toLocalDateTime().format(timeFmt)
                });
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String buildWhereClauseForPeriod(String dateColumn, String period) {
        if (period == null) period = "Today";
        switch (period) {
            case "This Week":
                return "YEARWEEK(" + dateColumn + ") = YEARWEEK(CURRENT_DATE)";
            case "This Month":
                return "YEAR(" + dateColumn + ") = YEAR(CURRENT_DATE) AND MONTH(" + dateColumn + ") = MONTH(CURRENT_DATE)";
            case "This Year":
                return "YEAR(" + dateColumn + ") = YEAR(CURRENT_DATE)";
            case "Today":
            default:
                return "DATE(" + dateColumn + ") = CURRENT_DATE";
        }
    }

    // Convenience wrappers for Today-only operations (keeps API stable)
    private void loadTodayStatsAndChart() {
        loadStatsAndChart("Today");
    }

    private void loadTodayAppOrders() {
        loadAppOrders("Today");
    }

    private JButton createPrimaryButton(String text) {
    JButton b = new RoundedButton(text);
        b.setFont(FONT_LABEL);
        b.setBackground(MAIN_COLOR);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(10, 20, 10, 20)));
        return b;
    }

    private void openSelectedOrder() {
        int r = ordersTable.getSelectedRow();
        if (r < 0) return;
        try {
            Object val = ordersModel.getValueAt(r, 0);
            if (val == null) return;
            int orderId = Integer.parseInt(val.toString());
            // Show order details dialog for this order
            showOrderDetailsDialog(orderId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showOrderDetailsDialog(int orderId) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Product", "Qty", "Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));

        double total = 0.0;
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String sql = "SELECT p.name as product_name, ad.quantity, ad.price, (ad.quantity * ad.price) as subtotal "
                    + "FROM app_order_details ad JOIN products p ON ad.product_id = p.id WHERE ad.order_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String prod = rs.getString("product_name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double subtotal = rs.getDouble("subtotal");
                total += subtotal;
                model.addRow(new Object[]{prod, qty, currencyFormat.format(price), currencyFormat.format(subtotal)});
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(540, 320));

        JLabel totalLabel = new JLabel("Total: " + currencyFormat.format(total));
        totalLabel.setFont(FONT_LABEL);

        JButton close = new RoundedButton("Close");
        util.UIUtils.styleActionButton(close, 120);
        close.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor((Component)e.getSource());
            if (w != null) w.dispose();
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(totalLabel, BorderLayout.WEST);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.setOpaque(false);
        btnWrap.add(close);
        bottom.add(btnWrap, BorderLayout.EAST);

        JPanel main = new JPanel(new BorderLayout(8,8));
        main.setBorder(new EmptyBorder(12,12,12,12));
        main.add(new JLabel("Order Details #" + orderId), BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order #" + orderId, true);
        dialog.getContentPane().add(main);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}