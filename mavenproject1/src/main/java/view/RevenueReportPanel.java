package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.print.PrinterException;

public class RevenueReportPanel extends JPanel {

    private final OrderDao orderDao = new OrderDao();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JLabel totalRevenueLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RevenueReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createFilters(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        // Defaults: today
        LocalDate today = LocalDate.now();
        fromDateField.setText(today.format(DATE_FMT));
        toDateField.setText(today.format(DATE_FMT));
        loadData();
    }

    private JPanel createFilters() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.add(new JLabel("From (yyyy-MM-dd):"));
        fromDateField = new JTextField(10);
        p.add(fromDateField);
        p.add(new JLabel("To:"));
        toDateField = new JTextField(10);
        p.add(toDateField);

        JButton todayBtn = new JButton("Today");
        JButton thisMonthBtn = new JButton("This Month");
        JButton thisYearBtn = new JButton("This Year");
        JButton filterBtn = new JButton("Filter");

        todayBtn.addActionListener(e -> setToday());
        thisMonthBtn.addActionListener(e -> setThisMonth());
        thisYearBtn.addActionListener(e -> setThisYear());
        filterBtn.addActionListener(e -> loadData());

        p.add(todayBtn);
        p.add(thisMonthBtn);
        p.add(thisYearBtn);
        p.add(filterBtn);
        return p;
    }

    private JScrollPane createTable() {
        tableModel = new DefaultTableModel(new Object[]{"Order ID", "Order Date", "Customer", "Staff", "Total", "Tax", "Discount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private JPanel createFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalRevenueLabel = new JLabel("Total: 0");
        JButton exportBtn = new JButton("Export Excel");
        JButton printBtn = new JButton("Print Report");

        exportBtn.addActionListener(e -> exportExcel());
        printBtn.addActionListener(e -> printReport());

        p.add(totalRevenueLabel);
        p.add(exportBtn);
        p.add(printBtn);
        return p;
    }

    private void loadData() {
        try {
            LocalDate from = LocalDate.parse(fromDateField.getText().trim(), DATE_FMT);
            LocalDate to = LocalDate.parse(toDateField.getText().trim(), DATE_FMT);
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);

            List<Order> orders = orderDao.listOrders(start, end);
            double total = orderDao.getTotalRevenue(start, end);

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
            totalRevenueLabel.setText("Total: " + String.format("%.2f", total));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setToday() {
        LocalDate d = LocalDate.now();
        fromDateField.setText(d.format(DATE_FMT));
        toDateField.setText(d.format(DATE_FMT));
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
        chooser.setSelectedFile(new java.io.File("revenue_report.xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = chooser.getSelectedFile();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Revenue Report");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header row
            Row header = sheet.createRow(0);
            String[] columns = {"Order ID", "Order Date", "Customer", "Staff", "Total", "Tax", "Discount"};
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
            JOptionPane.showMessageDialog(this, "Exported: " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        try {
            boolean ok = table.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("Revenue Report"), null);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Print canceled.");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


