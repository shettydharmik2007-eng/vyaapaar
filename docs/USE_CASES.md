# Use Case Documentation

---

## 1. System Actors

* **Customer**: An end-user who registers, logs in, browses the product catalog, manages a shopping cart, places orders through transactional checkout, and views historical order records.
* **Administrator**: A privileged store manager who logs in to organize product categories, manage catalog products, adjust and monitor inventory stock, view customer orders, update order fulfillment statuses, and inspect PostgreSQL analytics.

---

## 2. Customer Use Cases

| Use Case ID | Name | Description | Preconditions | Main Success Flow |
| :--- | :--- | :--- | :--- | :--- |
| **UC-C01** | Register Account | Create a new customer profile. | None | Enter name, valid email, password, phone, address $\rightarrow$ Profile saved with `CUSTOMER` role. |
| **UC-C02** | Customer Login | Authenticate customer into system. | Registered account | Enter email and password $\rightarrow$ Session established $\rightarrow$ Opens Customer Menu. |
| **UC-C03** | View Products | Display catalog of available products. | Logged in as Customer | View tabular list with ID, Name, Category, Price, and Available Stock. |
| **UC-C04** | Search Products | Search catalog by keyword. | Logged in as Customer | Enter keyword $\rightarrow$ System matches against name, description, or category. |
| **UC-C05** | Add to Cart | Place selected quantity into shopping cart. | Logged in as Customer | Enter Product ID & Quantity $\rightarrow$ System verifies product and stock $\rightarrow$ Item added/updated in cart. |
| **UC-C06** | View Cart | Review items currently in shopping cart. | Active cart exists | View itemized table with unit prices, subtotals, and calculated grand total. |
| **UC-C07** | Update Cart | Adjust item quantity in cart. | Item in cart | Enter Product ID & new quantity $\rightarrow$ System verifies stock $\rightarrow$ Quantity updated. |
| **UC-C08** | Remove from Cart | Delete an item from shopping cart. | Item in cart | Enter Product ID $\rightarrow$ Item deleted from active cart. |
| **UC-C09** | Checkout / Place Order | Finalize cart and convert into confirmed order. | Cart has $\ge 1$ items | Confirm delivery address & order prompt (Y/N) $\rightarrow$ Atomic transaction creates order, deducts stock, clears cart. |
| **UC-C10** | View Order History | Review past customer orders. | Logged in as Customer | Displays order IDs, dates, total amounts, fulfillment statuses, and purchased item snapshots. |
| **UC-C11** | Customer Logout | End customer session. | Logged in as Customer | Session terminated $\rightarrow$ Returns to Main Menu. |

---

## 3. Administrator Use Cases

| Use Case ID | Name | Description | Preconditions | Main Success Flow |
| :--- | :--- | :--- | :--- | :--- |
| **UC-A01** | Admin Login | Authenticate store administrator. | User with `ADMIN` role | Enter admin email and password $\rightarrow$ Verified role $\rightarrow$ Opens Admin Menu. |
| **UC-A02** | Manage Categories | Add, View, Update, or Delete product categories. | Logged in as Admin | Select action $\rightarrow$ Perform category CRUD with duplicate name validation. |
| **UC-A03** | Manage Products | Add, View, Search, Update, or Delete products. | Logged in as Admin | Select action $\rightarrow$ Perform product CRUD with category verification and initial stock provisioning. |
| **UC-A04** | Manage Inventory | Monitor stock levels and restock items. | Logged in as Admin | View inventory table, update quantities (preventing negative stock), or inspect low-stock alerts. |
| **UC-A05** | View All Orders | View all customer orders across system. | Logged in as Admin | Displays comprehensive order history with customer contact, order date, totals, and line items. |
| **UC-A06** | Update Order Status | Update order fulfillment status. | Logged in as Admin | Enter Order ID & choose valid status (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`). |
| **UC-A07** | View PostgreSQL Reports | Generate advanced SQL analytics. | Logged in as Admin | Select report type (Sales Summary, Category Revenue, Top Products, Top Customers, Monthly Sales, Customer Analysis). |
| **UC-A08** | Admin Logout | End administrator session. | Logged in as Admin | Session terminated $\rightarrow$ Returns to Main Menu. |
