/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

public enum PaymentMethod {
    TUNAI("Tunai"),
    TRANSFER("Transfer"),
    QRIS("QRIS");

    private final String label;

    private PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static PaymentMethod fromLabel(String label) {
        for (PaymentMethod m : PaymentMethod.values()) {
            if (!m.label.equals(label)) continue;
            return m;
        }
        throw new IllegalArgumentException("Metode pembayaran tidak dikenal: " + label);
    }

    public String toString() {
        return this.label;
    }
}

