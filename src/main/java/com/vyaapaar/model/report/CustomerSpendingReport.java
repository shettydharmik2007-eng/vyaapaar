package com.vyaapaar.model.report;

/**
 * DTO for Top Customers by Spending Report.
 */
public class CustomerSpendingReport {
    private int customerId;
    private String customerName;
    private String email;
    private int orderCount;
    private double totalSpent;
    private double avgOrderValue;

    public CustomerSpendingReport() {}

    public CustomerSpendingReport(int customerId, String customerName, String email, int orderCount, double totalSpent, double avgOrderValue) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
        this.avgOrderValue = avgOrderValue;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public double getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }
}
