package com.vyaapaar.model;

import java.sql.Timestamp;

/**
 * Model representing an Item inside a Shopping Cart.
 */
public class CartItem {
    private int cartItemId;
    private int cartId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;

    // Optional display helper fields (from joined queries)
    private String productName;
    private double unitPrice;

    public CartItem() {}

    // Constructor for adding item to cart
    public CartItem(int cartId, int productId, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Full constructor matching database table
    public CartItem(int cartItemId, int cartId, int productId, int quantity, Timestamp addedAt) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                (productName != null ? ", productName='" + productName + '\'' : "") +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
