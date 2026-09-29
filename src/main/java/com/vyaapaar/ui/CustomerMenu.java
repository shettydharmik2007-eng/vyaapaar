package com.vyaapaar.ui;

import com.vyaapaar.model.Cart;
import com.vyaapaar.model.CartItem;
import com.vyaapaar.model.Order;
import com.vyaapaar.model.OrderItem;
import com.vyaapaar.model.Product;
import com.vyaapaar.model.User;
import com.vyaapaar.service.CartService;
import com.vyaapaar.service.OrderService;
import com.vyaapaar.service.ProductService;

import java.util.List;

/**
 * Interactive Console Menu for Customer operations.
 */
public class CustomerMenu {

    private final User currentUser;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    public CustomerMenu(User currentUser) {
        this.currentUser = currentUser;
        this.productService = new ProductService();
        this.cartService = new CartService();
        this.orderService = new OrderService();
    }

    public void show() {
        boolean active = true;
        while (active) {
            ConsoleUtils.printHeader("CUSTOMER DASHBOARD | Welcome, " + currentUser.getFullName());
            System.out.println("1. View Products");
            System.out.println("2. Search Products");
            System.out.println("3. Add to Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Update Cart");
            System.out.println("6. Remove from Cart");
            System.out.println("7. Place Order (Checkout)");
            System.out.println("8. View Order History");
            System.out.println("9. Logout");
            ConsoleUtils.printDivider();

            int choice = ConsoleUtils.readInt("Select an option (1-9): ");

            switch (choice) {
                case 1 -> viewAllProducts();
                case 2 -> searchProducts();
                case 3 -> addToCart();
                case 4 -> viewCart();
                case 5 -> updateCart();
                case 6 -> removeFromCart();
                case 7 -> placeOrder();
                case 8 -> viewOrderHistory();
                case 9 -> {
                    ConsoleUtils.printSuccess("You have been logged out.");
                    active = false;
                }
                default -> ConsoleUtils.printError("Invalid option! Please select between 1 and 9.");
            }
        }
    }

    private void viewAllProducts() {
        ConsoleUtils.printHeader("AVAILABLE PRODUCTS CATALOG");
        try {
            List<Product> products = productService.getAllProducts();
            displayProductsTable(products);
        } catch (Exception e) {
            ConsoleUtils.printError("Failed to fetch products: " + e.getMessage());
        }
    }

    private void searchProducts() {
        ConsoleUtils.printHeader("SEARCH PRODUCTS");
        String keyword = ConsoleUtils.readString("Enter keyword to search (name, description, category): ");
        try {
            List<Product> results = productService.searchProducts(keyword);
            if (results.isEmpty()) {
                System.out.println("No products found matching: '" + keyword + "'");
            } else {
                displayProductsTable(results);
            }
        } catch (Exception e) {
            ConsoleUtils.printError("Search error: " + e.getMessage());
        }
    }

    private void addToCart() {
        ConsoleUtils.printHeader("ADD PRODUCT TO CART");
        int productId = ConsoleUtils.readPositiveInt("Enter Product ID to add: ");
        int quantity = ConsoleUtils.readPositiveInt("Enter Quantity: ");

        try {
            boolean added = cartService.addToCart(currentUser.getUserId(), productId, quantity);
            if (added) {
                ConsoleUtils.printSuccess("Product added to cart successfully!");
            } else {
                ConsoleUtils.printError("Failed to add product to cart.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void viewCart() {
        ConsoleUtils.printHeader("MY SHOPPING CART");
        try {
            Cart cart = cartService.getOrCreateCart(currentUser.getUserId());
            List<CartItem> items = cart.getItems();

            if (items == null || items.isEmpty()) {
                System.out.println("Your cart is currently empty.");
                return;
            }

            System.out.printf("%-10s | %-28s | %-8s | %-12s | %-12s%n",
                    "Prod ID", "Product Name", "Qty", "Unit Price", "Subtotal");
            ConsoleUtils.printDivider();

            for (CartItem item : items) {
                System.out.printf("%-10d | %-28s | %-8d | ₹%-11.2f | ₹%-11.2f%n",
                        item.getProductId(),
                        truncate(item.getProductName(), 28),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal());
            }

            ConsoleUtils.printDivider();
            double grandTotal = cartService.calculateCartTotal(items);
            System.out.printf("GRAND TOTAL: ₹%.2f%n", grandTotal);
        } catch (Exception e) {
            ConsoleUtils.printError("Error displaying cart: " + e.getMessage());
        }
    }

    private void updateCart() {
        ConsoleUtils.printHeader("UPDATE CART ITEM QUANTITY");
        int productId = ConsoleUtils.readPositiveInt("Enter Product ID to update: ");
        int newQuantity = ConsoleUtils.readPositiveInt("Enter New Quantity: ");

        try {
            boolean updated = cartService.updateProductQuantity(currentUser.getUserId(), productId, newQuantity);
            if (updated) {
                ConsoleUtils.printSuccess("Cart updated successfully!");
            } else {
                ConsoleUtils.printError("Failed to update cart item.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void removeFromCart() {
        ConsoleUtils.printHeader("REMOVE ITEM FROM CART");
        int productId = ConsoleUtils.readPositiveInt("Enter Product ID to remove: ");

        try {
            boolean removed = cartService.removeProductFromCart(currentUser.getUserId(), productId);
            if (removed) {
                ConsoleUtils.printSuccess("Product removed from cart!");
            } else {
                ConsoleUtils.printError("Failed to remove product from cart.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void placeOrder() {
        ConsoleUtils.printHeader("CHECKOUT & PLACE ORDER");
        try {
            Cart cart = cartService.getOrCreateCart(currentUser.getUserId());
            List<CartItem> items = cart.getItems();

            if (items == null || items.isEmpty()) {
                ConsoleUtils.printError("Your cart is empty. Add items before placing an order.");
                return;
            }

            viewCart();

            System.out.println("\nDelivery Address: " + currentUser.getAddress());
            boolean useDefaultAddress = ConsoleUtils.readConfirmation("Use registered address for delivery? (Y/N): ");
            String shippingAddress = currentUser.getAddress();
            if (!useDefaultAddress) {
                shippingAddress = ConsoleUtils.readString("Enter new shipping address: ");
            }

            boolean confirm = ConsoleUtils.readConfirmation("\nAre you sure you want to place this order? (Y/N): ");
            if (!confirm) {
                System.out.println("Checkout cancelled.");
                return;
            }

            Order placedOrder = orderService.checkout(currentUser.getUserId(), shippingAddress);
            ConsoleUtils.printHeader("ORDER PLACED SUCCESSFULLY!");
            System.out.println("Order ID     : #" + placedOrder.getOrderId());
            System.out.printf("Total Amount : ₹%.2f%n", placedOrder.getTotalAmount());
            System.out.println("Status       : " + placedOrder.getOrderStatus());
            System.out.println("Delivery To  : " + placedOrder.getShippingAddress());
            ConsoleUtils.printSuccess("Thank you for shopping with Vyaapaar!");
        } catch (Exception e) {
            ConsoleUtils.printError("Checkout failed: " + e.getMessage());
        }
    }

    private void viewOrderHistory() {
        ConsoleUtils.printHeader("MY ORDER HISTORY");
        try {
            List<Order> orders = orderService.getOrdersByUserId(currentUser.getUserId());
            if (orders.isEmpty()) {
                System.out.println("You have not placed any orders yet.");
                return;
            }

            for (Order order : orders) {
                ConsoleUtils.printDivider();
                System.out.printf("Order #%-5d | Date: %-19s | Status: %-10s | Total: ₹%.2f%n",
                        order.getOrderId(),
                        order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 19) : "N/A",
                        order.getOrderStatus(),
                        order.getTotalAmount());
                System.out.println("Shipping Address: " + order.getShippingAddress());

                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    System.out.println("Purchased Items:");
                    for (OrderItem item : order.getItems()) {
                        System.out.printf("   - %-25s x %-3d @ ₹%-8.2f = ₹%.2f%n",
                                item.getProductName() != null ? item.getProductName() : "Product #" + item.getProductId(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal());
                    }
                }
            }
            ConsoleUtils.printDivider();
        } catch (Exception e) {
            ConsoleUtils.printError("Error fetching order history: " + e.getMessage());
        }
    }

    private void displayProductsTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products available in the catalog.");
            return;
        }

        System.out.printf("%-6s | %-28s | %-18s | %-10s | %-8s%n",
                "ID", "Product Name", "Category", "Price", "Stock");
        ConsoleUtils.printDivider();

        for (Product p : products) {
            System.out.printf("%-6d | %-28s | %-18s | ₹%-9.2f | %-8d%n",
                    p.getProductId(),
                    truncate(p.getName(), 28),
                    truncate(p.getCategoryName() != null ? p.getCategoryName() : "General", 18),
                    p.getPrice(),
                    p.getStockQuantity());
        }
        ConsoleUtils.printDivider();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
