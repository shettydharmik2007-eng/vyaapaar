package com.vyaapaar.model;

import java.sql.Timestamp;

/**
 * Model representing Inventory stock for a Product.
 */
public class Inventory {
    private int inventoryId;
    private int productId;
    private int stockQuantity;
    private Timestamp lastUpdated;

    // Optional display helper
    private String productName;

    public Inventory() {}

    // Constructor for creating an initial inventory record
    public Inventory(int productId, int stockQuantity) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }

    // Full constructor matching database table
    public Inventory(int inventoryId, int productId, int stockQuantity, Timestamp lastUpdated) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.stockQuantity = stockQuantity;
        this.lastUpdated = lastUpdated;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", productId=" + productId +
                ", stockQuantity=" + stockQuantity +
                ", lastUpdated=" + lastUpdated +
                (productName != null ? ", productName='" + productName + '\'' : "") +
                '}';
    }
}
