package laundrykita.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import laundrykita.config.DatabaseConfig;
import laundrykita.model.Service;

public class ServiceDAO {
    private static final String SELECT_WITH_USAGE = "SELECT s.id_service, s.service_type, s.price_per_kg, s.category, s.description, COUNT(d.id_order_detail) AS dipakai FROM service s LEFT JOIN order_detail d ON d.id_service = s.id_service ";
    private static final String GROUP_BY = "GROUP BY s.id_service, s.service_type, s.price_per_kg, s.category, s.description ";

    public List<Service> findAll() {
        String sql = SELECT_WITH_USAGE + GROUP_BY + "ORDER BY s.id_service";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            try (ResultSet rs = ps.executeQuery();){
                List<Service> services = new ArrayList<Service>();
                while (rs.next()) {
                    services.add(ServiceDAO.map(rs));
                }
                return services;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data layanan", e);
        }
    }

    public Service findById(int serviceId) {
        String sql = SELECT_WITH_USAGE + "WHERE s.id_service = ? " + GROUP_BY;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery();){
                return rs.next() ? ServiceDAO.map(rs) : null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data layanan", e);
        }
    }

    public int insert(Service service) {
        String sql = "INSERT INTO service (service_type, price_per_kg, category, description) VALUES (?,?,?,?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, 1);){
            ps.setString(1, service.getServiceType());
            ps.setLong(2, service.getPricePerKg().longValueExact());
            ServiceDAO.setNullableString(ps, 3, service.getCategory());
            ServiceDAO.setNullableString(ps, 4, service.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys();){
                keys.next();
                return keys.getInt(1);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menambah layanan", e);
        }
    }

    public void update(Service service) {
        String sql = "UPDATE service SET service_type=?, price_per_kg=?, category=?, description=? WHERE id_service=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, service.getServiceType());
            ps.setLong(2, service.getPricePerKg().longValueExact());
            ServiceDAO.setNullableString(ps, 3, service.getCategory());
            ServiceDAO.setNullableString(ps, 4, service.getDescription());
            ps.setInt(5, service.getId());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah layanan", e);
        }
    }

    public void delete(int serviceId) {
        String sql = "DELETE FROM service WHERE id_service=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menghapus layanan", e);
        }
    }

    private static Service map(ResultSet rs) throws SQLException {
        Service s = new Service(rs.getInt("id_service"), rs.getString("service_type"), BigDecimal.valueOf(rs.getLong("price_per_kg")), rs.getString("category"), rs.getString("description"));
        s.setUsageCount(rs.getInt("dipakai"));
        return s;
    }

    private static void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, value);
        }
    }
}
