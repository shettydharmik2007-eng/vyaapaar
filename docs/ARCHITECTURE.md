# System Architecture

## 1. Multi-Tier Layered Architecture

**Vyaapaar** is designed according to the **3-Tier / Layered Software Architecture**. Each layer has distinct responsibilities, preventing tight coupling and ensuring that UI code never directly touches SQL or database connections.

```text
+-----------------------------------------------------------------------+
|                    PRESENTATION LAYER (Console UI)                    |
|  - MainMenu, CustomerMenu, AdminMenu, ConsoleUtils                    |
|  - Responsible solely for user interaction, menus, and input parsing. |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                     SERVICE LAYER (Business Logic)                    |
|  - AuthService, ProductService, CategoryService,                      |
|    InventoryService, CartService, OrderService, ReportingService      |
|  - Validations, Business Rules, Stock Checks, Transaction Boundaries. |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                    DATA ACCESS LAYER (DAO / JDBC)                     |
|  - UserDao, ProductDao, CategoryDao,                                  |
|    InventoryDao, CartDao, OrderDao, ReportingDao                      |
|  - Executes PreparedStatements, SQL mappings, and queries.            |
+-----------------------------------------------------------------------+
                     |                                   |
                     v                                   v
+-----------------------------+         +-------------------------------+
|  PRIMARY DATABASE (MySQL)   |         | REPORTING DB (PostgreSQL)     |
|  - vyaapaar_db              |         | - vyaapaar_reporting          |
|  - Users, Products, Orders, |         | - Analytical reports, joins,  |
|    Inventory, Cart, Items   |         |   aggregations, subqueries    |
+-----------------------------+         +-------------------------------+
```

---

## 2. Layer Responsibilities

### 1. Presentation Layer (`com.vyaapaar.ui`)
* Displays clean interactive ASCII menus.
* Uses [`ConsoleUtils.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/ui/ConsoleUtils.java) for robust `Scanner` input reading and type safety.
* **Contains zero SQL queries and zero business calculations**.
* Communicates solely by invoking methods on the Service layer.

### 2. Service Layer (`com.vyaapaar.service`)
* Implements input validation rules (e.g., non-empty strings, email format, positive integers, non-negative stock).
* Enforces role-based permissions (`CUSTOMER` vs `ADMIN`).
* Orchestrates multi-step business transactions (e.g., Checkout in `OrderService` which manages the JDBC connection transaction).
* Returns domain POJOs and DTOs to the UI layer.

### 3. Data Access Object (DAO) Layer (`com.vyaapaar.dao`)
* Encapsulates all relational database interaction.
* Executes SQL statements using `java.sql.PreparedStatement` with `?` parameter placeholders.
* Maps `ResultSet` rows into domain models ([`com.vyaapaar.model`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model)).
* Automatically manages database connection, statement, and result set lifecycles using `try-with-resources`.

### 4. Database Layer
* **MySQL (`vyaapaar_db`)**: Primary OLTP (Online Transaction Processing) database handling day-to-day shopping, cart persistence, inventory tracking, and order creation.
* **PostgreSQL (`vyaapaar_reporting`)**: Dedicated analytical and reporting database utilized for demonstrating advanced SQL aggregations and executive metrics.

---

## 3. PostgreSQL Reporting Architecture Flow

```text
Admin User Selects Report
           ↓
[AdminMenu.java]
           ↓ calls getSalesSummary() / getRevenueByCategory()
[ReportingService.java]
           ↓ validates parameters
[ReportingDao.java]
           ↓ executes Advanced SQL (JOIN, GROUP BY, HAVING, CASE, Subquery)
[PostgreSQL Database (vyaapaar_reporting)]
           ↓ returns ResultSet
[ReportingDao.java] maps to Report DTOs (e.g., SalesSummaryReport)
           ↓
[AdminMenu.java] renders formatted table to console
```
