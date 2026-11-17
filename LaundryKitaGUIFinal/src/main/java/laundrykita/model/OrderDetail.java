/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderDetail {
    private int id;
    private int orderId;
    private int serviceId;
    private String serviceType;
    private BigDecimal weightKg;
    private BigDecimal pricePerKg;
    private BigDecimal subtotal;

    public OrderDetail() {
    }

    public OrderDetail(int serviceId, BigDecimal weightKg, BigDecimal pricePerKg) {
        this.serviceId = serviceId;
        this.weightKg = weightKg;
        this.pricePerKg = pricePerKg;
        this.subtotal = OrderDetail.calculateSubtotal(weightKg, pricePerKg);
    }

    public static BigDecimal calculateSubtotal(BigDecimal weightKg, BigDecimal pricePerKg) {
        return weightKg.multiply(pricePerKg).setScale(0, RoundingMode.HALF_UP);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getWeightKg() {
        return this.weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getPricePerKg() {
        return this.pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}

