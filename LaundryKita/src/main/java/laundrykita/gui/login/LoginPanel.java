package laundrykita.gui.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import laundrykita.dao.DataAccessException;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.UserSession;
import laundrykita.service.AuthService;
import laundrykita.service.AuthenticationException;

public class LoginPanel
extends JPanel {
    static final String MSG_EMPTY = "Email dan password wajib diisi.";
    static final String MSG_DATABASE = "Tidak dapat terhubung ke database. Pastikan MySQL (XAMPP) sudah berjalan, lalu coba lagi.";
    static final String MSG_UNEXPECTED = "Terjadi kesalahan saat login. Silakan coba lagi.";
    private final AuthService authService;
    private final Consumer<UserSession> onLoginSuccess;
    private final JTextField emailField = UiComponents.textField(24, "nama@email.com");
    private final JPasswordField passwordField = UiComponents.passwordField(24, "Masukkan password");
    private final JLabel messageLabel = new JLabel(" ");
    private final JButton loginButton = UiComponents.primary("Masuk");
    private final JCheckBox showPassword = new JCheckBox("Tampilkan password");
    private final char defaultEchoChar;
    private boolean programmaticClear;

    public LoginPanel(Consumer<UserSession> onLoginSuccess) {
        this(new AuthService(), onLoginSuccess);
    }

    public LoginPanel(AuthService authService, Consumer<UserSession> onLoginSuccess) {
        if (authService == null || onLoginSuccess == null) {
            throw new IllegalArgumentException("AuthService dan aksi login sukses wajib diisi.");
        }
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
        this.defaultEchoChar = this.passwordField.getEchoChar();
        this.buildLayout();
        this.loginButton.addActionListener(e -> this.attemptLogin());
        this.emailField.addActionListener(e -> this.passwordField.requestFocusInWindow());
        this.passwordField.addActionListener(e -> this.attemptLogin());
        this.showPassword.addActionListener(e -> {
            this.passwordField.setEchoChar(this.showPassword.isSelected() ? (char) 0 : this.defaultEchoChar);
            this.passwordField.requestFocusInWindow();
        });
        DocumentListener clearError = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                LoginPanel.this.clearError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                LoginPanel.this.clearError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                LoginPanel.this.clearError();
            }
        };
        this.emailField.getDocument().addDocumentListener(clearError);
        this.passwordField.getDocument().addDocumentListener(clearError);
    }

    public void focusEmail() {
        this.emailField.requestFocusInWindow();
    }

    private void attemptLogin() {
        String email = this.emailField.getText();
        char[] password = this.passwordField.getPassword();
        UserSession session = null;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (email.trim().isEmpty() || password.length == 0) {
                this.showError(MSG_EMPTY, true);
            } else {
                session = this.authService.login(email, password);
            }
        } catch (AuthenticationException e) {
            this.showError(e.getMessage(), true);
        } catch (DataAccessException e) {
            this.showError(MSG_DATABASE, false);
        } catch (RuntimeException e) {
            this.showError(MSG_UNEXPECTED, false);
        } finally {
            Arrays.fill(password, '\u0000');
            this.programmaticClear = true;
            this.passwordField.setText("");
            this.programmaticClear = false;
            this.setCursor(Cursor.getDefaultCursor());
        }
        if (session != null) {
            this.clearError();
            this.onLoginSuccess.accept(session);
        }
    }

    private void showError(String message, boolean fieldError) {
        this.messageLabel.setText("<html><table width='330' cellpadding='0' cellspacing='0'><tr><td>"
                + message + "</td></tr></table></html>");
        UiComponents.setFieldError(this.emailField, fieldError);
        UiComponents.setFieldError(this.passwordField, fieldError);
        this.passwordField.requestFocusInWindow();
    }

    private void clearError() {
        if (this.programmaticClear) {
            return;
        }
        if (!" ".equals(this.messageLabel.getText())) {
            this.messageLabel.setText(" ");
        }
        UiComponents.setFieldError(this.emailField, false);
        UiComponents.setFieldError(this.passwordField, false);
    }

    private void buildLayout() {
        this.setLayout(new BorderLayout());
        this.setBackground(UiTheme.BG);
        this.add(this.buildBrandPanel(), BorderLayout.WEST);
        this.add(this.buildFormPanel(), BorderLayout.CENTER);
    }

    private JPanel buildBrandPanel() {
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UiTheme.ACCENT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillOval(getWidth() - 150, -110, 340, 340);
                g2.fillOval(-130, getHeight() - 200, 310, 310);
                g2.setColor(new Color(255, 255, 255, 26));
                g2.drawOval(getWidth() - 220, getHeight() - 130, 260, 260);
                g2.dispose();
            }
        };
        brand.setOpaque(true);
        brand.setPreferredSize(new Dimension(440, 0));
        brand.setBorder(new EmptyBorder(48, 52, 40, 48));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(336, 470));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mark = new JLabel(new ImageIcon(UiIcons.appIcon(32)));
        JLabel brandName = UiComponents.label("LaundryKita", 20, Font.BOLD, Color.WHITE);
        brandRow.add(mark);
        brandRow.add(brandName);

        JLabel heading = this.brandText("<html>Cucian rapi,<br>tanpa repot.</html>", 34, Font.BOLD, Color.WHITE);
        JLabel tagline = this.brandTagline("<html>Kelola pesanan, pelanggan, dan<br>layanan dalam satu tempat.</html>");

        content.add(brandRow);
        content.add(Box.createVerticalStrut(52));
        content.add(heading);
        content.add(Box.createVerticalStrut(16));
        content.add(tagline);
        content.add(Box.createVerticalStrut(30));
        content.add(this.feature("Status pesanan selalu terlihat"));
        content.add(Box.createVerticalStrut(10));
        content.add(this.feature("Total dihitung otomatis"));
        content.add(Box.createVerticalStrut(10));
        content.add(this.feature("Data pelanggan tersimpan rapi"));
        content.add(Box.createVerticalGlue());
        content.add(this.brandText("Area admin dan pelanggan LaundryKita.", 12, Font.PLAIN, UiTheme.ON_ACCENT));
        brand.add(content);
        return brand;
    }

    private JLabel brandText(String text, int size, int style, Color color) {
        JLabel label = UiComponents.label(text, size, style, color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension pref = label.getPreferredSize();
        Dimension fixed = new Dimension(336, pref.height);
        label.setPreferredSize(fixed);
        label.setMinimumSize(fixed);
        label.setMaximumSize(fixed);
        return label;
    }

    private JLabel brandTagline(String text) {
        JLabel tagline = UiComponents.label(text, 14, Font.PLAIN, UiTheme.ON_ACCENT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension fixed = new Dimension(336, 44);
        tagline.setPreferredSize(fixed);
        tagline.setMinimumSize(fixed);
        tagline.setMaximumSize(fixed);
        return tagline;
    }

    private JPanel feature(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(new JLabel(UiIcons.icon("check-circle", UiTheme.ON_ACCENT, 16)));
        row.add(UiComponents.label(text, 13, Font.PLAIN, UiTheme.ON_ACCENT));
        return row;
    }

    private JPanel buildFormPanel() {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(UiTheme.BG);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = UiComponents.label("Masuk", 24, Font.BOLD, UiTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = UiComponents.label("Gunakan email dan password akun Anda.", 13, Font.PLAIN, UiTheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = UiComponents.label("Email", 12, Font.BOLD, UiTheme.MUTED);
        emailLabel.setLabelFor(this.emailField);
        JLabel passwordLabel = UiComponents.label("Password", 12, Font.BOLD, UiTheme.MUTED);
        passwordLabel.setLabelFor(this.passwordField);

        this.emailField.setPreferredSize(new Dimension(340, 42));
        this.emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        this.passwordField.setPreferredSize(new Dimension(340, 42));
        this.passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        this.loginButton.setPreferredSize(new Dimension(340, 42));
        this.loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        UiComponents.styleCheckBox(this.showPassword);

        this.messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.messageLabel.setForeground(UiTheme.DANGER);
        this.messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension messageSize = new Dimension(340, 44);
        this.messageLabel.setPreferredSize(messageSize);
        this.messageLabel.setMinimumSize(messageSize);
        this.messageLabel.setMaximumSize(messageSize);
        this.messageLabel.setVerticalAlignment(JLabel.TOP);

        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(26));
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(this.emailField);
        form.add(Box.createVerticalStrut(16));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(this.passwordField);
        form.add(Box.createVerticalStrut(10));
        form.add(this.showPassword);
        form.add(Box.createVerticalStrut(4));
        form.add(this.messageLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(this.loginButton);

        UiComponents.RoundedPanel card = new UiComponents.RoundedPanel(new BorderLayout(), 16,
                UiTheme.SURFACE, UiTheme.BORDER);
        card.setBorder(new EmptyBorder(32, 30, 30, 30));
        card.add(form, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(400, 460));

        JLabel note = UiComponents.label("Akun pelanggan dibuat oleh admin LaundryKita.", 12,
                Font.PLAIN, UiTheme.MUTED);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        stack.add(card);
        stack.add(Box.createVerticalStrut(16));
        stack.add(note);

        side.add(stack);
        return side;
    }
}
