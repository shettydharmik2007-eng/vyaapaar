package com.vyaapaar.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a User's Shopping Cart.
 */
public class Cart {
    private int cartId;
    private int userId;
    private Timestamp createdAt;

    // Optional list of items in the cart
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    // Constructor for creating a new user's cart
    public Cart(int userId) {
        this.userId = userId;
    }

    // Full constructor matching database table
    public Cart(int cartId, int userId, Timestamp createdAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", itemCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
