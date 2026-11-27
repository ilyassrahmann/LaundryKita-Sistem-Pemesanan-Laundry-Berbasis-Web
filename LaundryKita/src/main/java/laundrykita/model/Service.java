/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.model;

import java.math.BigDecimal;

public class Service {
    private int id;
    private String serviceType;
    private BigDecimal pricePerKg;
    private String category;
    private String description;
    private int usageCount;

    public Service() {
    }

    public Service(int id, String serviceType, BigDecimal pricePerKg, String category, String description) {
        this.id = id;
        this.serviceType = serviceType;
        this.pricePerKg = pricePerKg;
        this.category = category;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getPricePerKg() {
        return this.pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUsageCount() {
        return this.usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public String toString() {
        return this.serviceType;
    }
}

