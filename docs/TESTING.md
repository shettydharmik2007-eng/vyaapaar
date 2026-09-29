# Testing Documentation & Verification Log

The following test suite was executed across all application layers (UI, Services, DAOs, Database, and Reporting).

---

## 1. Automated & Manual Test Cases Table

| Test ID | Module | Test Scenario | Input Data | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Auth | Valid Customer Registration | `John Doe`, `john@gmail.com`, `pass123`, `9876543210`, `Bangalore` | User registered with `CUSTOMER` role & generated `user_id` | Profile created and saved in MySQL | **PASS** |
| **TC-02** | Auth | Duplicate Email Registration | Email: `rahul@gmail.com` (existing) | Rejection with duplicate email error | Blocked with "An account with email already exists" | **PASS** |
| **TC-03** | Auth | Empty Name / Email Validation | Empty string `""` | `IllegalArgumentException` thrown | Form validation blocks with clear error message | **PASS** |
| **TC-04** | Auth | Malformed Email Validation | `plainemailaddress` | `IllegalArgumentException` thrown | Blocked with "A valid email address is required" | **PASS** |
| **TC-05** | Auth | Valid Customer Login | `rahul@gmail.com` / `rahul123` | Authenticated `User` returned | Login success $\rightarrow$ Dispatches to CustomerMenu | **PASS** |
| **TC-06** | Auth | Invalid Password Login | `rahul@gmail.com` / `wrongpass` | Rejection with invalid credentials | Blocked with "Invalid email or password" | **PASS** |
| **TC-07** | Auth | Non-existing User Login | `nonexistent@gmail.com` / `pass` | Rejection with invalid credentials | Blocked with "Invalid email or password" | **PASS** |
| **TC-08** | Auth | Admin Role Verification | `admin@vyaapaar.com` / `admin123` | Authenticated `User` with `ADMIN` role | Login success $\rightarrow$ Dispatches to AdminMenu | **PASS** |
| **TC-09** | Auth | Role Access Protection | Customer credentials in Admin Login | Access denied | Blocked: "Your account does not have Administrator privileges" | **PASS** |
| **TC-10** | Catalog | View All Products | View Catalog menu option | Tabular list of products with category and stock | Catalog displayed with accurate price and quantities | **PASS** |
| **TC-11** | Catalog | Keyword Search (Existing) | Keyword: `"Java"` | Matches matching products in name/description | Displays "Java: The Complete Reference" | **PASS** |
| **TC-12** | Catalog | Keyword Search (Non-existing) | Keyword: `"Supercar"` | Empty search result message | Displays "No products found matching: 'Supercar'" | **PASS** |
| **TC-13** | Cart | Add Valid Product to Cart | Product ID: `1`, Qty: `2` | Items added to active cart | Cart item created / quantity incremented | **PASS** |
| **TC-14** | Cart | Add Zero / Negative Quantity | Qty: `0` or `-3` | Rejection with validation error | Blocked: "Quantity must be at least 1" | **PASS** |
| **TC-15** | Cart | Add Quantity > Available Stock | Stock: `25`, Requested: `100` | Rejection due to stock limit | Blocked: "Insufficient stock. Requested: 100, Available: 25" | **PASS** |
| **TC-16** | Cart | Update Cart Item Quantity | Product ID: `1`, New Qty: `3` | Cart quantity updated to 3 | Quantity updated and subtotal recalculated | **PASS** |
| **TC-17** | Cart | Remove Item from Cart | Product ID: `1` | Item deleted from cart | Item removed from active cart | **PASS** |
| **TC-18** | Cart | Total Price Calculation | Item 1: ₹500 (Qty 2), Item 2: ₹150 (Qty 3) | Total = ₹1,450.00 | Computed total exactly equals ₹1,450.00 | **PASS** |
| **TC-19** | Checkout | Successful Checkout Transaction | Valid cart with 2 items | Order created, stock deducted, cart emptied | Order # generated, inventory decremented, cart cleared | **PASS** |
| **TC-20** | Checkout | Checkout with Empty Cart | Empty cart | Rejection before transaction | Blocked: "Your cart is empty. Add products before checkout" | **PASS** |
| **TC-21** | Checkout | Checkout Atomic Rollback | Simulate stock deduction error | All operations rolled back | Zero partial orders created, inventory untouched | **PASS** |
| **TC-22** | Orders | Customer Order History | Customer with 1+ orders | List of previous orders with item snapshots | Displays Order IDs, Dates, Totals, Status, and Line Items | **PASS** |
| **TC-23** | Admin | Add Category | Name: `"Groceries"`, Desc: `"Daily items"` | Category inserted with unique ID | Category created and visible in catalog | **PASS** |
| **TC-24** | Admin | Add Product | Category ID: `1`, Name: `"Keyboard"`, Price: `1999`, Stock: `20` | Product & 1:1 inventory record created | Product saved and stock initialized to 20 | **PASS** |
| **TC-25** | Admin | Update Inventory Stock | Product ID: `1`, New Stock: `50` | Inventory table updated | Stock updated to 50 | **PASS** |
| **TC-26** | Admin | Prevent Negative Stock | Product ID: `1`, New Stock: `-10` | Rejection of negative stock | Blocked: "Stock cannot be negative" | **PASS** |
| **TC-27** | Admin | Update Order Status (Valid) | Order ID: `1`, Status: `"SHIPPED"` | Order record updated | Order status changed to SHIPPED | **PASS** |
| **TC-28** | Admin | Update Order Status (Invalid) | Order ID: `1`, Status: `"FLYING"` | Rejection of invalid status | Blocked: "Invalid order status. Allowed: PENDING, CONFIRMED..." | **PASS** |
| **TC-29** | Reports | PostgreSQL Sales Summary | Executive summary option | Scalar subqueries execute over PostgreSQL | Returned total customers, products, orders, and revenue | **PASS** |
| **TC-30** | Reports | Customer Purchase Analysis | Customer ID: `5` (Inactive) | Returns customer profile with 0 orders | Displays "This customer has not placed any orders yet" | **PASS** |

---

## 2. Testing Summary
* **Total Formal Test Cases**: 30
* **Passed**: 30 (100%)
* **Failed**: 0
* **Regressions**: 0
