package view;

import dao.GetProduct;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);

    private static final Font FONT_BUTTON = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_LABEL = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 16);

    private JTable productTable;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JTextField totalTextField;
    private JTextField customerNameField;
    private JTextField discountField;
    private JSpinner quantitySpinner;
    private NumberFormat currencyFormat;

    public OrderPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        GridBagConstraints layout = new GridBagConstraints();
        layout.insets = new Insets(5, 5, 5, 5);

        layout.gridx = 0; layout.gridy = 0;
        layout.weightx = 0.45; layout.weighty = 0.9;
        layout.fill = GridBagConstraints.BOTH;
        add(createProductListPanelFromDatabase(), layout);

        layout.gridx = 1;
        layout.weightx = 0.1;
        layout.fill = GridBagConstraints.VERTICAL;
        add(createAddToCartPanel(), layout);

        layout.gridx = 2;
        layout.weightx = 0.45;
        layout.fill = GridBagConstraints.BOTH;
        add(createCartPanel(), layout);

        layout.gridx = 0; layout.gridy = 1; layout.gridwidth = 3;
        layout.weighty = 0;
        layout.fill = GridBagConstraints.HORIZONTAL;
        add(createOrderInfoPanel(), layout);

        layout.gridy = 2;
        add(createActionButtons(), layout);
    }

    private JScrollPane createProductListPanelFromDatabase() {
        String[] columns = {"Product Name", "Price"};
        DefaultTableModel productModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        List<Product> products = GetProduct.getAllProducts();
        for (Product p : products) {
            productModel.addRow(new Object[]{p.getName(), p.getPrice()});
        }

        productTable = new JTable(productModel);
        productTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        productTable.setRowHeight(28);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(createTitledBorder(" List of Products "));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JPanel createAddToCartPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Quantity:");
        label.setForeground(LIGHT_TEXT);
        label.setFont(FONT_LABEL);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(FONT_LABEL);
        quantitySpinner.setMaximumSize(new Dimension(80, 40));

        JButton addButton = new JButton("➕ Add to cart");
        addButton.setFont(FONT_BUTTON);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        panel.add(Box.createVerticalGlue());
        panel.add(label);
        panel.add(quantitySpinner);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(addButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JScrollPane createCartPanel() {
        String[] columns = {"Item", "Quantity", "Subtotal"};
        cartModel = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setFont(new Font("Helvetica", Font.PLAIN, 14));
        cartTable.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(createTitledBorder(" Cart "));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel nameLabel = createLabel("Customer Name:");
        customerNameField = new JTextField();

        JLabel discountLabel = createLabel("Discount:");
        discountField = new JTextField("0");

        JLabel totalLabel = createLabel("Total:");
        totalTextField = new JTextField("0");
        totalTextField.setEditable(false);

        JLabel dateLabel = createLabel("Date:");
        JTextField dateField = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        dateField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(customerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(discountLabel, gbc);
        gbc.gridx = 1;
        panel.add(discountField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(totalLabel, gbc);
        gbc.gridx = 3;
        panel.add(totalTextField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(dateLabel, gbc);
        gbc.gridx = 3;
        panel.add(dateField, gbc);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setOpaque(false);

        JButton createOrderButton = createButton("Create Order");
        JButton exportReceiptButton = createButton("Export Receipt");
        JButton cancelButton = createButton("Cancel Order");

        panel.add(createOrderButton);
        panel.add(exportReceiptButton);
        panel.add(cancelButton);

        return panel;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MAIN_COLOR, 2),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_TITLE,
                LIGHT_TEXT
        );
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(LIGHT_TEXT);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(MAIN_COLOR);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        return button;
    }
}
