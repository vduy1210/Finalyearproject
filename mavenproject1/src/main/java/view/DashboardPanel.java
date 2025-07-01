package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static final Color COLOR_BACKGROUND = new Color(44, 62, 80);
    private static final Color COLOR_SECONDARY_DARK = new Color(41, 128, 185);
    private static final Color COLOR_TEXT_LIGHT = new Color(236, 240, 241);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_LOGOUT = new Color(231, 76, 60);
    private static final Font FONT_WIDGET_TITLE = new Font("Helvetica", Font.BOLD, 14);

    public DashboardPanel() {
        // Thiết lập layout cho chính panel này
        this.setBackground(COLOR_BACKGROUND);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Top Stats Cards ---
        gbc.gridy = 0;
        gbc.weightx = 0.20;
        gbc.weighty = 0.15;
        gbc.gridx = 0;
        this.add(createWidgetCard("Total Sales today", COLOR_SECONDARY_DARK, COLOR_TEXT_LIGHT), gbc);
        gbc.gridx = 1;
        this.add(createWidgetCard("Total Orders today", COLOR_WHITE, Color.BLACK), gbc);
        gbc.gridx = 2;
        this.add(createWidgetCard("Total Customer today", COLOR_WHITE, Color.BLACK), gbc);
        gbc.gridx = 3;
        this.add(createWidgetCard("Total Sold products today", COLOR_WHITE, Color.BLACK), gbc);

        // --- Right Panel (Best Selling & Logout) ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false); // Make transparent
        rightPanel.add(createWidgetCard("List of best-selling products", COLOR_WHITE, Color.BLACK), BorderLayout.CENTER);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.20;
        gbc.weighty = 1.0;
        gbc.gridheight = 3;
        this.add(rightPanel, gbc);

        // --- Joint Chart ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 0.425;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        this.add(createWidgetCard("Joint chart this week", COLOR_WHITE, Color.BLACK), gbc);

        // --- Customers Buying ---
        gbc.gridy = 2;
        gbc.weighty = 0.425;
        this.add(createWidgetCard("Number of customers buying this week", COLOR_WHITE, Color.BLACK), gbc);
    }

    private JPanel createWidgetCard(String title, Color bgColor, Color fgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);

        Border padding = new EmptyBorder(15, 15, 15, 15);
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        card.setBorder(BorderFactory.createCompoundBorder(line, padding));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_WIDGET_TITLE);
        titleLabel.setForeground(fgColor);

        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }
}