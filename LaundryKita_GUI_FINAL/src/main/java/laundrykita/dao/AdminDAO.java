package laundrykita.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import laundrykita.config.DatabaseConfig;
import laundrykita.model.AccountCredential;

public class AdminDAO {
    public AccountCredential findCredentialByEmail(String email) {
        String sql = "SELECT id_admin, name_admin, password_hash FROM admin WHERE email_admin = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery();){
                if (!rs.next()) {
                    return null;
                }
                return new AccountCredential(rs.getInt("id_admin"), rs.getString("name_admin"), rs.getString("password_hash"));
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data admin", e);
        }
    }
}
