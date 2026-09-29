# Project Objectives

## 1. Primary Academic Objectives
* **Demonstrate Core OOP Principles**: Implement robust Encapsulation (POJOs), Abstraction and Separation of Concerns (DAO and Service patterns), and Polymorphism in pure Java SE.
* **Demonstrate Database & JDBC Proficiency**: Establish clean database lifecycles using `java.sql.Connection`, `PreparedStatement` parameter binding, `ResultSet` traversal, and explicit resource management.
* **Demonstrate ACID Transaction Control**: Implement programmatic multi-table transaction boundaries (`setAutoCommit(false)`, `commit()`, and `rollback()`) to ensure zero data corruption during checkout.
* **Demonstrate Advanced SQL Techniques**: Write complex analytical queries featuring multi-table `JOIN`s, `GROUP BY`, `HAVING`, Aggregates, Subqueries, `CASE` expressions, and Date aggregations.

---

## 2. Functional Application Objectives

### Customer Domain:
1. **User Management**: Support secure customer registration with input validation and credential authentication.
2. **Catalog Browsing**: Provide organized product catalog views and keyword-based multi-attribute search.
3. **Cart Operations**: Enable customers to add items to persistent carts, update quantities, remove items, and view real-time subtotals.
4. **Order Processing**: Execute safe checkout with address verification and immediate stock deduction.
5. **Order History**: Allow customers to review past orders, purchased item snapshots, and delivery statuses.

### Administrator Domain:
1. **Category Management**: Perform CRUD operations on product categories.
2. **Product Catalog Control**: Add products with pricing, descriptions, and category links.
3. **Inventory Control**: Update stock quantities, monitor stock levels, and inspect low-stock threshold alerts.
4. **Order Fulfillment**: Review all system orders and transition order fulfillment statuses (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
5. **Reporting & Analytics**: Generate executive sales summaries, category revenue breakdowns, top-customer rankings, and monthly sales trends using PostgreSQL.
