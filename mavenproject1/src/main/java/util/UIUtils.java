package util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    /**
     * Apply consistent styling to action buttons: preferred size and rounded corners preserved by RoundedButton.
     */
    public static void styleActionButton(AbstractButton b, int width) {
        if (b == null) return;
        b.setPreferredSize(new Dimension(width, 36));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // If it's a RoundedButton we already set margins; ensure opaque background for painting
        b.setOpaque(false);
    }
}
