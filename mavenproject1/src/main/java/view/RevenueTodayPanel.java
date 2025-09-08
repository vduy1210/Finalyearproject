package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RevenueTodayPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);
    private static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 18);
    private static final Font FONT_LABEL = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_CARD = new Font("Helvetica", Font.BOLD, 16);

    private JLabel revenueLabel;
    private JLabel totalUnitsLabel;
    private JLabel kindsLabel;
    private DefaultTableModel ordersModel;
    private NumberFormat currencyFormat;
    private JPanel chartPanel;
    private JPanel revenueCard;
    private JPanel totalUnitsCard;
    private JPanel kindsCard;

    public RevenueTodayPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Top summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);
        revenueCard = createCard("Total Revenue Today", "0", SUCCESS_COLOR, true);
        totalUnitsCard = createCard("Total Units Sold", "0", MAIN_COLOR, false);
        kindsCard = createCard("Kinds of Products Sold", "0", WHITE, false);
        summaryPanel.add(revenueCard);
        summaryPanel.add(totalUnitsCard);
        summaryPanel.add(kindsCard);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(FONT_LABEL);
        refreshButton.setBackground(MAIN_COLOR);
        refreshButton.setForeground(WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        refreshButton.addActionListener(e -> {
            loadTodayStatsAndChart();
            loadTodayAppOrders();
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(summaryPanel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: Chart and Table split
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Chart panel
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart((Graphics2D) g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(createTitledBorder("Units Sold by Product (Today)"));
        centerPanel.add(chartPanel);

        // Table of today's app orders (manual input)
        ordersModel = new DefaultTableModel(new String[]{"Order ID", "Customer", "Total", "Time"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable ordersTable = new JTable(ordersModel);
        ordersTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        ordersTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(ordersTable);
        tableScroll.setBorder(createTitledBorder("Today's App Orders (Manual Input)"));
        tableScroll.getViewport().setBackground(WHITE);
        centerPanel.add(tableScroll);

        add(centerPanel, BorderLayout.CENTER);

        // Load data (only app orders - manual input)
        loadTodayStatsAndChart();
        loadTodayAppOrders();
    }

    /**
     * Creates a card panel with a title and value. Also sets the value label to the appropriate field for updating.
     */
    private JPanel createCard(String title, String value, Color bgColor, boolean isRevenue) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 20, 15, 20)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_LABEL);
        titleLabel.setForeground(bgColor == WHITE ? Color.BLACK : WHITE);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_CARD);
        valueLabel.setForeground(bgColor == WHITE ? Color.BLACK : WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        // Set the correct field for updating
        if (isRevenue) {
            this.revenueLabel = valueLabel;
        } else if (title.contains("Units")) {
            this.totalUnitsLabel = valueLabel;
        } else {
            this.kindsLabel = valueLabel;
        }
        return card;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(FONT_TITLE);
        border.setTitleColor(LIGHT_TEXT);
        return border;
    }

    // Store product sales for chart
    private Map<String, Integer> productSales = new LinkedHashMap<>();

    private void loadTodayStatsAndChart() {
        productSales.clear();
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            // Total revenue, total units, kinds (only app orders - manual input)
            String sql = "SELECT COALESCE(SUM(ad.quantity * ad.price),0) as revenue, COALESCE(SUM(ad.quantity),0) as total_units, COUNT(DISTINCT ad.product_id) as kinds " +
                    "FROM app_order_details ad JOIN app_order a ON ad.order_id = a.order_id WHERE DATE(a.order_date) = CURRENT_DATE";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                revenueLabel.setText(currencyFormat.format(rs.getDouble("revenue")));
                totalUnitsLabel.setText(String.valueOf(rs.getInt("total_units")));
                kindsLabel.setText(String.valueOf(rs.getInt("kinds")));
            }
            rs.close();
            ps.close();

            // Product sales for chart (only app orders - manual input)
            String chartSql = "SELECT p.name, SUM(ad.quantity) as total_sold FROM app_order_details ad " +
                    "JOIN products p ON ad.product_id = p.id " +
                    "JOIN app_order a ON ad.order_id = a.order_id " +
                    "WHERE DATE(a.order_date) = CURRENT_DATE GROUP BY p.name ORDER BY total_sold DESC";
            ps = conn.prepareStatement(chartSql);
            rs = ps.executeQuery();
            while (rs.next()) {
                productSales.put(rs.getString("name"), rs.getInt("total_sold"));
            }
            rs.close();
            ps.close();
            chartPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            revenueLabel.setText("Error");
            totalUnitsLabel.setText("Error");
            kindsLabel.setText("Error");
        }
    }

    private void drawBarChart(Graphics2D g) {
        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();
        int padding = 40;
        int barWidth = 40;
        int maxBarHeight = height - 2 * padding - 30;
        int x = padding;
        int yBase = height - padding;
        int max = 1;
        for (int v : productSales.values()) max = Math.max(max, v);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawLine(padding, yBase, width - padding, yBase);
        int i = 0;
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            int barHeight = (int) ((entry.getValue() / (double) max) * maxBarHeight);
            g.setColor(new Color(52, 152, 219));
            g.fillRect(x, yBase - barHeight, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, yBase - barHeight, barWidth, barHeight);
            g.setFont(new Font("Helvetica", Font.PLAIN, 12));
            g.drawString(entry.getKey(), x, yBase + 15);
            g.drawString(String.valueOf(entry.getValue()), x + barWidth / 4, yBase - barHeight - 5);
            x += barWidth + 20;
            i++;
        }
    }

    private void loadTodayAppOrders() {
        ordersModel.setRowCount(0);
        try (Connection conn = database.DatabaseConnector.getConnection()) {
            String sql = "SELECT o.order_id, c.name as customer_name, o.total_amount, o.order_date FROM app_order o JOIN customers c ON o.customer_id = c.id WHERE DATE(o.order_date) = CURRENT_DATE ORDER BY o.order_date DESC";
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
} 