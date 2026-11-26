package laundrykita.gui.customer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;
import laundrykita.gui.NavSidebar;
import laundrykita.gui.UiTheme;
import laundrykita.model.Role;
import laundrykita.model.UserSession;
import laundrykita.service.CustomerService;
import laundrykita.service.OrderService;

public class CustomerPanel
extends JPanel {
    private static final String HOME = "home";
    private static final String ORDERS = "orders";
    private static final String PROFILE = "profile";
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(this.cards);
    private final CustomerDashboardPanel dashboardPanel;
    private final CustomerOrdersPanel ordersPanel;
    private final CustomerProfilePanel profilePanel;
    private final NavSidebar sidebar;

    public CustomerPanel(UserSession session, Runnable onLogout) {
        this(session, onLogout, new CustomerService(), new OrderService());
    }

    public CustomerPanel(UserSession session, Runnable onLogout, CustomerService customerService, OrderService orderService) {
        if (session == null || session.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Panel pelanggan hanya dapat dibuka dengan sesi pelanggan.");
        }
        if (onLogout == null) {
            throw new IllegalArgumentException("Aksi keluar (onLogout) wajib diisi.");
        }
        this.sidebar = new NavSidebar("Pelanggan", session.getName(), onLogout);
        this.dashboardPanel = new CustomerDashboardPanel(session, customerService, orderService, () -> this.show(ORDERS));
        this.ordersPanel = new CustomerOrdersPanel(session, orderService);
        this.profilePanel = new CustomerProfilePanel(session, customerService, newName -> {
            this.sidebar.setUserName(newName);
            this.dashboardPanel.refresh();
        });
        this.content.setBackground(UiTheme.BG);
        this.content.add(this.dashboardPanel, HOME);
        this.content.add(this.ordersPanel, ORDERS);
        this.content.add(this.profilePanel, PROFILE);

        this.sidebar.addItem(HOME, "Beranda", "home", () -> this.show(HOME));
        this.sidebar.addItem(ORDERS, "Pesanan Saya", "orders", () -> this.show(ORDERS));
        this.sidebar.addItem(PROFILE, "Profil", "user", () -> this.show(PROFILE));

        this.setLayout(new BorderLayout());
        this.setBackground(UiTheme.BG);
        this.add(this.sidebar, BorderLayout.WEST);
        this.add(this.content, BorderLayout.CENTER);
        this.show(HOME);
    }

    private void show(String card) {
        switch (card) {
            case HOME -> this.dashboardPanel.refresh();
            case ORDERS -> this.ordersPanel.refresh();
            case PROFILE -> this.profilePanel.refresh();
            default -> throw new IllegalArgumentException(card);
        }
        this.sidebar.setActive(card);
        this.cards.show(this.content, card);
    }
}
