# Problem Statement

## Background & Context
Small-to-medium retail businesses, local distributors, and academic institutions often manage their commercial operations using manual registers, basic spreadsheets, or disjointed software tools. As transaction volume scales, manual processes encounter severe operational bottlenecks:

### 1. Inaccurate Inventory & Stock Discrepancies
* Stock levels recorded in spreadsheets quickly drift out of synchronization with physical inventory.
* Overselling occurs when two buyers order the same item simultaneously without real-time concurrency control.
* Negative stock values and untracked replenishments lead to lost revenue and customer dissatisfaction.

### 2. Manual & Error-Prone Order Processing
* Calculating cart item subtotals, applying discounts/taxes, and totaling invoices manually increases calculation errors.
* Order placements without atomic stock verification can create orphan orders where items are confirmed but unavailable for dispatch.

### 3. Disorganized Product & Category Catalogs
* Lack of standardized category hierarchies makes searching, filtering, and catalog updates cumbersome.
* Product price changes often fail to reflect consistently across customer quotations and sales records.

### 4. Poor Customer Order Visibility
* Customers have no centralized record to track their historical purchases, delivery status, or order details.

### 5. Absence of Actionable Sales & Revenue Insights
* Store owners lack immediate access to sales summaries, top-selling products, category revenue contributions, or customer purchase habits without tedious manual aggregation.

---

## The Vyaapaar Solution
**Vyaapaar** replaces manual and error-prone procedures with an automated, structured relational database application:
* **Real-time Atomic Transactions**: Ensures inventory is deducted simultaneously when an order is created; if stock is insufficient, changes are automatically rolled back.
* **Role-Based Workflows**: Distinct interfaces and permissions for Customers and Administrators.
* **Persistent Cart & Order History**: Relational data structures preserving historical item pricing and order status tracking.
* **Dedicated SQL Analytics**: Advanced SQL aggregation queries for executive revenue summaries and category performance reports.
