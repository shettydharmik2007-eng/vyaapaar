# Checkout Transaction Flow & ACID Guarantees

## 1. Why Transactions are Critical in E-Commerce
In an e-commerce platform, order checkout is not a single database operation; it is a composite sequence of multiple interdependent mutations:
1. Creating an order header
2. Recording order item snapshots
3. Deducting physical inventory stock
4. Clearing the customer's active shopping cart

If auto-commit is enabled and a failure occurs midway (e.g., system crash, database connection drop, or stock underflow), the database enters a **corrupted state**:
* An order may be created, but inventory was not deducted (overselling risk).
* Stock was deducted, but the order record was not saved (lost money/items).
* Stock was deducted and order was placed, but the cart was not cleared (duplicate orders).

---

## 2. ACID Properties Enforced by Vyaapaar

* **Atomicity (All or Nothing)**: Either **all** 4 checkout steps succeed, or **none** of them take effect.
* **Consistency**: Database foreign keys, positive quantity constraints, and non-negative stock invariants (`stock_quantity >= 0`) are strictly maintained.
* **Isolation**: Intermediate state during checkout (e.g., inserted order before commit) is invisible to other concurrent sessions.
* **Durability**: Once `conn.commit()` succeeds, the order and stock deduction are permanently recorded on disk.

---

## 3. Step-by-Step Transaction Flow

```text
[Customer Confirms Checkout]
             ↓
1. Retrieve customer's active cart
             ↓
2. Verify cart is NOT empty (throws IllegalStateException if empty)
             ↓
3. Verify available stock for every cart item
             ↓
4. Calculate total amount & prepare order item snapshots
             ↓
5. BEGIN TRANSACTION (conn.setAutoCommit(false))
             │
             ├── Step 5.1: Insert 'orders' record -> Capture generated order_id
             │
             ├── Step 5.2: Batch insert 'order_items' (immutable quantity & price snapshot)
             │
             ├── Step 5.3: Deduct stock in 'inventory' for each product (stock = stock - qty)
             │
             ├── Step 5.4: Clear customer's 'cart_items'
             │
             └── Step 5.5: COMMIT TRANSACTION (conn.commit())
                                 │
           ┌─────────────────────┴─────────────────────┐
           ▼                                           ▼
      [SUCCESS]                                    [FAILURE]
  Order confirmed &                           conn.rollback() called.
  returned to Customer.                   All changes discarded.
                                          Stock and Cart remain untouched.
```

---

## 4. Code Implementation in `OrderService.java`

```java
Connection conn = null;
try {
    conn = DatabaseConnection.getConnection();
    conn.setAutoCommit(false); // Begin Transaction

    // 1. Create Order
    Order order = new Order(userId, totalAmount, "CONFIRMED", shippingAddress.trim());
    int orderId = orderDao.createOrder(conn, order);
    order.setOrderId(orderId);

    // 2. Batch Insert Order Items
    orderDao.addOrderItems(conn, orderId, orderItems);

    // 3. Deduct Stock for each Product
    for (CartItem item : cartItems) {
        Inventory inv = inventoryDao.getInventoryByProductId(item.getProductId());
        int updatedStock = inv.getStockQuantity() - item.getQuantity();
        inventoryDao.updateStock(conn, item.getProductId(), updatedStock);
    }

    // 4. Clear Customer's Cart
    cartDao.clearCart(conn, cart.getCartId());

    // 5. Commit
    conn.commit();
    return order;

} catch (Exception e) {
    if (conn != null) {
        conn.rollback(); // Atomic Rollback on Any Exception
    }
    throw new RuntimeException("Checkout failed: " + e.getMessage(), e);
} finally {
    if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
    }
}
```
