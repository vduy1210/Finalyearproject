package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends JFrame {

    // ======= MÀU SẮC & FONT CHỮ =======
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_COLOR = new Color(52, 152, 219);
    private static final Color TEXT_LIGHT = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;

    private static final Font TITLE_FONT = new Font("Helvetica", Font.BOLD, 20);
    private static final Font MENU_HEADER_FONT = new Font("Helvetica", Font.BOLD, 16);
    private static final Font MENU_ITEM_FONT = new Font("Helvetica", Font.PLAIN, 14);

    // ======= THÀNH PHẦN GIAO DIỆN =======
    private CardLayout layoutSwitcher;
    private JPanel contentPanel;
    private List<JButton> menuButtons;

    private String staffName;
    private String staffRole;

    public MainApplication(String staffName, String staffRole) {
        this.staffName = staffName;
        this.staffRole = staffRole;
        // Cấu hình cửa sổ
        setTitle("Ứng dụng Quản lý");
        setSize(1450, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo danh sách các nút menu
        menuButtons = new ArrayList<>();

        // ======= TẠO KHU VỰC HIỂN THỊ CHÍNH =======
        layoutSwitcher = new CardLayout();
        contentPanel = new JPanel(layoutSwitcher);

        // Thêm các màn hình nội dung
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new ProductManagerPanel(), "PRODUCT_MANAGER");
        contentPanel.add(new OrderPanel(), "ORDER");
        contentPanel.add(new OrderConfirmationPanel(), "HISTORY");
        contentPanel.add(new RevenueTodayPanel(), "REVENUE");

        // ======= THÊM CÁC PHẦN VÀO GIAO DIỆN CHÍNH =======
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebarMenu(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị màn hình mặc định
        layoutSwitcher.show(contentPanel, "DASHBOARD");
        highlightMenuButton("Dashboard");
    }

    // ======= TẠO PHẦN HEADER (TIÊU ĐỀ TRÊN CÙNG) =======
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MAIN_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Dashboard - Staff Management");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_LIGHT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField staffInfo = new JTextField(staffName + " - " + staffRole);
        staffInfo.setEditable(false);
        staffInfo.setBackground(WHITE);
        staffInfo.setPreferredSize(new Dimension(200, 30));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(MENU_HEADER_FONT);
        logoutButton.setPreferredSize(new Dimension(120, 30));
        logoutButton.addActionListener(e -> {
            // Close main window and show login form
            SwingUtilities.invokeLater(() -> {
                new LoginForm().setVisible(true);
            });
            this.dispose();
        });
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(staffInfo);
        rightPanel.add(logoutButton);

        header.add(title, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ======= TẠO MENU BÊN TRÁI =======
    private JPanel createSidebarMenu() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BACKGROUND_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(MENU_HEADER_FONT);
        menuTitle.setForeground(WHITE);
        menuTitle.setBorder(new EmptyBorder(0, 5, 20, 0));
        sidebar.add(menuTitle);

        // Thêm các nút menu
        addMenuButton("Dashboard", "DASHBOARD", sidebar);
        addMenuButton("Product management", "PRODUCT_MANAGER", sidebar);
        addMenuButton("Order", "ORDER", sidebar);
        addMenuButton("Order History", "HISTORY", sidebar);
        addMenuButton("Revenue Today", "REVENUE", sidebar);  // cần thêm panel sau

        return sidebar;
    }

    // ======= TẠO VÀ THÊM NÚT MENU =======
    private void addMenuButton(String label, String panelName, JPanel sidebar) {
        JButton menuButton = new JButton(label);
        menuButton.setFont(MENU_ITEM_FONT);
        menuButton.setBackground(WHITE);
        menuButton.setForeground(Color.BLACK);
        menuButton.setFocusPainted(false);
        menuButton.setHorizontalAlignment(SwingConstants.LEFT);
        menuButton.setBorder(new EmptyBorder(15, 20, 15, 20));
        menuButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, menuButton.getPreferredSize().height));
        menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Khi nhấn vào nút
        menuButton.addActionListener(e -> {
            layoutSwitcher.show(contentPanel, panelName);  // chuyển sang màn hình tương ứng
            highlightMenuButton(label);
        });

        menuButtons.add(menuButton);
        sidebar.add(menuButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));  // khoảng cách
    }

    // ======= TÔ MÀU NÚT MENU ĐANG CHỌN =======
    private void highlightMenuButton(String activeLabel) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(activeLabel)) {
                button.setBackground(MAIN_COLOR);
                button.setForeground(WHITE);
            } else {
                button.setBackground(WHITE);
                button.setForeground(Color.BLACK);
            }
        }
    }

    // ======= CHẠY CHƯƠNG TRÌNH =======
    public static void main(String[] args) {
        // new MainApplication("Nguyen Van A", "Manager").setVisible(true);
    }
}
