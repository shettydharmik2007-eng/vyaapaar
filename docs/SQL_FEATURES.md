# SQL Features & Concepts Documentation

The **Vyaapaar** application incorporates foundational and advanced SQL concepts across MySQL and PostgreSQL.

---

## 1. Relational Constraints & Schema Definition

### A. `CREATE TABLE`, `PRIMARY KEY`, & `NOT NULL`
Guarantees entity identity and mandatory data fields.
```sql
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
```

### B. `FOREIGN KEY` with Referential Integrity
Enforces parent-child relationships and cascades updates/deletions where appropriate.
```sql
CREATE TABLE cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
```

### C. `UNIQUE` & `CHECK` Constraints
Ensures domain business rules at the database level.
```sql
-- Unique cart per user and positive inventory stock
CREATE TABLE inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    stock_quantity INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_stock_positive CHECK (stock_quantity >= 0)
);
```

---

## 2. Advanced Querying & Analytical SQL

### D. `INNER JOIN` (Multi-Table Relational Navigation)
Joins categories, products, order items, and orders to compute category revenue.
```sql
SELECT c.category_name, SUM(oi.subtotal) AS total_revenue
FROM report_categories c
INNER JOIN report_products p ON c.category_id = p.category_id
INNER JOIN report_order_items oi ON p.product_id = oi.product_id
INNER JOIN report_orders o ON oi.order_id = o.order_id
WHERE o.order_status != 'CANCELLED'
GROUP BY c.category_id, c.category_name;
```

### E. `LEFT JOIN` (Preserving Unmatched Records)
Inspects individual customer purchase history, including customers with zero orders.
```sql
SELECT u.full_name, COUNT(o.order_id) AS order_count, SUM(o.total_amount) AS total_spent
FROM report_users u
LEFT JOIN report_orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.full_name;
```

### F. `GROUP BY` & `HAVING` (Post-Aggregation Filtering)
Ranks top customers who have placed at least one non-cancelled order.
```sql
SELECT u.full_name, COUNT(o.order_id) AS order_count, SUM(o.total_amount) AS total_spent
FROM report_users u
INNER JOIN report_orders o ON u.user_id = o.user_id
WHERE o.order_status != 'CANCELLED'
GROUP BY u.user_id, u.full_name
HAVING COUNT(o.order_id) > 0
ORDER BY total_spent DESC;
```

### G. Aggregate Functions (`SUM`, `COUNT`, `AVG`, `MIN`, `MAX`)
Calculates order summary and monthly sales trends.
```sql
SELECT COUNT(order_id) AS total_orders,
       SUM(total_amount) AS total_revenue,
       AVG(total_amount) AS avg_order_value,
       MIN(total_amount) AS smallest_order,
       MAX(total_amount) AS largest_order
FROM report_orders
WHERE order_status != 'CANCELLED';
```

### H. Scalar Subqueries in `SELECT`
Retrieves independent table metrics in a single row query.
```sql
SELECT 
    (SELECT COUNT(*) FROM report_users) AS total_customers,
    (SELECT COUNT(*) FROM report_products) AS total_products,
    COUNT(order_id) AS total_orders,
    SUM(total_amount) AS total_revenue
FROM report_orders
WHERE order_status != 'CANCELLED';
```

### I. Subquery with `NOT IN` (Inactive Customer Detection)
Identifies registered customers who have never placed an order.
```sql
SELECT user_id, full_name, email
FROM report_users
WHERE user_id NOT IN (SELECT DISTINCT user_id FROM report_orders);
```

### J. `CASE` Conditional Expressions
Calculates conditional spending totals excluding cancelled orders during customer analysis.
```sql
SELECT u.full_name,
       SUM(CASE WHEN o.order_status != 'CANCELLED' THEN o.total_amount ELSE 0 END) AS valid_spending
FROM report_users u
LEFT JOIN report_orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.full_name;
```

### K. Date Formatting & Grouping (`TO_CHAR`)
Groups transactions chronologically by year and month.
```sql
SELECT TO_CHAR(order_date, 'YYYY-MM') AS sales_month,
       COUNT(order_id) AS order_count,
       SUM(total_amount) AS total_revenue
FROM report_orders
WHERE order_status != 'CANCELLED'
GROUP BY TO_CHAR(order_date, 'YYYY-MM')
ORDER BY sales_month ASC;
```

### L. Parameterized `PreparedStatement` (SQL Injection Protection)
All dynamic user values are passed using `?` placeholders.
```sql
SELECT * FROM users WHERE email = ?;
```
