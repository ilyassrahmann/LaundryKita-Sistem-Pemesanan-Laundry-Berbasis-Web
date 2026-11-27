package laundrykita.gui.admin;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import laundrykita.gui.UiComponents;
import laundrykita.model.Customer;
import laundrykita.service.CustomerService;

class CustomerFormDialog
extends JDialog {
    private final CustomerService customerService;
    private final Customer existing;
    private final JTextField nameField = UiComponents.textField(25, "Nama lengkap");
    private final JTextField emailField = UiComponents.textField(25, "nama@email.com");
    private final JTextField phoneField = UiComponents.textField(25, "Nomor telepon");
    private final JTextField addressField = UiComponents.textField(25, "Alamat");
    private final JPasswordField passwordField = UiComponents.passwordField(25, "Minimal 6 karakter");
    private boolean saved;

    CustomerFormDialog(Window owner, CustomerService customerService, Customer existing) {
        super(owner, existing == null ? "Tambah Pelanggan" : "Ubah Pelanggan", Dialog.ModalityType.APPLICATION_MODAL);
        this.customerService = customerService;
        this.existing = existing;

        JPanel form = UiComponents.form();
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Nama *", this.nameField),
                UiComponents.fieldGroup("Email *", this.emailField)));
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Telepon", this.phoneField),
                UiComponents.fieldGroup("Alamat", this.addressField)));
        form.add(UiComponents.fieldGroup(existing == null ? "Password *" : "Password baru", this.passwordField,
                existing == null ? "Minimal 6 karakter" : "Kosongkan jika password tidak diganti"));

        if (existing != null) {
            this.nameField.setText(existing.getName());
            this.emailField.setText(existing.getEmail());
            this.phoneField.setText(existing.getPhone());
            this.addressField.setText(existing.getAddress());
        }

        JPanel content = new JPanel(new java.awt.BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(UiComponents.dialogHeader("Data pelanggan",
                existing == null ? "Lengkapi data pelanggan baru."
                        : "Perbarui data pelanggan. Kosongkan password jika tidak diganti."),
                java.awt.BorderLayout.NORTH);
        content.add(form, java.awt.BorderLayout.CENTER);

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
                CustomerFormDialog.this.nameField.requestFocusInWindow();
            }
        });
    }

    boolean isSaved() {
        return this.saved;
    }

    private void save() {
        boolean nameMissing = this.nameField.getText().trim().isEmpty();
        boolean emailMissing = this.emailField.getText().trim().isEmpty();
        UiComponents.setFieldError(this.nameField, nameMissing);
        UiComponents.setFieldError(this.emailField, emailMissing);
        if (nameMissing || emailMissing) {
            UiComponents.showWarning(this, "Nama dan email wajib diisi.");
            return;
        }
        Customer customer = new Customer();
        customer.setId(this.existing == null ? 0 : this.existing.getId());
        customer.setName(this.nameField.getText());
        customer.setEmail(this.emailField.getText());
        customer.setPhone(this.phoneField.getText());
        customer.setAddress(this.addressField.getText());
        char[] password = this.passwordField.getPassword();
        try {
            if (this.existing == null) {
                this.customerService.createCustomer(customer, password);
            } else {
                this.customerService.updateCustomer(customer, password);
            }
            this.saved = true;
            this.dispose();
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        } finally {
            Arrays.fill(password, '\u0000');
        }
    }
}
