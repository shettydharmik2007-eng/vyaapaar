package com.vyaapaar.ui;

import com.vyaapaar.model.Category;
import com.vyaapaar.model.Inventory;
import com.vyaapaar.model.Order;
import com.vyaapaar.model.OrderItem;
import com.vyaapaar.model.Product;
import com.vyaapaar.model.User;
import com.vyaapaar.model.report.CategoryRevenueReport;
import com.vyaapaar.model.report.CustomerAnalysisReport;
import com.vyaapaar.model.report.CustomerSpendingReport;
import com.vyaapaar.model.report.MonthlySalesReport;
import com.vyaapaar.model.report.ProductSalesReport;
import com.vyaapaar.model.report.SalesSummaryReport;
import com.vyaapaar.service.CategoryService;
import com.vyaapaar.service.InventoryService;
import com.vyaapaar.service.OrderService;
import com.vyaapaar.service.ProductService;
import com.vyaapaar.service.ReportingService;

import java.util.List;

/**
 * Interactive Console Menu for Administrator management operations.
 */
public class AdminMenu {

    private final User adminUser;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final ReportingService reportingService;

    public AdminMenu(User adminUser) {
        this.adminUser = adminUser;
        this.categoryService = new CategoryService();
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        this.orderService = new OrderService();
        this.reportingService = new ReportingService();
    }

    public void show() {
        boolean active = true;
        while (active) {
            ConsoleUtils.printHeader("ADMIN DASHBOARD | Logged in as: " + adminUser.getFullName());
            System.out.println("1. Manage Categories");
            System.out.println("2. Manage Products");
            System.out.println("3. Manage Inventory");
            System.out.println("4. View Orders");
            System.out.println("5. Update Order Status");
            System.out.println("6. PostgreSQL Analytics & Reports");
            System.out.println("7. Logout");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select an option (1-7): ");

            switch (choice) {
                case 1 -> manageCategories();
                case 2 -> manageProducts();
                case 3 -> manageInventory();
                case 4 -> viewAllOrders();
                case 5 -> updateOrderStatus();
                case 6 -> openReportingMenu();
                case 7 -> {
                    ConsoleUtils.printSuccess("Admin logged out successfully.");
                    active = false;
                }
                default -> ConsoleUtils.printError("Invalid option! Please select between 1 and 7.");
            }
        }
    }

    // ==========================================
    // 1. MANAGE CATEGORIES
    // ==========================================
    private void manageCategories() {
        boolean inMenu = true;
        while (inMenu) {
            ConsoleUtils.printHeader("MANAGE CATEGORIES");
            System.out.println("1. Add Category");
            System.out.println("2. View Categories");
            System.out.println("3. Update Category");
            System.out.println("4. Delete Category");
            System.out.println("5. Back to Admin Menu");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select option (1-5): ");
            switch (choice) {
                case 1 -> {
                    String name = ConsoleUtils.readString("Enter category name: ");
                    String desc = ConsoleUtils.readOptionalString("Enter category description: ");
                    try {
                        Category cat = categoryService.addCategory(name, desc);
                        ConsoleUtils.printSuccess("Category created successfully with ID: " + cat.getCategoryId());
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 2 -> displayCategories();
                case 3 -> {
                    displayCategories();
                    int catId = ConsoleUtils.readPositiveInt("Enter Category ID to update: ");
                    String newName = ConsoleUtils.readString("Enter new category name: ");
                    String newDesc = ConsoleUtils.readOptionalString("Enter new description: ");
                    try {
                        boolean updated = categoryService.updateCategory(catId, newName, newDesc);
                        if (updated) ConsoleUtils.printSuccess("Category updated successfully!");
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 4 -> {
                    displayCategories();
                    int catId = ConsoleUtils.readPositiveInt("Enter Category ID to delete: ");
                    boolean confirm = ConsoleUtils.readConfirmation("Are you sure you want to delete Category #" + catId + "? (Y/N): ");
                    if (confirm) {
                        try {
                            boolean deleted = categoryService.deleteCategory(catId);
                            if (deleted) ConsoleUtils.printSuccess("Category deleted successfully!");
                        } catch (Exception e) {
                            ConsoleUtils.printError("Cannot delete category: " + e.getMessage());
                        }
                    }
                }
                case 5 -> inMenu = false;
                default -> ConsoleUtils.printError("Invalid choice.");
            }
        }
    }

    private void displayCategories() {
        List<Category> list = categoryService.getAllCategories();
        if (list.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        System.out.printf("%-6s | %-25s | %-30s%n", "ID", "Category Name", "Description");
        ConsoleUtils.printDivider();
        for (Category c : list) {
            System.out.printf("%-6d | %-25s | %-30s%n", c.getCategoryId(), c.getCategoryName(), c.getDescription());
        }
        ConsoleUtils.printDivider();
    }

    // ==========================================
    // 2. MANAGE PRODUCTS
    // ==========================================
    private void manageProducts() {
        boolean inMenu = true;
        while (inMenu) {
            ConsoleUtils.printHeader("MANAGE PRODUCTS");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Search Products");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Back to Admin Menu");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select option (1-6): ");
            switch (choice) {
                case 1 -> {
                    displayCategories();
                    int catId = ConsoleUtils.readPositiveInt("Enter Category ID: ");
                    String name = ConsoleUtils.readString("Enter Product Name: ");
                    String desc = ConsoleUtils.readOptionalString("Enter Description: ");
                    double price = ConsoleUtils.readPositiveDouble("Enter Unit Price (₹): ");
                    int initialStock = ConsoleUtils.readNonNegativeInt("Enter Initial Stock Quantity: ");
                    try {
                        Product p = productService.addProduct(catId, name, desc, price, initialStock);
                        ConsoleUtils.printSuccess("Product added successfully with ID: " + p.getProductId());
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 2 -> displayProductsTable(productService.getAllProducts());
                case 3 -> {
                    String kw = ConsoleUtils.readString("Enter search keyword: ");
                    displayProductsTable(productService.searchProducts(kw));
                }
                case 4 -> {
                    displayProductsTable(productService.getAllProducts());
                    int prodId = ConsoleUtils.readPositiveInt("Enter Product ID to update: ");
                    displayCategories();
                    int newCatId = ConsoleUtils.readPositiveInt("Enter new Category ID: ");
                    String newName = ConsoleUtils.readString("Enter new Product Name: ");
                    String newDesc = ConsoleUtils.readOptionalString("Enter new Description: ");
                    double newPrice = ConsoleUtils.readPositiveDouble("Enter new Price (₹): ");
                    try {
                        boolean updated = productService.updateProduct(prodId, newCatId, newName, newDesc, newPrice);
                        if (updated) ConsoleUtils.printSuccess("Product updated successfully!");
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 5 -> {
                    displayProductsTable(productService.getAllProducts());
                    int prodId = ConsoleUtils.readPositiveInt("Enter Product ID to delete: ");
                    boolean confirm = ConsoleUtils.readConfirmation("Are you sure you want to delete Product #" + prodId + "? (Y/N): ");
                    if (confirm) {
                        try {
                            boolean deleted = productService.deleteProduct(prodId);
                            if (deleted) ConsoleUtils.printSuccess("Product deleted successfully!");
                        } catch (Exception e) {
                            ConsoleUtils.printError("Cannot delete product: " + e.getMessage());
                        }
                    }
                }
                case 6 -> inMenu = false;
                default -> ConsoleUtils.printError("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 3. MANAGE INVENTORY
    // ==========================================
    private void manageInventory() {
        boolean inMenu = true;
        while (inMenu) {
            ConsoleUtils.printHeader("MANAGE INVENTORY");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Inventory for Product");
            System.out.println("3. Update Stock Level");
            System.out.println("4. View Low Stock Products");
            System.out.println("5. Back to Admin Menu");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select option (1-5): ");
            switch (choice) {
                case 1 -> displayInventoryTable(inventoryService.getAllInventory());
                case 2 -> {
                    displayProductsTable(productService.getAllProducts());
                    int prodId = ConsoleUtils.readPositiveInt("Enter Product ID: ");
                    int stock = ConsoleUtils.readNonNegativeInt("Enter Stock Quantity: ");
                    try {
                        boolean added = inventoryService.addInventory(prodId, stock);
                        if (added) ConsoleUtils.printSuccess("Inventory record saved!");
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 3 -> {
                    displayInventoryTable(inventoryService.getAllInventory());
                    int prodId = ConsoleUtils.readPositiveInt("Enter Product ID to update stock: ");
                    int newStock = ConsoleUtils.readNonNegativeInt("Enter New Stock Quantity: ");
                    try {
                        boolean updated = inventoryService.updateStock(prodId, newStock);
                        if (updated) ConsoleUtils.printSuccess("Stock updated successfully!");
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                }
                case 4 -> {
                    int threshold = ConsoleUtils.readNonNegativeInt("Enter low-stock threshold limit (e.g., 20): ");
                    List<Inventory> lowStock = inventoryService.getLowStockProducts(threshold);
                    if (lowStock.isEmpty()) {
                        System.out.println("No products found below stock threshold: " + threshold);
                    } else {
                        displayInventoryTable(lowStock);
                    }
                }
                case 5 -> inMenu = false;
                default -> ConsoleUtils.printError("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 4. VIEW ALL ORDERS
    // ==========================================
    private void viewAllOrders() {
        ConsoleUtils.printHeader("ALL CUSTOMER ORDERS");
        try {
            List<Order> orders = orderService.getAllOrders();
            if (orders.isEmpty()) {
                System.out.println("No orders have been placed yet.");
                return;
            }

            for (Order o : orders) {
                ConsoleUtils.printDivider();
                System.out.printf("Order #%-5d | Customer: %-18s (%s)%n",
                        o.getOrderId(),
                        o.getCustomerName() != null ? o.getCustomerName() : "User #" + o.getUserId(),
                        o.getCustomerEmail() != null ? o.getCustomerEmail() : "");
                System.out.printf("Date: %-19s | Status: %-10s | Total: ₹%.2f%n",
                        o.getOrderDate() != null ? o.getOrderDate().toString().substring(0, 19) : "N/A",
                        o.getOrderStatus(),
                        o.getTotalAmount());
                System.out.println("Delivery Address: " + o.getShippingAddress());

                if (o.getItems() != null && !o.getItems().isEmpty()) {
                    System.out.println("Items:");
                    for (OrderItem item : o.getItems()) {
                        System.out.printf("   - %-25s x %-3d @ ₹%-8.2f = ₹%.2f%n",
                                item.getProductName() != null ? item.getProductName() : "Product #" + item.getProductId(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal());
                    }
                }
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Error fetching orders: " + e.getMessage());
        }
    }

    // ==========================================
    // 5. UPDATE ORDER STATUS
    // ==========================================
    private void updateOrderStatus() {
        ConsoleUtils.printHeader("UPDATE ORDER STATUS");
        viewAllOrders();

        int orderId = ConsoleUtils.readPositiveInt("Enter Order ID to update: ");
        System.out.println("Allowed Statuses: [PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED]");
        String newStatus = ConsoleUtils.readString("Enter new status: ");

        try {
            boolean updated = orderService.updateOrderStatus(orderId, newStatus);
            if (updated) {
                ConsoleUtils.printSuccess("Order #" + orderId + " status updated to " + newStatus.toUpperCase() + "!");
            }
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void displayProductsTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.printf("%-6s | %-28s | %-18s | %-10s | %-8s%n",
                "ID", "Product Name", "Category", "Price", "Stock");
        ConsoleUtils.printDivider();

        for (Product p : products) {
            System.out.printf("%-6d | %-28s | %-18s | ₹%-9.2f | %-8d%n",
                    p.getProductId(),
                    truncate(p.getName(), 28),
                    truncate(p.getCategoryName() != null ? p.getCategoryName() : "General", 18),
                    p.getPrice(),
                    p.getStockQuantity());
        }
        ConsoleUtils.printDivider();
    }

    private void displayInventoryTable(List<Inventory> list) {
        if (list.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        System.out.printf("%-8s | %-10s | %-28s | %-12s%n",
                "Inv ID", "Prod ID", "Product Name", "Stock Qty");
        ConsoleUtils.printDivider();

        for (Inventory i : list) {
            System.out.printf("%-8d | %-10d | %-28s | %-12d%n",
                    i.getInventoryId(),
                    i.getProductId(),
                    truncate(i.getProductName() != null ? i.getProductName() : "Product #" + i.getProductId(), 28),
                    i.getStockQuantity());
        }
        ConsoleUtils.printDivider();
    }

    // ==========================================
    // 6. POSTGRESQL ANALYTICS & REPORTING
    // ==========================================
    private void openReportingMenu() {
        boolean inReporting = true;
        while (inReporting) {
            ConsoleUtils.printHeader("POSTGRESQL ANALYTICS & ADVANCED SQL REPORTS");
            System.out.println("1. Sales Summary");
            System.out.println("2. Revenue by Category");
            System.out.println("3. Top Selling Products");
            System.out.println("4. Top Customers");
            System.out.println("5. Monthly Sales");
            System.out.println("6. Customer Purchase Analysis");
            System.out.println("7. Inactive Customers (Subquery)");
            System.out.println("8. Back to Admin Menu");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select report option (1-8): ");
            switch (choice) {
                case 1 -> showSalesSummaryReport();
                case 2 -> showRevenueByCategoryReport();
                case 3 -> showTopSellingProductsReport();
                case 4 -> showTopCustomersReport();
                case 5 -> showMonthlySalesReport();
                case 6 -> showCustomerAnalysisReport();
                case 7 -> showInactiveCustomersReport();
                case 8 -> inReporting = false;
                default -> ConsoleUtils.printError("Invalid choice! Please select 1-8.");
            }
        }
    }

    private void showSalesSummaryReport() {
        ConsoleUtils.printHeader("EXECUTIVE SALES SUMMARY (PostgreSQL)");
        try {
            SalesSummaryReport report = reportingService.getSalesSummary();
            if (report == null) {
                ConsoleUtils.printError("PostgreSQL reporting database is unavailable or returned empty data.");
                return;
            }

            System.out.printf("Total Customers     : %d%n", report.getTotalCustomers());
            System.out.printf("Total Products      : %d%n", report.getTotalProducts());
            System.out.printf("Total Orders Placed : %d%n", report.getTotalOrders());
            System.out.printf("Total Revenue       : ₹%.2f%n", report.getTotalRevenue());
            System.out.printf("Average Order Value : ₹%.2f%n", report.getAverageOrderValue());
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private void showRevenueByCategoryReport() {
        ConsoleUtils.printHeader("REVENUE BY CATEGORY (PostgreSQL Multi-table JOIN & GROUP BY)");
        try {
            List<CategoryRevenueReport> list = reportingService.getRevenueByCategory();
            if (list.isEmpty()) {
                System.out.println("No category sales data found in PostgreSQL database.");
                return;
            }

            System.out.printf("%-28s | %-12s | %-15s%n", "Category Name", "Units Sold", "Total Revenue");
            ConsoleUtils.printDivider();
            for (CategoryRevenueReport r : list) {
                System.out.printf("%-28s | %-12d | ₹%-14.2f%n",
                        r.getCategoryName(), r.getTotalQuantitySold(), r.getTotalRevenue());
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private void showTopSellingProductsReport() {
        ConsoleUtils.printHeader("TOP SELLING PRODUCTS (PostgreSQL Aggregate SUM & Multi-Column ORDER BY)");
        try {
            List<ProductSalesReport> list = reportingService.getTopSellingProducts();
            if (list.isEmpty()) {
                System.out.println("No product sales data found.");
                return;
            }

            System.out.printf("%-32s | %-18s | %-10s | %-14s%n",
                    "Product Name", "Category", "Qty Sold", "Revenue (₹)");
            ConsoleUtils.printDivider();
            for (ProductSalesReport r : list) {
                System.out.printf("%-32s | %-18s | %-10d | ₹%-13.2f%n",
                        truncate(r.getProductName(), 32),
                        truncate(r.getCategoryName(), 18),
                        r.getQuantitySold(),
                        r.getRevenueGenerated());
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private void showTopCustomersReport() {
        ConsoleUtils.printHeader("TOP CUSTOMERS (PostgreSQL HAVING & Aggregate Ranking)");
        try {
            List<CustomerSpendingReport> list = reportingService.getTopCustomers();
            if (list.isEmpty()) {
                System.out.println("No customer spending data found.");
                return;
            }

            System.out.printf("%-6s | %-20s | %-24s | %-7s | %-13s | %-13s%n",
                    "ID", "Customer Name", "Email", "Orders", "Total Spent", "Avg Order Val");
            ConsoleUtils.printDivider();
            for (CustomerSpendingReport r : list) {
                System.out.printf("%-6d | %-20s | %-24s | %-7d | ₹%-12.2f | ₹%-12.2f%n",
                        r.getCustomerId(),
                        truncate(r.getCustomerName(), 20),
                        truncate(r.getEmail(), 24),
                        r.getOrderCount(),
                        r.getTotalSpent(),
                        r.getAvgOrderValue());
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private void showMonthlySalesReport() {
        ConsoleUtils.printHeader("MONTHLY SALES AGGREGATION (PostgreSQL TO_CHAR & Date Grouping)");
        try {
            List<MonthlySalesReport> list = reportingService.getMonthlySales();
            if (list.isEmpty()) {
                System.out.println("No monthly sales data found.");
                return;
            }

            System.out.printf("%-12s | %-14s | %-15s%n", "Month", "Total Orders", "Total Revenue");
            ConsoleUtils.printDivider();
            for (MonthlySalesReport r : list) {
                System.out.printf("%-12s | %-14d | ₹%-14.2f%n",
                        r.getMonth(), r.getOrderCount(), r.getTotalRevenue());
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private void showCustomerAnalysisReport() {
        ConsoleUtils.printHeader("INDIVIDUAL CUSTOMER PURCHASE ANALYSIS (PostgreSQL CASE & LEFT JOIN)");
        int customerId = ConsoleUtils.readPositiveInt("Enter Customer ID to analyze: ");

        try {
            CustomerAnalysisReport report = reportingService.getCustomerPurchaseAnalysis(customerId);
            if (report == null) {
                ConsoleUtils.printError("Customer ID " + customerId + " not found in reporting database.");
                return;
            }

            System.out.println("Customer Name       : " + report.getCustomerName());
            System.out.println("Email Address       : " + report.getEmail());
            System.out.println("City                : " + (report.getCity() != null ? report.getCity() : "N/A"));
            System.out.println("Orders Placed       : " + report.getOrderCount());

            if (report.getOrderCount() == 0) {
                System.out.println("Purchase Activity   : This customer has not placed any orders yet.");
            } else {
                System.out.printf("Total Spending      : ₹%.2f%n", report.getTotalSpent());
                System.out.printf("Average Order Value : ₹%.2f%n", report.getAvgOrderValue());
                System.out.println("Last Order Date     : " + (report.getLastOrderDate() != null ? report.getLastOrderDate().toString().substring(0, 19) : "N/A"));
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void showInactiveCustomersReport() {
        ConsoleUtils.printHeader("INACTIVE CUSTOMERS (PostgreSQL NOT IN Subquery)");
        try {
            List<String> inactives = reportingService.getInactiveCustomers();
            if (inactives.isEmpty()) {
                System.out.println("All registered customers have placed at least one order.");
            } else {
                System.out.println("Customers with 0 orders placed:");
                for (String c : inactives) {
                    System.out.println("  * " + c);
                }
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Reporting error: " + e.getMessage());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}

