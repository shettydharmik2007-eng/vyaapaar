package com.vyaapaar.model.report;

import java.sql.Timestamp;

/**
 * DTO for Individual Customer Purchase Analysis Report.
 */
public class CustomerAnalysisReport {
    private int customerId;
    private String customerName;
    private String email;
    private String city;
    private int orderCount;
    private double totalSpent;
    private double avgOrderValue;
    private Timestamp lastOrderDate;

    public CustomerAnalysisReport() {}

    public CustomerAnalysisReport(int customerId, String customerName, String email, String city, int orderCount, double totalSpent, double avgOrderValue, Timestamp lastOrderDate) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.city = city;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
        this.avgOrderValue = avgOrderValue;
        this.lastOrderDate = lastOrderDate;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public double getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }

    public Timestamp getLastOrderDate() { return lastOrderDate; }
    public void setLastOrderDate(Timestamp lastOrderDate) { this.lastOrderDate = lastOrderDate; }
}
