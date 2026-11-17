/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import laundrykita.config.DatabaseConfig;
import laundrykita.dao.DataAccessException;
import laundrykita.model.AccountCredential;
import laundrykita.model.Customer;

public class CustomerDAO {
    private static final String SELECT_WITH_COUNT = "SELECT c.id_customer, c.name_customer, c.email_customer, c.phone, c.address, COUNT(o.id_order) AS jml FROM customer c LEFT JOIN orders o ON o.id_customer = c.id_customer ";
    private static final String GROUP_BY = "GROUP BY c.id_customer, c.name_customer, c.email_customer, c.phone, c.address ";

    public List<Customer> findAll() {
        String sql = SELECT_WITH_COUNT + GROUP_BY + "ORDER BY c.name_customer";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            try (ResultSet rs = ps.executeQuery();){
                List<Customer> customers = new ArrayList<Customer>();
                while (rs.next()) {
                    customers.add(CustomerDAO.map(rs));
                }
                return customers;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data pelanggan", e);
        }
    }

    public Customer findById(int customerId) {
        String sql = SELECT_WITH_COUNT + "WHERE c.id_customer = ? " + GROUP_BY;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery();){
                return rs.next() ? CustomerDAO.map(rs) : null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data pelanggan", e);
        }
    }

    public AccountCredential findCredentialByEmail(String email) {
        String sql = "SELECT id_customer, name_customer, password_hash FROM customer WHERE email_customer = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery();){
                if (!rs.next()) {
                    return null;
                }
                return new AccountCredential(rs.getInt("id_customer"), rs.getString("name_customer"), rs.getString("password_hash"));
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data login", e);
        }
    }

    public String findPasswordHash(int customerId) {
        String sql = "SELECT password_hash FROM customer WHERE id_customer = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery();){
                return rs.next() ? rs.getString(1) : null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca password pelanggan", e);
        }
    }

    public int insert(Customer customer, String passwordHash) {
        String sql = "INSERT INTO customer (name_customer, email_customer, password_hash, phone, address) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, 1);){
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, passwordHash);
            CustomerDAO.setNullableString(ps, 4, customer.getPhone());
            CustomerDAO.setNullableString(ps, 5, customer.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys();){
                keys.next();
                return keys.getInt(1);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menambah pelanggan", e);
        }
    }

    public void update(Customer customer) {
        String sql = "UPDATE customer SET name_customer=?, email_customer=?, phone=?, address=? WHERE id_customer=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            CustomerDAO.setNullableString(ps, 3, customer.getPhone());
            CustomerDAO.setNullableString(ps, 4, customer.getAddress());
            ps.setInt(5, customer.getId());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah pelanggan", e);
        }
    }

    public void updatePasswordHash(int customerId, String passwordHash) {
        String sql = "UPDATE customer SET password_hash=? WHERE id_customer=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, passwordHash);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal mengganti password", e);
        }
    }

    public void delete(int customerId) {
        String sql = "DELETE FROM customer WHERE id_customer=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menghapus pelanggan", e);
        }
    }

    private static Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer(rs.getInt("id_customer"), rs.getString("name_customer"), rs.getString("email_customer"), rs.getString("phone"), rs.getString("address"));
        c.setOrderCount(rs.getInt("jml"));
        return c;
    }

    private static void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, value);
        }
    }
}
