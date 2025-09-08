package view;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;




public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Login Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(21, 75, 158));
        leftPanel.setPreferredSize(new Dimension(350, getHeight()));
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(51, 132, 233));
        rightPanel.setLayout(null);
        add(rightPanel, BorderLayout.CENTER);

        JLabel titleLogin = new JLabel("<html><div style='text-align: center;'>Login</div></html>");
        titleLogin.setFont(new Font("Arial", Font.BOLD, 24));
        titleLogin.setForeground(Color.WHITE);
        titleLogin.setBounds(250, 50, 400, 60);
        rightPanel.add(titleLogin);

        JLabel title = new JLabel("<html><div style='text-align: center;'>SALES MANAGEMENT<br>APPLICATION</div></html>");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(title, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(200, 150, 100, 30);
        rightPanel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(200, 180, 300, 30);
        usernameField.setBackground(Color.WHITE);
        rightPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(200, 230, 100, 30);
        rightPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(200, 260, 300, 30);
        passwordField.setBackground(Color.WHITE);
        rightPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(280, 320, 140, 35);
        loginButton.setBackground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(loginButton);

        JButton gotoRegisterButton = new JButton("Go to Register");
        gotoRegisterButton.setBounds(280, 450, 140, 35);
        gotoRegisterButton.setBackground(Color.LIGHT_GRAY);
        gotoRegisterButton.setFont(new Font("Arial", Font.BOLD, 16));
        leftPanel.add(gotoRegisterButton, BorderLayout.SOUTH);

        gotoRegisterButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                RegisterForm registerForm = new RegisterForm();
                registerForm.setVisible(true);
            });
                dispose();
        });

        // Create an instance of UserDao
        UserDAO userDao = new UserDAO();

        // === ĐOẠN CODE ĐƯỢC CẬP NHẬT ===
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userDao.authenticateUser(username, password)) {
                // Đăng nhập thành công
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Lấy role từ database
                String role = userDao.getUserRole(username);
                // Chuyển sang cửa sổ chính và đóng cửa sổ login
                SwingUtilities.invokeLater(() -> {
                    MainApplication mainApp = new MainApplication(username, role); // Tạo cửa sổ chính với tên và vai trò thực tế
                    mainApp.setVisible(true); // Hiển thị nó
                });

                dispose(); // Đóng cửa sổ LoginForm hiện tại

            } else {
                // Đăng nhập thất bại
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        // Chạy chương trình bắt đầu từ form Login
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}