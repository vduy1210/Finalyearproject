package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import dao.UserDAO;

public class LoginForm extends JFrame {

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);       // Blue
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);   // Light Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    
    // Modern typography
    // Slightly smaller header to match prior size
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BRAND_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font BRAND_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 17);
    // removed unused FONT constants

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Sales Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Set modern look and feel
        setBackground(BACKGROUND_COLOR);
        
        // Create main content
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Left panel - Branding
        JPanel leftPanel = createBrandingPanel();
        leftPanel.setPreferredSize(new Dimension(500, 0));
        
    // Right panel - Login form
    JPanel rightPanel = createLoginPanel();
    // Give even more horizontal room so input fields can be longer
    rightPanel.setPreferredSize(new Dimension(900, 0));
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private JPanel createBrandingPanel() {
        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBackground(PRIMARY_COLOR);
        brandingPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        // Logo/Icon - Make it more prominent
        JLabel logoLabel = new JLabel("🏪");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Title
        JLabel titleLabel = new JLabel("SALES MANAGEMENT");
        titleLabel.setFont(BRAND_TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("APPLICATION");
        subtitleLabel.setFont(BRAND_TITLE_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center; color: white;'>" +
                "Manage your business efficiently with our<br>" +
                "comprehensive sales management solution</div></html>");
        descLabel.setFont(BRAND_DESC_FONT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features list
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBackground(PRIMARY_COLOR);
        featuresPanel.setBorder(new EmptyBorder(30, 20, 0, 20));
        

        
        brandingPanel.add(Box.createVerticalGlue());
        brandingPanel.add(logoLabel);
        brandingPanel.add(titleLabel);
        brandingPanel.add(Box.createVerticalStrut(8));
        brandingPanel.add(subtitleLabel);
        brandingPanel.add(Box.createVerticalStrut(25));
        brandingPanel.add(descLabel);
        brandingPanel.add(featuresPanel);
        brandingPanel.add(Box.createVerticalGlue());
        
        return brandingPanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(BACKGROUND_COLOR);
        loginPanel.setLayout(new GridBagLayout());
        
        // Login card
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(CARD_COLOR);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            // Reduce side padding further so the fields appear longer
            new EmptyBorder(50, 36, 50, 36)
        ));
        // Fix the card size to a more square proportion as requested
        Dimension cardSize = new Dimension(480, 548);
        loginCard.setPreferredSize(cardSize);
        loginCard.setMinimumSize(cardSize);
        loginCard.setMaximumSize(cardSize);
        
        // Login header
    JLabel loginTitle = new JLabel("Welcome Back!");
    loginTitle.setFont(TITLE_FONT);
    loginTitle.setForeground(TEXT_PRIMARY);
    // Left align like the reference image
    loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    JLabel loginSubtitle = new JLabel("Please sign in to your account");
    loginSubtitle.setFont(SUBTITLE_FONT);
    loginSubtitle.setForeground(TEXT_SECONDARY);
    // Left align like the reference image
    loginSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usernameField = createCleanTextField("");
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passwordField = createCleanPasswordField("Enter your password");
        
        // Login button
    JButton loginButton = createCleanButton("Sign In", PRIMARY_COLOR, Color.WHITE);
    // Left align so the button lines up with input fields
    loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        registerPanel.setBackground(CARD_COLOR);
        
        JLabel registerText = new JLabel("Don't have an account? ");
        registerText.setFont(INPUT_FONT);
        registerText.setForeground(TEXT_SECONDARY);
        
    JButton registerLink = new RoundedButton("Create Account");
    util.UIUtils.styleActionButton(registerLink, 140);
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerLink.setForeground(PRIMARY_COLOR);
        registerLink.setBorder(null);
        registerLink.setBackground(CARD_COLOR);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setFocusPainted(false);
        
        registerPanel.add(registerText);
        registerPanel.add(registerLink);
        
        // Add components to card - Simplified spacing like in image
        loginCard.add(loginTitle);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(loginSubtitle);
        loginCard.add(Box.createVerticalStrut(35));
        
        loginCard.add(usernameLabel);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(18));
        
        loginCard.add(passwordLabel);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(30));
        
        loginCard.add(loginButton);
        
        // Event handlers
        setupEventHandlers(loginButton);
        
        GridBagConstraints gbc = new GridBagConstraints();
        loginPanel.add(loginCard, gbc);
        
        return loginPanel;
    }

    private JTextField createCleanTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(204, 204, 204), 1),
            new EmptyBorder(14, 16, 14, 16)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(13, 15, 13, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 204, 204), 1),
                    new EmptyBorder(14, 16, 14, 16)
                ));
            }
        });
        
        return field;
    }

    private JPasswordField createCleanPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(204, 204, 204), 1),
            new EmptyBorder(14, 16, 14, 16)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setEchoChar((char) 0); // Show text initially for placeholder
        
        // Placeholder effect
        field.setText(placeholder);
        field.setForeground(TEXT_SECONDARY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('●'); // Set password character
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(13, 15, 13, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0); // Show placeholder text
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 204, 204), 1),
                    new EmptyBorder(14, 16, 14, 16)
                ));
            }
        });
        
        return field;
    }

    private JButton createCleanButton(String text, Color bgColor, Color textColor) {
    JButton button = new RoundedButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(new EmptyBorder(14, 30, 14, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        
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

    private void setupEventHandlers(JButton loginButton) {
        UserDAO userDao = new UserDAO();
        
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String usernameText = usernameField.getText().trim();
                String passwordText = new String(passwordField.getPassword());
            
            // Clear placeholder text for validation
            if (passwordText.equals("Enter your password")) passwordText = "";
            
            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                showModernDialog("Please fill in all fields", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (userDao.authenticateUser(usernameText, passwordText)) {
                showModernDialog("Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                String role = userDao.getUserRole(usernameText);
                final String finalUsername = usernameText; // Make effectively final
                SwingUtilities.invokeLater(() -> {
                    MainApplication mainApp = new MainApplication(finalUsername, role);
                    mainApp.setVisible(true);
                });
                
                dispose();
            } else {
                showModernDialog("Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            }
        });
        
        // Enter key support
        getRootPane().setDefaultButton(loginButton);
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        // Chạy chương trình bắt đầu từ form Login
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}