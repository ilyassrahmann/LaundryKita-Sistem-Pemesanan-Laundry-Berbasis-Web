package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
import laundrykita.service.OrderService;

class AdminOrdersPanel
extends JPanel {
    private static final String[] COLUMNS = {"Kode", "Pelanggan", "Tanggal", "Berat", "Total", "Status", "Pembayaran"};
    private static final String ALL_STATUS = "Semua status";
    private final OrderService orderService;
    private final JTextField searchField = UiComponents.searchField(18, "Cari nama atau kode pesanan");
    private final JComboBox<Object> statusFilter = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.tableModel));
    private final JLabel countLabel = UiComponents.muted("");
    private final Timer searchTimer = new Timer(250, e -> this.refresh());
    private List<Order> orders = new ArrayList<>();

    AdminOrdersPanel(OrderService orderService) {
        this.orderService = orderService;
        this.searchTimer.setRepeats(false);
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        JButton refreshButton = UiComponents.secondary("Muat ulang", UiIcons.icon("refresh", UiTheme.MUTED, 16));
        refreshButton.addActionListener(e -> this.refresh());
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(UiComponents.header("Pesanan",
                "Cari, periksa detail, dan perbarui status pesanan.", refreshButton), BorderLayout.NORTH);

        this.searchField.setPreferredSize(new Dimension(300, 40));
        this.statusFilter.addItem(ALL_STATUS);
        for (OrderStatus status : OrderStatus.values()) {
            this.statusFilter.addItem(status);
        }
        UiComponents.styleCombo(this.statusFilter);
        this.statusFilter.setPreferredSize(new Dimension(170, 40));
        JButton reset = UiComponents.ghost("Atur ulang");
        reset.addActionListener(e -> {
            this.searchField.setText("");
            this.statusFilter.setSelectedIndex(0);
        });
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(this.searchField);
        toolbar.add(this.statusFilter);
        toolbar.add(reset);
        top.add(toolbar, BorderLayout.CENTER);
        this.add(top, BorderLayout.NORTH);

        UiComponents.sizeColumns(this.table, 90, 200, 110, 90, 130, 120, 150);
        this.table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(4).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(5).setCellRenderer(UiComponents.badgeRenderer());
        this.table.getColumnModel().getColumn(6).setCellRenderer(UiComponents.badgeRenderer());
        UiComponents.enableSorting(this.table, 3, 4);
        this.table.setToolTipText("Klik dua kali untuk melihat detail pesanan");
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    AdminOrdersPanel.this.showDetail();
                }
            }
        });

        JButton deleteButton = UiComponents.danger("Hapus");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> this.deleteOrder());
        JButton detailButton = UiComponents.primary("Detail atau ubah status");
        detailButton.setEnabled(false);
        detailButton.addActionListener(e -> this.showDetail());
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            boolean hasSelection = this.table.getSelectedRow() >= 0;
            deleteButton.setEnabled(hasSelection);
            detailButton.setEnabled(hasSelection);
        });
        this.table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openDetail");
        this.table.getActionMap().put("openDetail", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminOrdersPanel.this.showDetail();
            }
        });
        JPanel footer = UiComponents.tableFooter(this.countLabel, deleteButton, detailButton);
        this.add(UiComponents.tableCard(this.table, "Belum ada pesanan",
                "Pesanan baru akan muncul di sini. Ubah kata kunci atau filter jika tidak menemukan yang dicari.",
                footer), BorderLayout.CENTER);

        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                AdminOrdersPanel.this.searchTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                AdminOrdersPanel.this.searchTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                AdminOrdersPanel.this.searchTimer.restart();
            }
        });
        this.searchField.addActionListener(e -> this.refresh());
        this.statusFilter.addActionListener(e -> this.refresh());
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            Object selected = this.statusFilter.getSelectedItem();
            OrderStatus status = selected instanceof OrderStatus orderStatus ? orderStatus : null;
            try {
                this.orders = this.orderService.searchOrders(this.searchField.getText(), status);
            } catch (RuntimeException error) {
                this.orders = new ArrayList<>();
                UiComponents.showError(this, error);
            }
            this.tableModel.setRowCount(0);
            for (Order order : this.orders) {
                this.tableModel.addRow(new Object[]{
                        order.getCode(),
                        order.getCustomerName(),
                        UiFormat.date(order.getOrderDate()),
                        UiFormat.weight(order.getWeight()),
                        UiFormat.rupiah(order.getTotalPrice()),
                        order.getStatus(),
                        order.getPaymentStatus()});
            }
            this.countLabel.setText(this.orders.size() + " pesanan");
        });
    }

    private Order selectedOrder() {
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            UiComponents.showWarning(this, "Pilih satu pesanan pada tabel terlebih dahulu.");
            return null;
        }
        int modelRow = this.table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= this.orders.size()) {
            return null;
        }
        return this.orders.get(modelRow);
    }

    private void showDetail() {
        Order selected = this.selectedOrder();
        if (selected == null) {
            return;
        }
        try {
            Order order = this.orderService.getOrderById(selected.getId());
            OrderDetailDialog dialog = new OrderDetailDialog(SwingUtilities.getWindowAncestor(this), this.orderService, order);
            dialog.setVisible(true);
            if (dialog.isChanged()) {
                this.refresh();
            }
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
            this.refresh();
        }
    }

    private void deleteOrder() {
        Order selected = this.selectedOrder();
        if (selected == null) {
            return;
        }
        if (!UiComponents.confirm(this, "Hapus pesanan " + selected.getCode() + " milik "
                + selected.getCustomerName() + "?\nRincian item pesanan ikut terhapus.")) {
            return;
        }
        try {
            this.orderService.deleteOrder(selected.getId());
            UiComponents.showInfo(this, "Pesanan berhasil dihapus.");
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
        this.refresh();
    }
}
