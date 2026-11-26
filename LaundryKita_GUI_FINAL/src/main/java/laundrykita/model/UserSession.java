package laundrykita.model;


public final class UserSession {
    private final Role role;
    private final int userId;
    private final String name;

    public UserSession(Role role, int userId, String name) {
        this.role = role;
        this.userId = userId;
        this.name = name;
    }

    public Role getRole() {
        return this.role;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}

