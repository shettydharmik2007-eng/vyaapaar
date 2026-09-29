package com.vyaapaar.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Customer Order.
 */
public class Order {
    private int orderId;
    private int userId;
    private double totalAmount;
    private String orderStatus; // "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    private String shippingAddress;
    private Timestamp orderDate;

    // Optional display and relationship helpers
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    // Constructor for creating a new order at checkout
    public Order(int userId, double totalAmount, String orderStatus, String shippingAddress) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
    }

    // Full constructor matching database table
    public Order(int orderId, int userId, double totalAmount, String orderStatus, String shippingAddress, Timestamp orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", orderDate=" + orderDate +
                (customerName != null ? ", customerName='" + customerName + '\'' : "") +
                ", itemCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
