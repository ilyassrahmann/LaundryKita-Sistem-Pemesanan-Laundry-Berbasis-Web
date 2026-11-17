package laundrykita.gui.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
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
import laundrykita.model.DashboardStats;
import laundrykita.model.Order;
import laundrykita.model.UserSession;
import laundrykita.service.OrderService;

class AdminDashboardPanel
extends JPanel {
    private static final int LATEST_COUNT = 5;
    private static final String[] COLUMNS = {"Kode", "Pelanggan", "Tanggal", "Total", "Status", "Pembayaran"};
    private final OrderService orderService;
    private final JLabel queueCaption = UiComponents.label("Perlu dikerjakan", 12, Font.BOLD, UiTheme.ON_ACCENT);
    private final JLabel waitingValue = UiComponents.label("0", 46, Font.BOLD, UiTheme.ON_ACCENT);
    private final JLabel summaryLabel = UiComponents.label("Memuat ringkasan...", 13, Font.PLAIN, UiTheme.ON_ACCENT);
    private final JLabel totalsLabel = UiComponents.label(" ", 12, Font.PLAIN, UiTheme.ON_ACCENT);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.tableModel));

    AdminDashboardPanel(UserSession session, OrderService orderService, Runnable openOrders) {
        this.orderService = orderService;
        this.setLayout(new BorderLayout(0, 18));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 32, 24, 32));

        JButton refresh = UiComponents.secondary("Muat ulang", UiIcons.icon("refresh", UiTheme.MUTED, 16));
        refresh.addActionListener(e -> this.refresh());
        this.add(UiComponents.header("Ringkasan",
                "Pantau pesanan dan pelanggan hari ini.", refresh),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        body.add(this.buildQueueCard(openOrders), BorderLayout.NORTH);

        JPanel latest = UiComponents.card(new BorderLayout(0, 12));
        JPanel latestHeader = new JPanel(new BorderLayout());
        latestHeader.setOpaque(false);
        latestHeader.add(UiComponents.sectionTitle("Pesanan terbaru"), BorderLayout.WEST);
        JButton seeAll = UiComponents.secondary("Lihat semua");
        seeAll.addActionListener(e -> openOrders.run());
        latestHeader.add(UiComponents.actions(seeAll), BorderLayout.EAST);
        latest.add(latestHeader, BorderLayout.NORTH);

        this.table.setAutoCreateRowSorter(false);
        UiComponents.sizeColumns(this.table, 90, 220, 120, 120, 110, 120);
        this.table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(4).setCellRenderer(UiComponents.badgeRenderer());
        this.table.getColumnModel().getColumn(5).setCellRenderer(UiComponents.badgeRenderer());
        UiComponents.enableSorting(this.table, 3);
        latest.add(UiComponents.tableView(this.table, "Belum ada pesanan",
                "Pesanan terbaru akan muncul di sini setelah admin mencatatnya."), BorderLayout.CENTER);

        body.add(latest, BorderLayout.CENTER);
        this.add(body, BorderLayout.CENTER);
        this.table.setToolTipText("Pesanan terbaru");
    }

    private JPanel buildQueueCard(Runnable openOrders) {
        UiComponents.RoundedPanel hero = new UiComponents.RoundedPanel(new BorderLayout(24, 0), 16,
                UiTheme.ACCENT, null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 16));
                int size = 168;
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(getWidth() - size - 18, getHeight() - size + 18,
                        size, size, 34, 34));
                g2.setColor(new Color(255, 255, 255, 24));
                g2.fillOval(getWidth() - 92, -38, 130, 130);
                g2.dispose();
            }
        };
        hero.setBorder(new EmptyBorder(22, 26, 22, 24));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        this.queueCaption.setAlignmentX(LEFT_ALIGNMENT);
        JPanel waitingRow = new JPanel();
        waitingRow.setOpaque(false);
        waitingRow.setLayout(new BoxLayout(waitingRow, BoxLayout.X_AXIS));
        waitingRow.setAlignmentX(LEFT_ALIGNMENT);
        waitingValue.setAlignmentX(LEFT_ALIGNMENT);
        JLabel waitingUnit = UiComponents.label("pesanan menunggu", 14, Font.PLAIN, UiTheme.ON_ACCENT);
        waitingUnit.setAlignmentX(LEFT_ALIGNMENT);
        waitingRow.add(waitingValue);
        waitingRow.add(Box.createHorizontalStrut(12));
        waitingRow.add(waitingUnit);
        waitingRow.add(Box.createHorizontalGlue());
        waitingRow.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 72));
        this.summaryLabel.setAlignmentX(LEFT_ALIGNMENT);
        this.totalsLabel.setAlignmentX(LEFT_ALIGNMENT);

        text.add(this.queueCaption);
        text.add(Box.createVerticalStrut(2));
        text.add(waitingRow);
        text.add(Box.createVerticalStrut(8));
        text.add(this.summaryLabel);
        text.add(Box.createVerticalStrut(3));
        text.add(this.totalsLabel);

        JPanel actionSide = new JPanel(new GridBagLayout());
        actionSide.setOpaque(false);
        JButton open = UiComponents.light("Buka pesanan", UiIcons.icon("orders", UiTheme.ACCENT_DARK, 16));
        open.addActionListener(e -> openOrders.run());
        actionSide.add(open);

        hero.add(text, BorderLayout.CENTER);
        hero.add(actionSide, BorderLayout.EAST);
        return hero;
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            try {
                DashboardStats stats = this.orderService.getDashboardStats();
                this.waitingValue.setText(String.valueOf(stats.getWaiting()));
                this.queueCaption.setText(stats.getWaiting() == 0 ? "Antrean bersih" : "Perlu dikerjakan");
                if (stats.getWaiting() == 0) {
                    this.summaryLabel.setText("Tidak ada pesanan menunggu saat ini.");
                } else {
                    this.summaryLabel.setText(stats.getWaiting() + " pesanan menunggu dikerjakan, "
                            + stats.getInProgress() + " sedang diproses.");
                }
                this.totalsLabel.setText("Total " + stats.getTotalOrders() + " pesanan, "
                        + stats.getCustomers() + " pelanggan terdaftar.");
                this.tableModel.setRowCount(0);
                for (Order order : this.orderService.getLatestOrders(LATEST_COUNT)) {
                    this.tableModel.addRow(new Object[]{
                            order.getCode(),
                            order.getCustomerName(),
                            UiFormat.date(order.getOrderDate()),
                            UiFormat.rupiah(order.getTotalPrice()),
                            order.getStatus(),
                            order.getPaymentStatus()});
                }
            } catch (RuntimeException error) {
                this.waitingValue.setText("-");
                this.queueCaption.setText("Ringkasan tidak tersedia");
                this.summaryLabel.setText("Periksa koneksi database, lalu muat ulang.");
                this.totalsLabel.setText(" ");
                this.tableModel.setRowCount(0);
                UiComponents.showError(this, error);
            }
        });
    }
}
