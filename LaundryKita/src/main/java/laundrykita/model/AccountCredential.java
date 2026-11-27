/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

public final class AccountCredential {
    private final int id;
    private final String name;
    private final String passwordHash;

    public AccountCredential(int id, String name, String passwordHash) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }
}

