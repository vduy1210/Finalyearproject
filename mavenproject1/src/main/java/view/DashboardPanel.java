package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// Removed ActionListener imports as dashboard no longer has period filters
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Charts
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardPanel extends JPanel {

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);       // Blue
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);        // Green
    private static final Color WARNING_COLOR = new Color(255, 152, 0);        // Orange
    private static final Color INFO_COLOR = new Color(156, 39, 176);          // Purple
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);   // Light Blue Gray
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
        
        // Main chart area (left side)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setBackground(BACKGROUND_COLOR);
        
    leftPanel.add(createSalesChartCard(), BorderLayout.NORTH);
    leftPanel.add(createCustomerActivityChartCard(), BorderLayout.CENTER);
    leftPanel.add(createWeeklyJointChartCard(), BorderLayout.SOUTH);
        
        // Right side panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(350, 0));
    rightPanel.add(createBestSellersChartCard());
        
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
    private JPanel createSalesChartCard() {
        JPanel card = createChartCard("📊 Sales Analytics Today", "Today's sales performance and trends");
        JPanel content = (JPanel) card.getComponent(1);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1200, "Sales", "Mon");
        dataset.addValue(1500, "Sales", "Tue");
        dataset.addValue(900,  "Sales", "Wed");
        dataset.addValue(1800, "Sales", "Thu");
        dataset.addValue(1600, "Sales", "Fri");
        dataset.addValue(2000, "Sales", "Sat");
        dataset.addValue(1700, "Sales", "Sun");

        JFreeChart chart = ChartFactory.createLineChart(
            null, "Day", "Amount ($)", dataset, PlotOrientation.VERTICAL, false, true, false
        );
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        content.add(cp, BorderLayout.CENTER);
        return card;
    }

    // New: weekly joint chart (revenue + orders)
    private JPanel createWeeklyJointChartCard() {
        JPanel card = createChartCard("📈 Joint Chart This Week", "Revenue and Orders by day (this week)");
        JPanel content = (JPanel) card.getComponent(1);
        // placeholder chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(null, "Day", "Value", dataset);
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        content.setLayout(new BorderLayout());
        content.add(cp, BorderLayout.CENTER);
        // store chart panel for later updates
        card.putClientProperty("jointChartPanel", cp);
        return card;
    }

    private JPanel createCustomerActivityChartCard() {
        JPanel card = createChartCard("👥 Customer Activity Today", "Customer engagement and purchase patterns today");
        JPanel content = (JPanel) card.getComponent(1);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(30, "Visits", "Mon");
        dataset.addValue(45, "Visits", "Tue");
        dataset.addValue(25, "Visits", "Wed");
        dataset.addValue(60, "Visits", "Thu");
        dataset.addValue(55, "Visits", "Fri");
        dataset.addValue(70, "Visits", "Sat");
        dataset.addValue(40, "Visits", "Sun");

        JFreeChart chart = ChartFactory.createBarChart(
            null, "Day", "Visits", dataset, PlotOrientation.VERTICAL, false, true, false
        );
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        content.add(cp, BorderLayout.CENTER);
        // store chart for weekly customers per day update
        card.putClientProperty("customersChartPanel", cp);
        return card;
    }

    // Helper to update weekly charts
    private void refreshWeeklyCharts() {
        // compute start of current week (Monday) and end (Sunday)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.DayOfWeek firstDayOfWeek = java.time.DayOfWeek.MONDAY;
        java.time.LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(firstDayOfWeek));
        java.time.LocalDateTime from = startOfWeek.atStartOfDay();
        java.time.LocalDateTime to = startOfWeek.plusDays(7).atStartOfDay().minusNanos(1);

        dao.AppOrderDao dao = new dao.AppOrderDao();
        java.util.Map<java.time.LocalDate, Double> revenueByDay = dao.getRevenueByDay(from, to);
        java.util.Map<java.time.LocalDate, Long> ordersByDay = dao.getOrderCountByDay(from, to);
        java.util.Map<java.time.LocalDate, Long> customersByDay = dao.getDistinctCustomerCountByDay(from, to);

        // Prepare datasets
        DefaultCategoryDataset jointDatasetRevenue = new DefaultCategoryDataset();
        DefaultCategoryDataset jointDatasetOrders = new DefaultCategoryDataset();
        DefaultCategoryDataset customersDataset = new DefaultCategoryDataset();

        // Iterate days from startOfWeek to startOfWeek+6
        for (int i = 0; i < 7; i++) {
            java.time.LocalDate d = startOfWeek.plusDays(i);
            String label = d.getDayOfWeek().toString().substring(0,3);
            double rev = revenueByDay.getOrDefault(d, 0.0);
            long ord = ordersByDay.getOrDefault(d, 0L);
            long cust = customersByDay.getOrDefault(d, 0L);
            jointDatasetRevenue.addValue(rev, "Revenue", label);
            jointDatasetOrders.addValue(ord, "Orders", label);
            customersDataset.addValue(cust, "Customers", label);
        }

        // Update joint chart
        // find joint chart panel (search in component tree)
        java.util.List<Component> cards = findStatCards(this);
        for (Component comp : cards) {
            if (comp instanceof JComponent) {
                JComponent jc = (JComponent) comp;
                Object cpObj = jc.getClientProperty("jointChartPanel");
                if (cpObj instanceof ChartPanel) {
                    ChartPanel cp = (ChartPanel) cpObj;
                    // create combined chart: use Revenue as line dataset and Orders as bar dataset
                    JFreeChart chart = ChartFactory.createBarChart(null, "Day", "Revenue", jointDatasetRevenue);
                    cp.setChart(chart);
                }
                Object customersObj = jc.getClientProperty("customersChartPanel");
                if (customersObj instanceof ChartPanel) {
                    ChartPanel ccp = (ChartPanel) customersObj;
                    JFreeChart chartCust = ChartFactory.createLineChart(null, "Day", "Customers", customersDataset);
                    ccp.setChart(chartCust);
                }
            }
        }
    }

    private JPanel createBestSellersChartCard() {
        JPanel card = createChartCard("🏆 Best Selling Products", "Top performing products today");
        JPanel content = (JPanel) card.getComponent(1);

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Coffee", 35);
        dataset.setValue("Tea", 25);
        dataset.setValue("Snacks", 20);
        dataset.setValue("Desserts", 20);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, true, false);
        ChartPanel cp = new ChartPanel(chart);
        content.add(cp, BorderLayout.CENTER);
        return card;
    }
}