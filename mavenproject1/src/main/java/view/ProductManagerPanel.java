package view;

import dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.regex.Pattern;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ProductManagerPanel extends JPanel {

    // Colors and fonts aligned with the modern app style
    private static final Color COLOR_BACKGROUND = new Color(245, 247, 251); // light app background
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_MUTED = new Color(108, 117, 125);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    // Table background request: #E5E7EB
    private static final Color TABLE_BG = new Color(0xE5, 0xE7, 0xEB);
    private static final Color ROW_ALT = new Color(0xEF, 0xF1, 0xF5); // slightly lighter for zebra effect

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 16); // slightly larger for prominence
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 14);

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private final List<Integer> productIdList = new ArrayList<>();

    public ProductManagerPanel() {
        setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Header with title and subtitle
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Products");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT);
        JLabel subtitle = new JLabel("Manage your product catalog, pricing, and stock");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(COLOR_MUTED);
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        header.add(createToolbar(), BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(COLOR_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(8, 8, 8, 8)
        ));

        // Table model and table
        String[] columnNames = {"No.", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setFont(FONT_TABLE);
        productTable.setRowHeight(40);
        productTable.setShowGrid(false);
        productTable.setIntercellSpacing(new Dimension(0, 0));
        productTable.setFillsViewportHeight(true);
        productTable.setSelectionBackground(new Color(232, 244, 253));
        productTable.setSelectionForeground(COLOR_TEXT);
        productTable.setBackground(TABLE_BG);
        // Header styling
        JTableHeader th = productTable.getTableHeader();
        th.setFont(FONT_TABLE_HEADER);
        th.setBackground(Color.WHITE);
        th.setForeground(COLOR_TEXT);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Alternating row background
        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground((row % 2 == 0) ? TABLE_BG : ROW_ALT);
                }
                return c;
            }
        };
        // Name column renderer to be a bit bolder for emphasis
        DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = altRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(FONT_TABLE.deriveFont(Font.BOLD));
                return c;
            }
        };
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (i == 1) {
                productTable.getColumnModel().getColumn(i).setCellRenderer(nameRenderer);
            } else {
                productTable.getColumnModel().getColumn(i).setCellRenderer(altRenderer);
            }
        }

        // Row sorter and filtering
        rowSorter = new TableRowSorter<>(tableModel);
        productTable.setRowSorter(rowSorter);

        // Double-click to edit
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openEditDialog();
                }
            }
        });

    JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    // Match viewport background to table bg so edges look seamless
    scrollPane.getViewport().setBackground(TABLE_BG);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        loadProductData();
    }

    private JPanel createToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setOpaque(false);

        // Search field with placeholder
        searchField = new JTextField(18);
        searchField.setFont(FONT_TABLE);
        searchField.setToolTipText("Search products by name");
        searchField.putClientProperty("JTextField.placeholderText", "Search products...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));

        // Live filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (text == null || text.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text.trim()), 1));
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        JButton addBtn = createPrimaryButton("Add");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { openAddProductDialog(); }
        });

        JButton editBtn = createDefaultButton("Edit");
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { openEditDialog(); }
        });

        JButton deleteBtn = createDangerButton("Delete");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { deleteSelectedProduct(); }
        });

        JButton refreshBtn = createDefaultButton("Refresh");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                searchField.setText("");
                loadProductData();
            }
        });

        bar.add(searchField);
        bar.add(addBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(refreshBtn);
        return bar;
    }

    public void loadProductData() {
        tableModel.setRowCount(0);
        productIdList.clear();

        java.util.List<Object[]> productList = ProductDAO.getAllProducts();
        int index = 1;
        for (Object[] row : productList) {
            int id = (int) row[0];
            productIdList.add(id);
            Object[] displayRow = {index++, row[1], row[2], row[3]};
            tableModel.addRow(displayRow);
        }
        if (rowSorter != null) {
            rowSorter.setRowFilter(null);
        }
    }

    private void openAddProductDialog() {
        ProductDialog.openAddDialog(this, this::loadProductData);
    }

    private void openEditDialog() {
        int selectedViewRow = productTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = productTable.convertRowIndexToModel(selectedViewRow);
        int productId = productIdList.get(modelRow);
        ProductDialog.openEditDialog(this, productId, this::loadProductData);
    }

    private void deleteSelectedProduct() {
        int selectedViewRow = productTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = productTable.convertRowIndexToModel(selectedViewRow);
            int productId = productIdList.get(modelRow);
            boolean deleted = ProductDAO.deleteProduct(productId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProductData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_BORDER),
        new EmptyBorder(10, 16, 10, 16)
    ));
        button.setBackground(Color.WHITE);
        button.setForeground(COLOR_TEXT);
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(COLOR_PRIMARY);
        b.setForeground(Color.WHITE);
        return b;
    }

    private JButton createDefaultButton(String text) {
        return baseButton(text);
    }

    private JButton createDangerButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(new Color(220, 53, 69));
        b.setForeground(Color.WHITE);
        return b;
    }
}
