package com.vyaapaar.model;

import java.sql.Timestamp;

/**
 * Model representing a Product in the catalog.
 */
public class Product {
    private int productId;
    private int categoryId;
    private String name;
    private String description;
    private double price;
    private Timestamp createdAt;

    // Optional display helper fields (from joined queries)
    private String categoryName;
    private int stockQuantity;

    public Product() {}

    // Constructor for creating a new product
    public Product(int categoryId, String name, String description, double price) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Full constructor matching database table
    public Product(int productId, int categoryId, String name, String description, double price, Timestamp createdAt) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                (categoryName != null ? ", categoryName='" + categoryName + '\'' : "") +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
