package com.vyaapaar.dao;

import com.vyaapaar.config.DatabaseConnection;
import com.vyaapaar.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product catalog operations.
 */
public class ProductDao {

    /**
     * Adds a new product to the catalog.
     *
     * @param product Product object to insert
     * @return true if added successfully, false otherwise
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (category_id, name, description, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all products joined with category name and stock quantity.
     *
     * @return List of Product objects
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, COALESCE(i.stock_quantity, 0) AS stock_quantity " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                     "ORDER BY p.product_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param productId Product ID
     * @return Product object if found, null otherwise
     */
    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name, COALESCE(i.stock_quantity, 0) AS stock_quantity " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                     "WHERE p.product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Searches for products matching a keyword in name, description, or category.
     *
     * @param keyword Search term
     * @return List of matching Product objects
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, COALESCE(i.stock_quantity, 0) AS stock_quantity " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                     "WHERE p.name LIKE ? OR p.description LIKE ? OR c.category_name LIKE ? " +
                     "ORDER BY p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId Category ID
     * @return List of Product objects in the category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, COALESCE(i.stock_quantity, 0) AS stock_quantity " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                     "WHERE p.category_id = ? " +
                     "ORDER BY p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
        }
        return products;
    }

    /**
     * Updates an existing product's details.
     *
     * @param product Product object with updated details
     * @return true if updated successfully, false otherwise
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, price = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId Product ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet row to a Product object.
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product(
                rs.getInt("product_id"),
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getTimestamp("created_at")
        );
        product.setCategoryName(rs.getString("category_name"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        return product;
    }
}
