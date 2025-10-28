package view;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO; // Keeping this import as it is used in the code

public class RegisterForm extends JFrame {

    public RegisterForm() {
        setTitle("Register Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Các thành phần giao diện không đổi ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(21, 75, 158));
        leftPanel.setPreferredSize(new Dimension(350, getHeight()));
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(51, 132, 233));
        rightPanel.setLayout(new BorderLayout());
        add(rightPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(51, 132, 233));
        rightPanel.add(formPanel, BorderLayout.CENTER);

        JLabel titleRegister = new JLabel("<html><div style='text-align: center;'>Register</div></html>");
        titleRegister.setFont(new Font("Arial", Font.BOLD, 24));
        titleRegister.setForeground(Color.WHITE);
        titleRegister.setBounds(250, 50, 400, 60);
        formPanel.add(titleRegister);

        JLabel title = new JLabel("<html><div style='text-align: center;'>SALES MANAGEMENT<br>APPLICATION</div></html>");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(title, BorderLayout.CENTER);

        JLabel registeruserLabel = new JLabel("Username");
        registeruserLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        registeruserLabel.setForeground(Color.WHITE);
        registeruserLabel.setBounds(200, 150, 100, 30);
        formPanel.add(registeruserLabel);

        JTextField registerUsernameField = new JTextField();
        registerUsernameField.setBounds(200, 180, 300, 30);
        registerUsernameField.setBackground(Color.WHITE);
        formPanel.add(registerUsernameField);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(200, 230, 100, 30);
        formPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(200, 260, 300, 30);
        emailField.setBackground(Color.WHITE);
        formPanel.add(emailField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(200, 310, 100, 30);
        formPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(200, 340, 300, 30);
        passwordField.setBackground(Color.WHITE);
        formPanel.add(passwordField);

    JButton registerButton = new RoundedButton("Register");
    util.UIUtils.styleActionButton(registerButton, 140);
        registerButton.setBounds(280, 400, 140, 35);
        registerButton.setBackground(Color.LIGHT_GRAY);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(registerButton);

    JButton gotoLoginButton = new RoundedButton("Go to Login");
    util.UIUtils.styleActionButton(gotoLoginButton, 140);
        gotoLoginButton.setBounds(280, 450, 140, 35);
        gotoLoginButton.setBackground(Color.LIGHT_GRAY);
        gotoLoginButton.setFont(new Font("Arial", Font.BOLD, 16));
        leftPanel.add(gotoLoginButton, BorderLayout.SOUTH);

        // --- Action Listeners ---

        gotoLoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                dispose();
            }
        });

        registerButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String username = registerUsernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Validate input
                if (username.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UserDAO userDAO = new UserDAO();
                if (userDAO.registerUser(username, email, password)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Registration successful!");
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                    RegisterForm.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Registration failed. Username or email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterForm registerForm = new RegisterForm();
            registerForm.setVisible(true);
        });
    }
}