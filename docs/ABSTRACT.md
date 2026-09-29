# Project Abstract

## Vyaapaar (व्यापार) - E-Commerce Order & Inventory Management System

**Vyaapaar** is an academic software project developed in **Core Java** using **Java Database Connectivity (JDBC)**, **MySQL**, and **PostgreSQL**. The application is designed to demonstrate foundational software engineering concepts, Object-Oriented Programming (OOP) principles, relational database management, and multi-tier architectural separation.

In traditional retail and manual inventory tracking, businesses frequently suffer from inventory discrepancies, order processing delays, stock underflows, and lack of consolidated sales insights. **Vyaapaar** resolves these challenges by providing an automated, console-based order and inventory management system with role-based access for Customers and Administrators.

### Key Highlights & Architecture:
1. **Core Application (MySQL)**:
   * **Customer Portal**: User registration, authentication, categorized product catalog browsing, keyword search, active shopping cart management, transactional order checkout, and historical order tracking.
   * **Admin Portal**: Product catalog management, category organization, inventory tracking with low-stock alerts, and order status fulfillment (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
   * **ACID Transaction Management**: Multi-table checkout operations wrapped in atomic JDBC transactions (`setAutoCommit(false)`, `commit()`, `rollback()`) to guarantee stock integrity.
2. **Analytics & Reporting (PostgreSQL)**:
   * Decoupled analytical module demonstrating advanced SQL queries including multi-table `INNER/LEFT JOIN`s, `GROUP BY`, `HAVING`, aggregate calculations (`SUM`, `AVG`, `COUNT`), scalar and `NOT IN` subqueries, `CASE` expressions, and chronological date-based aggregations.

By strictly utilizing **Java Standard Edition (Java SE)** and raw **JDBC PreparedStatements** without high-level ORM frameworks or web abstractions, this project provides complete transparency into query execution, database connection lifecycles, and backend data flow, making it ideal for academic evaluation and viva demonstration.
