package com.vyaapaar.model.report;

/**
 * DTO for Revenue by Category Report.
 */
public class CategoryRevenueReport {
    private String categoryName;
    private double totalRevenue;
    private int totalQuantitySold;

    public CategoryRevenueReport() {}

    public CategoryRevenueReport(String categoryName, double totalRevenue, int totalQuantitySold) {
        this.categoryName = categoryName;
        this.totalRevenue = totalRevenue;
        this.totalQuantitySold = totalQuantitySold;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalQuantitySold() { return totalQuantitySold; }
    public void setTotalQuantitySold(int totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }
}
