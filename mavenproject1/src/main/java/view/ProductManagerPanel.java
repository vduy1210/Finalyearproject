package view;

import dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManagerPanel extends JPanel {

    private static final Color COLOR_BACKGROUND = new Color(44, 62, 80);
    private static final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Font FONT_BUTTON = new Font("Helvetica", Font.BOLD, 12);

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Integer> productIdList = new ArrayList<>();

    public ProductManagerPanel() {
        this.setBackground(COLOR_BACKGROUND);
        this.setBorder(new EmptyBorder(10, 20, 20, 20));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton addButton = createActionButton("Add");
        addButton.addActionListener(e -> openAddProductDialog());

        JButton editButton = createActionButton("Edit");
        editButton.addActionListener(e -> openEditDialog());

        JButton deleteButton = createActionButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedProduct());

        actionsPanel.add(addButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0, 0, 10, 0);
        this.add(actionsPanel, gbc);

        String[] columnNames = {"No.", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(new Font("Helvetica", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(productTable);

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(scrollPane, gbc);

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Helvetica", Font.PLAIN, 14));

        JButton searchButton = createActionButton("Search");
        searchButton.addActionListener(e -> searchProduct());

        JPanel searchWrapper = new JPanel(new BorderLayout(10, 0));
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField, BorderLayout.CENTER);
        searchWrapper.add(searchButton, BorderLayout.EAST);

        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtonsPanel.setOpaque(false);

        JButton showAllButton = createActionButton("Show all product");
        showAllButton.addActionListener(e -> loadProductData());


        leftButtonsPanel.add(showAllButton);

        bottomPanel.add(leftButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(searchWrapper, BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 0, 0);
        this.add(bottomPanel, gbc);

        loadProductData();
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        productIdList.clear();

        List<Object[]> productList = ProductDAO.getAllProducts();
        int index = 1;
        for (Object[] row : productList) {
            int id = (int) row[0];
            productIdList.add(id);
            Object[] displayRow = {index++, row[1], row[2], row[3]};
            tableModel.addRow(displayRow);
        }
    }

    private void searchProduct() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (!name.contains(keyword)) {
                tableModel.removeRow(i);
                productIdList.remove(i);
            }
        }
    }

    private void openAddProductDialog() {
        ProductDialog.openAddDialog(this, () -> loadProductData());
    }

    private void openEditDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productId = productIdList.get(selectedRow);
        ProductDialog.openEditDialog(this, productId, () -> loadProductData());
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int productId = productIdList.get(selectedRow);
            boolean deleted = ProductDAO.deleteProduct(productId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProductData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(COLOR_WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }
}
