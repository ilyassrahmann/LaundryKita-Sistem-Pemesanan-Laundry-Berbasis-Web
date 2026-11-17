/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import laundrykita.dao.CustomerDAO;
import laundrykita.dao.DataAccessException;
import laundrykita.model.Customer;
import laundrykita.service.ServiceException;
import laundrykita.util.PasswordHasher;

public class CustomerService {
    private static final int MAX_NAME = 100;
    private static final int MAX_EMAIL = 100;
    private static final int MAX_PHONE = 20;
    private static final int MAX_ADDRESS = 255;
    private static final int MIN_PASSWORD = 6;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    static final String NOT_FOUND = "Pelanggan tidak ditemukan.";
    static final String ACCOUNT_NOT_FOUND = "Akun pelanggan tidak ditemukan.";
    static final String HAS_ORDERS = "Pelanggan tidak bisa dihapus karena sudah memiliki pesanan.";
    static final String PASSWORD_TOO_SHORT = "Password minimal 6 karakter.";
    private final CustomerDAO customerDAO;

    public CustomerService() {
        this(new CustomerDAO());
    }

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return this.customerDAO.findAll();
    }

    public Customer getCustomerById(int customerId) {
        Customer customer = this.customerDAO.findById(customerId);
        if (customer == null) {
            throw new ServiceException(NOT_FOUND);
        }
        return customer;
    }

    public void createCustomer(Customer customer, char[] password) {
        Customer clean = CustomerService.validated(customer);
        CustomerService.requirePassword(password);
        try {
            this.customerDAO.insert(clean, PasswordHasher.hash(password));
        }
        catch (DataAccessException e) {
            throw CustomerService.translateDuplicate(e, "Email sudah terdaftar.");
        }
    }

    public void updateCustomer(Customer customer, char[] newPasswordOrNull) {
        boolean changePassword;
        Customer clean = CustomerService.validated(customer);
        boolean bl = changePassword = newPasswordOrNull != null && newPasswordOrNull.length > 0;
        if (changePassword) {
            CustomerService.requirePassword(newPasswordOrNull);
        }
        if (this.customerDAO.findById(clean.getId()) == null) {
            throw new ServiceException(NOT_FOUND);
        }
        try {
            this.customerDAO.update(clean);
        }
        catch (DataAccessException e) {
            throw CustomerService.translateDuplicate(e, "Email sudah terdaftar.");
        }
        if (changePassword) {
            this.customerDAO.updatePasswordHash(clean.getId(), PasswordHasher.hash(newPasswordOrNull));
        }
    }

    public void deleteCustomer(int customerId) {
        Customer existing = this.customerDAO.findById(customerId);
        if (existing == null) {
            throw new ServiceException(NOT_FOUND);
        }
        if (existing.getOrderCount() > 0) {
            throw new ServiceException(HAS_ORDERS);
        }
        try {
            this.customerDAO.delete(customerId);
        }
        catch (DataAccessException e) {
            if (e.isForeignKeyViolation()) {
                throw new ServiceException(HAS_ORDERS, e);
            }
            throw e;
        }
    }

    public void updateOwnProfile(int authenticatedCustomerId, Customer customer) {
        Customer clean = CustomerService.validated(customer);
        clean.setId(authenticatedCustomerId);
        if (this.customerDAO.findById(authenticatedCustomerId) == null) {
            throw new ServiceException(ACCOUNT_NOT_FOUND);
        }
        try {
            this.customerDAO.update(clean);
        }
        catch (DataAccessException e) {
            throw CustomerService.translateDuplicate(e, "Email sudah dipakai akun lain.");
        }
    }

    public void changeOwnPassword(int authenticatedCustomerId, char[] oldPassword, char[] newPassword, char[] confirmPassword) {
        String storedHash = this.customerDAO.findPasswordHash(authenticatedCustomerId);
        if (storedHash == null) {
            throw new ServiceException(ACCOUNT_NOT_FOUND);
        }
        if (!PasswordHasher.verify(oldPassword, storedHash)) {
            throw new ServiceException("Password lama salah.");
        }
        if (newPassword == null || newPassword.length < 6) {
            throw new ServiceException("Password baru minimal 6 karakter.");
        }
        if (!Arrays.equals(newPassword, confirmPassword)) {
            throw new ServiceException("Konfirmasi password tidak sama.");
        }
        this.customerDAO.updatePasswordHash(authenticatedCustomerId, PasswordHasher.hash(newPassword));
    }

    private static void requirePassword(char[] password) {
        if (password == null || password.length < 6) {
            throw new ServiceException(PASSWORD_TOO_SHORT);
        }
    }

    private static RuntimeException translateDuplicate(DataAccessException e, String message) {
        return e.isDuplicateEntry() ? new ServiceException(message, e) : e;
    }

    private static Customer validated(Customer input) {
        String address;
        if (input == null) {
            throw new ServiceException("Data pelanggan tidak boleh kosong.");
        }
        String name = CustomerService.trimToNull(input.getName());
        if (name == null) {
            throw new ServiceException("Nama wajib diisi.");
        }
        CustomerService.requireMaxLength(name, 100, "Nama");
        String email = CustomerService.trimToNull(input.getEmail());
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new ServiceException("Email tidak valid.");
        }
        CustomerService.requireMaxLength(email, 100, "Email");
        String phone = CustomerService.trimToNull(input.getPhone());
        if (phone != null) {
            CustomerService.requireMaxLength(phone, 20, "Telepon");
        }
        if ((address = CustomerService.trimToNull(input.getAddress())) != null) {
            CustomerService.requireMaxLength(address, 255, "Alamat");
        }
        return new Customer(input.getId(), name, email, phone, address);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void requireMaxLength(String value, int max, String label) {
        if (value.codePointCount(0, value.length()) > max) {
            throw new ServiceException(label + " maksimal " + max + " karakter.");
        }
    }
}

