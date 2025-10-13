package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import dao.UserDAO;
import model.user;

public class UserManagementPanel extends JPanel {
    private final UserDAO userDAO = new UserDAO();
    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleCombo;
    private JTextField searchField;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);      // Blue
    private static final Color SECONDARY_COLOR = new Color(255, 193, 7);     // Amber
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);       // Green
    private static final Color ERROR_COLOR = new Color(244, 67, 54);         // Red
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Light Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    public UserManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        reloadTable();
    }

    private JComponent createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleIcon = new JLabel("👥");
        titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel subtitle = new JLabel("Manage system users and their permissions");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);
        
        titlePanel.add(titleIcon);
        titlePanel.add(title);
        
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BACKGROUND_COLOR);
        titleWrapper.add(titlePanel, BorderLayout.NORTH);
        titleWrapper.add(subtitle, BorderLayout.CENTER);
        
        // Search section
        JPanel searchPanel = createSearchPanel();
        
        headerPanel.add(titleWrapper, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setBackground(Color.WHITE);
        
        // Add search functionality
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        
        JButton refreshBtn = createModernButton("🔄 Refresh", PRIMARY_COLOR, Color.WHITE);
        refreshBtn.addActionListener(evt -> reloadTable());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(refreshBtn);
        
        return searchPanel;
    }

    private JComponent createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Left side - Table
        JPanel leftPanel = createTablePanel();
        
        // Right side - Form
        JPanel rightPanel = createFormPanel();
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        return mainPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Table title
        JLabel tableTitle = new JLabel("User List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create modern table
        JScrollPane tableScrollPane = createModernTable();
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JScrollPane createModernTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Role", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(35);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setSelectionBackground(new Color(33, 150, 243, 50));
        userTable.setSelectionForeground(TEXT_PRIMARY);
        userTable.setGridColor(new Color(240, 240, 240));
        userTable.setShowGrid(true);
        userTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Modern table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(0, 40));
        
        // Custom cell renderer for better styling
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }
                
                // Style role column
                if (column == 3 && value != null) {
                    String role = value.toString().toLowerCase();
                    if ("admin".equals(role)) {
                        setForeground(ERROR_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(PRIMARY_COLOR);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        };
        
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Role
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Status
        
        userTable.getSelectionModel().addListSelectionListener(evt -> fillFormFromSelection());
        
        // Add double-click to edit
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fillFormFromSelection();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        return scrollPane;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(350, 0));
        formContainer.setBackground(BACKGROUND_COLOR);
        
        // Form card
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(CARD_COLOR);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Form title
        JLabel formTitle = new JLabel("User Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel formSubtitle = new JLabel("Add or edit user information");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formSubtitle.setForeground(TEXT_SECONDARY);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(formSubtitle);
        formCard.add(Box.createVerticalStrut(25));
        
        // Form fields
        formCard.add(createFormField("👤 Username", usernameField = new JTextField()));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormField("🔒 Password", passwordField = new JPasswordField()));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormField("📧 Email", emailField = new JTextField()));
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(createFormField("👔 Role", roleCombo = new JComboBox<>(new String[]{"staff", "admin"})));
        
        formCard.add(Box.createVerticalStrut(30));
        
        // Action buttons
        JPanel buttonPanel = createFormButtons();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(buttonPanel);
        
        formContainer.add(formCard, BorderLayout.NORTH);
        return formContainer;
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(CARD_COLOR);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(TEXT_PRIMARY);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(0, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setBackground(Color.WHITE);
        }
        
        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(field);
        
        return fieldPanel;
    }

    private JPanel createFormButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JButton addBtn = createModernButton("➕ Add", SUCCESS_COLOR, Color.WHITE);
        JButton updateBtn = createModernButton("✏️ Update", SECONDARY_COLOR, Color.WHITE);
        JButton deleteBtn = createModernButton("🗑️ Delete", ERROR_COLOR, Color.WHITE);
        JButton clearBtn = createModernButton("🧹 Clear", new Color(158, 158, 158), Color.WHITE);
        
        addBtn.addActionListener(evt -> handleAdd());
        updateBtn.addActionListener(evt -> handleUpdate());
        deleteBtn.addActionListener(evt -> handleDelete());
        clearBtn.addActionListener(evt -> clearForm());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        return buttonPanel;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
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

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            reloadTable();
            return;
        }
        
        List<user> allUsers = userDAO.listUsers();
        tableModel.setRowCount(0);
        
        for (user u : allUsers) {
            if (u.getUserName().toLowerCase().contains(searchText) ||
                u.getEmail().toLowerCase().contains(searchText) ||
                u.getRole().toLowerCase().contains(searchText)) {
                tableModel.addRow(new Object[]{
                    u.getUserId(), 
                    u.getUserName(), 
                    u.getEmail(), 
                    u.getRole(),
                    "Active"
                });
            }
        }
    }

    private void reloadTable() {
        List<user> users = userDAO.listUsers();
        tableModel.setRowCount(0);
        for (user u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(), 
                u.getUserName(), 
                u.getEmail(), 
                u.getRole(),
                "Active"
            });
        }
        searchField.setText("");
    }

    private void fillFormFromSelection() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            usernameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
            emailField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
            String role = String.valueOf(tableModel.getValueAt(row, 3));
            roleCombo.setSelectedItem(role != null ? role.toLowerCase() : "staff");
            passwordField.setText("");
        }
    }

    private void handleAdd() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = String.valueOf(roleCombo.getSelectedItem());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showModernDialog("Please fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.isUsernameExists(username)) {
            showModernDialog("Username already exists.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.isEmailExists(email)) {
            showModernDialog("Email already exists.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean ok = userDAO.createUser(username, password, email, role);
        if (ok) {
            showModernDialog("User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            reloadTable();
            clearForm();
        } else {
            String err = userDAO.getLastErrorMessage();
            showModernDialog("Create failed: " + (err != null ? err : "Unknown error"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showModernDialog("Please select a user to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = String.valueOf(roleCombo.getSelectedItem());

        if (username.isEmpty() || email.isEmpty()) {
            showModernDialog("Username and email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            password = fetchCurrentPassword(userId);
        }
        
        boolean ok = userDAO.updateUser(userId, username, password, email, role);
        if (ok) {
            showModernDialog("User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            reloadTable();
            clearForm();
        } else {
            showModernDialog("Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showModernDialog("Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = String.valueOf(tableModel.getValueAt(row, 1));
        
        int confirm = showModernConfirmDialog(
            "Are you sure you want to delete user '" + username + "'?", 
            "Confirm Deletion"
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userDAO.deleteUser(userId);
            if (ok) {
                showModernDialog("User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadTable();
                clearForm();
            } else {
                showModernDialog("Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleCombo.setSelectedItem("staff");
        userTable.clearSelection();
    }

    private String fetchCurrentPassword(int userId) {
        try {
            for (user u : userDAO.listUsers()) {
                if (u.getUserId() == userId) return u.getPassword();
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private int showModernConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
}


