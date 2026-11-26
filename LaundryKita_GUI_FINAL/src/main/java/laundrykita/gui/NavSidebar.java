package laundrykita.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public final class NavSidebar extends JPanel {
    private final Map<String, NavButton> items = new LinkedHashMap<>();
    private final JLabel userNameLabel = new JLabel();
    private final JPanel menu = new JPanel();

    public NavSidebar(String role, String userName, Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(UiTheme.SURFACE);
        setPreferredSize(new Dimension(236, 0));
        setBorder(new MatteBorder(0, 0, 0, 1, UiTheme.BORDER));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(24, 20, 0, 20));

        JLabel mark = new JLabel(new ImageIcon(UiIcons.appIcon(34)));
        mark.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel brand = UiComponents.label("LaundryKita", 18, Font.BOLD, UiTheme.TEXT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roleLabel = UiComponents.label(role, 12, Font.PLAIN, UiTheme.MUTED);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandText.add(brand);
        brandText.add(Box.createVerticalStrut(2));
        brandText.add(roleLabel);

        JPanel brandRow = new JPanel(new BorderLayout(11, 0));
        brandRow.setOpaque(false);
        brandRow.add(mark, BorderLayout.WEST);
        brandRow.add(brandText, BorderLayout.CENTER);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuLabel = UiComponents.label("Menu", 12, Font.BOLD, UiTheme.MUTED);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(brandRow);
        top.add(Box.createVerticalStrut(30));
        top.add(menuLabel);
        top.add(Box.createVerticalStrut(8));
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(menu);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(0, 14, 16, 14));

        JPanel userRow = new JPanel(new BorderLayout(10, 0));
        userRow.setOpaque(false);
        userRow.add(UiComponents.avatar(userName, 34), BorderLayout.WEST);
        JPanel who = new JPanel();
        who.setOpaque(false);
        who.setLayout(new BoxLayout(who, BoxLayout.Y_AXIS));
        userNameLabel.setText(userName);
        userNameLabel.setFont(UiTheme.BODY_BOLD);
        userNameLabel.setForeground(UiTheme.TEXT);
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roleSmall = UiComponents.label(role, 11, Font.PLAIN, UiTheme.MUTED);
        roleSmall.setAlignmentX(Component.LEFT_ALIGNMENT);
        who.add(userNameLabel);
        who.add(Box.createVerticalStrut(2));
        who.add(roleSmall);
        userRow.add(who, BorderLayout.CENTER);

        UiComponents.RoundedPanel userCard = new UiComponents.RoundedPanel(new BorderLayout(), 12,
                UiTheme.SURFACE_ALT, UiTheme.BORDER_SOFT);
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));
        userCard.add(userRow, BorderLayout.CENTER);
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JButton logout = UiComponents.ghost("Keluar", UiIcons.icon("logout", UiTheme.MUTED, 16));
        logout.setHorizontalAlignment(JButton.LEFT);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logout.setBorder(new EmptyBorder(9, 12, 9, 12));
        logout.addActionListener(e -> onLogout.run());

        bottom.add(userCard);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(logout);
        add(bottom, BorderLayout.SOUTH);
    }

    public void addItem(String key, String label, String iconName, Runnable action) {
        NavButton button = new NavButton(label, iconName);
        button.addActionListener(e -> {
            setActive(key);
            action.run();
        });
        items.put(key, button);
        menu.add(button);
        menu.add(Box.createVerticalStrut(4));
        menu.revalidate();
    }

    public void setActive(String key) {
        for (Map.Entry<String, NavButton> entry : items.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(key));
        }
    }

    public void setUserName(String name) {
        userNameLabel.setText(name);
    }

    private static final class NavButton extends JButton {
        private final String iconName;
        private boolean active;

        NavButton(String label, String iconName) {
            super(label);
            this.iconName = iconName;
            setHorizontalAlignment(JButton.LEFT);
            setIconTextGap(12);
            setFont(UiTheme.BODY_BOLD);
            setForeground(UiTheme.TEXT);
            setIcon(UiIcons.icon(iconName, UiTheme.MUTED, 18));
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(0, 42));
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? UiTheme.ACCENT_DARK : UiTheme.TEXT);
            setIcon(UiIcons.icon(iconName, active ? UiTheme.ACCENT : UiTheme.MUTED, 18));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(UiTheme.ACCENT_SOFT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            } else if (getModel().isRollover()) {
                g2.setColor(UiTheme.HOVER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            }
            if (isFocusOwner()) {
                g2.setColor(UiTheme.ACCENT);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, getWidth() - 3f, getHeight() - 3f, 9, 9));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
