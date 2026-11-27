package laundrykita.gui.customer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Order;
import laundrykita.model.OrderStatus;
import laundrykita.model.UserSession;
import laundrykita.service.OrderService;

class CustomerOrdersPanel
extends JPanel {
    private static final String[] COLUMNS = {"Kode", "Tanggal", "Berat", "Total", "Status", "Pembayaran", "Metode"};
    private static final String ALL_STATUS = "Semua status";
    private final UserSession session;
    private final OrderService orderService;
    private final JTextField keywordField = UiComponents.searchField(16, "Cari kode, mis. LK012");
    private final JComboBox<Object> statusBox = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.model));
    private final JLabel countLabel = UiComponents.muted("");
    private List<Order> loaded = Collections.emptyList();
    private List<Order> shown = Collections.emptyList();

    CustomerOrdersPanel(UserSession session, OrderService orderService) {
        this.session = session;
        this.orderService = orderService;
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        JButton refreshButton = UiComponents.secondary("Muat ulang", UiIcons.icon("refresh", UiTheme.MUTED, 16));
        refreshButton.addActionListener(e -> this.refresh());
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(UiComponents.header("Pesanan Saya", "Riwayat dan status cucian Anda.", refreshButton), BorderLayout.NORTH);

        this.statusBox.addItem(ALL_STATUS);
        for (OrderStatus status : OrderStatus.values()) {
            this.statusBox.addItem(status);
        }
        UiComponents.styleCombo(this.statusBox);
        this.statusBox.setPreferredSize(new Dimension(170, 40));
        this.keywordField.setPreferredSize(new Dimension(260, 40));
        JButton reset = UiComponents.ghost("Atur ulang");
        reset.addActionListener(e -> {
            this.keywordField.setText("");
            this.statusBox.setSelectedIndex(0);
        });
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filter.setOpaque(false);
        filter.add(this.keywordField);
        filter.add(this.statusBox);
        filter.add(reset);
        top.add(filter, BorderLayout.CENTER);
        this.add(top, BorderLayout.NORTH);

        UiComponents.sizeColumns(this.table, 90, 110, 90, 130, 120, 150, 100);
        this.table.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(4).setCellRenderer(UiComponents.badgeRenderer());
        this.table.getColumnModel().getColumn(5).setCellRenderer(UiComponents.badgeRenderer());
        UiComponents.enableSorting(this.table, 2, 3);
        this.table.setToolTipText("Klik dua kali untuk melihat detail pesanan");
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CustomerOrdersPanel.this.openSelected();
                }
            }
        });
        JButton detail = UiComponents.primary("Lihat detail");
        detail.setEnabled(false);
        detail.addActionListener(e -> this.openSelected());
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                detail.setEnabled(this.table.getSelectedRow() >= 0);
            }
        });
        this.table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openDetail");
        this.table.getActionMap().put("openDetail", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerOrdersPanel.this.openSelected();
            }
        });
        JPanel footer = UiComponents.tableFooter(this.countLabel, detail);
        this.add(UiComponents.tableCard(this.table, "Belum ada pesanan",
                "Pesanan Anda akan muncul di sini setelah admin mencatatnya. Ubah filter jika perlu.",
                footer), BorderLayout.CENTER);

        this.keywordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                CustomerOrdersPanel.this.applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CustomerOrdersPanel.this.applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CustomerOrdersPanel.this.applyFilter();
            }
        });
        this.statusBox.addActionListener(e -> this.applyFilter());
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            try {
                this.loaded = this.orderService.getOrdersByCustomerId(this.session.getUserId());
            } catch (RuntimeException error) {
                this.loaded = Collections.emptyList();
                UiComponents.showError(this, error);
            }
            this.applyFilter();
        });
    }

    private void applyFilter() {
        String keyword = this.keywordField.getText().trim().toLowerCase(Locale.ROOT);
        if (keyword.startsWith("#")) {
            keyword = keyword.substring(1);
        }
        Object selected = this.statusBox.getSelectedItem();
        OrderStatus status = selected instanceof OrderStatus orderStatus ? orderStatus : null;
        this.shown = new ArrayList<>();
        this.model.setRowCount(0);
        for (Order order : this.loaded) {
            boolean codeMatches = keyword.isEmpty() || order.getCode().toLowerCase(Locale.ROOT).contains(keyword);
            boolean statusMatches = status == null || order.getStatus() == status;
            if (!codeMatches || !statusMatches) {
                continue;
            }
            this.shown.add(order);
            this.model.addRow(new Object[]{
                    order.getCode(),
                    UiFormat.date(order.getOrderDate()),
                    UiFormat.weight(order.getWeight()),
                    UiFormat.rupiah(order.getTotalPrice()),
                    order.getStatus(),
                    order.getPaymentStatus(),
                    order.getPaymentMethod()});
        }
        this.countLabel.setText(this.shown.size() + " dari " + this.loaded.size() + " pesanan");
    }

    private void openSelected() {
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            UiComponents.showWarning(this, "Pilih satu pesanan terlebih dahulu.");
            return;
        }
        int modelRow = this.table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= this.shown.size()) {
            return;
        }
        OrderDetailDialog.show(this, this.orderService, this.session.getUserId(), this.shown.get(modelRow).getId());
    }
}
