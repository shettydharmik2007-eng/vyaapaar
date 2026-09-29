package com.vyaapaar.model.report;

/**
 * DTO for High-Level Sales Summary Report.
 */
public class SalesSummaryReport {
    private int totalCustomers;
    private int totalProducts;
    private int totalOrders;
    private double totalRevenue;
    private double averageOrderValue;

    public SalesSummaryReport() {}

    public SalesSummaryReport(int totalCustomers, int totalProducts, int totalOrders, double totalRevenue, double averageOrderValue) {
        this.totalCustomers = totalCustomers;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
    }

    public int getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
}
