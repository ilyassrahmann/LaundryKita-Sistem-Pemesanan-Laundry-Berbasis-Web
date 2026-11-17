package laundrykita.gui.admin;

import java.awt.BorderLayout;
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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiFormat;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Service;
import laundrykita.service.CatalogService;

class AdminServicesPanel
extends JPanel {
    private static final String[] COLUMNS = {"ID", "Layanan", "Kategori", "Harga per kg", "Deskripsi", "Dipakai"};
    private final CatalogService catalogService;
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = UiComponents.table(new JTable(this.tableModel));
    private final JLabel countLabel = UiComponents.muted("");
    private List<Service> services = new ArrayList<>();

    AdminServicesPanel(CatalogService catalogService) {
        this.catalogService = catalogService;
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        JButton addButton = UiComponents.primary("Tambah layanan", UiIcons.icon("plus", UiTheme.ON_ACCENT, 16));
        addButton.addActionListener(e -> this.addService());
        this.add(UiComponents.header("Layanan",
                "Atur daftar layanan dan harga per kilogram.", addButton), BorderLayout.NORTH);

        this.table.removeColumn(this.table.getColumnModel().getColumn(0));
        UiComponents.sizeColumns(this.table, 220, 150, 140, 300, 100);
        this.table.getColumnModel().getColumn(2).setCellRenderer(UiComponents.rightRenderer());
        this.table.getColumnModel().getColumn(4).setCellRenderer(UiComponents.centerRenderer());
        UiComponents.enableSorting(this.table, 3, 5);
        JButton deleteButton = UiComponents.danger("Hapus");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> this.deleteService());
        JButton editButton = UiComponents.secondary("Ubah");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> this.editService());
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            boolean hasSelection = this.table.getSelectedRow() >= 0;
            deleteButton.setEnabled(hasSelection);
            editButton.setEnabled(hasSelection);
        });
        this.table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editService");
        this.table.getActionMap().put("editService", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminServicesPanel.this.editService();
            }
        });
        JPanel footer = UiComponents.tableFooter(this.countLabel, deleteButton, editButton);
        this.add(UiComponents.tableCard(this.table, "Belum ada layanan",
                "Tambahkan layanan lewat tombol Tambah layanan agar bisa dipilih saat mencatat pesanan.",
                footer), BorderLayout.CENTER);
    }

    void refresh() {
        UiComponents.withBusy(this, () -> {
            try {
                this.services = this.catalogService.getAllServices();
            } catch (RuntimeException error) {
                this.services = new ArrayList<>();
                UiComponents.showError(this, error);
            }
            this.tableModel.setRowCount(0);
            for (Service service : this.services) {
                this.tableModel.addRow(new Object[]{
                        service.getId(),
                        service.getServiceType(),
                        UiFormat.text(service.getCategory()),
                        UiFormat.rupiah(service.getPricePerKg()),
                        UiFormat.text(service.getDescription()),
                        service.getUsageCount()});
            }
            this.countLabel.setText(this.services.size() + " layanan");
        });
    }

    private Service selectedService() {
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            UiComponents.showWarning(this, "Pilih satu layanan pada tabel terlebih dahulu.");
            return null;
        }
        int modelRow = this.table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= this.services.size()) {
            return null;
        }
        return this.services.get(modelRow);
    }

    private void addService() {
        ServiceFormDialog dialog = new ServiceFormDialog(SwingUtilities.getWindowAncestor(this), this.catalogService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            this.refresh();
        }
    }

    private void editService() {
        Service selected = this.selectedService();
        if (selected == null) {
            return;
        }
        ServiceFormDialog dialog = new ServiceFormDialog(SwingUtilities.getWindowAncestor(this), this.catalogService, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            this.refresh();
        }
    }

    private void deleteService() {
        Service selected = this.selectedService();
        if (selected == null) {
            return;
        }
        if (!UiComponents.confirm(this, "Hapus layanan \"" + selected.getServiceType() + "\"?")) {
            return;
        }
        try {
            this.catalogService.deleteService(selected.getId());
            UiComponents.showInfo(this, "Layanan berhasil dihapus.");
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
        this.refresh();
    }
}
