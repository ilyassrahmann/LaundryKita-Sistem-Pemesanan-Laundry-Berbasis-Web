package laundrykita.gui.admin;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import laundrykita.gui.UiComponents;
import laundrykita.model.Service;
import laundrykita.service.CatalogService;

class ServiceFormDialog
extends JDialog {
    private final CatalogService catalogService;
    private final Service existing;
    private final JTextField nameField = UiComponents.textField(25, "Contoh: Cuci Kering");
    private final JTextField priceField = UiComponents.textField(25, "Contoh: 7000");
    private final JTextField categoryField = UiComponents.textField(25, "Contoh: Reguler");
    private final JTextField descriptionField = UiComponents.textField(25, "Deskripsi singkat");
    private boolean saved;

    ServiceFormDialog(Window owner, CatalogService catalogService, Service existing) {
        super(owner, existing == null ? "Tambah Layanan" : "Ubah Layanan", Dialog.ModalityType.APPLICATION_MODAL);
        this.catalogService = catalogService;
        this.existing = existing;

        JPanel form = UiComponents.form();
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Nama layanan *", this.nameField),
                UiComponents.fieldGroup("Harga per kg (Rp) *", this.priceField, "Angka bulat, contoh: 7000")));
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Kategori", this.categoryField),
                UiComponents.fieldGroup("Deskripsi", this.descriptionField)));

        if (existing != null) {
            this.nameField.setText(existing.getServiceType());
            this.priceField.setText(existing.getPricePerKg().toPlainString());
            this.categoryField.setText(existing.getCategory());
            this.descriptionField.setText(existing.getDescription());
        }

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(UiComponents.dialogHeader("Data layanan",
                existing == null ? "Tambahkan layanan beserta harga per kilogram."
                        : "Perbarui nama, harga, atau keterangan layanan."),
                BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);

        JButton saveButton = UiComponents.primary("Simpan");
        saveButton.addActionListener(e -> this.save());
        JButton cancelButton = UiComponents.secondary("Batal");
        cancelButton.addActionListener(e -> this.dispose());
        this.setContentPane(UiComponents.dialogBody(content, UiComponents.actions(cancelButton, saveButton)));
        this.getRootPane().setDefaultButton(saveButton);
        UiComponents.installEscape(this);
        this.pack();
        this.setLocationRelativeTo(owner);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                ServiceFormDialog.this.nameField.requestFocusInWindow();
            }
        });
    }

    boolean isSaved() {
        return this.saved;
    }

    private void save() {
        boolean nameMissing = this.nameField.getText().trim().isEmpty();
        UiComponents.setFieldError(this.nameField, nameMissing);
        if (nameMissing) {
            UiComponents.showWarning(this, "Nama layanan wajib diisi.");
            return;
        }
        BigDecimal price;
        try {
            price = new BigDecimal(this.priceField.getText().trim());
            UiComponents.setFieldError(this.priceField, false);
        } catch (NumberFormatException error) {
            UiComponents.setFieldError(this.priceField, true);
            UiComponents.showWarning(this, "Harga harus berupa angka bulat, contoh: 7000.");
            return;
        }
        Service service = new Service();
        service.setId(this.existing == null ? 0 : this.existing.getId());
        service.setServiceType(this.nameField.getText());
        service.setPricePerKg(price);
        service.setCategory(this.categoryField.getText());
        service.setDescription(this.descriptionField.getText());
        try {
            if (this.existing == null) {
                this.catalogService.createService(service);
            } else {
                this.catalogService.updateService(service);
            }
            this.saved = true;
            this.dispose();
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
    }
}
