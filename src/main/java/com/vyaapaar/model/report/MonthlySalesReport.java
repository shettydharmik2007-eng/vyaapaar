package com.vyaapaar.model.report;

/**
 * DTO for Monthly Sales Aggregation Report.
 */
public class MonthlySalesReport {
    private String month;
    private int orderCount;
    private double totalRevenue;

    public MonthlySalesReport() {}

    public MonthlySalesReport(String month, int orderCount, double totalRevenue) {
        this.month = month;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
