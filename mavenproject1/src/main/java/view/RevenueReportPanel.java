package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import dao.OrderDao;
import model.Order;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RevenueReportPanel extends JPanel {

    private final OrderDao orderDao = new OrderDao();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JLabel totalRevenueLabel;
    private JLabel totalOrdersLabel;
    private JLabel avgOrderLabel;

    // Modern color scheme
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(33, 150, 243);      // Blue
    private static final java.awt.Color SUCCESS_COLOR = new java.awt.Color(76, 175, 80);       // Green
    private static final java.awt.Color WARNING_COLOR = new java.awt.Color(255, 152, 0);       // Orange
    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(250, 250, 250);  // Light Gray
    private static final java.awt.Color CARD_COLOR = java.awt.Color.WHITE;
    private static final java.awt.Color TEXT_PRIMARY = new java.awt.Color(33, 33, 33);
    private static final java.awt.Color TEXT_SECONDARY = new java.awt.Color(117, 117, 117);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RevenueReportPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setBackground(BACKGROUND_COLOR);

        // Instantiate shared date fields early so other panels can use them
        fromDateField = new JTextField(10);
        toDateField = new JTextField(10);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        // Defaults: today
        LocalDate today = LocalDate.now();
        fromDateField.setText(today.format(DATE_FMT));
        toDateField.setText(today.format(DATE_FMT));
        loadData();
    }

    private JComponent createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleIcon = new JLabel("📊");
        titleIcon.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 28));
        
        JLabel title = new JLabel("Revenue Report");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 32));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel subtitle = new JLabel("Track sales performance and analyze business trends");
        subtitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);
        
        titlePanel.add(titleIcon);
        titlePanel.add(title);
        
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BACKGROUND_COLOR);
        titleWrapper.add(titlePanel, BorderLayout.NORTH);
    titleWrapper.add(subtitle, BorderLayout.CENTER);
    // Add top filter toolbar directly under the title so filters are always visible
    titleWrapper.add(createTopFilterToolbar(), BorderLayout.SOUTH);
        
        headerPanel.add(titleWrapper, BorderLayout.WEST);
        
        return headerPanel;
    }

    private JComponent createTopFilterToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        toolbar.setBackground(BACKGROUND_COLOR);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        fromLabel.setForeground(TEXT_SECONDARY);

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        toLabel.setForeground(TEXT_SECONDARY);

        // Use independent text fields in the top toolbar to avoid adding the same component
        final JTextField topFromField = new JTextField(10);
        final JTextField topToField = new JTextField(10);

        JButton todayBtn = createModernButton("Today", PRIMARY_COLOR, java.awt.Color.WHITE);
        todayBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                setToday();
                // sync top fields
                topFromField.setText(fromDateField.getText());
                topToField.setText(toDateField.getText());
            }
        });
        JButton weekBtn = createModernButton("This Week", new java.awt.Color(156, 39, 176), java.awt.Color.WHITE);
        weekBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisWeek();
                topFromField.setText(fromDateField.getText());
                topToField.setText(toDateField.getText());
            }
        });
        JButton monthBtn = createModernButton("This Month", SUCCESS_COLOR, java.awt.Color.WHITE);
        monthBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisMonth();
                topFromField.setText(fromDateField.getText());
                topToField.setText(toDateField.getText());
            }
        });
        JButton yearBtn = createModernButton("This Year", WARNING_COLOR, java.awt.Color.WHITE);
        yearBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisYear();
                topFromField.setText(fromDateField.getText());
                topToField.setText(toDateField.getText());
            }
        });

        // Apply button will copy values from the top fields to the shared fields and load
        JButton applyBtn = createModernButton("🔍 Apply", new java.awt.Color(96, 125, 139), java.awt.Color.WHITE);
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromDateField.setText(topFromField.getText().trim());
                toDateField.setText(topToField.getText().trim());
                loadData();
            }
        });

        toolbar.add(fromLabel);
        toolbar.add(topFromField);
        toolbar.add(toLabel);
        toolbar.add(topToField);
        toolbar.add(applyBtn);
        toolbar.add(todayBtn);
        toolbar.add(weekBtn);
        toolbar.add(monthBtn);
        toolbar.add(yearBtn);

        return toolbar;
    }

    private JComponent createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Top section - Filters and Statistics
        JPanel topSection = new JPanel(new BorderLayout(20, 0));
        topSection.setBackground(BACKGROUND_COLOR);
        topSection.add(createFiltersPanel(), BorderLayout.WEST);
        topSection.add(createStatsPanel(), BorderLayout.EAST);
        
        // Bottom section - Table and Export
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(BACKGROUND_COLOR);
        bottomSection.add(createTablePanel(), BorderLayout.CENTER);
        bottomSection.add(createExportPanel(), BorderLayout.SOUTH);
        
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(bottomSection, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersCard = new JPanel();
        filtersCard.setLayout(new BoxLayout(filtersCard, BoxLayout.Y_AXIS));
        filtersCard.setBackground(CARD_COLOR);
        filtersCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        filtersCard.setPreferredSize(new Dimension(400, 0));

        // Title
        JLabel filterTitle = new JLabel("📅 Date Range Filter");
        filterTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        filterTitle.setForeground(TEXT_PRIMARY);
        filterTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        filtersCard.add(filterTitle);
        filtersCard.add(Box.createVerticalStrut(20));

        // Date fields
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 15, 15));
        datePanel.setBackground(CARD_COLOR);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel fromLabel = new JLabel("From Date:");
        fromLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        fromLabel.setForeground(TEXT_PRIMARY);
        
        // reuse the shared fromDateField instantiated in constructor
        
        JLabel toLabel = new JLabel("To Date:");
        toLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        toLabel.setForeground(TEXT_PRIMARY);
        
        // reuse the shared toDateField instantiated in constructor
        
        datePanel.add(fromLabel);
        datePanel.add(fromDateField);
        datePanel.add(toLabel);
        datePanel.add(toDateField);
        
        filtersCard.add(datePanel);
        filtersCard.add(Box.createVerticalStrut(20));

        // Quick filter buttons
        JPanel quickFiltersPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        quickFiltersPanel.setBackground(CARD_COLOR);
        quickFiltersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JButton todayBtn = createModernButton("📅 Today", PRIMARY_COLOR, java.awt.Color.WHITE);
        JButton thisWeekBtn = createModernButton("📆 This Week", new java.awt.Color(156, 39, 176), java.awt.Color.WHITE);
        JButton thisMonthBtn = createModernButton("📊 This Month", SUCCESS_COLOR, java.awt.Color.WHITE);
        JButton thisYearBtn = createModernButton("📈 This Year", WARNING_COLOR, java.awt.Color.WHITE);
        JButton filterBtn = createModernButton("🔍 Apply Filter", new java.awt.Color(96, 125, 139), java.awt.Color.WHITE);

        todayBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setToday();
            }
        });
        thisWeekBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisWeek();
            }
        });
        thisMonthBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisMonth();
            }
        });
        thisYearBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setThisYear();
            }
        });
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadData();
            }
        });

        quickFiltersPanel.add(todayBtn);
        quickFiltersPanel.add(thisWeekBtn);
        quickFiltersPanel.add(thisMonthBtn);
        quickFiltersPanel.add(thisYearBtn);
        quickFiltersPanel.add(filterBtn);
        // Add empty cell to maintain grid layout
        quickFiltersPanel.add(new JLabel());
        
        filtersCard.add(quickFiltersPanel);
        
        return filtersCard;
    }

    private JPanel createStatsPanel() {
        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(BACKGROUND_COLOR);
        statsContainer.setPreferredSize(new Dimension(350, 0));
        
        // Revenue card
        JPanel revenueCard = createStatCard("💰", "Total Revenue", "0 VND", SUCCESS_COLOR);
        totalRevenueLabel = (JLabel) ((JPanel) revenueCard.getComponent(1)).getComponent(1);
        
        // Orders card  
        JPanel ordersCard = createStatCard("📦", "Total Orders", "0", PRIMARY_COLOR);
        totalOrdersLabel = (JLabel) ((JPanel) ordersCard.getComponent(1)).getComponent(1);
        
        // Average card
        JPanel avgCard = createStatCard("📊", "Average Order", "0 VND", WARNING_COLOR);
        avgOrderLabel = (JLabel) ((JPanel) avgCard.getComponent(1)).getComponent(1);
        
        statsContainer.add(revenueCard);
        statsContainer.add(Box.createVerticalStrut(15));
        statsContainer.add(ordersCard);
        statsContainer.add(Box.createVerticalStrut(15));
        statsContainer.add(avgCard);
        
        return statsContainer;
    }

    private JPanel createStatCard(String icon, String title, String value, java.awt.Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Icon panel
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(accentColor);
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setBorder(BorderFactory.createEmptyBorder());
        iconPanel.setLayout(new BorderLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Table title
        JLabel tableTitle = new JLabel("📋 Order Details");
        tableTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Create modern table
        JScrollPane tableScrollPane = createModernTable();
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JScrollPane createModernTable() {
        tableModel = new DefaultTableModel(new Object[]{"Order ID", "Date & Time", "Customer", "Staff", "Amount", "Tax", "Discount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(new java.awt.Color(33, 150, 243, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new java.awt.Color(240, 240, 240));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Modern table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        header.setBackground(new java.awt.Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(0, 40));
        
        // Custom cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(java.awt.Color.WHITE);
                    } else {
                        c.setBackground(new java.awt.Color(248, 249, 250));
                    }
                }
                
                // Format currency columns
                if ((column == 4 || column == 5 || column == 6) && value instanceof Number) {
                    setText(String.format("%,.0f VND", ((Number) value).doubleValue()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setForeground(SUCCESS_COLOR);
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(TEXT_PRIMARY);
                    setFont(getFont().deriveFont(java.awt.Font.PLAIN));
                }
                
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // Order ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Date & Time
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Customer
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Staff
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Amount
        table.getColumnModel().getColumn(5).setPreferredWidth(80);   // Tax
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Discount
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 1));
        scrollPane.getViewport().setBackground(java.awt.Color.WHITE);
        
        return scrollPane;
    }

    private JPanel createExportPanel() {
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        exportPanel.setBackground(BACKGROUND_COLOR);
        
        JButton exportBtn = createModernButton("📊 Export Excel", SUCCESS_COLOR, java.awt.Color.WHITE);
        JButton printBtn = createModernButton("🖨️ Print Report", PRIMARY_COLOR, java.awt.Color.WHITE);

        exportBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportExcel();
            }
        });
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printReport();
            }
        });

        exportPanel.add(exportBtn);
        exportPanel.add(printBtn);
        
        return exportPanel;
    }

    private JButton createModernButton(String text, java.awt.Color bgColor, java.awt.Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void loadData() {
        try {
            LocalDate from = LocalDate.parse(fromDateField.getText().trim(), DATE_FMT);
            LocalDate to = LocalDate.parse(toDateField.getText().trim(), DATE_FMT);
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);

            List<Order> orders = orderDao.listOrders(start, end);
            double totalRevenue = orderDao.getTotalRevenue(start, end);
            int totalOrders = orders.size();
            double avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;

            // Update table
            tableModel.setRowCount(0);
            DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Order o : orders) {
                tableModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getOrderDate() != null ? o.getOrderDate().format(dtFmt) : "",
                        o.getCustomerId(),
                        o.getStaffId(),
                        o.getTotalAmount(),
                        o.getTax(),
                        o.getDiscount()
                });
            }
            
            // Update statistics
            totalRevenueLabel.setText(String.format("%,.0f VND", totalRevenue));
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            avgOrderLabel.setText(String.format("%,.0f VND", avgOrder));
            
        } catch (Exception ex) {
            showModernDialog("Invalid date format. Please use yyyy-MM-dd format.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setToday() {
        LocalDate d = LocalDate.now();
        fromDateField.setText(d.format(DATE_FMT));
        toDateField.setText(d.format(DATE_FMT));
        loadData();
    }

    private void setThisWeek() {
        LocalDate today = LocalDate.now();
        // Get Monday of current week (ISO-8601 standard where Monday is first day)
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        // Get Sunday of current week
        LocalDate sunday = monday.plusDays(6);
        
        fromDateField.setText(monday.format(DATE_FMT));
        toDateField.setText(sunday.format(DATE_FMT));
        loadData();
    }

    private void setThisMonth() {
        LocalDate d = LocalDate.now();
        LocalDate first = d.withDayOfMonth(1);
        LocalDate last = d.withDayOfMonth(d.lengthOfMonth());
        fromDateField.setText(first.format(DATE_FMT));
        toDateField.setText(last.format(DATE_FMT));
        loadData();
    }

    private void setThisYear() {
        LocalDate d = LocalDate.now();
        LocalDate first = d.withDayOfYear(1);
        LocalDate last = d.withDayOfYear(d.lengthOfYear());
        fromDateField.setText(first.format(DATE_FMT));
        toDateField.setText(last.format(DATE_FMT));
        loadData();
    }

    private void exportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("revenue_report_" + LocalDate.now().format(DATE_FMT) + ".xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = chooser.getSelectedFile();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Revenue Report");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            Row header = sheet.createRow(0);
            String[] columns = {"Order ID", "Date & Time", "Customer", "Staff", "Amount", "Tax", "Discount"};
            for (int i = 0; i < columns.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(columns[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    Cell cell = row.createCell(c);
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else {
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
            }

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            showModernDialog("Report exported successfully to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showModernDialog("Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        try {
            boolean ok = table.print(JTable.PrintMode.FIT_WIDTH, 
                new java.text.MessageFormat("Revenue Report - " + LocalDate.now().format(DATE_FMT)), 
                new java.text.MessageFormat("Page {0}"));
            if (!ok) {
                showModernDialog("Print operation was cancelled.", "Print Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            showModernDialog("Print failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}


