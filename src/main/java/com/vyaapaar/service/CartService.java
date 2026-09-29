package com.vyaapaar.service;

import com.vyaapaar.dao.CartDao;
import com.vyaapaar.dao.InventoryDao;
import com.vyaapaar.dao.ProductDao;
import com.vyaapaar.model.Cart;
import com.vyaapaar.model.CartItem;
import com.vyaapaar.model.Inventory;
import com.vyaapaar.model.Product;

import java.util.List;

/**
 * Service managing customer cart operations, stock availability checks, and price calculations.
 */
public class CartService {

    private final CartDao cartDao;
    private final ProductDao productDao;
    private final InventoryDao inventoryDao;

    public CartService() {
        this.cartDao = new CartDao();
        this.productDao = new ProductDao();
        this.inventoryDao = new InventoryDao();
    }

    public CartService(CartDao cartDao, ProductDao productDao, InventoryDao inventoryDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.inventoryDao = inventoryDao;
    }

    /**
     * Retrieves or creates an active cart for a customer.
     *
     * @param userId User ID
     * @return Cart object with refreshed items
     */
    public Cart getOrCreateCart(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return cartDao.getOrCreateCartByUserId(userId);
    }

    /**
     * Adds a product to the user's cart after validating product existence and stock availability.
     *
     * @param userId User ID
     * @param productId Product ID
     * @param quantity Quantity to add
     * @return true if added successfully
     * @throws IllegalArgumentException if product not found, invalid quantity, or insufficient stock
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        }

        Inventory inventory = inventoryDao.getInventoryByProductId(productId);
        int availableStock = inventory != null ? inventory.getStockQuantity() : 0;

        if (availableStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Requested: " + quantity + ", Available: " + availableStock);
        }

        Cart cart = getOrCreateCart(userId);
        return cartDao.addCartItem(cart.getCartId(), productId, quantity);
    }

    /**
     * Updates quantity of an existing item in cart.
     *
     * @param cartItemId Cart Item ID
     * @param productId Product ID
     * @param newQuantity New quantity
     * @return true if updated successfully
     */
    public boolean updateItemQuantity(int cartItemId, int productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        Inventory inventory = inventoryDao.getInventoryByProductId(productId);
        int availableStock = inventory != null ? inventory.getStockQuantity() : 0;

        if (availableStock < newQuantity) {
            throw new IllegalArgumentException("Cannot update quantity. Available stock: " + availableStock);
        }

        return cartDao.updateCartItemQuantity(cartItemId, newQuantity);
    }

    /**
     * Updates the quantity of a product in the user's cart by productId.
     *
     * @param userId User ID
     * @param productId Product ID
     * @param newQuantity New quantity
     * @return true if updated successfully
     */
    public boolean updateProductQuantity(int userId, int productId, int newQuantity) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getProductId() == productId) {
                    return updateItemQuantity(item.getCartItemId(), productId, newQuantity);
                }
            }
        }
        throw new IllegalArgumentException("Product ID " + productId + " is not in your cart.");
    }

    /**
     * Removes a product from the user's cart by productId.
     *
     * @param userId User ID
     * @param productId Product ID
     * @return true if removed successfully
     */
    public boolean removeProductFromCart(int userId, int productId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getProductId() == productId) {
                    return removeItem(item.getCartItemId());
                }
            }
        }
        throw new IllegalArgumentException("Product ID " + productId + " is not in your cart.");
    }

    /**
     * Removes an item from the cart by its cartItemId.
     *
     * @param cartItemId Cart Item ID
     * @return true if removed successfully
     */
    public boolean removeItem(int cartItemId) {
        if (cartItemId <= 0) {
            throw new IllegalArgumentException("Invalid cart item ID.");
        }
        return cartDao.removeCartItem(cartItemId);
    }

    /**
     * Clears all items from a customer's cart.
     */
    public boolean clearCart(int cartId) {
        if (cartId <= 0) {
            throw new IllegalArgumentException("Invalid cart ID.");
        }
        return cartDao.clearCart(cartId);
    }

    /**
     * Calculates the total amount for a list of cart items.
     *
     * @param items List of CartItem objects
     * @return Total monetary sum
     */
    public double calculateCartTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
