package com.vyaapaar.model.report;

/**
 * DTO for Top Selling Products Report.
 */
public class ProductSalesReport {
    private String productName;
    private String categoryName;
    private int quantitySold;
    private double revenueGenerated;

    public ProductSalesReport() {}

    public ProductSalesReport(String productName, String categoryName, int quantitySold, double revenueGenerated) {
        this.productName = productName;
        this.categoryName = categoryName;
        this.quantitySold = quantitySold;
        this.revenueGenerated = revenueGenerated;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }

    public double getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(double revenueGenerated) { this.revenueGenerated = revenueGenerated; }
}
