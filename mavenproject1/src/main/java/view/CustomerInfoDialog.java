package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomerInfoDialog extends JDialog {
    
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(236, 240, 241);
    
    private static final Font FONT_LABEL = new Font("Helvetica", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 16);
    
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private boolean confirmed = false;
    
    public CustomerInfoDialog(JFrame parent, String phoneNumber) {
        super(parent, "New Customer Information", true);
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Initialize fields
        nameField = new JTextField(20);
        phoneField = new JTextField(phoneNumber, 20);
        emailField = new JTextField(20);
        
        // Phone field is read-only since it's passed from order
        phoneField.setEditable(false);
        phoneField.setBackground(new Color(240, 240, 240));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Please provide customer information:");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(LIGHT_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Name field
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        mainPanel.add(createLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        mainPanel.add(nameField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        mainPanel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        mainPanel.add(phoneField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        mainPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        mainPanel.add(emailField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton confirmButton = createButton("Confirm");
        JButton cancelButton = createButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(LIGHT_TEXT);
        return label;
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_LABEL);
        button.setBackground(MAIN_COLOR);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        return button;
    }
    
    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getCustomerName() {
        return nameField.getText().trim();
    }
    
    public String getCustomerPhone() {
        return phoneField.getText().trim();
    }
    
    public String getCustomerEmail() {
        return emailField.getText().trim();
    }
} 