package laundrykita.gui.customer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Customer;
import laundrykita.model.Order;
import laundrykita.model.OrderStatus;
import laundrykita.model.UserSession;
import laundrykita.service.CustomerService;
import laundrykita.service.OrderService;

class CustomerDashboardPanel
extends JPanel {
    private static final int RECENT_LIMIT = 5;
    private static final String[] COLUMNS = {"Kode", "Tanggal", "Berat", "Total", "Status", "Pembayaran", "Metode"};
    private final UserSession session;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final Runnable openOrders;
    private final JLabel subtitleLabel = UiComponents.label("", 13, Font.PLAIN, UiTheme.MUTED);
    private final JLabel activeCodeLabel = UiComponents.label("-", 24, Font.BOLD, UiTheme.TEXT);
    private final JLabel activeEstimateLabel = UiComponents.label(" ", 12, Font.PLAIN, UiTheme.MUTED);
    private final JLabel activePaymentLabel = UiComponents.label(" ", 12, Font.PLAIN, UiTheme.MUTED);
    private final JLabel activeTotalLabel = UiComponents.valueLabel();
    private final UiComponents.BadgeLabel activeStatusBadge = UiComponents.badge(OrderStatus.MENUNGGU);
    private final JButton activeDetailButton = UiComponents.primary("Lihat detail");
    private final CardLayout activeCards = new CardLayout();
    private final JPanel activeSlot = new JPanel(this.activeCards);
    private final JLabel totalValue = UiComponents.statValueLabel();
    private final JLabel finishedValue = UiComponents.statValueLabel();
    private final JLabel spentValue = UiComponents.valueLabel();
    private final DefaultTableModel recentModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable recentTable = UiComponents.table(new JTable(this.recentModel));
    private int activeOrderId = -1;

    CustomerDashboardPanel(UserSession session, CustomerService customerService, OrderService orderService,
            Runnable openOrders) {
        this.session = session;
        this.customerService = customerService;
        this.orderService = orderService;
        this.openOrders = openOrders;
        this.setLayout(new BorderLayout(0, 18));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 32, 24, 32));

        JButton seeOrders = UiComponents.primary("Lihat semua pesanan", UiIcons.icon("orders", UiTheme.ON_ACCENT, 16));
        seeOrders.addActionListener(e -> openOrders.run());
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = UiComponents.label("Beranda", 24, Font.BOLD, UiTheme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);
        this.subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(this.subtitleLabel);
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));
        header.add(text, BorderLayout.CENTER);
        header.add(UiComponents.actions(seeOrders), BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        this.activeSlot.setOpaque(false);
        this.activeSlot.add(this.buildActiveCard(), "active");
        this.activeSlot.add(this.buildEmptyCard(), "empty");

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        JPanel totalCard = UiComponents.statCard("Total pesanan", this.totalValue, UiTheme.ACCENT, "receipt");
        JPanel finishedCard = UiComponents.statCard("Selesai", this.finishedValue, UiTheme.SUCCESS, "check-circle");
        JPanel spentCard = UiComponents.statCard("Total belanja", this.spentValue, UiTheme.ACCENT_DARK, "wallet");
        for (JPanel card : new JPanel[]{totalCard, finishedCard, spentCard}) {
            card.setAlignmentX(LEFT_ALIGNMENT);
            stats.add(card);
            stats.add(Box.createVerticalStrut(12));
        }

        JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 0));
        topRow.setOpaque(false);
        topRow.add(this.activeSlot);
        topRow.add(stats);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(topRow, BorderLayout.NORTH);

        JPanel recent = UiComponents.card(new BorderLayout(0, 12));
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.add(UiComponents.sectionTitle("Pesanan terbaru"), BorderLayout.WEST);
        JButton all = UiComponents.secondary("Lihat semua");
        all.addActionListener(e -> openOrders.run());
        recentHeader.add(UiComponents.actions(all), BorderLayout.EAST);
        recent.add(recentHeader, BorderLayout.NORTH);

        UiComponents.sizeColumns(this.recentTable, 85, 105, 90, 125, 120, 145, 95);
        this.recentTable.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        this.recentTable.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        this.recentTable.getColumnModel().getColumn(4).setCellRenderer(UiComponents.badgeRenderer());
        this.recentTable.getColumnModel().getColumn(5).setCellRenderer(UiComponents.badgeRenderer());
        UiComponents.enableSorting(this.recentTable, 2, 3);
        recent.add(UiComponents.tableView(this.recentTable, "Belum ada pesanan",
                "Pesanan Anda akan muncul di sini setelah admin mencatatnya."), BorderLayout.CENTER);

        body.add(recent, BorderLayout.CENTER);
        this.add(body, BorderLayout.CENTER);

        this.activeDetailButton.addActionListener(e -> {
            if (this.activeOrderId > 0) {
                OrderDetailDialog.show(this, this.orderService, this.session.getUserId(), this.activeOrderId);
            }
        });
    }

    private JPanel buildActiveCard() {
        UiComponents.RoundedPanel card = UiComponents.surface(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel head = new JPanel(new BorderLayout(12, 0));
        head.setOpaque(false);
        head.add(UiComponents.iconChip("orders", UiTheme.ACCENT, UiTheme.ACCENT_SOFT, 40), BorderLayout.WEST);
        JPanel headText = new JPanel();
        headText.setOpaque(false);
        headText.setLayout(new BoxLayout(headText, BoxLayout.Y_AXIS));
        JLabel caption = UiComponents.label("Pesanan aktif", 12, Font.BOLD, UiTheme.MUTED);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.activeCodeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headText.add(caption);
        headText.add(Box.createVerticalStrut(2));
        headText.add(this.activeCodeLabel);
        head.add(headText, BorderLayout.CENTER);
        JPanel badgeWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(this.activeStatusBadge);
        head.add(badgeWrap, BorderLayout.EAST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        this.activeEstimateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.activePaymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(this.activeEstimateLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(this.activePaymentLabel);

        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        JPanel amount = new JPanel();
        amount.setOpaque(false);
        amount.setLayout(new BoxLayout(amount, BoxLayout.Y_AXIS));
        JLabel totalCaption = UiComponents.label("Total tagihan", 12, Font.PLAIN, UiTheme.MUTED);
        totalCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.activeTotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.activeTotalLabel.setForeground(UiTheme.ACCENT_DARK);
        amount.add(totalCaption);
        amount.add(Box.createVerticalStrut(2));
        amount.add(this.activeTotalLabel);
        footer.add(amount, BorderLayout.CENTER);
        footer.add(UiComponents.actions(this.activeDetailButton), BorderLayout.EAST);

        card.add(head, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildEmptyCard() {
        UiComponents.RoundedPanel card = UiComponents.surface(new GridBagLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        var icon = UiComponents.iconChip("inbox", UiTheme.MUTED, UiTheme.SURFACE_ALT, 44);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = UiComponents.label("Tidak ada pesanan aktif", 14, Font.BOLD, UiTheme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel description = UiComponents.label(
                "<html><div style='width:290px;text-align:center'>Pesanan yang sedang diproses akan muncul di sini.</div></html>",
                12, Font.PLAIN, UiTheme.MUTED);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton action = UiComponents.secondary("Lihat semua pesanan");
        action.setAlignmentX(Component.CENTER_ALIGNMENT);
        action.addActionListener(e -> this.openOrders.run());
        stack.add(icon);
        stack.add(Box.createVerticalStrut(12));
        stack.add(title);
        stack.add(Box.createVerticalStrut(5));
        stack.add(description);
        stack.add(Box.createVerticalStrut(14));
        stack.add(action);
        card.add(stack);
        return card;
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            String name = this.session.getName();
            List<Order> orders = Collections.emptyList();
            try {
                Customer me = this.customerService.getCustomerById(this.session.getUserId());
                name = me.getName();
                orders = this.orderService.getOrdersByCustomerId(this.session.getUserId());
            } catch (RuntimeException error) {
                this.subtitleLabel.setText("Ringkasan tidak dapat dimuat. Periksa koneksi database.");
                UiComponents.showError(this, error);
                return;
            }

            this.subtitleLabel.setText("Selamat datang, " + name + ". Berikut ringkasan cucian Anda.");
            int active = 0;
            int finished = 0;
            BigDecimal spent = BigDecimal.ZERO;
            Order latestActive = null;
            for (Order order : orders) {
                if (order.getStatus() == OrderStatus.SELESAI) {
                    finished++;
                } else {
                    active++;
                    if (latestActive == null) {
                        latestActive = order;
                    }
                }
                if (order.getTotalPrice() != null) {
                    spent = spent.add(order.getTotalPrice());
                }
            }
            this.totalValue.setText(String.valueOf(orders.size()));
            this.finishedValue.setText(String.valueOf(finished));
            this.spentValue.setText(UiFormat.rupiah(spent));

            if (latestActive == null) {
                this.activeOrderId = -1;
                this.activeCards.show(this.activeSlot, "empty");
            } else {
                this.activeOrderId = latestActive.getId();
                this.activeCodeLabel.setText(latestActive.getCode());
                UiComponents.updateBadge(this.activeStatusBadge, latestActive.getStatus());
                this.activeEstimateLabel.setText(latestActive.getEstimatedDate() == null
                        ? "Estimasi selesai belum ditentukan"
                        : "Estimasi selesai " + UiFormat.date(latestActive.getEstimatedDate()));
                this.activePaymentLabel.setText("Pembayaran: " + latestActive.getPaymentStatus()
                        + ", metode " + latestActive.getPaymentMethod());
                this.activeTotalLabel.setText(UiFormat.rupiah(latestActive.getTotalPrice()));
                this.activeCards.show(this.activeSlot, "active");
            }

            this.recentModel.setRowCount(0);
            for (int i = 0; i < Math.min(RECENT_LIMIT, orders.size()); i++) {
                Order order = orders.get(i);
                this.recentModel.addRow(new Object[]{
                        order.getCode(),
                        UiFormat.date(order.getOrderDate()),
                        UiFormat.weight(order.getWeight()),
                        UiFormat.rupiah(order.getTotalPrice()),
                        order.getStatus(),
                        order.getPaymentStatus(),
                        order.getPaymentMethod()});
            }
        });
    }
}
