package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// Removed ActionListener imports as dashboard no longer has period filters
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Charts
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardPanel extends JPanel {

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);       // Blue
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);        // Green
    private static final Color WARNING_COLOR = new Color(255, 152, 0);        // Orange
    private static final Color INFO_COLOR = new Color(156, 39, 176);          // Purple
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);   // Gray tinted white
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    
    // Modern typography
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font WIDGET_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font WIDGET_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Dashboard shows only today's data

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        // Start auto-refresh to keep dashboard up-to-date
        startAutoRefresh();
        // Trigger an immediate refresh in background so UI doesn't wait for first timer tick
        new javax.swing.SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                        try {
                            refreshTodayStats();
                            refreshWeeklyCharts();
                        } catch (Exception ex) {
                            System.err.println("[Dashboard] Error during refreshTodayStats/refreshWeeklyCharts: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                return null;
            }
        }.execute();
    }

    private JComponent createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleIcon = new JLabel("📊");
        titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel title = new JLabel("Dashboard");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        titlePanel.add(titleIcon);
        titlePanel.add(title);
        
        // Date section
        JLabel dateLabel = new JLabel("Today: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        dateLabel.setFont(SUBTITLE_FONT);
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BACKGROUND_COLOR);
        titleWrapper.add(titlePanel, BorderLayout.NORTH);
        titleWrapper.add(dateLabel, BorderLayout.CENTER);
        
        headerPanel.add(titleWrapper, BorderLayout.WEST);
        
        // Dashboard shows only today's data; no period filter controls on dashboard
        // (Filters are available in RevenueReportPanel)
        
        return headerPanel;
    }

    private JComponent createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Top stats cards
        mainPanel.add(createStatsCardsPanel(), BorderLayout.NORTH);
        
        // Bottom charts section
        mainPanel.add(createChartsPanel(), BorderLayout.CENTER);
        
        return mainPanel;
    }

    private JPanel createStatsCardsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setPreferredSize(new Dimension(0, 140));
        
    JPanel salesCard = createStatCard("💰", "Total Sales Today", "--", "", SUCCESS_COLOR);
    JPanel ordersCard = createStatCard("📦", "Total Orders Today", "--", "", PRIMARY_COLOR);
    JPanel customersCard = createStatCard("👥", "Total Customers Today", "--", "", WARNING_COLOR);
    JPanel productsCard = createStatCard("📈", "Products Sold Today", "--", "", INFO_COLOR);
        
        statsPanel.add(salesCard);
        statsPanel.add(ordersCard);
        statsPanel.add(customersCard);
        statsPanel.add(productsCard);
        
        return statsPanel;
    }

    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new BorderLayout(20, 0));
        chartsPanel.setBackground(BACKGROUND_COLOR);
        
        // Main chart area (left side) - Top 5 products
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.add(createTop5ProductsChartCard(), BorderLayout.CENTER);
        
        // Right side panel - Top customers this month
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.add(createTopCustomersCard(), BorderLayout.CENTER);
        
        chartsPanel.add(leftPanel, BorderLayout.CENTER);
        chartsPanel.add(rightPanel, BorderLayout.EAST);
        
        return chartsPanel;
    }

    private JPanel createStatCard(String icon, String title, String value, String change, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Add subtle hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_COLOR);
            }
        });
        
        // Icon and title row
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(card.getBackground());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Value
    JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(WIDGET_VALUE_FONT);
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // store reference on the card component for later update
    card.putClientProperty("valueLabel", valueLabel);
        
        // No dynamic period on dashboard; values shown are for today
        
        // Change indicator
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeLabel.setForeground(accentColor);
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(changeLabel);
        
        return card;
    }

    // Refresh today's stats from database using DAO
    private void refreshTodayStats() {
        // compute today's range
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime from = today.atStartOfDay();
        java.time.LocalDateTime to = today.plusDays(1).atStartOfDay().minusNanos(1);

    // Query AppOrderDao (app_order table)
    dao.AppOrderDao dao = new dao.AppOrderDao();
    double revenue = dao.getTotalRevenue(from, to);
    long orders = dao.getOrderCount(from, to);
    long customers = dao.getDistinctCustomerCount(from, to);
    long products = dao.getProductsSold(from, to);

    // Debug logging to help diagnose why UI may not update
    System.out.println("[Dashboard] Fetched stats: revenue=" + revenue + ", orders=" + orders + ", customers=" + customers + ", products=" + products);

        java.text.NumberFormat currencyFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.of("vi", "VN"));
        String revenueStr = currencyFmt.format(revenue);
        String ordersStr = java.text.NumberFormat.getIntegerInstance().format(orders);
        String customersStr = java.text.NumberFormat.getIntegerInstance().format(customers);
        String productsStr = java.text.NumberFormat.getIntegerInstance().format(products);

        // Update UI on EDT
        SwingUtilities.invokeLater(() -> {
            for (Component c : getComponents()) {
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    java.util.List<Component> found = findStatCards(jc);
                    for (Component sc : found) {
                        if (sc instanceof JComponent) {
                            JComponent card = (JComponent) sc;
                            Component titleComp = getTitleLabelFromCard(card);
                            if (titleComp instanceof JLabel) {
                                String t = ((JLabel) titleComp).getText();
                                if (t != null) {
                                    if (t.contains("Total Sales")) setCardValue(card, revenueStr);
                                    else if (t.contains("Total Orders")) setCardValue(card, ordersStr);
                                    else if (t.contains("Total Customers")) setCardValue(card, customersStr);
                                    else if (t.contains("Products Sold")) setCardValue(card, productsStr);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    // Auto-refresh implementation using SwingWorker that polls every X seconds
    private transient javax.swing.Timer refreshTimer;
    private void startAutoRefresh() {
        // Poll every 5 seconds
        int delayMs = 5000;
        if (refreshTimer != null) refreshTimer.stop();
        refreshTimer = new javax.swing.Timer(delayMs, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Use a SwingWorker to run DB queries off EDT and publish results safely
                        new javax.swing.SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                try {
                                    System.out.println("[Dashboard] Background refresh starting");
                                    refreshTodayStats();
                                    refreshWeeklyCharts();
                                    System.out.println("[Dashboard] Background refresh finished");
                                } catch (Exception ex) {
                                    System.err.println("[Dashboard] Error during refreshTodayStats/refreshWeeklyCharts: " + ex.getMessage());
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
            }
        });
        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }

    private java.util.List<Component> findStatCards(Container root) {
        java.util.List<Component> cards = new java.util.ArrayList<>();
        for (Component c : root.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                // heuristic: stat cards have WIDGET_VALUE_FONT on a JLabel in children
                for (Component child : p.getComponents()) {
                    if (child instanceof JLabel) {
                        JLabel l = (JLabel) child;
                        if (WIDGET_VALUE_FONT.equals(l.getFont())) {
                            cards.add(p);
                            break;
                        }
                    }
                }
            }
            if (c instanceof Container) cards.addAll(findStatCards((Container) c));
        }
        return cards;
    }
    
    // Helper method to find component by client property recursively
    private <T> T findComponentByProperty(Container root, String propertyName, Class<T> type) {
        for (Component c : root.getComponents()) {
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                Object prop = jc.getClientProperty(propertyName);
                if (type.isInstance(prop)) {
                    return type.cast(prop);
                }
            }
            if (c instanceof Container) {
                T result = findComponentByProperty((Container) c, propertyName, type);
                if (result != null) return result;
            }
        }
        return null;
    }

    private Component getTitleLabelFromCard(JComponent card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel) {
                for (Component cc : ((JPanel) c).getComponents()) {
                    if (cc instanceof JLabel) {
                        String txt = ((JLabel) cc).getText();
                        if (txt != null && (txt.contains("Total") || txt.contains("Products"))) return cc;
                    }
                }
            } else if (c instanceof JLabel) {
                String txt = ((JLabel) c).getText();
                if (txt != null && (txt.contains("Total") || txt.contains("Products"))) return c;
            }
        }
        return null;
    }

    private void setCardValue(JComponent card, String value) {
        Object prop = card.getClientProperty("valueLabel");
        if (prop instanceof JLabel) {
            ((JLabel) prop).setText(value);
            return;
        }
        // fallback: find first JLabel with WIDGET_VALUE_FONT
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                if (WIDGET_VALUE_FONT.equals(l.getFont())) {
                    l.setText(value);
                    return;
                }
            }
            if (c instanceof Container) {
                for (Component cc : ((Container) c).getComponents()) {
                    if (cc instanceof JLabel) {
                        JLabel l = (JLabel) cc;
                        if (WIDGET_VALUE_FONT.equals(l.getFont())) {
                            l.setText(value);
                            return;
                        }
                    }
                }
            }
        }
    }

    private JPanel createChartCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        card.setPreferredSize(new Dimension(0, 250));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(WIDGET_TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Placeholder panel for extension
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(new Color(248, 249, 250));
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        
        return card;
    }

    // --- Chart cards using JFreeChart ---
    private JPanel createTop5ProductsChartCard() {
        JPanel card = createChartCard("Top 5 Best Selling Products", "");
        card.setPreferredSize(new Dimension(0, 500));
        JPanel content = (JPanel) card.getComponent(1);

        // Initial placeholder dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Quantity", "Loading...");

        JFreeChart chart = ChartFactory.createBarChart(
            null, "Product", "Quantity Sold", dataset, PlotOrientation.VERTICAL, false, true, false
        );
        
        // Customize chart appearance
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        
        // Set Y-axis to display only integers
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        
        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setDrawBarOutline(false);
        
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        content.add(cp, BorderLayout.CENTER);
        
        // Store chart panel for later updates
        card.putClientProperty("top5ChartPanel", cp);
        
        return card;
    }

    private JPanel createTopCustomersCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Top Customers This Month");
        titleLabel.setFont(WIDGET_TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Customers with highest purchases");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Customer list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CARD_COLOR);
        
        // Store list panel for later updates
        card.putClientProperty("customerListPanel", listPanel);
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCustomerListItem(int rank, String name, String phone, double totalSpent) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(CARD_COLOR);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            new EmptyBorder(8, 8, 8, 8)  // Reduced padding: 15 -> 8
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Limit height to 60px
        
        // Rank badge
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Smaller font: 16 -> 14
        rankLabel.setForeground(rank <= 3 ? WARNING_COLOR : TEXT_SECONDARY);
        rankLabel.setPreferredSize(new Dimension(35, 0));  // Narrower: 40 -> 35
        
        // Customer info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));  // Smaller: 14 -> 13
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel phoneLabel = new JLabel(phone);
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));  // Smaller: 12 -> 11
        phoneLabel.setForeground(TEXT_SECONDARY);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));  // Smaller gap: 3 -> 2
        infoPanel.add(phoneLabel);
        
        // Amount
        java.text.NumberFormat currencyFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.of("vi", "VN"));
        JLabel amountLabel = new JLabel(currencyFmt.format(totalSpent));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));  // Smaller: 14 -> 13
        amountLabel.setForeground(SUCCESS_COLOR);
        
        item.add(rankLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(amountLabel, BorderLayout.EAST);
        
        return item;
    }

    // Helper to update charts with real data
    private void refreshWeeklyCharts() {
        // Update Top 5 Products Chart
        updateTop5ProductsChart();
        
        // Update Top Customers List
        updateTopCustomersList();
    }
    
    private void updateTop5ProductsChart() {
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            
            // Query to get top 5 products by quantity sold from app_order_details
            String sql = "SELECT p.name, SUM(od.quantity) as total_qty " +
                        "FROM app_order_details od " +
                        "JOIN products p ON od.product_id = p.id " +
                        "GROUP BY p.id, p.name " +
                        "ORDER BY total_qty DESC " +
                        "LIMIT 5";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            boolean hasData = false;
            
            while (rs.next()) {
                String productName = rs.getString("name");
                int quantity = rs.getInt("total_qty");
                dataset.addValue(quantity, "Quantity", productName);
                hasData = true;
                System.out.println("[Dashboard] Product: " + productName + ", Quantity: " + quantity);
            }
            
            if (!hasData) {
                System.out.println("[Dashboard] No product data found!");
                // Add dummy data for display
                dataset.addValue(0, "Quantity", "No Data");
            }
            
            rs.close();
            ps.close();
            conn.close();
            
            // Update chart on EDT
            SwingUtilities.invokeLater(() -> {
                System.out.println("[Dashboard] Finding chart panel...");
                // Use new helper method to find chart panel
                ChartPanel cp = findComponentByProperty(this, "top5ChartPanel", ChartPanel.class);
                
                if (cp != null) {
                    System.out.println("[Dashboard] Chart panel found! Creating chart...");
                    JFreeChart chart = ChartFactory.createBarChart(
                        null, "Product", "Quantity Sold", dataset, 
                        PlotOrientation.VERTICAL, false, true, false
                    );
                    
                    // Customize chart
                    org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
                    plot.setBackgroundPaint(Color.WHITE);
                    plot.setRangeGridlinePaint(new Color(200, 200, 200));
                    
                    // Set Y-axis to display only integers
                    org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
                    rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
                    
                    org.jfree.chart.renderer.category.BarRenderer renderer = 
                        (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
                    renderer.setSeriesPaint(0, PRIMARY_COLOR);
                    renderer.setDrawBarOutline(false);
                    
                    cp.setChart(chart);
                    cp.repaint();
                    System.out.println("[Dashboard] Chart updated successfully!");
                } else {
                    System.out.println("[Dashboard] ERROR: Chart panel not found!");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[Dashboard] Error updating top 5 products: " + e.getMessage());
        }
    }
    
    private void updateTopCustomersList() {
        try {
            Connection conn = database.DatabaseConnector.getConnection();
            
            // Get start and end of current month
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate startOfMonth = today.withDayOfMonth(1);
            java.time.LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
            
            // Query to get top customers by total spending this month from BOTH app_order and web_order
            // Use UNION ALL to combine results from both tables
            String sql = "SELECT c.name, c.phone, COALESCE(total_spent, 0) as total_spent FROM customers c " +
                        "LEFT JOIN ( " +
                        "  SELECT customer_id, SUM(total) as total_spent " +
                        "  FROM ( " +
                        "    SELECT customer_id, total FROM app_order " +
                        "    WHERE DATE(order_date) >= ? AND DATE(order_date) <= ? " +
                        "    UNION ALL " +
                        "    SELECT customer_id, total FROM web_order " +
                        "    WHERE DATE(order_date) >= ? AND DATE(order_date) <= ? " +
                        "  ) combined " +
                        "  WHERE customer_id IS NOT NULL " +
                        "  GROUP BY customer_id " +
                        ") orders ON c.id = orders.customer_id " +
                        "WHERE total_spent > 0 " +
                        "ORDER BY total_spent DESC " +
                        "LIMIT 10";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, startOfMonth.toString());
            ps.setString(2, endOfMonth.toString());
            ps.setString(3, startOfMonth.toString());
            ps.setString(4, endOfMonth.toString());
            ResultSet rs = ps.executeQuery();
            
            java.util.List<CustomerData> customers = new java.util.ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                double totalSpent = rs.getDouble("total_spent");
                customers.add(new CustomerData(name, phone, totalSpent));
                System.out.println("[Dashboard] Customer: " + name + ", Phone: " + phone + ", Spent: " + totalSpent);
            }
            
            if (customers.isEmpty()) {
                System.out.println("[Dashboard] No customer data found for this month!");
            }
            
            rs.close();
            ps.close();
            conn.close();
            
            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                System.out.println("[Dashboard] Updating customer list UI...");
                // Use new helper method to find customer list panel
                JPanel listPanel = findComponentByProperty(this, "customerListPanel", JPanel.class);
                
                if (listPanel != null) {
                    System.out.println("[Dashboard] Customer list panel found!");
                    listPanel.removeAll();
                    
                    if (customers.isEmpty()) {
                        JLabel emptyLabel = new JLabel("No data available");
                        emptyLabel.setFont(SUBTITLE_FONT);
                        emptyLabel.setForeground(TEXT_SECONDARY);
                        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        listPanel.add(emptyLabel);
                        System.out.println("[Dashboard] Added 'No data' label");
                    } else {
                        System.out.println("[Dashboard] Adding " + customers.size() + " customers to list");
                        int rank = 1;
                        for (CustomerData customer : customers) {
                            JPanel item = createCustomerListItem(
                                rank++, 
                                customer.name, 
                                customer.phone, 
                                customer.totalSpent
                            );
                            listPanel.add(item);
                        }
                        // Add glue to push items to the top and leave empty space below
                        listPanel.add(Box.createVerticalGlue());
                    }
                    
                    listPanel.revalidate();
                    listPanel.repaint();
                    System.out.println("[Dashboard] Customer list updated successfully!");
                } else {
                    System.out.println("[Dashboard] ERROR: Customer list panel not found!");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[Dashboard] Error updating top customers: " + e.getMessage());
        }
    }
    
    // Helper class to store customer data
    private static class CustomerData {
        String name;
        String phone;
        double totalSpent;
        
        CustomerData(String name, String phone, double totalSpent) {
            this.name = name;
            this.phone = phone;
            this.totalSpent = totalSpent;
        }
    }
}