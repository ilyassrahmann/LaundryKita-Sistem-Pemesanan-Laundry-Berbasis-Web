package laundrykita.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import laundrykita.dao.DataAccessException;
import laundrykita.model.OrderStatus;
import laundrykita.model.PaymentMethod;
import laundrykita.model.PaymentStatus;
import laundrykita.service.ServiceException;

public final class UiComponents {
    private static final String HOVER_ROW = "laundrykita.hoverRow";
    private static final String FONT_FAMILY = "Segoe UI";

    private UiComponents() {
    }

    public enum ButtonStyle {
        PRIMARY, SECONDARY, DANGER, GHOST, LIGHT
    }

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, style, size));
        label.setForeground(color);
        return label;
    }

    public static JLabel muted(String text) {
        return label(text, 12, Font.PLAIN, UiTheme.MUTED);
    }

    public static JLabel hint(String text) {
        return label(text, 12, Font.PLAIN, UiTheme.MUTED);
    }

    public static JLabel sectionTitle(String text) {
        return label(text, 15, Font.BOLD, UiTheme.TEXT);
    }

    public static JPanel header(String title, String subtitle, JComponent... actions) {
        JPanel wrapper = new JPanel(new BorderLayout(16, 0));
        wrapper.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = label(title, 24, Font.BOLD, UiTheme.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(titleLabel);
        if (subtitle != null && !subtitle.isBlank()) {
            text.add(Box.createVerticalStrut(5));
            JLabel subtitleLabel = label(subtitle, 13, Font.PLAIN, UiTheme.MUTED);
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            text.add(subtitleLabel);
        }
        wrapper.add(text, BorderLayout.CENTER);
        if (actions.length > 0) {
            wrapper.add(actions(actions), BorderLayout.EAST);
        }
        wrapper.setBorder(new EmptyBorder(0, 0, 8, 0));
        return wrapper;
    }

    public static JScrollPane pageScroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    public static RoundedPanel card(LayoutManager layout) {
        RoundedPanel panel = new RoundedPanel(layout, 14, UiTheme.SURFACE, UiTheme.BORDER);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    public static RoundedPanel card() {
        return card(new BorderLayout(0, 12));
    }

    public static RoundedPanel surface(LayoutManager layout) {
        return new RoundedPanel(layout, 14, UiTheme.SURFACE, UiTheme.BORDER);
    }

    public static JButton primary(String text) {
        return new AppButton(text, ButtonStyle.PRIMARY, null);
    }

    public static JButton primary(String text, Icon icon) {
        return new AppButton(text, ButtonStyle.PRIMARY, icon);
    }

    public static JButton secondary(String text) {
        return new AppButton(text, ButtonStyle.SECONDARY, null);
    }

    public static JButton secondary(String text, Icon icon) {
        return new AppButton(text, ButtonStyle.SECONDARY, icon);
    }

    public static JButton danger(String text) {
        return new AppButton(text, ButtonStyle.DANGER, null);
    }

    public static JButton ghost(String text) {
        return new AppButton(text, ButtonStyle.GHOST, null);
    }

    public static JButton ghost(String text, Icon icon) {
        return new AppButton(text, ButtonStyle.GHOST, icon);
    }

    public static JButton light(String text) {
        return new AppButton(text, ButtonStyle.LIGHT, null);
    }

    public static JButton light(String text, Icon icon) {
        return new AppButton(text, ButtonStyle.LIGHT, icon);
    }

    public static JTextField textField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        styleField(field, placeholder);
        return field;
    }

    public static JTextField searchField(int columns, String placeholder) {
        JTextField field = textField(columns, placeholder);
        field.putClientProperty("JTextField.leadingComponent",
                new JLabel(UiIcons.icon("search", UiTheme.MUTED, 16)));
        field.putClientProperty("JTextField.showClearButton", Boolean.TRUE);
        field.putClientProperty("JTextField.padding", new Insets(0, 6, 0, 4));
        return field;
    }

    public static JPasswordField passwordField(int columns, String placeholder) {
        JPasswordField field = new JPasswordField(columns);
        styleField(field, placeholder);
        return field;
    }

    private static void styleField(JTextField field, String placeholder) {
        field.setFont(UiTheme.BODY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 42));
        field.setMargin(new Insets(4, 10, 4, 10));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.roundRect", Boolean.TRUE);
    }

    public static void styleCombo(JComboBox<?> box) {
        box.setFont(UiTheme.BODY);
        box.setBackground(UiTheme.SURFACE);
        box.setForeground(UiTheme.TEXT);
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 42));
        box.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleCheckBox(JCheckBox box) {
        box.setOpaque(false);
        box.setFont(UiTheme.SMALL);
        box.setForeground(UiTheme.MUTED);
        box.setFocusPainted(false);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JPanel form() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    public static JPanel fieldGroup(String label, JComponent field) {
        return fieldGroup(label, field, null);
    }

    public static JPanel fieldGroup(String label, JComponent field, String hint) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);
        JLabel caption = label(label, 12, Font.BOLD, UiTheme.MUTED);
        caption.setLabelFor(field);
        if (field.getAccessibleContext() != null && field.getAccessibleContext().getAccessibleName() == null) {
            field.getAccessibleContext().setAccessibleName(label);
        }
        group.add(caption, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        if (hint != null && !hint.isBlank()) {
            JLabel helper = hint(hint);
            helper.setBorder(new EmptyBorder(2, 0, 0, 0));
            group.add(helper, BorderLayout.SOUTH);
        }
        group.setBorder(new EmptyBorder(0, 0, 14, 0));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        return group;
    }

    public static JPanel columns(JComponent... components) {
        JPanel panel = new JPanel(new GridLayout(1, components.length, 14, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }

    public static JPanel actions(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        for (JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }

    public static void setFieldError(JComponent field, boolean error) {
        field.putClientProperty("JComponent.outline", error ? "error" : null);
        field.repaint();
    }

    public static JTable table(JTable table) {
        table.setFont(UiTheme.BODY);
        table.setForeground(UiTheme.TEXT);
        table.setBackground(UiTheme.SURFACE);
        table.setSelectionBackground(UiTheme.ACCENT_SOFT);
        table.setSelectionForeground(UiTheme.TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(42);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setDefaultRenderer(Object.class, new TextRenderer(JLabel.LEFT));
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBackground(UiTheme.SURFACE);
        header.setForeground(UiTheme.MUTED);
        header.setFont(UiTheme.SMALL_BOLD);
        header.setDefaultRenderer(new HeaderRenderer());
        installRowHover(table);
        return table;
    }

    private static void installRowHover(JTable table) {
        table.putClientProperty(HOVER_ROW, -1);
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                Object previous = table.getClientProperty(HOVER_ROW);
                if (previous == null || (int) previous != row) {
                    table.putClientProperty(HOVER_ROW, row);
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.putClientProperty(HOVER_ROW, -1);
                table.repaint();
            }
        });
    }

    private static int hoverRow(JTable table) {
        Object value = table.getClientProperty(HOVER_ROW);
        return value instanceof Integer row ? row : -1;
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(UiTheme.SURFACE);
        scroll.getViewport().setBackground(UiTheme.SURFACE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    public static JPanel tableView(JTable table, String emptyMessage) {
        return tableView(table, "Belum ada data", emptyMessage);
    }

    public static JPanel tableView(JTable table, String emptyTitle, String emptyDescription) {
        JPanel panel = new JPanel(new CardLayout());
        panel.setOpaque(false);
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setBackground(UiTheme.SURFACE);
        empty.add(emptyState(emptyTitle, emptyDescription));
        panel.add(tableScroll(table), "rows");
        panel.add(empty, "empty");
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));
        Runnable update = () -> {
            CardLayout cards = (CardLayout) panel.getLayout();
            cards.show(panel, table.getModel().getRowCount() == 0 ? "empty" : "rows");
        };
        table.getModel().addTableModelListener(e -> update.run());
        update.run();
        return panel;
    }

    public static JComponent emptyState(String title, String description) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(28, 24, 28, 24));

        JComponent icon = iconChip("inbox", UiTheme.MUTED, UiTheme.SURFACE_ALT, 46);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleLabel = label(title, 14, Font.BOLD, UiTheme.TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descriptionLabel = label("<html><div style='width:320px;text-align:center'>"
                + escape(description) + "</div></html>", 12, Font.PLAIN, UiTheme.MUTED);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(icon);
        box.add(Box.createVerticalStrut(14));
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(6));
        box.add(descriptionLabel);
        return box;
    }

    public static JPanel tableCard(JTable table, String emptyMessage) {
        return tableCard(table, emptyMessage, null);
    }

    public static JPanel tableCard(JTable table, String emptyMessage, JComponent footer) {
        return tableCard(table, "Belum ada data", emptyMessage, footer);
    }

    public static JPanel tableCard(JTable table, String emptyTitle, String emptyDescription, JComponent footer) {
        RoundedPanel card = surface(new BorderLayout());
        card.add(tableView(table, emptyTitle, emptyDescription), BorderLayout.CENTER);
        if (footer != null) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(1, 0, 0, 0, UiTheme.BORDER_SOFT),
                    new EmptyBorder(11, 18, 13, 18)));
            wrapper.add(footer, BorderLayout.CENTER);
            card.add(wrapper, BorderLayout.SOUTH);
        }
        return card;
    }

    public static JPanel tableFooter(JComponent left, JComponent... right) {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.add(left, BorderLayout.WEST);
        footer.add(actions(right), BorderLayout.EAST);
        return footer;
    }

    public static void sizeColumns(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setMinWidth(Math.min(56, widths[i]));
        }
    }

    public static void enableSorting(JTable table, int... numericModelColumns) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        for (int column : numericModelColumns) {
            sorter.setComparator(column, NUMBER_COMPARATOR);
        }
        table.setRowSorter(sorter);
    }

    public static TableCellRenderer badgeRenderer() {
        return new BadgeRenderer();
    }

    public static TableCellRenderer rightRenderer() {
        return new TextRenderer(JLabel.RIGHT);
    }

    public static TableCellRenderer centerRenderer() {
        return new TextRenderer(JLabel.CENTER);
    }

    public static BadgeLabel badge(Object value) {
        BadgeLabel badge = new BadgeLabel(value == null ? "-" : value.toString());
        updateBadge(badge, value);
        return badge;
    }

    public static void updateBadge(BadgeLabel badge, Object value) {
        badge.setText(value == null ? "-" : value.toString());
        Color[] colors = badgeColors(value);
        badge.setColors(colors[0], colors[1]);
    }

    public static JLabel statValueLabel() {
        FitLabel label = new FitLabel(26, 16);
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    public static JLabel valueLabel() {
        FitLabel label = new FitLabel(22, 14);
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    public static JPanel statCard(String caption, JLabel value, Color accent) {
        return statCard(caption, value, accent, null);
    }

    public static JPanel statCard(String caption, JLabel value, Color accent, String iconName) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(14, 0), 14, UiTheme.SURFACE, UiTheme.BORDER);
        card.setBorder(new EmptyBorder(15, 16, 15, 16));
        if (iconName != null) {
            card.add(iconChip(iconName, accent, softTint(accent), 38), BorderLayout.WEST);
        }
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel captionLabel = label(caption, 12, Font.BOLD, UiTheme.MUTED);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!(value instanceof FitLabel)) {
            value.setFont(UiTheme.METRIC);
        }
        value.setForeground(UiTheme.TEXT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(captionLabel);
        text.add(Box.createVerticalStrut(3));
        text.add(value);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    public static JComponent iconChip(String iconName, Color foreground, Color background, int size) {
        return new IconChip(iconName, foreground, background, size);
    }

    public static JComponent avatar(String name, int size) {
        return new Avatar(name, size);
    }

    public static JPanel detailPanel() {
        RoundedPanel panel = new RoundedPanel(new GridBagLayout(), 12, UiTheme.SURFACE_ALT, UiTheme.BORDER_SOFT);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        return panel;
    }

    public static void addDetail(JPanel panel, int row, String label, String value) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 10, 24);
        panel.add(label(label, 12, Font.PLAIN, UiTheme.MUTED), c);
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 10, 0);
        JLabel valueLabel = new JLabel("<html><div style='width:320px'>"
                + escape(value == null || value.isBlank() ? "-" : value) + "</div></html>");
        valueLabel.setFont(UiTheme.BODY);
        valueLabel.setForeground(UiTheme.TEXT);
        panel.add(valueLabel, c);
    }

    public static JPanel dialogHeader(String title, String subtitle) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        JLabel titleLabel = label(title, 19, Font.BOLD, UiTheme.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(titleLabel);
        if (subtitle != null && !subtitle.isBlank()) {
            wrapper.add(Box.createVerticalStrut(4));
            JLabel subtitleLabel = label(subtitle, 12, Font.PLAIN, UiTheme.MUTED);
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.add(subtitleLabel);
        }
        return wrapper;
    }

    public static JPanel dialogBody(JComponent content, JComponent actions) {
        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(UiTheme.SURFACE);
        root.setBorder(new EmptyBorder(26, 28, 20, 28));
        root.add(content, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    public static void installEscape(JDialog dialog) {
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    public static void withBusy(Component context, Runnable action) {
        Window window = SwingUtilities.getWindowAncestor(context);
        Component target = window != null ? window : context;
        Cursor previousWindow = target.getCursor();
        Cursor previousContext = context.getCursor();
        target.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        context.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            action.run();
        } finally {
            target.setCursor(previousWindow);
            context.setCursor(previousContext);
        }
    }

    public static void showDetails(Component parent, String title, String subtitle, String[][] rows) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        installEscape(dialog);
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(dialogHeader(title, subtitle), BorderLayout.NORTH);
        JPanel details = detailPanel();
        for (int i = 0; i < rows.length; i++) {
            addDetail(details, i, rows[i][0], rows[i][1]);
        }
        body.add(details, BorderLayout.CENTER);
        JButton close = secondary("Tutup");
        close.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(dialogBody(body, actions(close)));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showError(Component parent, RuntimeException error) {
        String message;
        if (error instanceof ServiceException && error.getMessage() != null && !error.getMessage().isBlank()) {
            message = error.getMessage();
        } else if (error instanceof DataAccessException) {
            message = "Data tidak dapat dimuat. Pastikan MySQL (XAMPP) sudah berjalan, lalu coba lagi.";
        } else {
            message = "Terjadi kesalahan. Silakan coba lagi.";
        }
        JOptionPane.showMessageDialog(parent, message, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static Color softTint(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 28);
    }

    private static Color[] badgeColors(Object value) {
        if (value instanceof OrderStatus status) {
            return switch (status) {
                case MENUNGGU -> new Color[]{UiTheme.WARNING, UiTheme.WARNING_SOFT};
                case SELESAI -> new Color[]{UiTheme.SUCCESS, UiTheme.SUCCESS_SOFT};
                default -> new Color[]{UiTheme.INFO, UiTheme.INFO_SOFT};
            };
        }
        if (value instanceof PaymentStatus status) {
            return status == PaymentStatus.LUNAS
                    ? new Color[]{UiTheme.SUCCESS, UiTheme.SUCCESS_SOFT}
                    : new Color[]{UiTheme.WARNING, UiTheme.WARNING_SOFT};
        }
        if (value instanceof PaymentMethod) {
            return new Color[]{UiTheme.MUTED, UiTheme.SURFACE_ALT};
        }
        return new Color[]{UiTheme.MUTED, UiTheme.SURFACE_ALT};
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static Double parseNumber(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        String text = value.toString().replace("Rp", "").replace("kg", "").trim();
        if (text.isEmpty() || text.equals("-")) {
            return null;
        }
        text = text.replace(".", "").replace(',', '.');
        text = text.replaceAll("[^0-9.\\-]", "");
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static final Comparator<Object> NUMBER_COMPARATOR = (a, b) -> {
        Double left = parseNumber(a);
        Double right = parseNumber(b);
        if (left != null && right != null) {
            return Double.compare(left, right);
        }
        return String.valueOf(a == null ? "" : a).compareToIgnoreCase(String.valueOf(b == null ? "" : b));
    };

    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color stroke;

        public RoundedPanel(LayoutManager layout, int radius, Color fill, Color stroke) {
            super(layout);
            this.radius = radius;
            this.fill = fill;
            this.stroke = stroke;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            if (stroke != null) {
                g2.setColor(stroke);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, radius, radius));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static final class BadgeLabel extends JLabel {
        private Color fill = UiTheme.SURFACE_ALT;

        public BadgeLabel(String text) {
            super(text);
            setOpaque(false);
            setFont(UiTheme.TINY_BOLD);
            setBorder(new EmptyBorder(3, 8, 3, 8));
        }

        public void setColors(Color foreground, Color background) {
            setForeground(foreground);
            this.fill = background;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static final class FitLabel extends JLabel {
        private final int baseSize;
        private final int minSize;

        public FitLabel(int baseSize, int minSize) {
            this.baseSize = baseSize;
            this.minSize = minSize;
            setFont(new Font(FONT_FAMILY, Font.BOLD, baseSize));
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    fit();
                }
            });
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension preferred = getPreferredSize();
            return new Dimension(0, preferred.height);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension preferred = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, preferred.height);
        }

        @Override
        public void setText(String text) {
            super.setText(text);
            fit();
        }

        private void fit() {
            int width = getWidth();
            String text = getText();
            if (width <= 0 || text == null || text.isEmpty()) {
                setFont(new Font(FONT_FAMILY, Font.BOLD, baseSize));
                return;
            }
            int size = baseSize;
            while (size > minSize) {
                Font candidate = new Font(FONT_FAMILY, Font.BOLD, size);
                if (getFontMetrics(candidate).stringWidth(text) <= width) {
                    break;
                }
                size--;
            }
            Font target = new Font(FONT_FAMILY, Font.BOLD, size);
            if (!target.equals(getFont())) {
                setFont(target);
            }
        }
    }

    private static final class AppButton extends JButton {
        private final ButtonStyle style;

        AppButton(String text, ButtonStyle style, Icon icon) {
            super(text);
            this.style = style;
            setIcon(icon);
            setIconTextGap(8);
            setFont(UiTheme.BODY_BOLD);
            applyForeground();
            setBorder(new EmptyBorder(9, 16, 9, 16));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            putClientProperty("JComponent.focusWidth", 0);
            Dimension preferred = super.getPreferredSize();
            setPreferredSize(new Dimension(preferred.width + 8, 40));
        }

        private void applyForeground() {
            if (!isEnabled()) {
                setForeground(UiTheme.MUTED);
                return;
            }
            setForeground(switch (style) {
                case PRIMARY -> UiTheme.ON_ACCENT;
                case SECONDARY -> UiTheme.TEXT;
                case DANGER -> UiTheme.DANGER;
                case GHOST -> UiTheme.MUTED;
                case LIGHT -> UiTheme.ACCENT_DARK;
            });
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            applyForeground();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean hover = getModel().isRollover();
            boolean pressed = getModel().isPressed();
            Color background = switch (style) {
                case PRIMARY -> !isEnabled() ? UiTheme.BORDER_SOFT
                        : pressed ? UiTheme.ACCENT_DARK.darker() : hover ? UiTheme.ACCENT_DARK : UiTheme.ACCENT;
                case SECONDARY -> !isEnabled() ? UiTheme.SURFACE_ALT
                        : pressed ? UiTheme.ACCENT_SOFT : hover ? UiTheme.HOVER : UiTheme.SURFACE;
                case DANGER -> !isEnabled() ? UiTheme.SURFACE_ALT
                        : pressed ? new Color(0xF1D3CF) : hover ? new Color(0xF5DCD8) : UiTheme.DANGER_SOFT;
                case GHOST -> !isEnabled() ? null
                        : pressed ? UiTheme.BORDER_SOFT : hover ? UiTheme.HOVER : null;
                case LIGHT -> !isEnabled() ? new Color(0xF3E3DB)
                        : pressed ? UiTheme.ACCENT_SOFT : hover ? new Color(0xFFF1EA) : UiTheme.ON_ACCENT;
            };
            if (background != null) {
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            if (style == ButtonStyle.SECONDARY && isEnabled()) {
                g2.setColor(UiTheme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            if (isFocusOwner()) {
                g2.setColor(style == ButtonStyle.PRIMARY ? UiTheme.ON_ACCENT : UiTheme.ACCENT);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final int CELL_PAD = 16;

    private static class CellRenderer extends JPanel implements TableCellRenderer {
        protected final JLabel content;

        CellRenderer(JLabel content, int alignment, boolean compact) {
            super(new BorderLayout());
            this.content = content;
            content.setFont(UiTheme.BODY);
            content.setForeground(UiTheme.TEXT);
            content.setHorizontalAlignment(alignment);
            setOpaque(true);
            setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
            add(compact ? centerVertically(content) : content, BorderLayout.CENTER);
        }

        private static JPanel centerVertically(JComponent content) {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1;
            wrap.add(content, gbc);
            return wrap;
        }

        int getAlignment() {
            return this.content.getHorizontalAlignment();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            applyValue(value);
            setBackground(isSelected ? table.getSelectionBackground()
                    : row == hoverRow(table) ? UiTheme.HOVER : table.getBackground());
            return this;
        }

        protected void applyValue(Object value) {
            this.content.setText(value == null ? "" : value.toString());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(UiTheme.BORDER_SOFT);
            g.fillRect(0, getHeight() - 1, getWidth(), 1);
        }
    }

    private static final class TextRenderer extends CellRenderer {
        TextRenderer(int alignment) {
            super(new JLabel(), alignment, false);
        }
    }

    private static final class BadgeRenderer extends CellRenderer {
        BadgeRenderer() {
            super(new BadgeLabel(""), JLabel.LEFT, true);
        }

        @Override
        protected void applyValue(Object value) {
            updateBadge((BadgeLabel) this.content, value);
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        private transient JTable currentTable;
        private transient int currentColumn;

        HeaderRenderer() {
            setOpaque(true);
            setFont(UiTheme.SMALL_BOLD);
            setForeground(UiTheme.MUTED);
            setBackground(UiTheme.SURFACE);
            setBorder(headerBorder());
        }

        private static javax.swing.border.Border headerBorder() {
            return BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, UiTheme.BORDER),
                    new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            this.currentTable = table;
            this.currentColumn = column;
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int alignment = JLabel.LEFT;
            if (tableColumn.getCellRenderer() instanceof CellRenderer renderer) {
                alignment = renderer.getAlignment();
            }
            setHorizontalAlignment(alignment);
            setBackground(UiTheme.SURFACE);
            setForeground(UiTheme.MUTED);
            setFont(UiTheme.SMALL_BOLD);
            setBorder(headerBorder());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentTable == null) {
                return;
            }
            RowSorter<?> sorter = currentTable.getRowSorter();
            if (sorter == null) {
                return;
            }
            int modelColumn = currentTable.convertColumnIndexToModel(currentColumn);
            SortOrder order = null;
            for (RowSorter.SortKey key : sorter.getSortKeys()) {
                if (key.getColumn() == modelColumn) {
                    order = key.getSortOrder();
                    break;
                }
            }
            if (order == null || order == SortOrder.UNSORTED) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiTheme.MUTED);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int x = getWidth() - 8;
            int y = getHeight() / 2;
            if (order == SortOrder.ASCENDING) {
                g2.drawLine(x - 4, y + 2, x, y - 2);
                g2.drawLine(x, y - 2, x + 4, y + 2);
            } else {
                g2.drawLine(x - 4, y - 2, x, y + 2);
                g2.drawLine(x, y + 2, x + 4, y - 2);
            }
            g2.dispose();
        }
    }

    private static final class IconChip extends JComponent {
        private final Icon icon;
        private final Color background;
        private final int size;

        IconChip(String iconName, Color foreground, Color background, int size) {
            this.icon = UiIcons.icon(iconName, foreground, Math.round(size * 0.46f));
            this.background = background;
            this.size = size;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(size, size);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, size, size, Math.round(size * 0.3f), Math.round(size * 0.3f));
            int iconSize = icon.getIconWidth();
            icon.paintIcon(this, g2, (size - iconSize) / 2, (size - iconSize) / 2);
            g2.dispose();
        }
    }

    private static final class Avatar extends JComponent {
        private final String initial;
        private final int size;

        Avatar(String name, int size) {
            this.size = size;
            String text = name == null || name.isBlank() ? "?" : name.trim();
            this.initial = text.substring(0, 1).toUpperCase();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(size, size);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiTheme.ACCENT_SOFT);
            g2.fillOval(0, 0, size, size);
            g2.setColor(UiTheme.ACCENT_DARK);
            g2.setFont(new Font(FONT_FAMILY, Font.BOLD, Math.round(size * 0.42f)));
            FontMetrics fm = g2.getFontMetrics();
            int x = (size - fm.stringWidth(initial)) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initial, x, y);
            g2.dispose();
        }
    }
}
