package view;

import javax.swing.*;
import java.awt.*;

/**
 * Simple rounded button with customizable corner radius.
 * Uses the button's background/foreground colors and preserves existing behavior.
 */
public class RoundedButton extends JButton {

    private int radius = 12;

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton() {
        super();
        init();
    }

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setMargin(new Insets(8, 16, 8, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill rounded background
            if (getBackground() != null) {
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            }

            // Clip to rounded rect so text/icon are rendered inside the rounded shape
            java.awt.geom.RoundRectangle2D clip = new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setClip(clip);

            // Paint the button (text, icon) using the clipped graphics
            super.paintComponent(g2);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public void setContentAreaFilled(boolean b) {
        // ignore to keep rounded painting consistent
    }
}
