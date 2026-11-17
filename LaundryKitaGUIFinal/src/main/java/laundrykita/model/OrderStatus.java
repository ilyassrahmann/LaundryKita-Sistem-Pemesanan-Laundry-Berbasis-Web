/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

public enum OrderStatus {
    MENUNGGU("Menunggu"),
    DIPROSES("Diproses"),
    DICUCI("Dicuci"),
    DISETRIKA("Disetrika"),
    SELESAI("Selesai");

    private final String label;

    private OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static OrderStatus fromLabel(String label) {
        for (OrderStatus s : OrderStatus.values()) {
            if (!s.label.equals(label)) continue;
            return s;
        }
        throw new IllegalArgumentException("Status pesanan tidak dikenal: " + label);
    }

    public String toString() {
        return this.label;
    }
}

