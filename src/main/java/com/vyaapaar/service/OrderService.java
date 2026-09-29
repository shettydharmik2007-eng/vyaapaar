package com.vyaapaar.service;

import com.vyaapaar.config.DatabaseConnection;
import com.vyaapaar.dao.CartDao;
import com.vyaapaar.dao.InventoryDao;
import com.vyaapaar.dao.OrderDao;
import com.vyaapaar.model.Cart;
import com.vyaapaar.model.CartItem;
import com.vyaapaar.model.Inventory;
import com.vyaapaar.model.Order;
import com.vyaapaar.model.OrderItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing Order processing, ACID transactional checkout, and order fulfillment.
 */
public class OrderService {

    private final OrderDao orderDao;
    private final CartDao cartDao;
    private final InventoryDao inventoryDao;

    public OrderService() {
        this.orderDao = new OrderDao();
        this.cartDao = new CartDao();
        this.inventoryDao = new InventoryDao();
    }

    public OrderService(OrderDao orderDao, CartDao cartDao, InventoryDao inventoryDao) {
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.inventoryDao = inventoryDao;
    }

    /**
     * Executes the complete checkout flow inside a single ACID JDBC Transaction.
     *
     * Flow:
     * 1. Get customer's cart.
     * 2. Verify cart is not empty.
     * 3. Verify stock for every cart item.
     * 4. Calculate total amount.
     * 5. Create Order record.
     * 6. Create OrderItem snapshot records.
     * 7. Deduct inventory stock.
     * 8. Clear customer's cart.
     * 9. Commit transaction.
     *
     * In case of ANY failure: Transaction is completely rolled back.
     *
     * @param userId Customer ID
     * @param shippingAddress Delivery address
     * @return Created Order object
     * @throws IllegalStateException if cart is empty or stock is insufficient
     * @throws RuntimeException if database transaction fails
     */
    public Order checkout(int userId, String shippingAddress) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address cannot be empty.");
        }

        // 1. Get customer's cart
        Cart cart = cartDao.getOrCreateCartByUserId(userId);
        if (cart == null) {
            throw new IllegalStateException("Unable to retrieve user shopping cart.");
        }

        List<CartItem> cartItems = cart.getItems();

        // 2. Verify cart is not empty
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Add products to cart before checkout.");
        }

        // 3. Pre-check stock availability
        for (CartItem item : cartItems) {
            Inventory inv = inventoryDao.getInventoryByProductId(item.getProductId());
            int currentStock = inv != null ? inv.getStockQuantity() : 0;
            if (currentStock < item.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for '" + item.getProductName() +
                        "'. Requested: " + item.getQuantity() + ", Available: " + currentStock
                );
            }
        }

        // 4. Calculate total amount and prepare order items
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            double subtotal = item.getUnitPrice() * item.getQuantity();
            totalAmount += subtotal;
            orderItems.add(new OrderItem(0, item.getProductId(), item.getQuantity(), item.getUnitPrice(), subtotal));
        }

        // 5-9. Execute atomic JDBC transaction
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // Step 5: Insert Order
            Order order = new Order(userId, totalAmount, "CONFIRMED", shippingAddress.trim());
            int orderId = orderDao.createOrder(conn, order);
            if (orderId <= 0) {
                throw new SQLException("Failed to create order record.");
            }
            order.setOrderId(orderId);

            // Step 6: Insert Order Items
            boolean itemsInserted = orderDao.addOrderItems(conn, orderId, orderItems);
            if (!itemsInserted) {
                throw new SQLException("Failed to create order item records.");
            }

            // Step 7: Deduct Inventory stock
            for (CartItem item : cartItems) {
                Inventory inv = inventoryDao.getInventoryByProductId(item.getProductId());
                int updatedStock = (inv != null ? inv.getStockQuantity() : 0) - item.getQuantity();
                if (updatedStock < 0) {
                    throw new SQLException("Stock underflow detected for product ID: " + item.getProductId());
                }
                boolean stockDeducted = inventoryDao.updateStock(conn, item.getProductId(), updatedStock);
                if (!stockDeducted) {
                    throw new SQLException("Failed to deduct inventory for product: " + item.getProductName());
                }
            }

            // Step 8: Clear customer's cart
            boolean cartCleared = cartDao.clearCart(conn, cart.getCartId());
            if (!cartCleared) {
                throw new SQLException("Failed to clear shopping cart during checkout.");
            }

            // Step 9: Commit Transaction
            conn.commit();
            order.setItems(orderItems);
            return order;

        } catch (Exception e) {
            // Roll back on any failure
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println(">>> [TRANSACTION ROLLBACK] Order checkout failed. Changes rolled back. Reason: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Checkout failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection after transaction: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves an order by its ID with all item details.
     */
    public Order getOrderById(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID.");
        }
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order #" + orderId + " not found.");
        }
        return order;
    }

    /**
     * Retrieves order history for a customer.
     */
    public List<Order> getOrdersByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return orderDao.getOrdersByUserId(userId);
    }

    /**
     * Retrieves all orders in the system (for Admin).
     */
    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    /**
     * Updates an order status with valid status validation.
     *
     * @param orderId Order ID
     * @param newStatus New status ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')
     * @return true if updated successfully
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID.");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }

        String statusUpper = newStatus.trim().toUpperCase();
        if (!isValidStatus(statusUpper)) {
            throw new IllegalArgumentException("Invalid order status: " + newStatus +
                    ". Allowed: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED");
        }

        Order existing = orderDao.getOrderById(orderId);
        if (existing == null) {
            throw new IllegalArgumentException("Order #" + orderId + " does not exist.");
        }

        return orderDao.updateOrderStatus(orderId, statusUpper);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") ||
               status.equals("CONFIRMED") ||
               status.equals("SHIPPED") ||
               status.equals("DELIVERED") ||
               status.equals("CANCELLED");
    }
}
