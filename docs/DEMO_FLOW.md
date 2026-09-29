# Demonstration Flow & Presentation Script (5–10 Minutes)

This step-by-step script is optimized for academic project demonstrations, live lab evaluations, and professor viva presentations.

---

## 🎬 Step-by-Step Demo Sequence

### Step 1: Start the Application
* **Action**: Launch `Main.java` via terminal:
  ```bash
  mvn exec:java
  ```
* **Explanation**: Explain that the application launches with a clean console interface presenting the Main Menu with role separation.

---

### Step 2: Customer Registration
* **Menu Selection**: Choose `2. Customer Registration`
* **Inputs**:
  * Full Name: `Siddharth Rao`
  * Email: `siddharth@gmail.com`
  * Password: `sid123`
  * Phone: `9876543210`
  * Delivery Address: `#45, 2nd Cross, Indiranagar, Bengaluru`
* **Explanation**: Explain input validation (email structure, minimum password length) and duplicate email detection.

---

### Step 3: Customer Login
* **Menu Selection**: Choose `1. Customer Login`
* **Inputs**:
  * Email: `siddharth@gmail.com`
  * Password: `sid123`
* **Explanation**: Show successful authentication and dynamic routing into the Customer Dashboard.

---

### Step 4: Browse Catalog & Search
* **Menu Selection**: Choose `1. View Products`
  * *Show formatted table with IDs, names, categories, prices, and stock.*
* **Menu Selection**: Choose `2. Search Products`
  * *Enter Keyword: `Java` or `Headphones`.*
  * *Show filtered search results.*

---

### Step 5: Add to Cart & Inspect Real-time Total
* **Menu Selection**: Choose `3. Add to Cart`
  * *Enter Product ID: `1` (Headphones), Quantity: `1`.*
* **Menu Selection**: Choose `3. Add to Cart` again
  * *Enter Product ID: `3` (Mouse), Quantity: `2`.*
* **Menu Selection**: Choose `4. View Cart`
  * *Show itemized cart breakdown with unit prices, line subtotals, and calculated grand total.*

---

### Step 6: Checkout & ACID Transaction Execution
* **Menu Selection**: Choose `7. Place Order (Checkout)`
* **Inputs**:
  * Use default address: `Y`
  * Confirm order placement: `Y`
* **Explanation**: Highlight the ACID transaction execution in `OrderService.java` where order creation, item snapshotting, inventory stock reduction, and cart clearing all succeed atomically.

---

### Step 7: View Order History & Logout
* **Menu Selection**: Choose `8. View Order History`
  * *Show the newly generated Order ID, date, total, status (`CONFIRMED`), and line items.*
* **Menu Selection**: Choose `9. Logout`

---

### Step 8: Admin Login & Inventory Management
* **Menu Selection**: Choose `3. Admin Login`
* **Inputs**:
  * Email: `admin@vyaapaar.com`
  * Password: `admin123`
* **Menu Selection**: Choose `3. Manage Inventory` $\rightarrow$ `1. View Inventory`
  * *Show that stock for Product #1 was decremented from the previous customer order.*
* **Menu Selection**: Choose `3. Update Stock Level`
  * *Update stock back up.*

---

### Step 9: Order Fulfillment & Status Update
* **Menu Selection**: Choose `4. View Orders`
  * *Show all customer orders placed across the system.*
* **Menu Selection**: Choose `5. Update Order Status`
  * *Select the order ID and update status from `CONFIRMED` to `SHIPPED`.*

---

### Step 10: PostgreSQL Analytics & Reporting Demonstration
* **Menu Selection**: Choose `6. PostgreSQL Analytics & Reports`
* **Demonstrate Reports**:
  1. `1. Sales Summary` — Demonstrates Scalar Subqueries and `SUM`/`AVG`/`COUNT`.
  2. `2. Revenue by Category` — Demonstrates multi-table `INNER JOIN` and `GROUP BY`.
  3. `3. Top Selling Products` — Demonstrates multi-column `ORDER BY` and `SUM(quantity)`.
  4. `4. Top Customers` — Demonstrates `HAVING` clause filtering.
  5. `5. Monthly Sales` — Demonstrates date grouping with `TO_CHAR(order_date, 'YYYY-MM')`.
  6. `6. Customer Purchase Analysis` — Enter customer ID `1` to show `LEFT JOIN` and `CASE` expressions.
* **Menu Selection**: Choose `8. Back` $\rightarrow$ `7. Logout` $\rightarrow$ `4. Exit`.
