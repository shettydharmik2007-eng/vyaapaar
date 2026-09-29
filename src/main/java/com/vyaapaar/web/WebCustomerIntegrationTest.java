package com.vyaapaar.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Automated HTTP Integration Test Suite for Vyaapaar Customer Website.
 * Tests end-to-end customer workflow over actual HTTP/REST endpoints.
 */
public class WebCustomerIntegrationTest {

    private static String baseUrl = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            baseUrl = args[0].trim();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }
        System.out.println("==========================================================================");
        System.out.println("     VYAAPAAR - CUSTOMER WEBSITE HTTP END-TO-END INTEGRATION TEST SUITE    ");
        System.out.println(" Target Base URL: " + baseUrl);
        System.out.println("==========================================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: Static HTML Pages
        System.out.print("[TEST 1] GET / (Home Page HTML) ... ");
        if (testHttpGet("/", 200, "VYAAPAAR")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.print("[TEST 2] GET /products.html ... ");
        if (testHttpGet("/products.html", 200, "Products Catalog")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.print("[TEST 3] GET /product-details.html ... ");
        if (testHttpGet("/product-details.html", 200, "Product Details")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.print("[TEST 4] GET /cart.html ... ");
        if (testHttpGet("/cart.html", 200, "Shopping Cart")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.print("[TEST 5] GET /checkout.html ... ");
        if (testHttpGet("/checkout.html", 200, "Order Checkout")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.print("[TEST 6] GET /orders.html ... ");
        if (testHttpGet("/orders.html", 200, "Order History")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 2: Categories API
        System.out.print("[TEST 7] GET /api/categories ... ");
        if (testHttpGet("/api/categories", 200, "Electronics")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 3: Products API (Real Database Query)
        System.out.print("[TEST 8] GET /api/products (All Products) ... ");
        if (testHttpGet("/api/products", 200, "productId")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 4: Product by ID API
        System.out.print("[TEST 9] GET /api/products/1 (Product Details) ... ");
        if (testHttpGet("/api/products/1", 200, "Wireless Bluetooth Headphones")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 5: Search Products API
        System.out.print("[TEST 10] GET /api/products?q=Wireless (Search Query) ... ");
        if (testHttpGet("/api/products?q=Wireless", 200, "Wireless")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 6: Customer Registration
        String uniqueEmail = "webuser" + System.currentTimeMillis() + "@gmail.com";
        String regJson = String.format("{\"fullName\":\"Siddharth Rao\",\"email\":\"%s\",\"password\":\"pass123\",\"phone\":\"9876543210\",\"address\":\"#42 Palm Grove, Bengaluru\"}", uniqueEmail);
        System.out.print("[TEST 11] POST /api/auth/register (Customer Registration) ... ");
        String regResponse = testHttpPost("/api/auth/register", regJson, 200);
        if (regResponse != null && regResponse.contains("\"success\":true")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED: " + regResponse);
            failed++;
        }

        // Test 7: Duplicate Email Registration Prevention
        System.out.print("[TEST 12] POST /api/auth/register (Duplicate Email Block) ... ");
        String dupResponse = testHttpPost("/api/auth/register", regJson, 400);
        if (dupResponse != null && dupResponse.contains("already exists")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED: " + dupResponse);
            failed++;
        }

        // Test 8: Customer Login
        String loginJson = String.format("{\"email\":\"%s\",\"password\":\"pass123\"}", uniqueEmail);
        System.out.print("[TEST 13] POST /api/auth/login (Customer Authentication) ... ");
        String loginResponse = testHttpPost("/api/auth/login", loginJson, 200);
        int userId = 0;
        if (loginResponse != null && loginResponse.contains("\"success\":true")) {
            // Extract user ID
            int idx = loginResponse.indexOf("\"userId\":");
            if (idx != -1) {
                int comma = loginResponse.indexOf(",", idx);
                int brace = loginResponse.indexOf("}", idx);
                int end = (comma != -1 && comma < brace) ? comma : brace;
                userId = Integer.parseInt(loginResponse.substring(idx + 9, end).trim());
            }
            System.out.println("PASSED (User ID: " + userId + ")");
            passed++;
        } else {
            System.out.println("FAILED: " + loginResponse);
            failed++;
        }

        // Test 9: Invalid Password Login Handling
        String badLoginJson = String.format("{\"email\":\"%s\",\"password\":\"wrongpassword\"}", uniqueEmail);
        System.out.print("[TEST 14] POST /api/auth/login (Invalid Password Error Handling) ... ");
        String badLoginResponse = testHttpPost("/api/auth/login", badLoginJson, 401);
        if (badLoginResponse != null && badLoginResponse.contains("Invalid email or password")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED: " + badLoginResponse);
            failed++;
        }

        if (userId > 0) {
            // Test 10: Add to Cart
            String addCartJson = String.format("{\"userId\":%d,\"productId\":1,\"quantity\":2}", userId);
            System.out.print("[TEST 15] POST /api/cart/add (Add to Cart) ... ");
            String addCartRes = testHttpPost("/api/cart/add", addCartJson, 200);
            if (addCartRes != null && addCartRes.contains("\"success\":true")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED: " + addCartRes);
                failed++;
            }

            // Test 11: Get Cart
            System.out.print("[TEST 16] GET /api/cart (Fetch Active Cart) ... ");
            if (testHttpGet("/api/cart?userId=" + userId, 200, "Wireless Bluetooth Headphones")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 12: Update Cart Quantity
            String updateCartJson = String.format("{\"userId\":%d,\"productId\":1,\"quantity\":3}", userId);
            System.out.print("[TEST 17] POST /api/cart/update (Update Quantity) ... ");
            String upCartRes = testHttpPost("/api/cart/update", updateCartJson, 200);
            if (upCartRes != null && upCartRes.contains("\"success\":true")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED: " + upCartRes);
                failed++;
            }

            // Test 13: Checkout ACID Transaction
            String checkoutJson = String.format("{\"userId\":%d,\"shippingAddress\":\"#42 Palm Grove, Bengaluru 560038\"}", userId);
            System.out.print("[TEST 18] POST /api/checkout (Atomic Checkout Transaction) ... ");
            String checkoutRes = testHttpPost("/api/checkout", checkoutJson, 200);
            if (checkoutRes != null && checkoutRes.contains("\"success\":true") && checkoutRes.contains("\"orderId\":")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED: " + checkoutRes);
                failed++;
            }

            // Test 14: Cart Cleared After Checkout
            System.out.print("[TEST 19] GET /api/cart (Verify Cart Cleared Post-Checkout) ... ");
            if (testHttpGet("/api/cart?userId=" + userId, 200, "\"items\":[]")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 15: Order History
            System.out.print("[TEST 20] GET /api/orders (View Customer Order History) ... ");
            if (testHttpGet("/api/orders?userId=" + userId, 200, "orderId")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }
        }

        // Test 16: Admin Authentication
        System.out.print("[TEST 21] POST /api/auth/login (Admin Login Verification) ... ");
        String adminLoginJson = "{\"email\":\"admin@vyaapaar.com\",\"password\":\"admin123\"}";
        String adminLoginRes = testHttpPost("/api/auth/login", adminLoginJson, 200);
        if (adminLoginRes != null && adminLoginRes.contains("\"success\":true") && adminLoginRes.contains("\"role\":\"ADMIN\"")) {
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED: " + adminLoginRes);
            failed++;
        }

        System.out.println("==========================================================================");
        System.out.printf(">>> [SUMMARY] %d of %d tests PASSED. (Failed: %d)%n", passed, (passed + failed), failed);
        System.out.println("==========================================================================");
    }

    private static boolean testHttpGet(String path, int expectedStatus, String expectedSubstring) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.statusCode() == expectedStatus && response.body().contains(expectedSubstring);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
    }

    private static String testHttpPost(String path, String jsonBody, int expectedStatus) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == expectedStatus) {
                return response.body();
            } else {
                return "HTTP " + response.statusCode() + ": " + response.body();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
