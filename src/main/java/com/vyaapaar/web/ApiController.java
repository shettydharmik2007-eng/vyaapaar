package com.vyaapaar.web;

import com.sun.net.httpserver.HttpExchange;
import com.vyaapaar.model.*;
import com.vyaapaar.service.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST API Controller that bridges HTTP requests to Vyaapaar Service Layer.
 */
public class ApiController {

    private final AuthService authService = new AuthService();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final InventoryService inventoryService = new InventoryService();
    private final CartService cartService = new CartService();
    private final OrderService orderService = new OrderService();

    // GET /api/products
    public void handleGetProducts(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String keyword = queryParams.get("q");
            String categoryIdStr = queryParams.get("categoryId");

            List<Product> products;
            if (keyword != null && !keyword.trim().isEmpty()) {
                products = productService.searchProducts(keyword);
            } else {
                products = productService.getAllProducts();
            }

            if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                try {
                    int catId = Integer.parseInt(categoryIdStr.trim());
                    products = products.stream().filter(p -> p.getCategoryId() == catId).toList();
                } catch (NumberFormatException ignored) {}
            }

            sendJsonResponse(exchange, 200, JsonUtil.productsToJson(products));
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Failed to load products: " + e.getMessage());
        }
    }

    // GET /api/products/{id}
    public void handleGetProductById(HttpExchange exchange, int productId) throws IOException {
        try {
            Product product = productService.getProductById(productId);
            if (product != null) {
                int stock = inventoryService.getCurrentStock(productId);
                product.setStockQuantity(stock);
                sendJsonResponse(exchange, 200, JsonUtil.productToJson(product));
            } else {
                sendErrorResponse(exchange, 404, "Product not found with ID: " + productId);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error retrieving product: " + e.getMessage());
        }
    }

    // GET /api/categories
    public void handleGetCategories(HttpExchange exchange) throws IOException {
        try {
            List<Category> categories = categoryService.getAllCategories();
            sendJsonResponse(exchange, 200, JsonUtil.categoriesToJson(categories));
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Failed to load categories: " + e.getMessage());
        }
    }

    // POST /api/auth/register
    public void handleRegister(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            String fullName = data.get("fullName");
            String email = data.get("email");
            String password = data.get("password");
            String phone = data.get("phone");
            String address = data.get("address");

            User user = authService.registerCustomer(fullName, email, password, phone, address);
            if (user != null) {
                String response = String.format("{\"success\":true,\"message\":\"Registration successful! You can now log in.\",\"user\":%s}",
                        JsonUtil.userToJson(user));
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 400, "Registration failed. Please verify your details.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Registration error: " + e.getMessage());
        }
    }

    // POST /api/auth/login
    public void handleLogin(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            String email = data.get("email");
            String password = data.get("password");

            User user = authService.login(email, password);
            if (user != null) {
                String response = String.format("{\"success\":true,\"message\":\"Login successful!\",\"user\":%s}",
                        JsonUtil.userToJson(user));
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 401, "Invalid email or password.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Login error: " + e.getMessage());
        }
    }

    // GET /api/cart
    public void handleGetCart(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String userIdStr = queryParams.get("userId");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                sendErrorResponse(exchange, 400, "User ID is required to access cart.");
                return;
            }

            int userId = Integer.parseInt(userIdStr.trim());
            Cart cart = cartService.getOrCreateCart(userId);
            double total = cart != null ? cartService.calculateCartTotal(cart.getItems()) : 0.0;
            sendJsonResponse(exchange, 200, JsonUtil.cartToJson(cart, total));
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Failed to retrieve cart: " + e.getMessage());
        }
    }

    // POST /api/cart/add
    public void handleAddToCart(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            int userId = Integer.parseInt(data.getOrDefault("userId", "0"));
            int productId = Integer.parseInt(data.getOrDefault("productId", "0"));
            int quantity = Integer.parseInt(data.getOrDefault("quantity", "1"));

            if (userId <= 0) {
                sendErrorResponse(exchange, 401, "Please log in to add items to your cart.");
                return;
            }

            boolean success = cartService.addToCart(userId, productId, quantity);
            if (success) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Product added to cart successfully!\"}");
            } else {
                sendErrorResponse(exchange, 400, "Failed to add product to cart. Please check stock availability.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Cart error: " + e.getMessage());
        }
    }

    // POST /api/cart/update
    public void handleUpdateCart(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            int userId = Integer.parseInt(data.getOrDefault("userId", "0"));
            int productId = Integer.parseInt(data.getOrDefault("productId", "0"));
            int quantity = Integer.parseInt(data.getOrDefault("quantity", "1"));

            if (userId <= 0) {
                sendErrorResponse(exchange, 401, "Please log in to update cart.");
                return;
            }

            boolean success = cartService.updateProductQuantity(userId, productId, quantity);
            if (success) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Cart updated successfully!\"}");
            } else {
                sendErrorResponse(exchange, 400, "Failed to update quantity. Insufficient stock or invalid quantity.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Cart update error: " + e.getMessage());
        }
    }

    // POST /api/cart/remove
    public void handleRemoveFromCart(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            int userId = Integer.parseInt(data.getOrDefault("userId", "0"));
            int productId = Integer.parseInt(data.getOrDefault("productId", "0"));

            if (userId <= 0) {
                sendErrorResponse(exchange, 401, "Please log in to modify cart.");
                return;
            }

            boolean success = cartService.removeProductFromCart(userId, productId);
            if (success) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Item removed from cart.\"}");
            } else {
                sendErrorResponse(exchange, 400, "Failed to remove item from cart.");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Cart removal error: " + e.getMessage());
        }
    }

    // POST /api/checkout
    public void handleCheckout(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = JsonUtil.parseJson(body);

            int userId = Integer.parseInt(data.getOrDefault("userId", "0"));
            String shippingAddress = data.get("shippingAddress");

            if (userId <= 0) {
                sendErrorResponse(exchange, 401, "Please log in to place an order.");
                return;
            }

            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                sendErrorResponse(exchange, 400, "Shipping address is required.");
                return;
            }

            Order placedOrder = orderService.checkout(userId, shippingAddress);
            if (placedOrder != null) {
                String response = String.format("{\"success\":true,\"message\":\"Order placed successfully!\",\"order\":%s}",
                        JsonUtil.orderToJson(placedOrder));
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 400, "Checkout transaction failed. Please check your cart and stock.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Checkout error: " + e.getMessage());
        }
    }

    // GET /api/orders
    public void handleGetOrders(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String userIdStr = queryParams.get("userId");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                sendErrorResponse(exchange, 400, "User ID is required to view order history.");
                return;
            }

            int userId = Integer.parseInt(userIdStr.trim());
            List<Order> orders = orderService.getOrdersByUserId(userId);
            sendJsonResponse(exchange, 200, JsonUtil.ordersToJson(orders));
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Failed to retrieve orders: " + e.getMessage());
        }
    }

    // Helper: Parse URL query parameters
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.trim().isEmpty()) return map;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = java.net.URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String val = java.net.URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    map.put(key, val);
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = String.format("{\"success\":false,\"error\":\"%s\"}", JsonUtil.escape(message));
        sendJsonResponse(exchange, statusCode, json);
    }
}
