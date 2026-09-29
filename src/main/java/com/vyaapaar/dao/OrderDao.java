package com.vyaapaar.dao;

import com.vyaapaar.config.DatabaseConnection;
import com.vyaapaar.model.Order;
import com.vyaapaar.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Orders and Order Items.
 */
public class OrderDao {

    /**
     * Creates an order record using a standalone connection.
     *
     * @param order Order entity
     * @return Generated order ID, or -1 if failed
     */
    public int createOrder(Order order) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return createOrder(conn, order);
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Creates an order record within an active transaction.
     *
     * @param conn Active SQL connection
     * @param order Order entity
     * @return Generated order ID, or -1 if failed
     * @throws SQLException if a database error occurs
     */
    public int createOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, order_status, shipping_address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.setString(3, order.getOrderStatus() != null ? order.getOrderStatus() : "CONFIRMED");
            stmt.setString(4, order.getShippingAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setOrderId(orderId);
                        return orderId;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Inserts list of order items within an active transaction.
     *
     * @param conn Active SQL connection
     * @param orderId ID of the parent order
     * @param items List of OrderItem objects
     * @return true if all items inserted successfully
     * @throws SQLException if a database error occurs
     */
    public boolean addOrderItems(Connection conn, int orderId, List<OrderItem> items) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (OrderItem item : items) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getUnitPrice());
                stmt.setDouble(5, item.getSubtotal());
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            return results.length == items.size();
        }
    }

    /**
     * Retrieves an order by its ID along with its associated items and customer info.
     *
     * @param orderId Order ID
     * @return Order object with populated items, or null if not found
     */
    public Order getOrderById(int orderId) {
        String orderSql = "SELECT o.*, u.full_name AS customer_name, u.email AS customer_email " +
                          "FROM orders o " +
                          "JOIN users u ON o.user_id = u.user_id " +
                          "WHERE o.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            Order order = null;
            try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
                stmt.setInt(1, orderId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        order = mapResultSetToOrder(rs);
                    }
                }
            }

            if (order != null) {
                order.setItems(getOrderItems(conn, orderId));
            }
            return order;
        } catch (SQLException e) {
            System.err.println("Error fetching order by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId User ID
     * @return List of Order objects
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name, u.email AS customer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.user_id = ? " +
                     "ORDER BY o.order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        orders.add(mapResultSetToOrder(rs));
                    }
                }
            }

            // Populate items for each order
            for (Order order : orders) {
                order.setItems(getOrderItems(conn, order.getOrderId()));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by user ID: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Retrieves all orders in the system (for Admin management).
     *
     * @return List of all Order objects
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS customer_name, u.email AS customer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "ORDER BY o.order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }

            // Populate items for each order
            for (Order order : orders) {
                order.setItems(getOrderItems(conn, order.getOrderId()));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId Order ID
     * @param newStatus New order status ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')
     * @return true if updated successfully, false otherwise
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Helper to fetch order items for a specific order.
     */
    private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = ? " +
                     "ORDER BY oi.order_item_id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                            rs.getInt("order_item_id"),
                            rs.getInt("order_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("subtotal")
                    );
                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Helper method to map a ResultSet row to an Order object.
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order(
                rs.getInt("order_id"),
                rs.getInt("user_id"),
                rs.getDouble("total_amount"),
                rs.getString("order_status"),
                rs.getString("shipping_address"),
                rs.getTimestamp("order_date")
        );
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerEmail(rs.getString("customer_email"));
        return order;
    }
}
