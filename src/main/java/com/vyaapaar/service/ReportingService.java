package com.vyaapaar.service;

import com.vyaapaar.dao.ReportingDao;
import com.vyaapaar.model.report.CategoryRevenueReport;
import com.vyaapaar.model.report.CustomerAnalysisReport;
import com.vyaapaar.model.report.CustomerSpendingReport;
import com.vyaapaar.model.report.MonthlySalesReport;
import com.vyaapaar.model.report.ProductSalesReport;
import com.vyaapaar.model.report.SalesSummaryReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing Reporting, Analytics, and SQL Aggregations over PostgreSQL.
 */
public class ReportingService {

    private final ReportingDao reportingDao;

    public ReportingService() {
        this.reportingDao = new ReportingDao();
    }

    public ReportingService(ReportingDao reportingDao) {
        this.reportingDao = reportingDao;
    }

    /**
     * Retrieves overall sales metrics.
     */
    public SalesSummaryReport getSalesSummary() {
        if (reportingDao == null) return null;
        return reportingDao.getSalesSummary();
    }

    /**
     * Retrieves revenue grouped by category.
     */
    public List<CategoryRevenueReport> getRevenueByCategory() {
        if (reportingDao == null) return new ArrayList<>();
        return reportingDao.getRevenueByCategory();
    }

    /**
     * Retrieves products ranked by sales quantity and revenue.
     */
    public List<ProductSalesReport> getTopSellingProducts() {
        if (reportingDao == null) return new ArrayList<>();
        return reportingDao.getTopSellingProducts();
    }

    /**
     * Retrieves high-spending customer ranking.
     */
    public List<CustomerSpendingReport> getTopCustomers() {
        if (reportingDao == null) return new ArrayList<>();
        return reportingDao.getTopCustomers();
    }

    /**
     * Retrieves monthly revenue aggregation.
     */
    public List<MonthlySalesReport> getMonthlySales() {
        if (reportingDao == null) return new ArrayList<>();
        return reportingDao.getMonthlySales();
    }

    /**
     * Retrieves detailed purchase analysis for a specific customer.
     *
     * @param customerId Customer ID to analyze
     * @return CustomerAnalysisReport or null
     * @throws IllegalArgumentException if ID is invalid
     */
    public CustomerAnalysisReport getCustomerPurchaseAnalysis(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID: " + customerId);
        }
        if (reportingDao == null) return null;
        return reportingDao.getCustomerPurchaseAnalysis(customerId);
    }

    /**
     * Retrieves customers who have not placed any orders.
     */
    public List<String> getInactiveCustomers() {
        if (reportingDao == null) return new ArrayList<>();
        return reportingDao.getInactiveCustomers();
    }
}
