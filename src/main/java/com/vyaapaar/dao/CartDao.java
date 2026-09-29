package com.vyaapaar.dao;

import com.vyaapaar.config.DatabaseConnection;
import com.vyaapaar.model.Cart;
import com.vyaapaar.model.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Shopping Cart and Cart Items.
 */
public class CartDao {

    /**
     * Retrieves the user's active cart or creates one if none exists.
     *
     * @param userId User's ID
     * @return Cart object
     */
    public Cart getOrCreateCartByUserId(int userId) {
        String findSql = "SELECT * FROM cart WHERE user_id = ?";
        String insertSql = "INSERT INTO cart (user_id) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Try to find existing cart
            try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
                findStmt.setInt(1, userId);
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        Cart cart = new Cart(
                                rs.getInt("cart_id"),
                                rs.getInt("user_id"),
                                rs.getTimestamp("created_at")
                        );
                        cart.setItems(getCartItems(conn, cart.getCartId()));
                        return cart;
                    }
                }
            }

            // 2. If not found, create new cart for user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int cartId = generatedKeys.getInt(1);
                            Cart newCart = new Cart(cartId, userId, new java.sql.Timestamp(System.currentTimeMillis()));
                            newCart.setItems(new ArrayList<>());
                            return newCart;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting or creating cart: " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds an item to the cart or increments quantity if already present.
     *
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Quantity to add
     * @return true if added/updated successfully, false otherwise
     */
    public boolean addCartItem(int cartId, int productId, int quantity) {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates the quantity of a specific cart item.
     *
     * @param cartItemId Cart Item ID
     * @param quantity New quantity
     * @return true if updated successfully, false otherwise
     */
    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
        }
        return false;
    }

    /**
     * Removes an item from the cart.
     *
     * @param cartItemId Cart Item ID
     * @return true if removed successfully, false otherwise
     */
    public boolean removeCartItem(int cartItemId) {
        String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartItemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing cart item: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all items in a cart with product details.
     *
     * @param cartId Cart ID
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems(int cartId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return getCartItems(conn, cartId);
        } catch (SQLException e) {
            System.err.println("Error fetching cart items: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Helper to fetch cart items using an existing database connection.
     */
    private List<CartItem> getCartItems(Connection conn, int cartId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, p.name AS product_name, p.price AS unit_price " +
                     "FROM cart_items ci " +
                     "JOIN products p ON ci.product_id = p.product_id " +
                     "WHERE ci.cart_id = ? " +
                     "ORDER BY ci.cart_item_id ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                            rs.getInt("cart_item_id"),
                            rs.getInt("cart_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("added_at")
                    );
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Clears all items from a cart.
     *
     * @param cartId Cart ID
     * @return true if cleared successfully, false otherwise
     */
    public boolean clearCart(int cartId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return clearCart(conn, cartId);
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }
        return false;
    }

    /**
     * Clears all items from a cart within an active transaction.
     *
     * @param conn Active SQL connection
     * @param cartId Cart ID
     * @return true if cleared successfully, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean clearCart(Connection conn, int cartId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            return true;
        }
    }
}
