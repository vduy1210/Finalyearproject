package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// mouse listeners not used now; icon+button wrapper used instead
import java.util.ArrayList;
import java.util.List;


public class MainApplication extends JFrame {

    // ======= MODERN COLOR SCHEME & TYPOGRAPHY =======
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);       // Blue
    private static final Color SIDEBAR_COLOR = new Color(240, 242, 245);      // White with gray tint
    // Full header background color requested by user (#1452f1)
    private static final Color HEADER_BG = new Color(20, 82, 241);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);   // Light Blue Gray
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);          // Dark Gray
    // TEXT_SECONDARY removed (unused)
    private static final Color DANGER_COLOR = new Color(244, 67, 54);         // Red
    private static final Color BORDER_COLOR = new Color(224, 224, 224);       // Light Border
    private static final Color WHITE = Color.WHITE;

    // Modern typography using Segoe UI
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font MENU_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MENU_ITEM_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // ======= THÀNH PHẦN GIAO DIỆN =======
    private CardLayout layoutSwitcher;
    private JPanel contentPanel;
    private List<JButton> menuButtons;
    private OrderPanel orderPanel;
    private ProductManagerPanel productManagerPanel;
    private UserManagementPanel userManagementPanel;
    private RevenueReportPanel revenueReportPanel;

    private String staffName;
    private String staffRole;

    public MainApplication(String staffName, String staffRole) {
        this.staffName = staffName;
        this.staffRole = staffRole;
        
        // Modern window configuration
        setTitle("Coffee Shop Management System");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Initialize menu buttons list
        menuButtons = new ArrayList<>();

        // ======= CREATE MAIN CONTENT AREA =======
        layoutSwitcher = new CardLayout();
        contentPanel = new JPanel(layoutSwitcher);
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Thêm các màn hình nội dung
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        productManagerPanel = new ProductManagerPanel();
        contentPanel.add(productManagerPanel, "PRODUCT_MANAGER");
        orderPanel = new OrderPanel();
        orderPanel.setOrderListener(() -> {
            if (productManagerPanel != null) {
                productManagerPanel.loadProductData();
            }
        });
        contentPanel.add(orderPanel, "ORDER");
        contentPanel.add(new OrderConfirmationPanel(), "HISTORY");
    contentPanel.add(new RevenueTodayPanel(), "REVENUE");
    // Customer management panel
    contentPanel.add(new CustomerManagementPanel(), "CUSTOMERS");

        // Admin-only panels (added but only shown via menu for ADMIN)
        userManagementPanel = new UserManagementPanel();
        revenueReportPanel = new RevenueReportPanel();
        contentPanel.add(userManagementPanel, "USER_MANAGEMENT");
        contentPanel.add(revenueReportPanel, "REVENUE_REPORT");

        // ======= THÊM CÁC PHẦN VÀO GIAO DIỆN CHÍNH =======
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebarMenu(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị màn hình mặc định
    layoutSwitcher.show(contentPanel, "DASHBOARD");
    highlightMenuButton("Dashboard");
        

    }

    public void showOrderWithOrderId(int orderId) {
        if (orderPanel != null) {
            orderPanel.loadOrderIntoCart(orderId);
        }
        layoutSwitcher.show(contentPanel, "ORDER");
        highlightMenuButton("Order");
    }

    // ======= CREATE MODERN HEADER =======
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(15, 25, 15, 25)
        ));

        // Left side - App title with icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(HEADER_BG);
        leftPanel.setOpaque(true);

        JLabel title = new JLabel("Coffee Shop Management");
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));

        leftPanel.add(title);

        // Right side - Staff info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(HEADER_BG);
        rightPanel.setOpaque(true);

        // Staff info card
        JPanel staffCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        staffCard.setBackground(WHITE);
        staffCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));


        JLabel staffInfo = new JLabel(staffName + " (" + staffRole + ")");
        staffInfo.setFont(MENU_ITEM_FONT);
        staffInfo.setForeground(TEXT_PRIMARY);


        staffCard.add(staffInfo);

        // Modern logout button (icon + text)
        JButton logoutButton = new RoundedButton("Logout");
    // icon removed per user preference
        util.UIUtils.styleActionButton(logoutButton, 140);
        logoutButton.setFont(MENU_ITEM_FONT);
        logoutButton.setBackground(DANGER_COLOR);
        logoutButton.setForeground(WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginForm().setVisible(true);
                    }
                });
                MainApplication.this.dispose();
            }
        });

        rightPanel.add(staffCard);
        rightPanel.add(logoutButton);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ======= CREATE MODERN SIDEBAR MENU =======
    private JPanel createSidebarMenu() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
            new EmptyBorder(24, 20, 24, 20)
        ));

        // Menu header with icon
        JPanel menuHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        menuHeader.setBackground(SIDEBAR_COLOR);
    menuHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        

            // Add menu buttons
            addMenuButton("Dashboard", "DASHBOARD", sidebar);
            addMenuButton("Product Management", "PRODUCT_MANAGER", sidebar);
            addMenuButton("Order", "ORDER", sidebar);
            addMenuButton("Order Confirmation", "HISTORY", sidebar);
            addMenuButton("Revenue Today", "REVENUE", sidebar);
            addMenuButton("Customers", "CUSTOMERS", sidebar);
            

        // Add flexible space between main and admin sections to push admin to bottom
        sidebar.add(Box.createVerticalGlue());

        // Admin-only menu items
        if (staffRole != null && staffRole.equalsIgnoreCase("ADMIN")) {
            sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
            
            // Admin section header
            JPanel adminHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            adminHeader.setBackground(SIDEBAR_COLOR);
            adminHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            

            
            JLabel adminTitle = new JLabel("ADMIN");
            adminTitle.setFont(MENU_HEADER_FONT);
            adminTitle.setForeground(PRIMARY_COLOR);
            adminTitle.setBorder(new EmptyBorder(0, 16, 0, 0));
            
            adminHeader.add(adminTitle);
            sidebar.add(adminHeader);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            
            addMenuButton("User Management", "USER_MANAGEMENT", sidebar);
            addMenuButton("Revenue Report", "REVENUE_REPORT", sidebar);
        }

        return sidebar;
    }

    // ======= CREATE AND ADD MODERN MENU BUTTON =======
    private void addMenuButton(String label, String panelName, JPanel sidebar) {
    JButton menuButton = new RoundedButton(label);
        // menu icons removed per user preference
    // menu icons removed per user preference
        menuButton.setFont(MENU_ITEM_FONT);
        menuButton.setBackground(WHITE);
        menuButton.setForeground(TEXT_PRIMARY);
        menuButton.setFocusPainted(false);
        menuButton.setHorizontalAlignment(SwingConstants.LEFT);
        menuButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        menuButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutSwitcher.show(contentPanel, panelName);
                highlightMenuButton(label);
            }
        });

        menuButtons.add(menuButton);
        sidebar.add(menuButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // ======= HIGHLIGHT ACTIVE MENU BUTTON =======
    private void highlightMenuButton(String activeLabel) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(activeLabel)) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, PRIMARY_COLOR.darker()),
                    new EmptyBorder(12, 12, 12, 12)
                ));
            } else {
                button.setBackground(WHITE);
                button.setForeground(TEXT_PRIMARY);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    new EmptyBorder(12, 16, 12, 16)
                ));
            }
        }
    }

    // ======= CHẠY CHƯƠNG TRÌNH =======
    public static void main(String[] args) {
        // new MainApplication("Nguyen Van A", "Manager").setVisible(true);
    }
    

}
