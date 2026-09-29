# Final Demonstration Script & Presentation Guide (5–10 Minutes)

This script outlines the exact sequence of actions, terminal commands, inputs, and expected outputs to demonstrate during a college project evaluation or viva.

---

## 📋 Demonstration Overview Table

| Step # | Action / Feature | Actor | Input / Command | Expected Output & viva Key Point |
| :---: | :--- | :--- | :--- | :--- |
| **1** | Start Application | System | `mvn exec:java` | Main Menu displays with 4 clean options (Customer Login, Register, Admin Login, Exit). |
| **2** | Customer Registration | Customer | Option `2` $\rightarrow$ `Vikram Rao`, `vikram@gmail.com`, `vik123`, `9876543210`, `Indiranagar, Bengaluru` | Success message with generated user ID. Demonstrates input validation & duplicate email checks. |
| **3** | Customer Login | Customer | Option `1` $\rightarrow$ `vikram@gmail.com`, `vik123` | Authentication success $\rightarrow$ Customer Dashboard displays. Demonstrates role-based access control. |
| **4** | Browse Catalog | Customer | Option `1. View Products` | Tabular catalog showing Product IDs, Names, Categories, Prices, and Stock levels. |
| **5** | Search Product | Customer | Option `2. Search Products` $\rightarrow$ `Headphones` | Filtered list displaying "Wireless Bluetooth Headphones" at ₹2,499.00. |
| **6** | Add to Cart | Customer | Option `3. Add to Cart` $\rightarrow$ Product ID `1`, Qty `1` | Success notification. Demonstrates stock availability validation. |
| **7** | View Cart Total | Customer | Option `4. View Cart` | Itemized table showing unit price, subtotal (₹2,499.00), and grand total. |
| **8** | Transactional Checkout | Customer | Option `7. Place Order` $\rightarrow$ Confirm address `Y` $\rightarrow$ Confirm checkout `Y` | Generated Order #, Total amount, and status `CONFIRMED`. Demonstrates atomic JDBC transaction. |
| **9** | Order History & Logout | Customer | Option `8. View Order History` $\rightarrow$ Option `9. Logout` | Displays placed order details and line items. Returns to Main Menu. |
| **10** | Admin Login | Admin | Option `3. Admin Login` $\rightarrow$ `admin@vyaapaar.com`, `admin123` | Success message $\rightarrow$ Admin Dashboard displays. Customers cannot access this menu. |
| **11** | Verify Stock Deduction | Admin | Option `3. Manage Inventory` $\rightarrow$ `1. View Inventory` | Stock for Product #1 was automatically decremented by 1 during previous checkout. |
| **12** | Restock Inventory | Admin | Option `3. Update Stock Level` $\rightarrow$ Product `1`, New Stock `30` | Inventory updated. Demonstrates negative-stock prevention. |
| **13** | View All Customer Orders | Admin | Option `4. View Orders` | Master list of all customer orders placed across the system. |
| **14** | Update Order Status | Admin | Option `5. Update Order Status` $\rightarrow$ Order ID `1`, Status `SHIPPED` | Order status updated to `SHIPPED`. Validates against allowed statuses. |
| **15** | Open PostgreSQL Reports | Admin | Option `6. PostgreSQL Analytics & Reports` | Sub-menu opens showing 7 advanced analytical report options. |
| **16** | Executive Sales Summary | Admin | Option `1. Sales Summary` | Displays Total Customers, Products, Orders, Revenue, and Average Order Value using scalar subqueries. |
| **17** | Revenue by Category | Admin | Option `2. Revenue by Category` | Category revenue ranking using multi-table `INNER JOIN` and `GROUP BY`. |
| **18** | Exit Application | Admin | Option `8. Back` $\rightarrow$ `7. Logout` $\rightarrow$ `4. Exit` | Application exits cleanly. |

---

## 💡 Key Viva Points to Mention During Demo

1. **Architecture**: Explain the strict 3-tier decoupling (UI does not contain SQL; DAOs do not contain business rules; Services orchestrate transactions).
2. **ACID Transaction**: Point to `OrderService.java` to explain how `conn.setAutoCommit(false)`, `commit()`, and `rollback()` guarantee that stock deductions and order creation succeed or fail as a single atomic unit.
3. **Dual Database Roles**: MySQL handles transactional operations (OLTP), while PostgreSQL handles complex analytical aggregations (OLAP).
4. **Security**: PreparedStatements are used everywhere to eliminate SQL Injection risks, and passwords are never printed in plain text.
