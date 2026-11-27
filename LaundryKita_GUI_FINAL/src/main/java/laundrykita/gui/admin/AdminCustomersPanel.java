package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Customer;
import laundrykita.service.CustomerService;

class AdminCustomersPanel
extends JPanel {
    private static final String[] COLUMNS = {"ID", "Nama", "Email", "Telepon", "Alamat", "Pesanan"};
    private final CustomerService customerService;
    private final JTextField searchField = UiComponents.searchField(21, "Cari nama, email, atau telepon");
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.tableModel));
    private final JLabel countLabel = UiComponents.muted("");
    private List<Customer> allCustomers = new ArrayList<>();
    private List<Customer> shownCustomers = new ArrayList<>();

    AdminCustomersPanel(CustomerService customerService) {
        this.customerService = customerService;
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        JButton addButton = UiComponents.primary("Tambah pelanggan", UiIcons.icon("plus", UiTheme.ON_ACCENT, 16));
        addButton.addActionListener(e -> this.addCustomer());
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(UiComponents.header("Pelanggan",
                "Simpan dan kelola data pelanggan LaundryKita.", addButton), BorderLayout.NORTH);

        this.searchField.setPreferredSize(new Dimension(320, 40));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchBar.setOpaque(false);
        searchBar.add(this.searchField);
        top.add(searchBar, BorderLayout.CENTER);
        this.add(top, BorderLayout.NORTH);

        this.table.removeColumn(this.table.getColumnModel().getColumn(0));
        UiComponents.sizeColumns(this.table, 220, 240, 130, 260, 100);
        UiComponents.enableSorting(this.table, 5);
        JButton deleteButton = UiComponents.danger("Hapus");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> this.deleteCustomer());
        JButton editButton = UiComponents.secondary("Ubah");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> this.editCustomer());
        JButton detailButton = UiComponents.primary("Lihat detail");
        detailButton.setEnabled(false);
        detailButton.addActionListener(e -> this.showDetail());
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            boolean hasSelection = this.table.getSelectedRow() >= 0;
            deleteButton.setEnabled(hasSelection);
            editButton.setEnabled(hasSelection);
            detailButton.setEnabled(hasSelection);
        });
        this.table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openDetail");
        this.table.getActionMap().put("openDetail", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminCustomersPanel.this.showDetail();
            }
        });
        JPanel footer = UiComponents.tableFooter(this.countLabel, deleteButton, editButton, detailButton);
        this.add(UiComponents.tableCard(this.table, "Belum ada pelanggan",
                "Tambahkan pelanggan lewat tombol Tambah pelanggan di atas.", footer), BorderLayout.CENTER);

        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                AdminCustomersPanel.this.applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                AdminCustomersPanel.this.applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                AdminCustomersPanel.this.applyFilter();
            }
        });
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            try {
                this.allCustomers = this.customerService.getAllCustomers();
            } catch (RuntimeException error) {
                this.allCustomers = new ArrayList<>();
                UiComponents.showError(this, error);
            }
            this.applyFilter();
        });
    }

    private void applyFilter() {
        String keyword = this.searchField.getText().trim().toLowerCase();
        this.shownCustomers = new ArrayList<>();
        this.tableModel.setRowCount(0);
        for (Customer customer : this.allCustomers) {
            if (!keyword.isEmpty() && !this.matches(customer, keyword)) {
                continue;
            }
            this.shownCustomers.add(customer);
            this.tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    UiFormat.text(customer.getPhone()),
                    UiFormat.text(customer.getAddress()),
                    customer.getOrderCount()});
        }
        this.countLabel.setText(this.shownCustomers.size() + " pelanggan");
    }

    private boolean matches(Customer customer, String keyword) {
        return this.contains(customer.getName(), keyword)
                || this.contains(customer.getEmail(), keyword)
                || this.contains(customer.getPhone(), keyword);
    }

    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    private Customer selectedCustomer() {
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            UiComponents.showWarning(this, "Pilih satu pelanggan pada tabel terlebih dahulu.");
            return null;
        }
        int modelRow = this.table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= this.shownCustomers.size()) {
            return null;
        }
        return this.shownCustomers.get(modelRow);
    }

    private void addCustomer() {
        CustomerFormDialog dialog = new CustomerFormDialog(SwingUtilities.getWindowAncestor(this), this.customerService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            this.refresh();
        }
    }

    private void editCustomer() {
        Customer selected = this.selectedCustomer();
        if (selected == null) {
            return;
        }
        CustomerFormDialog dialog = new CustomerFormDialog(SwingUtilities.getWindowAncestor(this), this.customerService, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            this.refresh();
        }
    }

    private void showDetail() {
        Customer selected = this.selectedCustomer();
        if (selected == null) {
            return;
        }
        try {
            Customer customer = this.customerService.getCustomerById(selected.getId());
            UiComponents.showDetails(this, "Detail Pelanggan", customer.getName(), new String[][]{
                    {"ID", String.valueOf(customer.getId())},
                    {"Nama", customer.getName()},
                    {"Email", customer.getEmail()},
                    {"Telepon", UiFormat.text(customer.getPhone())},
                    {"Alamat", UiFormat.text(customer.getAddress())},
                    {"Jumlah pesanan", String.valueOf(customer.getOrderCount())}});
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
            this.refresh();
        }
    }

    private void deleteCustomer() {
        Customer selected = this.selectedCustomer();
        if (selected == null) {
            return;
        }
        if (!UiComponents.confirm(this, "Hapus pelanggan \"" + selected.getName() + "\"?")) {
            return;
        }
        try {
            this.customerService.deleteCustomer(selected.getId());
            UiComponents.showInfo(this, "Pelanggan berhasil dihapus.");
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
        this.refresh();
    }
}
