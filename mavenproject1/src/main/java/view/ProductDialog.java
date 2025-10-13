package view;

import dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductDialog extends JDialog {
    private static final Color COLOR_BORDER = new Color(230, 235, 241);
    private static final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;

    public static void openAddDialog(Component parent, Runnable onSuccess) {
        ProductDialog dialog = new ProductDialog(null, onSuccess);
        dialog.setTitle("Add New Product");
        dialog.setVisible(true);
    }

    public static void openEditDialog(Component parent, int productId, Runnable onSuccess) {
        Object[] product = ProductDAO.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(parent, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProductDialog dialog = new ProductDialog(product, onSuccess);
        dialog.setTitle("Edit Product");
        dialog.setVisible(true);
    }

    private ProductDialog(Object[] product, Runnable onSuccess) {
    setModal(true);
    // Let the layout decide the exact size; we'll provide a reasonable preferred size.
    setPreferredSize(new Dimension(460, 380));
    setLayout(new BorderLayout());

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Name
    JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(FONT_LABEL);
        nameLabel.setForeground(COLOR_TEXT);
    nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField = new JTextField();
        nameField.setFont(FONT_FIELD);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
    nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price
    JLabel priceLabel = new JLabel("Price");
        priceLabel.setFont(FONT_LABEL);
        priceLabel.setForeground(COLOR_TEXT);
    priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceField = new JTextField();
        priceField.setFont(FONT_FIELD);
        priceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
    priceField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Stock
    JLabel stockLabel = new JLabel("Stock");
        stockLabel.setFont(FONT_LABEL);
        stockLabel.setForeground(COLOR_TEXT);
    stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockField = new JTextField();
        stockField.setFont(FONT_FIELD);
        stockField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
    stockField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to body with spacing
        body.add(nameLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(nameField);
        body.add(Box.createVerticalStrut(12));
        body.add(priceLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(priceField);
        body.add(Box.createVerticalStrut(12));
        body.add(stockLabel);
        body.add(Box.createVerticalStrut(6));
    body.add(stockField);
    // Spacer to avoid tight overlap with the bottom button bar
    body.add(Box.createVerticalStrut(8));

        if (product != null) {
            nameField.setText(product[1].toString());
            priceField.setText(product[2].toString());
            stockField.setText(product[3].toString());
        }

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(COLOR_PRIMARY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(new EmptyBorder(10, 16, 10, 16));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { dispose(); }
        });

        JButton saveButton = new JButton("Save");
        saveButton.setFont(FONT_BUTTON);
        saveButton.setBackground(COLOR_PRIMARY);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
    saveButton.setBorder(new EmptyBorder(10, 16, 10, 16));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

                if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                    JOptionPane.showMessageDialog(ProductDialog.this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) throw new NumberFormatException();
                int stock = Integer.parseInt(stockText);
                if (stock < 0) throw new NumberFormatException();

                boolean success;
                if (product == null) {
                    success = ProductDAO.addProduct(name, price, stock);
                } else {
                    int id = (int) product[0];
                    success = ProductDAO.updateProduct(id, name, price, stock);
                }

                if (success) {
                    JOptionPane.showMessageDialog(ProductDialog.this, "Product saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    onSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(ProductDialog.this, "Failed to save product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ProductDialog.this, "Invalid price or stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 16, 20));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

    add(body, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    // Size to fit content and center on screen
    pack();
    setResizable(false);
    setLocationRelativeTo(null);
    }
}
