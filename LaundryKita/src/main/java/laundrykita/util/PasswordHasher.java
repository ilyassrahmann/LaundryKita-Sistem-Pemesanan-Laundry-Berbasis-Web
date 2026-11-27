/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mindrot.jbcrypt.BCrypt
 */
package laundrykita.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {
    private static final int COST = 10;

    private PasswordHasher() {
    }

    public static boolean verify(char[] password, String storedHash) {
        if (password == null || storedHash == null || !storedHash.startsWith("$2")) {
            return false;
        }
        String hash = storedHash.startsWith("$2y$") ? "$2a$" + storedHash.substring(4) : storedHash;
        try {
            return BCrypt.checkpw((String)new String(password), (String)hash);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String hash(char[] password) {
        return BCrypt.hashpw((String)new String(password), (String)BCrypt.gensalt((int)10));
    }
}

