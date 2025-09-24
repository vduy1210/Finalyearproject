package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);

        reloadTable();
    }

    private JComponent createHeader() {
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Helvetica", Font.BOLD, 18));
        return title;
    }

    private JComponent createCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(createTable(), BorderLayout.CENTER);
        center.add(createForm(), BorderLayout.EAST);
        return center;
    }

    private JScrollPane createTable() {
        tableModel = new DefaultTableModel(new Object[]{"UserID", "Username", "Email", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());
        return new JScrollPane(userTable);
    }

    private JPanel createForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(16);
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(16);
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Role"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"admin", "staff"});
        form.add(roleCombo, gbc);

        return form;
    }

    private JPanel createActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        refreshBtn.addActionListener(e -> reloadTable());

        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        return actions;
    }

    private void reloadTable() {
        List<user> users = userDAO.listUsers();
        tableModel.setRowCount(0);
        for (user u : users) {
            tableModel.addRow(new Object[]{u.getUserId(), u.getUserName(), u.getEmail(), u.getRole()});
        }
    }

    private void fillFormFromSelection() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            usernameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
            emailField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
            String role = String.valueOf(tableModel.getValueAt(row, 3));
            roleCombo.setSelectedItem(role != null ? role.toUpperCase() : "STAFF");
            passwordField.setText("");
        }
    }

    private void handleAdd() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = String.valueOf(roleCombo.getSelectedItem());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.isUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = userDAO.createUser(username, password, email, role);
        if (ok) {
            JOptionPane.showMessageDialog(this, "User created.");
            reloadTable();
            clearForm();
        } else {
            String err = userDAO.getLastErrorMessage();
            JOptionPane.showMessageDialog(this, "Create failed: " + (err != null ? err : "Unknown error"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = String.valueOf(roleCombo.getSelectedItem());

        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Keep old password if none entered
        if (password.isEmpty()) {
            // Fetch current password (not best practice, but placeholder for now)
            password = fetchCurrentPassword(userId);
        }
        boolean ok = userDAO.updateUser(userId, username, password, email, role);
        if (ok) {
            JOptionPane.showMessageDialog(this, "User updated.");
            reloadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }
        int userId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userDAO.deleteUser(userId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                reloadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
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
        // Fallback query to get current password
        // NOTE: In a real app, never pull raw passwords; use hashes and dedicated flows
        try {
            for (user u : userDAO.listUsers()) {
                if (u.getUserId() == userId) return u.getPassword();
            }
        } catch (Exception ignore) {
        }
        return "";
    }
}


