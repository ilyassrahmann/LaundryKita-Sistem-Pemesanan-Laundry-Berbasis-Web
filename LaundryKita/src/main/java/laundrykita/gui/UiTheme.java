package laundrykita.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

public final class UiTheme {
    public static final Color BG = new Color(0xFAF6F1);
    public static final Color SURFACE = new Color(0xFFFFFF);
    public static final Color SURFACE_ALT = new Color(0xF7F1EA);
    public static final Color HOVER = new Color(0xF3EAE1);
    public static final Color BORDER = new Color(0xE8DDD1);
    public static final Color BORDER_SOFT = new Color(0xF0E7DD);
    public static final Color TEXT = new Color(0x2B2520);
    public static final Color MUTED = new Color(0x6E645B);
    public static final Color ACCENT = new Color(0xB4552F);
    public static final Color ACCENT_DARK = new Color(0x8A3E24);
    public static final Color ACCENT_SOFT = new Color(0xF7E7DE);
    public static final Color SUCCESS = new Color(0x2F6B4F);
    public static final Color SUCCESS_SOFT = new Color(0xE5F1EA);
    public static final Color WARNING = new Color(0x8A5A15);
    public static final Color WARNING_SOFT = new Color(0xF8EDD8);
    public static final Color DANGER = new Color(0xA33A32);
    public static final Color DANGER_SOFT = new Color(0xF8E5E2);
    public static final Color INFO = new Color(0x3B5B7A);
    public static final Color INFO_SOFT = new Color(0xE7EFF5);
    public static final Color ON_ACCENT = new Color(0xFFF7F2);
    public static final Color ON_ACCENT_MUTED = new Color(0xF2D4C6);

    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TINY_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font H3 = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font METRIC = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font METRIC_SMALL = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HERO = new Font("Segoe UI", Font.BOLD, 44);
    public static final Font BRAND = new Font("Segoe UI", Font.BOLD, 19);

    private UiTheme() {
    }

    public static void install() {
        FlatLightLaf.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        UIManager.put("defaultFont", BODY);
        UIManager.put("Panel.background", BG);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Component.accentColor", ACCENT);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("Component.borderColor", BORDER);
        UIManager.put("Component.disabledBorderColor", BORDER_SOFT);
        UIManager.put("Component.background", SURFACE);
        UIManager.put("Component.foreground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.disabledForeground", MUTED);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.placeholderForeground", MUTED);
        UIManager.put("TextField.selectionBackground", ACCENT_SOFT);
        UIManager.put("TextField.selectionForeground", TEXT);
        UIManager.put("TextField.focusedBorderColor", ACCENT);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.selectionBackground", ACCENT_SOFT);
        UIManager.put("PasswordField.selectionForeground", TEXT);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", ACCENT_SOFT);
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("ComboBox.buttonBackground", SURFACE);
        UIManager.put("ComboBox.buttonArrowColor", MUTED);
        UIManager.put("ComboBox.focusedBorderColor", ACCENT);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", ACCENT_SOFT);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor", BORDER_SOFT);
        UIManager.put("TableHeader.background", SURFACE);
        UIManager.put("TableHeader.foreground", MUTED);
        UIManager.put("TableHeader.font", SMALL_BOLD);
        UIManager.put("TableHeader.separatorColor", BORDER);
        UIManager.put("TableHeader.bottomSeparatorColor", BORDER);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", SURFACE);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollPane.smoothScrolling", Boolean.TRUE);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
        UIManager.put("ToolTip.background", TEXT);
        UIManager.put("ToolTip.foreground", SURFACE);
        UIManager.put("TitlePane.background", SURFACE);
        UIManager.put("TitlePane.inactiveBackground", BG);
        UIManager.put("TitlePane.foreground", TEXT);
        UIManager.put("TitlePane.inactiveForeground", MUTED);
        UIManager.put("TitlePane.buttonHoverBackground", HOVER);
        UIManager.put("TitlePane.buttonPressedBackground", ACCENT_SOFT);
    }
}
