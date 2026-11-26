package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiTheme;
import laundrykita.model.Order;
import laundrykita.model.OrderDetail;
import laundrykita.model.OrderStatus;
import laundrykita.model.PaymentStatus;
import laundrykita.service.OrderService;

class OrderDetailDialog
extends JDialog {
    private static final String[] COLUMNS = {"Layanan", "Berat", "Harga per kg", "Subtotal"};
    private final OrderService orderService;
    private final Order order;
    private final JComboBox<OrderStatus> statusBox = new JComboBox<>(OrderStatus.values());
    private final JComboBox<PaymentStatus> paymentBox = new JComboBox<>(PaymentStatus.values());
    private final UiComponents.BadgeLabel statusBadge = UiComponents.badge(OrderStatus.MENUNGGU);
    private final UiComponents.BadgeLabel paymentBadge = UiComponents.badge(PaymentStatus.BELUM_BAYAR);
    private JButton saveStatusButton;
    private boolean changed;

    OrderDetailDialog(Window owner, OrderService orderService, Order order) {
        super(owner, "Detail pesanan " + order.getCode(), Dialog.ModalityType.APPLICATION_MODAL);
        this.orderService = orderService;
        this.order = order;

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        JLabel code = UiComponents.label(order.getCode(), 20, Font.BOLD, UiTheme.TEXT);
        code.setAlignmentX(LEFT_ALIGNMENT);
        JLabel date = UiComponents.label("Dipesan " + UiFormat.date(order.getOrderDate()), 12, Font.PLAIN, UiTheme.MUTED);
        date.setAlignmentX(LEFT_ALIGNMENT);
        title.add(code);
        title.add(Box.createVerticalStrut(3));
        title.add(date);

        UiComponents.updateBadge(this.statusBadge, order.getStatus());
        UiComponents.updateBadge(this.paymentBadge, order.getPaymentStatus());
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setOpaque(false);
        badges.add(this.statusBadge);
        badges.add(this.paymentBadge);

        JPanel headerRow = new JPanel(new BorderLayout(12, 0));
        headerRow.setOpaque(false);
        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(badges, BorderLayout.EAST);

        JPanel info = UiComponents.detailPanel();
        UiComponents.addDetail(info, 0, "Pelanggan", order.getCustomerName());
        UiComponents.addDetail(info, 1, "Tanggal pesan", UiFormat.date(order.getOrderDate()));
        UiComponents.addDetail(info, 2, "Estimasi selesai", UiFormat.date(order.getEstimatedDate()));
        UiComponents.addDetail(info, 3, "Metode pembayaran", String.valueOf(order.getPaymentMethod()));
        UiComponents.addDetail(info, 4, "Alamat tujuan", order.getDestination());
        UiComponents.addDetail(info, 5, "Catatan", order.getNotes());

        DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (OrderDetail detail : order.getDetails()) {
            model.addRow(new Object[]{
                    detail.getServiceType(),
                    UiFormat.weight(detail.getWeightKg()),
                    UiFormat.rupiah(detail.getPricePerKg()),
                    UiFormat.rupiah(detail.getSubtotal())});
        }
        JTable table = UiComponents.table(new JTable(model));
        UiComponents.sizeColumns(table, 220, 120, 140, 140);
        table.getColumnModel().getColumn(1).setCellRenderer(UiComponents.rightRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        JScrollPane tableScroll = UiComponents.tableScroll(table);
        tableScroll.setPreferredSize(new Dimension(580, 160));

        UiComponents.RoundedPanel totalRow = new UiComponents.RoundedPanel(new BorderLayout(12, 0), 10,
                UiTheme.SURFACE_ALT, UiTheme.BORDER_SOFT);
        totalRow.setBorder(new EmptyBorder(12, 16, 12, 16));
        totalRow.add(UiComponents.muted("Berat total " + UiFormat.weight(order.getWeight())), BorderLayout.WEST);
        JLabel totalValue = UiComponents.valueLabel();
        totalValue.setText(UiFormat.rupiah(order.getTotalPrice()));
        totalValue.setForeground(UiTheme.ACCENT_DARK);
        totalRow.add(totalValue, BorderLayout.EAST);

        UiComponents.styleCombo(this.statusBox);
        UiComponents.styleCombo(this.paymentBox);
        this.statusBox.setSelectedItem(order.getStatus());
        this.paymentBox.setSelectedItem(order.getPaymentStatus());
        this.saveStatusButton = UiComponents.primary("Simpan status");
        this.saveStatusButton.setEnabled(false);
        this.saveStatusButton.addActionListener(e -> this.saveStatus());
        JPanel statusFields = UiComponents.columns(
                UiComponents.fieldGroup("Status pesanan", this.statusBox),
                UiComponents.fieldGroup("Status pembayaran", this.paymentBox));
        Runnable updateDirty = () -> this.saveStatusButton.setEnabled(
                this.statusBox.getSelectedItem() != this.order.getStatus()
                        || this.paymentBox.getSelectedItem() != this.order.getPaymentStatus());
        this.statusBox.addActionListener(e -> updateDirty.run());
        this.paymentBox.addActionListener(e -> updateDirty.run());
        JPanel statusRow = new JPanel(new BorderLayout(12, 0));
        statusRow.setOpaque(false);
        statusRow.add(statusFields, BorderLayout.CENTER);
        statusRow.add(UiComponents.actions(this.saveStatusButton), BorderLayout.EAST);
        JPanel statusCard = UiComponents.card(new BorderLayout(0, 12));
        statusCard.setBorder(new EmptyBorder(16, 18, 8, 18));
        statusCard.add(UiComponents.sectionTitle("Perbarui status"), BorderLayout.NORTH);
        statusCard.add(statusRow, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(headerRow);
        content.add(Box.createVerticalStrut(16));
        content.add(info);
        content.add(Box.createVerticalStrut(16));
        JLabel itemsTitle = UiComponents.sectionTitle("Rincian layanan");
        itemsTitle.setAlignmentX(LEFT_ALIGNMENT);
        content.add(itemsTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(tableScroll);
        content.add(Box.createVerticalStrut(8));
        content.add(totalRow);
        content.add(Box.createVerticalStrut(16));
        content.add(statusCard);

        JButton closeButton = UiComponents.secondary("Tutup");
        closeButton.addActionListener(e -> this.dispose());
        this.setContentPane(UiComponents.dialogBody(content, UiComponents.actions(closeButton)));
        UiComponents.installEscape(this);
        this.pack();
        this.setSize(Math.max(680, this.getWidth()), this.getHeight());
        this.setLocationRelativeTo(owner);
    }

    boolean isChanged() {
        return this.changed;
    }

    private void saveStatus() {
        try {
            OrderStatus status = (OrderStatus) this.statusBox.getSelectedItem();
            PaymentStatus payment = (PaymentStatus) this.paymentBox.getSelectedItem();
            this.orderService.updateOrderStatus(this.order.getId(), status, payment);
            this.order.setStatus(status);
            this.order.setPaymentStatus(payment);
            UiComponents.updateBadge(this.statusBadge, status);
            UiComponents.updateBadge(this.paymentBadge, payment);
            this.saveStatusButton.setEnabled(false);
            this.changed = true;
            UiComponents.showInfo(this, "Status pesanan dan pembayaran berhasil disimpan.");
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
    }
}
