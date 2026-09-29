package com.vyaapaar.service;

import com.vyaapaar.dao.InventoryDao;
import com.vyaapaar.dao.ProductDao;
import com.vyaapaar.model.Inventory;
import com.vyaapaar.model.Product;

import java.util.List;

/**
 * Service managing Inventory stock tracking, updates, and low-stock alerts.
 */
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final ProductDao productDao;

    public InventoryService() {
        this.inventoryDao = new InventoryDao();
        this.productDao = new ProductDao();
    }

    public InventoryService(InventoryDao inventoryDao, ProductDao productDao) {
        this.inventoryDao = inventoryDao;
        this.productDao = productDao;
    }

    /**
     * Initializes or adds inventory for a product.
     *
     * @param productId Product ID
     * @param initialStock Starting stock
     * @return true if created, false otherwise
     */
    public boolean addInventory(int productId, int initialStock) {
        if (initialStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product ID " + productId + " does not exist.");
        }
        return inventoryDao.createInventory(new Inventory(productId, initialStock));
    }

    /**
     * Retrieves the current available stock for a product.
     *
     * @param productId Product ID
     * @return Available stock quantity
     */
    public int getCurrentStock(int productId) {
        Inventory inv = inventoryDao.getInventoryByProductId(productId);
        return inv != null ? inv.getStockQuantity() : 0;
    }

    /**
     * Updates the stock level for a product, preventing negative values.
     *
     * @param productId Product ID
     * @param newQuantity New stock quantity
     * @return true if updated successfully
     * @throws IllegalArgumentException if quantity is negative or product doesn't exist
     */
    public boolean updateStock(int productId, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        }
        return inventoryDao.updateStock(productId, newQuantity);
    }

    /**
     * Checks whether sufficient stock exists for an order or cart request.
     *
     * @param productId Product ID
     * @param requestedQty Quantity requested
     * @return true if stock >= requestedQty
     */
    public boolean hasSufficientStock(int productId, int requestedQty) {
        if (requestedQty <= 0) {
            return false;
        }
        int available = getCurrentStock(productId);
        return available >= requestedQty;
    }

    /**
     * Retrieves all inventory items with product details.
     *
     * @return List of all Inventory records
     */
     public List<Inventory> getAllInventory() {
         return inventoryDao.getAllInventory();
     }

    /**
     * Retrieves products that have low stock (<= threshold).
     *
     * @param threshold Low stock threshold limit
     * @return List of low stock Inventory items
     */
    public List<Inventory> getLowStockProducts(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        return inventoryDao.getLowStockProducts(threshold);
    }
}
