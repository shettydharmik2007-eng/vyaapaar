package com.vyaapaar.dao;

import com.vyaapaar.config.DatabaseConnection;
import com.vyaapaar.model.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product Inventory management.
 */
public class InventoryDao {

    /**
     * Creates an initial inventory record for a product.
     *
     * @param inventory Inventory object
     * @return true if created successfully, false otherwise
     */
    public boolean createInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (product_id, stock_quantity) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, inventory.getProductId());
            stmt.setInt(2, inventory.getStockQuantity());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        inventory.setInventoryId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating inventory: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves inventory details for a specific product.
     *
     * @param productId Product ID
     * @return Inventory object if found, null otherwise
     */
    public Inventory getInventoryByProductId(int productId) {
        String sql = "SELECT i.*, p.name AS product_name " +
                     "FROM inventory i " +
                     "JOIN products p ON i.product_id = p.product_id " +
                     "WHERE i.product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Inventory inv = new Inventory(
                            rs.getInt("inventory_id"),
                            rs.getInt("product_id"),
                            rs.getInt("stock_quantity"),
                            rs.getTimestamp("last_updated")
                    );
                    inv.setProductName(rs.getString("product_name"));
                    return inv;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory by product ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the stock quantity for a product using a new standalone connection.
     *
     * @param productId Product ID
     * @param newQuantity New stock quantity
     * @return true if updated successfully, false otherwise
     */
    public boolean updateStock(int productId, int newQuantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStock(conn, productId, newQuantity);
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates the stock quantity for a product within an active transaction.
     *
     * @param conn Active SQL connection
     * @param productId Product ID
     * @param newQuantity New stock quantity
     * @return true if updated successfully, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateStock(Connection conn, int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE inventory SET stock_quantity = ? WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all inventory items with stock level at or below a given threshold.
     *
     * @param threshold Low stock threshold limit
     * @return List of low-stock Inventory items
     */
    public List<Inventory> getLowStockProducts(int threshold) {
        List<Inventory> lowStockList = new ArrayList<>();
        String sql = "SELECT i.*, p.name AS product_name " +
                     "FROM inventory i " +
                     "JOIN products p ON i.product_id = p.product_id " +
                     "WHERE i.stock_quantity <= ? " +
                     "ORDER BY i.stock_quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inv = new Inventory(
                            rs.getInt("inventory_id"),
                            rs.getInt("product_id"),
                            rs.getInt("stock_quantity"),
                            rs.getTimestamp("last_updated")
                    );
                    inv.setProductName(rs.getString("product_name"));
                    lowStockList.add(inv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low-stock products: " + e.getMessage());
        }
        return lowStockList;
    }

    /**
     * Retrieves all inventory items with product details.
     *
     * @return List of all Inventory records
     */
    public List<Inventory> getAllInventory() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.*, p.name AS product_name " +
                     "FROM inventory i " +
                     "JOIN products p ON i.product_id = p.product_id " +
                     "ORDER BY p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Inventory inv = new Inventory(
                        rs.getInt("inventory_id"),
                        rs.getInt("product_id"),
                        rs.getInt("stock_quantity"),
                        rs.getTimestamp("last_updated")
                );
                inv.setProductName(rs.getString("product_name"));
                list.add(inv);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all inventory: " + e.getMessage());
        }
        return list;
    }
}
