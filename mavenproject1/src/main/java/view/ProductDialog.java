package view;

import dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductDialog extends JDialog {
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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        if (product != null) {
            nameField.setText(product[1].toString());
            priceField.setText(product[2].toString());
            stockField.setText(product[3].toString());
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);

                boolean success;
                if (product == null) {
                    success = ProductDAO.addProduct(name, price, stock);
                } else {
                    int id = (int) product[0];
                    success = ProductDAO.updateProduct(id, name, price, stock);
                }

                if (success) {
                    JOptionPane.showMessageDialog(this, "Product saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    onSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price or stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
