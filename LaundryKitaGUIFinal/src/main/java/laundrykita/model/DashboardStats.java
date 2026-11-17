/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

public final class DashboardStats {
    private final int totalOrders;
    private final int waiting;
    private final int inProgress;
    private final int finished;
    private final int customers;

    public DashboardStats(int totalOrders, int waiting, int inProgress, int finished, int customers) {
        this.totalOrders = totalOrders;
        this.waiting = waiting;
        this.inProgress = inProgress;
        this.finished = finished;
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

    public int getFinished() {
        return this.finished;
    }

    public int getCustomers() {
        return this.customers;
    }
}

