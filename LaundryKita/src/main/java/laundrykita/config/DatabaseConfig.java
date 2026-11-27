/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {
    private static final String FILE_NAME = "database.properties";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/laundrykita?useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final Properties FILE_SETTINGS = DatabaseConfig.loadFile();

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.setting("db.url", DEFAULT_URL), DatabaseConfig.setting("db.user", DEFAULT_USER), DatabaseConfig.setting("db.password", DEFAULT_PASSWORD));
    }

    private static String setting(String key, String defaultValue) {
        String value = System.getProperty("laundrykita." + key);
        if (value == null) {
            value = System.getenv("LAUNDRYKITA_" + key.toUpperCase().replace('.', '_'));
        }
        if (value == null) {
            value = FILE_SETTINGS.getProperty(key);
        }
        return value != null ? value : defaultValue;
    }

    private static Properties loadFile() {
        Properties props = new Properties();
        Path path = Path.of(FILE_NAME, new String[0]);
        if (Files.isRegularFile(path, new LinkOption[0])) {
            try (InputStream in = Files.newInputStream(path, new OpenOption[0]);){
                props.load(in);
            }
            catch (IOException e) {
                throw new IllegalStateException("Gagal membaca database.properties", e);
            }
        }
        return props;
    }
}

