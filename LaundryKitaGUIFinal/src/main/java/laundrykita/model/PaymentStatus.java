/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

public enum PaymentStatus {
    BELUM_BAYAR("Belum Bayar"),
    LUNAS("Lunas");

    private final String label;

    private PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static PaymentStatus fromLabel(String label) {
        for (PaymentStatus s : PaymentStatus.values()) {
            if (!s.label.equals(label)) continue;
            return s;
        }
        throw new IllegalArgumentException("Status pembayaran tidak dikenal: " + label);
    }

    public String toString() {
        return this.label;
    }
}

