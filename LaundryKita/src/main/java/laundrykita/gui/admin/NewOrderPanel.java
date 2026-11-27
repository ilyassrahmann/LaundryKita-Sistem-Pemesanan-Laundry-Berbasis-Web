package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Customer;
import laundrykita.model.Order;
import laundrykita.model.OrderDetail;
import laundrykita.model.OrderStatus;
import laundrykita.model.PaymentMethod;
import laundrykita.model.PaymentStatus;
import laundrykita.model.Service;
import laundrykita.model.UserSession;
import laundrykita.service.CatalogService;
import laundrykita.service.CustomerService;
import laundrykita.service.OrderService;

class NewOrderPanel
extends JPanel {
    private static final String[] COLUMNS = {"Layanan", "Harga per kg", "Berat (kg)", "Subtotal"};
    private final UserSession session;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final CatalogService catalogService;
    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JTextField addressField = UiComponents.textField(30, "Alamat tujuan");
    private final JTextField estimateField = UiComponents.textField(10, "dd-MM-yyyy");
    private final JComboBox<OrderStatus> statusBox = new JComboBox<>(OrderStatus.values());
    private final JComboBox<PaymentMethod> methodBox = new JComboBox<>(PaymentMethod.values());
    private final JComboBox<PaymentStatus> paymentBox = new JComboBox<>(PaymentStatus.values());
    private final JTextField notesField = UiComponents.textField(30, "Catatan tambahan (opsional, maks. 255 karakter)");
    private final JLabel totalLabel = UiComponents.valueLabel();
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.tableModel));
    private List<Service> services = new ArrayList<>();

    NewOrderPanel(UserSession session, OrderService orderService, CustomerService customerService, CatalogService catalogService) {
        this.session = session;
        this.orderService = orderService;
        this.customerService = customerService;
        this.catalogService = catalogService;
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        this.add(UiComponents.header("Pesanan baru",
                "Catat pesanan pelanggan dan total dihitung otomatis."), BorderLayout.NORTH);

        UiComponents.styleCombo(this.customerBox);
        UiComponents.styleCombo(this.statusBox);
        UiComponents.styleCombo(this.methodBox);
        UiComponents.styleCombo(this.paymentBox);
        this.customerBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                String text = value == null
                        ? "-- Pilih pelanggan --"
                        : ((Customer) value).getName() + " (" + ((Customer) value).getEmail() + ")";
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        this.customerBox.addActionListener(e -> {
            this.fillAddressFromCustomer();
            UiComponents.setFieldError(this.customerBox, false);
        });
        this.addressField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                UiComponents.setFieldError(NewOrderPanel.this.addressField, false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                UiComponents.setFieldError(NewOrderPanel.this.addressField, false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                UiComponents.setFieldError(NewOrderPanel.this.addressField, false);
            }
        });

        JPanel form = UiComponents.form();
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Pelanggan *", this.customerBox),
                UiComponents.fieldGroup("Estimasi selesai", this.estimateField),
                UiComponents.fieldGroup("Status pesanan", this.statusBox)));
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Alamat tujuan *", this.addressField),
                UiComponents.fieldGroup("Metode bayar", this.methodBox),
                UiComponents.fieldGroup("Status bayar", this.paymentBox)));
        form.add(UiComponents.fieldGroup("Catatan", this.notesField));

        JPanel formCard = UiComponents.card(new BorderLayout(0, 12));
        formCard.add(UiComponents.sectionTitle("Detail pesanan"), BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);

        this.tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                this.updatePreview();
            }
        });
        UiComponents.sizeColumns(this.table, 200, 140, 120, 140);
        this.table.getColumnModel().getColumn(1).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(3).setCellRenderer(UiComponents.rightRenderer());
        this.table.setToolTipText("Isi berat untuk layanan yang dipakai, contoh: 2,5");

        JPanel serviceCard = UiComponents.card(new BorderLayout(0, 12));
        JPanel serviceHeader = new JPanel(new BorderLayout());
        serviceHeader.setOpaque(false);
        serviceHeader.add(UiComponents.sectionTitle("Layanan"), BorderLayout.WEST);
        serviceHeader.add(UiComponents.hint("Isi berat pada layanan yang dipakai, contoh 2,5"), BorderLayout.EAST);
        serviceCard.add(serviceHeader, BorderLayout.NORTH);
        JPanel serviceView = UiComponents.tableView(this.table, "Belum ada layanan",
                "Tambahkan layanan lewat menu Layanan sebelum mencatat pesanan.");
        serviceView.setPreferredSize(new Dimension(560, 150));
        serviceCard.add(serviceView, BorderLayout.CENTER);

        UiComponents.RoundedPanel totalPanel = new UiComponents.RoundedPanel(new BorderLayout(12, 0), 10,
                UiTheme.SURFACE_ALT, UiTheme.BORDER_SOFT);
        totalPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        totalPanel.add(UiComponents.label("Total pratinjau", 12, Font.BOLD, UiTheme.MUTED), BorderLayout.WEST);
        this.totalLabel.setForeground(UiTheme.ACCENT_DARK);
        totalPanel.add(this.totalLabel, BorderLayout.EAST);
        serviceCard.add(totalPanel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(formCard, BorderLayout.NORTH);
        content.add(serviceCard, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JButton resetButton = UiComponents.secondary("Kosongkan form");
        resetButton.addActionListener(e -> this.refresh());
        JButton saveButton = UiComponents.primary("Simpan pesanan", UiIcons.icon("check", UiTheme.ON_ACCENT, 16));
        saveButton.addActionListener(e -> this.submit());
        bottom.add(UiComponents.actions(resetButton, saveButton), BorderLayout.EAST);

        this.add(UiComponents.pageScroll(content), BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
        this.updatePreview();
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            this.customerBox.removeAllItems();
            this.customerBox.addItem(null);
            this.tableModel.setRowCount(0);
            this.services = new ArrayList<>();
            try {
                for (Customer customer : this.customerService.getAllCustomers()) {
                    this.customerBox.addItem(customer);
                }
                this.services = this.catalogService.getAllServices();
            } catch (RuntimeException error) {
                UiComponents.showError(this, error);
            }
            for (Service service : this.services) {
                this.tableModel.addRow(new Object[]{
                        service.getServiceType(), UiFormat.rupiah(service.getPricePerKg()), "", ""});
            }
            this.customerBox.setSelectedIndex(0);
            this.addressField.setText("");
            this.estimateField.setText("");
            this.statusBox.setSelectedItem(OrderStatus.MENUNGGU);
            this.methodBox.setSelectedItem(PaymentMethod.TUNAI);
            this.paymentBox.setSelectedItem(PaymentStatus.BELUM_BAYAR);
            this.notesField.setText("");
            UiComponents.setFieldError(this.customerBox, false);
            UiComponents.setFieldError(this.addressField, false);
            this.updatePreview();
        });
    }

    private void fillAddressFromCustomer() {
        Customer customer = (Customer) this.customerBox.getSelectedItem();
        this.addressField.setText(customer == null || customer.getAddress() == null ? "" : customer.getAddress());
    }

    private BigDecimal weightAt(int row) {
        Object value = this.tableModel.getValueAt(row, 2);
        String text = value == null ? "" : value.toString().trim().replace(',', '.');
        if (text.isEmpty()) {
            return null;
        }
        return new BigDecimal(text);
    }

    private void updatePreview() {
        BigDecimal total = BigDecimal.ZERO;
        for (int row = 0; row < this.services.size(); row++) {
            String subtotalText = "";
            try {
                BigDecimal weight = this.weightAt(row);
                if (weight != null && weight.signum() > 0) {
                    BigDecimal subtotal = OrderDetail.calculateSubtotal(weight, this.services.get(row).getPricePerKg());
                    total = total.add(subtotal);
                    subtotalText = UiFormat.rupiah(subtotal);
                }
            } catch (NumberFormatException e) {
                subtotalText = "Berat tidak valid";
            }
            if (!subtotalText.equals(this.tableModel.getValueAt(row, 3))) {
                this.tableModel.setValueAt(subtotalText, row, 3);
            }
        }
        this.totalLabel.setText(UiFormat.rupiah(total));
    }

    private void submit() {
        if (this.table.isEditing()) {
            this.table.getCellEditor().stopCellEditing();
        }
        Customer customer = (Customer) this.customerBox.getSelectedItem();
        boolean customerMissing = customer == null;
        boolean addressMissing = this.addressField.getText().trim().isEmpty();
        UiComponents.setFieldError(this.customerBox, customerMissing);
        UiComponents.setFieldError(this.addressField, addressMissing);
        if (customerMissing || addressMissing) {
            UiComponents.showWarning(this, "Pilih pelanggan dan isi alamat tujuan terlebih dahulu.");
            return;
        }
        List<OrderDetail> details = new ArrayList<>();
        for (int row = 0; row < this.services.size(); row++) {
            BigDecimal weight;
            try {
                weight = this.weightAt(row);
            } catch (NumberFormatException e) {
                UiComponents.showWarning(this, "Berat untuk layanan \"" + this.services.get(row).getServiceType()
                        + "\" harus berupa angka, contoh: 2,5.");
                return;
            }
            if (weight == null) {
                continue;
            }
            OrderDetail detail = new OrderDetail();
            detail.setServiceId(this.services.get(row).getId());
            detail.setWeightKg(weight);
            details.add(detail);
        }

        if (details.isEmpty()) {
            UiComponents.showWarning(this, "Isi berat minimal satu layanan, contoh 2,5.");
            if (this.table.getRowCount() > 0) {
                this.table.editCellAt(0, 2);
                this.table.requestFocusInWindow();
            }
            return;
        }

        LocalDate estimate;
        try {
            estimate = UiFormat.parseDate(this.estimateField.getText());
        } catch (DateTimeParseException e) {
            UiComponents.showWarning(this, "Format estimasi selesai harus dd-MM-yyyy, contoh: 25-12-2026.");
            return;
        }

        Order order = new Order();
        order.setCustomerId(customer.getId());
        order.setDestination(this.addressField.getText());
        order.setEstimatedDate(estimate);
        order.setStatus((OrderStatus) this.statusBox.getSelectedItem());
        order.setPaymentMethod((PaymentMethod) this.methodBox.getSelectedItem());
        order.setPaymentStatus((PaymentStatus) this.paymentBox.getSelectedItem());
        order.setNotes(this.notesField.getText());
        try {
            int newId = this.orderService.createOrder(this.session, order, details);
            UiComponents.showInfo(this, "Pesanan " + String.format("#LK%03d", newId) + " berhasil disimpan.");
            this.refresh();
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
    }
}
