package laundrykita.model;

public final class DashboardStats {
    private final int totalOrders;
    private final int waiting;
    private final int inProgress;
    private final int customers;

    public DashboardStats(int totalOrders, int waiting, int inProgress, int customers) {
        this.totalOrders = totalOrders;
        this.waiting = waiting;
        this.inProgress = inProgress;
        this.customers = customers;
    }

    public int getTotalOrders() {
        return this.totalOrders;
    }

    public int getWaiting() {
        return this.waiting;
    }

    public int getInProgress() {
        return this.inProgress;
    }

    public int getCustomers() {
        return this.customers;
    }
}

