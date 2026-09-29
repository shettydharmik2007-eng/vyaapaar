# Functional Module Documentation

---

## 1. Authentication & Security Module
* **Purpose**: Manages user registration, credential authentication, role determination, and session context.
* **Key Classes**:
  * [`AuthService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/AuthService.java)
  * [`UserDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/UserDao.java)
  * [`User.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/User.java)
* **Key Operations**:
  * `registerCustomer(fullName, email, password, phone, address)`: Validates inputs, rejects duplicate emails, saves user with `'CUSTOMER'` role.
  * `login(email, password)`: Verifies credentials and returns authenticated `User`.
  * `isAdmin(user)` / `isCustomer(user)`: Role evaluation helpers.

---

## 2. Product Catalog Module
* **Purpose**: Manages product listing, search, retrieval, pricing, and catalog updates.
* **Key Classes**:
  * [`ProductService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/ProductService.java)
  * [`ProductDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/ProductDao.java)
  * [`Product.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/Product.java)
* **Key Operations**:
  * `getAllProducts()`: Retrieves catalog joined with category name and current stock.
  * `searchProducts(keyword)`: Multi-attribute keyword search.
  * `addProduct(...)` / `updateProduct(...)` / `deleteProduct(id)`: Catalog management with category validation.

---

## 3. Category Module
* **Purpose**: Organizes products into logical classifications.
* **Key Classes**:
  * [`CategoryService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/CategoryService.java)
  * [`CategoryDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/CategoryDao.java)
  * [`Category.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/Category.java)
* **Key Operations**:
  * `addCategory(name, description)`: Inserts category with unique name validation.
  * `getAllCategories()` / `getCategoryById(id)` / `updateCategory(...)` / `deleteCategory(id)`.

---

## 4. Inventory Management Module
* **Purpose**: Tracks real-time stock levels and prevents stock underflows.
* **Key Classes**:
  * [`InventoryService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/InventoryService.java)
  * [`InventoryDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/InventoryDao.java)
  * [`Inventory.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/Inventory.java)
* **Key Operations**:
  * `getCurrentStock(productId)`: Returns available quantity.
  * `updateStock(productId, newQuantity)`: Updates stock while enforcing non-negative invariant (`stock >= 0`).
  * `hasSufficientStock(productId, requestedQty)`: Pre-check before cart addition and checkout.
  * `getLowStockProducts(threshold)`: Lists items needing replenishment.

---

## 5. Shopping Cart Module
* **Purpose**: Manages persistent customer shopping carts and real-time total calculations.
* **Key Classes**:
  * [`CartService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/CartService.java)
  * [`CartDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/CartDao.java)
  * [`Cart.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/Cart.java)
  * [`CartItem.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/CartItem.java)
* **Key Operations**:
  * `getOrCreateCart(userId)`: Fetches active cart with itemized products.
  * `addToCart(userId, productId, quantity)`: Checks product stock and adds item.
  * `updateProductQuantity(userId, productId, newQuantity)` / `removeProductFromCart(userId, productId)`.
  * `calculateCartTotal(items)`: Computes subtotal sum.

---

## 6. Order Processing Module
* **Purpose**: Executes multi-table checkout transactions and manages customer order history.
* **Key Classes**:
  * [`OrderService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/OrderService.java)
  * [`OrderDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/OrderDao.java)
  * [`Order.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/Order.java)
  * [`OrderItem.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/model/OrderItem.java)
* **Key Operations**:
  * `checkout(userId, shippingAddress)`: Atomic JDBC transaction creating order, capturing item price snapshots, reducing inventory, and clearing cart.
  * `getOrdersByUserId(userId)` / `getAllOrders()` / `updateOrderStatus(orderId, newStatus)`.

---

## 7. Administrator Management Module
* **Purpose**: Consolidates catalog maintenance, stock control, and order fulfillment into an interactive dashboard.
* **Key Classes**:
  * [`AdminMenu.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/ui/AdminMenu.java)

---

## 8. PostgreSQL Reporting & Analytics Module
* **Purpose**: Generates advanced business intelligence and aggregate sales metrics over PostgreSQL.
* **Key Classes**:
  * [`ReportingService.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/service/ReportingService.java)
  * [`ReportingDao.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/dao/ReportingDao.java)
  * [`PostgreSQLConnection.java`](file:///c:/Users/shett/OneDrive/Vyaapaar/src/main/java/com/vyaapaar/config/PostgreSQLConnection.java)
  * Report DTOs: `SalesSummaryReport`, `CategoryRevenueReport`, `ProductSalesReport`, `CustomerSpendingReport`, `MonthlySalesReport`, `CustomerAnalysisReport`.
