package laundrykita.gui.customer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiTheme;
import laundrykita.model.Order;
import laundrykita.model.OrderDetail;
import laundrykita.model.OrderStatus;
import laundrykita.service.OrderService;

final class OrderDetailDialog {
    private static final String[] COLUMNS = {"Layanan", "Berat", "Harga per kg", "Subtotal"};

    private OrderDetailDialog() {
    }

    static void show(Component parent, OrderService orderService, int customerId, int orderId) {
        Order order;
        try {
            order = orderService.getOrderById(customerId, orderId);
        } catch (RuntimeException error) {
            UiComponents.showError(parent, error);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Detail pesanan " + order.getCode(), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(buildContent(dialog, order));
        UiComponents.installEscape(dialog);
        dialog.pack();
        dialog.setSize(Math.max(620, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel buildContent(JDialog dialog, Order order) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        JLabel code = UiComponents.label(order.getCode(), 22, Font.BOLD, UiTheme.TEXT);
        code.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel date = UiComponents.label("Dipesan " + UiFormat.date(order.getOrderDate()), 12, Font.PLAIN, UiTheme.MUTED);
        date.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.add(code);
        title.add(Box.createVerticalStrut(3));
        title.add(date);
        JPanel badges = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        badges.setOpaque(false);
        badges.add(UiComponents.badge(order.getStatus()));
        badges.add(UiComponents.badge(order.getPaymentStatus()));
        header.add(title, BorderLayout.WEST);
        header.add(badges, BorderLayout.EAST);

        JPanel info = UiComponents.detailPanel();
        int row = 0;
        if (order.getStatus() != OrderStatus.SELESAI && order.getEstimatedDate() != null) {
            UiComponents.addDetail(info, row++, "Estimasi selesai", UiFormat.date(order.getEstimatedDate()));
        }
        UiComponents.addDetail(info, row++, "Metode pembayaran", String.valueOf(order.getPaymentMethod()));
        UiComponents.addDetail(info, row++, "Status pembayaran", String.valueOf(order.getPaymentStatus()));
        UiComponents.addDetail(info, row++, "Alamat tujuan", order.getDestination());
        UiComponents.addDetail(info, row, "Catatan", order.getNotes());

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
        UiComponents.sizeColumns(table, 220, 110, 140, 140);
        table.getColumnModel().getColumn(1).setCellRenderer(UiComponents.rightRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        JScrollPane tableScroll = UiComponents.tableScroll(table);
        tableScroll.setPreferredSize(new Dimension(540, 150));

        UiComponents.RoundedPanel summary = new UiComponents.RoundedPanel(new GridLayout(1, 2, 12, 0), 10,
                UiTheme.SURFACE_ALT, UiTheme.BORDER_SOFT);
        summary.setBorder(new EmptyBorder(14, 18, 14, 18));
        JPanel weightRow = new JPanel();
        weightRow.setOpaque(false);
        weightRow.setLayout(new BoxLayout(weightRow, BoxLayout.Y_AXIS));
        JLabel weightCaption = UiComponents.label("Berat total", 12, Font.PLAIN, UiTheme.MUTED);
        weightCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel weightValue = UiComponents.label(UiFormat.weight(order.getWeight()), 15, Font.BOLD, UiTheme.TEXT);
        weightValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightRow.add(weightCaption);
        weightRow.add(Box.createVerticalStrut(2));
        weightRow.add(weightValue);
        JPanel totalRow = new JPanel();
        totalRow.setOpaque(false);
        totalRow.setLayout(new BoxLayout(totalRow, BoxLayout.Y_AXIS));
        JLabel totalCaption = UiComponents.label("Total tagihan", 12, Font.PLAIN, UiTheme.MUTED);
        totalCaption.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel totalValue = UiComponents.valueLabel();
        totalValue.setText(UiFormat.rupiah(order.getTotalPrice()));
        totalValue.setForeground(UiTheme.ACCENT_DARK);
        totalValue.setAlignmentX(Component.RIGHT_ALIGNMENT);
        totalRow.add(totalCaption);
        totalRow.add(Box.createVerticalStrut(2));
        totalRow.add(totalValue);
        summary.add(weightRow);
        summary.add(totalRow);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(header);
        content.add(Box.createVerticalStrut(16));
        content.add(info);
        content.add(Box.createVerticalStrut(16));
        JLabel itemsTitle = UiComponents.sectionTitle("Rincian layanan");
        itemsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(itemsTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(tableScroll);
        content.add(Box.createVerticalStrut(12));
        content.add(summary);

        JButton close = UiComponents.secondary("Tutup");
        close.addActionListener(e -> dialog.dispose());
        return UiComponents.dialogBody(content, UiComponents.actions(close));
    }
}
