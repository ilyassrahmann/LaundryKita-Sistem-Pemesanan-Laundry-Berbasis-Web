package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;
import laundrykita.gui.NavSidebar;
import laundrykita.gui.UiTheme;
import laundrykita.model.UserSession;
import laundrykita.service.CatalogService;
import laundrykita.service.CustomerService;
import laundrykita.service.OrderService;

public class AdminPanel
extends JPanel {
    private static final String DASHBOARD = "dashboard";
    private static final String ORDERS = "orders";
    private static final String NEW_ORDER = "newOrder";
    private static final String CUSTOMERS = "customers";
    private static final String SERVICES = "services";
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(this.cards);
    private final AdminDashboardPanel dashboardPanel;
    private final AdminOrdersPanel ordersPanel;
    private final NewOrderPanel newOrderPanel;
    private final AdminCustomersPanel customersPanel;
    private final AdminServicesPanel servicesPanel;
    private final NavSidebar sidebar;

    public AdminPanel(UserSession session, Runnable onLogout) {
        this(session, onLogout, new OrderService(), new CustomerService(), new CatalogService());
    }

    AdminPanel(UserSession session, Runnable onLogout, OrderService orderService, CustomerService customerService, CatalogService catalogService) {
        if (session == null || !session.isAdmin()) {
            throw new IllegalArgumentException("AdminPanel hanya untuk sesi ADMIN.");
        }
        this.dashboardPanel = new AdminDashboardPanel(session, orderService, this::openOrders);
        this.ordersPanel = new AdminOrdersPanel(orderService);
        this.newOrderPanel = new NewOrderPanel(session, orderService, customerService, catalogService);
        this.customersPanel = new AdminCustomersPanel(customerService);
        this.servicesPanel = new AdminServicesPanel(catalogService);
        this.content.setBackground(UiTheme.BG);
        this.content.add(this.dashboardPanel, DASHBOARD);
        this.content.add(this.ordersPanel, ORDERS);
        this.content.add(this.newOrderPanel, NEW_ORDER);
        this.content.add(this.customersPanel, CUSTOMERS);
        this.content.add(this.servicesPanel, SERVICES);

        this.sidebar = new NavSidebar("Admin", session.getName(), onLogout);
        this.sidebar.addItem(DASHBOARD, "Ringkasan", "dashboard", () -> this.show(DASHBOARD));
        this.sidebar.addItem(ORDERS, "Pesanan", "orders", () -> this.show(ORDERS));
        this.sidebar.addItem(NEW_ORDER, "Pesanan Baru", "plus", () -> this.show(NEW_ORDER));
        this.sidebar.addItem(CUSTOMERS, "Pelanggan", "customers", () -> this.show(CUSTOMERS));
        this.sidebar.addItem(SERVICES, "Layanan", "services", () -> this.show(SERVICES));

        this.setLayout(new BorderLayout());
        this.setBackground(UiTheme.BG);
        this.add(this.sidebar, BorderLayout.WEST);
        this.add(this.content, BorderLayout.CENTER);
        this.show(DASHBOARD);
    }

    private void openOrders() {
        this.show(ORDERS);
    }

    private void show(String cardName) {
        switch (cardName) {
            case DASHBOARD -> this.dashboardPanel.refresh();
            case ORDERS -> this.ordersPanel.refresh();
            case NEW_ORDER -> this.newOrderPanel.refresh();
            case CUSTOMERS -> this.customersPanel.refresh();
            case SERVICES -> this.servicesPanel.refresh();
            default -> throw new IllegalArgumentException(cardName);
        }
        this.sidebar.setActive(cardName);
        this.cards.show(this.content, cardName);
    }
}
