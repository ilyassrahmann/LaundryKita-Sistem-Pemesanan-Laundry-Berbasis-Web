package laundrykita.service;

import laundrykita.dao.AdminDAO;
import laundrykita.dao.CustomerDAO;
import laundrykita.model.AccountCredential;
import laundrykita.model.Role;
import laundrykita.model.UserSession;
import laundrykita.util.PasswordHasher;

public class AuthService {
    static final String INVALID_LOGIN = "Email atau password tidak valid.";
    private final AdminDAO adminDAO;
    private final CustomerDAO customerDAO;

    public AuthService() {
        this(new AdminDAO(), new CustomerDAO());
    }

    public AuthService(AdminDAO adminDAO, CustomerDAO customerDAO) {
        this.adminDAO = adminDAO;
        this.customerDAO = customerDAO;
    }

    public UserSession login(String email, char[] password) throws AuthenticationException {
        if (email == null || email.trim().isEmpty() || password == null) {
            throw new AuthenticationException(INVALID_LOGIN);
        }
        String cleanEmail = email.trim();
        AccountCredential admin = this.adminDAO.findCredentialByEmail(cleanEmail);
        if (admin != null && PasswordHasher.verify(password, admin.getPasswordHash())) {
            return new UserSession(Role.ADMIN, admin.getId(), admin.getName());
        }
        AccountCredential customer = this.customerDAO.findCredentialByEmail(cleanEmail);
        if (customer != null && PasswordHasher.verify(password, customer.getPasswordHash())) {
            return new UserSession(Role.CUSTOMER, customer.getId(), customer.getName());
        }
        throw new AuthenticationException(INVALID_LOGIN);
    }
}

