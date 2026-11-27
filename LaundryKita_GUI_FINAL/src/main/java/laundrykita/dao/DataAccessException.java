package laundrykita.dao;

import java.sql.SQLException;

public class DataAccessException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final int DUPLICATE_ENTRY = 1062;
    public static final int FOREIGN_KEY_RESTRICT = 1451;
    private final int sqlErrorCode;

    public DataAccessException(String message, SQLException cause) {
        super(message, cause);
        this.sqlErrorCode = cause.getErrorCode();
    }

    public boolean isDuplicateEntry() {
        return this.sqlErrorCode == 1062;
    }

    public boolean isForeignKeyViolation() {
        return this.sqlErrorCode == 1451;
    }
}

