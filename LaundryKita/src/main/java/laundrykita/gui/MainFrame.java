package laundrykita.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import laundrykita.gui.admin.AdminPanel;
import laundrykita.gui.customer.CustomerPanel;
import laundrykita.gui.login.LoginPanel;
import laundrykita.model.Role;
import laundrykita.model.UserSession;
import laundrykita.service.AuthService;

public class MainFrame
extends JFrame {
    private static final String LOGIN_CARD = "login";
    private static final String AREA_CARD = "area";
    private final AuthService authService;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(this.cards);
    private UserSession currentSession;
    private LoginPanel loginPanel;
    private JPanel areaPanel;

    public MainFrame() {
        this(new AuthService());
    }

    MainFrame(AuthService authService) {
        super("LaundryKita");
        this.authService = authService;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1180, 780);
        this.setMinimumSize(new Dimension(1024, 660));
        this.setLocationRelativeTo(null);
        this.setIconImage(UiIcons.appIcon(64));
        this.root.setBackground(UiTheme.BG);
        this.setContentPane(this.root);
        this.showLogin();
    }

    public UserSession getCurrentSession() {
        return this.currentSession;
    }

    public void showLogin() {
        this.currentSession = null;
        this.removeArea();
        if (this.loginPanel != null) {
            this.root.remove(this.loginPanel);
        }
        this.loginPanel = new LoginPanel(this.authService, this::handleLogin);
        this.root.add(this.loginPanel, LOGIN_CARD);
        this.cards.show(this.root, LOGIN_CARD);
        this.setTitle("LaundryKita - Masuk");
        this.root.revalidate();
        this.root.repaint();
        this.loginPanel.focusEmail();
    }

    public void showAdmin(UserSession session) {
        if (session == null || session.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Area admin hanya untuk sesi ADMIN.");
        }
        this.showArea(session, new AdminPanel(session, this::logout), "LaundryKita - Admin");
    }

    public void showCustomer(UserSession session) {
        if (session == null || session.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Area pelanggan hanya untuk sesi CUSTOMER.");
        }
        this.showArea(session, new CustomerPanel(session, this::logout), "LaundryKita - Pelanggan");
    }

    public void logout() {
        this.showLogin();
    }

    void handleLogin(UserSession session) {
        if (session == null || session.getRole() == null) {
            JOptionPane.showMessageDialog(this, "Sesi login tidak valid. Silakan coba lagi.", "LaundryKita", JOptionPane.WARNING_MESSAGE);
            return;
        }
        switch (session.getRole()) {
            case ADMIN -> this.showAdmin(session);
            case CUSTOMER -> this.showCustomer(session);
        }
    }

    private void showArea(UserSession session, JPanel panel, String title) {
        this.removeArea();
        if (this.loginPanel != null) {
            this.root.remove(this.loginPanel);
            this.loginPanel = null;
        }
        this.areaPanel = panel;
        this.currentSession = session;
        this.root.add(this.areaPanel, AREA_CARD);
        this.cards.show(this.root, AREA_CARD);
        this.setTitle(title);
        this.root.revalidate();
        this.root.repaint();
    }

    private void removeArea() {
        if (this.areaPanel != null) {
            this.root.remove(this.areaPanel);
            this.areaPanel = null;
        }
    }
}
