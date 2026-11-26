package laundrykita.gui.customer;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import laundrykita.gui.UiComponents;
import laundrykita.gui.UiIcons;
import laundrykita.gui.UiTheme;
import laundrykita.model.Customer;
import laundrykita.model.UserSession;
import laundrykita.service.CustomerService;

class CustomerProfilePanel
extends JPanel {
    private final UserSession session;
    private final CustomerService customerService;
    private final Consumer<String> onNameChanged;
    private final JTextField nameField = UiComponents.textField(28, "Nama lengkap");
    private final JTextField emailField = UiComponents.textField(28, "nama@email.com");
    private final JTextField phoneField = UiComponents.textField(28, "Nomor telepon");
    private final JTextField addressField = UiComponents.textField(28, "Alamat");
    private final JPasswordField oldPasswordField = UiComponents.passwordField(20, "Password lama");
    private final JPasswordField newPasswordField = UiComponents.passwordField(20, "Password baru");
    private final JPasswordField confirmPasswordField = UiComponents.passwordField(20, "Ulangi password baru");

    CustomerProfilePanel(UserSession session, CustomerService customerService, Consumer<String> onNameChanged) {
        this.session = session;
        this.customerService = customerService;
        this.onNameChanged = onNameChanged;
        this.setLayout(new BorderLayout(0, 16));
        this.setBackground(UiTheme.BG);
        this.setBorder(new EmptyBorder(24, 28, 24, 28));

        this.add(UiComponents.header("Profil", "Perbarui data kontak dan kata sandi Anda."), BorderLayout.NORTH);

        JPanel content = UiComponents.form();
        content.add(this.profileCard());
        content.add(javax.swing.Box.createVerticalStrut(16));
        content.add(this.passwordCard());
        JPanel contentWrap = new JPanel(new BorderLayout());
        contentWrap.setOpaque(false);
        contentWrap.add(content, BorderLayout.NORTH);
        this.add(UiComponents.pageScroll(contentWrap), BorderLayout.CENTER);
    }

    private JPanel profileCard() {
        JPanel form = UiComponents.form();
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Nama", this.nameField),
                UiComponents.fieldGroup("Email", this.emailField)));
        form.add(UiComponents.columns(
                UiComponents.fieldGroup("Telepon", this.phoneField),
                UiComponents.fieldGroup("Alamat", this.addressField)));

        JButton save = UiComponents.primary("Simpan profil", UiIcons.icon("check", UiTheme.ON_ACCENT, 16));
        save.addActionListener(e -> this.saveProfile());

        JPanel card = UiComponents.card(new BorderLayout(0, 12));
        card.add(UiComponents.sectionTitle("Data profil"), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(UiComponents.actions(save), BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        card.setAlignmentX(LEFT_ALIGNMENT);
        return card;
    }

    private JPanel passwordCard() {
        JPanel form = UiComponents.columns(
                UiComponents.fieldGroup("Password lama", this.oldPasswordField),
                UiComponents.fieldGroup("Password baru", this.newPasswordField),
                UiComponents.fieldGroup("Konfirmasi password baru", this.confirmPasswordField));

        JButton change = UiComponents.primary("Ubah password", UiIcons.icon("check", UiTheme.ON_ACCENT, 16));
        change.addActionListener(e -> this.changePassword());

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.add(UiComponents.sectionTitle("Ganti password"), BorderLayout.WEST);
        cardHeader.add(UiComponents.hint("Minimal 6 karakter"), BorderLayout.EAST);

        JPanel card = UiComponents.card(new BorderLayout(0, 12));
        card.add(cardHeader, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(UiComponents.actions(change), BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        card.setAlignmentX(LEFT_ALIGNMENT);
        return card;
    }

    void refresh() {
        this.clearPasswords();
        try {
            Customer me = this.customerService.getCustomerById(this.session.getUserId());
            this.nameField.setText(me.getName());
            this.emailField.setText(me.getEmail());
            this.phoneField.setText(me.getPhone() == null ? "" : me.getPhone());
            this.addressField.setText(me.getAddress() == null ? "" : me.getAddress());
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
        }
    }

    private void saveProfile() {
        boolean nameMissing = this.nameField.getText().trim().isEmpty();
        boolean emailMissing = this.emailField.getText().trim().isEmpty();
        UiComponents.setFieldError(this.nameField, nameMissing);
        UiComponents.setFieldError(this.emailField, emailMissing);
        if (nameMissing || emailMissing) {
            UiComponents.showWarning(this, "Nama dan email wajib diisi.");
            return;
        }
        Customer edited = new Customer(this.session.getUserId(), this.nameField.getText(), this.emailField.getText(),
                this.phoneField.getText(), this.addressField.getText());
        try {
            this.customerService.updateOwnProfile(this.session.getUserId(), edited);
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
            return;
        }
        this.refresh();
        this.onNameChanged.accept(this.nameField.getText());
        UiComponents.showInfo(this, "Profil berhasil disimpan.");
    }

    private void changePassword() {
        if (this.oldPasswordField.getPassword().length == 0
                || this.newPasswordField.getPassword().length == 0
                || this.confirmPasswordField.getPassword().length == 0) {
            UiComponents.showWarning(this, "Isi password lama, password baru, dan konfirmasi password baru.");
            return;
        }
        char[] oldPassword = this.oldPasswordField.getPassword();
        char[] newPassword = this.newPasswordField.getPassword();
        char[] confirm = this.confirmPasswordField.getPassword();
        try {
            this.customerService.changeOwnPassword(this.session.getUserId(), oldPassword, newPassword, confirm);
        } catch (RuntimeException error) {
            UiComponents.showError(this, error);
            return;
        } finally {
            Arrays.fill(oldPassword, '\u0000');
            Arrays.fill(newPassword, '\u0000');
            Arrays.fill(confirm, '\u0000');
        }
        this.clearPasswords();
        UiComponents.showInfo(this, "Password berhasil diubah.");
    }

    private void clearPasswords() {
        this.oldPasswordField.setText("");
        this.newPasswordField.setText("");
        this.confirmPasswordField.setText("");
    }
}
