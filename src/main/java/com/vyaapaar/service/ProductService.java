package com.vyaapaar.service;

import com.vyaapaar.dao.CategoryDao;
import com.vyaapaar.dao.InventoryDao;
import com.vyaapaar.dao.ProductDao;
import com.vyaapaar.model.Category;
import com.vyaapaar.model.Inventory;
import com.vyaapaar.model.Product;

import java.util.List;

/**
 * Service managing Product catalog operations, validations, and category associations.
 */
public class ProductService {

    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final InventoryDao inventoryDao;

    public ProductService() {
        this.productDao = new ProductDao();
        this.categoryDao = new CategoryDao();
        this.inventoryDao = new InventoryDao();
    }

    public ProductService(ProductDao productDao, CategoryDao categoryDao, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
        this.inventoryDao = inventoryDao;
    }

    /**
     * Adds a new product to the catalog with initial stock.
     *
     * @param categoryId ID of the category
     * @param name Product name
     * @param description Product description
     * @param price Unit price
     * @param initialStock Starting inventory quantity
     * @return Created Product object
     * @throws IllegalArgumentException if validation fails
     */
    public Product addProduct(int categoryId, String name, String description, double price, int initialStock) {
        validateProductData(categoryId, name, price);

        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock quantity cannot be negative.");
        }

        Product product = new Product(categoryId, name.trim(), description != null ? description.trim() : "", price);
        boolean created = productDao.addProduct(product);
        if (!created) {
            throw new RuntimeException("Failed to save product in catalog.");
        }

        // Initialize Inventory record for the newly created product
        Inventory inventory = new Inventory(product.getProductId(), initialStock);
        inventoryDao.createInventory(inventory);
        product.setStockQuantity(initialStock);

        return product;
    }

    /**
     * Retrieves all products in the catalog.
     */
    public List<Product> getAllProducts() {
        return productDao != null ? productDao.getAllProducts() : new java.util.ArrayList<>();
    }

    /**
     * Retrieves a single product by ID.
     */
    public Product getProductById(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        if (productDao == null) {
            return null;
        }
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " was not found.");
        }
        return product;
    }

    /**
     * Searches products by keyword in name, description, or category.
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDao != null ? productDao.searchProducts(keyword.trim()) : new java.util.ArrayList<>();
    }

    /**
     * Retrieves products belonging to a category.
     */
    public List<Product> getProductsByCategory(int categoryId) {
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " does not exist.");
        }
        return productDao.getProductsByCategory(categoryId);
    }

    /**
     * Updates an existing product's information.
     */
    public boolean updateProduct(int productId, int categoryId, String name, String description, double price) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        validateProductData(categoryId, name, price);

        Product existing = productDao.getProductById(productId);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        Product updated = new Product(productId, categoryId, name.trim(), description != null ? description.trim() : "", price, null);
        return productDao.updateProduct(updated);
    }

    /**
     * Deletes a product by ID.
     */
    public boolean deleteProduct(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        Product existing = productDao.getProductById(productId);
        if (existing == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        }
        return productDao.deleteProduct(productId);
    }

    /**
     * Validates product fields and checks category existence.
     */
    private void validateProductData(int categoryId, String name, double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price <= 0.0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Invalid Category ID: " + categoryId + ". Category does not exist.");
        }
    }
}
