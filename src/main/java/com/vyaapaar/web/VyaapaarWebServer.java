package com.vyaapaar.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Built-in HTTP Web Server for Vyaapaar E-Commerce Customer Website.
 * Implemented using standard Java SE 17 com.sun.net.httpserver.HttpServer without external web frameworks.
 */
public class VyaapaarWebServer {

    public static final int DEFAULT_PORT = 8080;
    private HttpServer server;
    private final ApiController apiController = new ApiController();

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        VyaapaarWebServer webServer = new VyaapaarWebServer();
        webServer.start(port);
    }

    public synchronized void start(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(10));

            // API Route Handler
            server.createContext("/api/", new ApiHandler());

            // Static File Route Handler
            server.createContext("/", new StaticFileHandler());

            server.start();

            System.out.println("========================================================================");
            System.out.println("          VYAAPAAR - E-COMMERCE CUSTOMER WEB APPLICATION                ");
            System.out.println("========================================================================");
            System.out.println(">>> [STATUS] Vyaapaar Web Server is ACTIVE and RUNNING!");
            System.out.println(">>> [URL]    http://localhost:" + port + "/");
            System.out.println(">>> [TECH]   Java SE 17 HttpServer + JDBC + MySQL Relational Engine");
            System.out.println("========================================================================");
            System.out.println("Available Pages:");
            System.out.println("  1. Home Page        : http://localhost:" + port + "/");
            System.out.println("  2. Products Catalog : http://localhost:" + port + "/products.html");
            System.out.println("  3. Customer Login   : http://localhost:" + port + "/login.html");
            System.out.println("  4. Customer Register: http://localhost:" + port + "/register.html");
            System.out.println("  5. Shopping Cart    : http://localhost:" + port + "/cart.html");
            System.out.println("  6. Checkout         : http://localhost:" + port + "/checkout.html");
            System.out.println("  7. Order History    : http://localhost:" + port + "/orders.html");
            System.out.println("========================================================================");

        } catch (IOException e) {
            if (port == DEFAULT_PORT) {
                System.out.println("Port " + DEFAULT_PORT + " is busy. Trying fallback port 8085...");
                start(8085);
            } else {
                System.err.println("Fatal: Failed to start web server on port " + port + ": " + e.getMessage());
            }
        }
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Vyaapaar Web Server stopped.");
        }
    }

    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            // Handle pre-flight CORS requests
            if ("OPTIONS".equals(method)) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if (path.equals("/api/products") && "GET".equals(method)) {
                    apiController.handleGetProducts(exchange);
                } else if (path.startsWith("/api/products/") && "GET".equals(method)) {
                    String idStr = path.substring("/api/products/".length());
                    int productId = Integer.parseInt(idStr);
                    apiController.handleGetProductById(exchange, productId);
                } else if (path.equals("/api/categories") && "GET".equals(method)) {
                    apiController.handleGetCategories(exchange);
                } else if (path.equals("/api/auth/register") && "POST".equals(method)) {
                    apiController.handleRegister(exchange);
                } else if (path.equals("/api/auth/login") && "POST".equals(method)) {
                    apiController.handleLogin(exchange);
                } else if (path.equals("/api/cart") && "GET".equals(method)) {
                    apiController.handleGetCart(exchange);
                } else if (path.equals("/api/cart/add") && "POST".equals(method)) {
                    apiController.handleAddToCart(exchange);
                } else if (path.equals("/api/cart/update") && "POST".equals(method)) {
                    apiController.handleUpdateCart(exchange);
                } else if (path.equals("/api/cart/remove") && "POST".equals(method)) {
                    apiController.handleRemoveFromCart(exchange);
                } else if (path.equals("/api/checkout") && "POST".equals(method)) {
                    apiController.handleCheckout(exchange);
                } else if (path.equals("/api/orders") && "GET".equals(method)) {
                    apiController.handleGetOrders(exchange);
                } else {
                    send404(exchange, "API Endpoint not found: " + path);
                }
            } catch (NumberFormatException e) {
                send400(exchange, "Invalid numeric path parameter.");
            } catch (Exception e) {
                send500(exchange, "Internal server error: " + e.getMessage());
            }
        }
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/")) {
                path = "/index.html";
            }

            // Remove leading slash for resource loader
            String resourcePath = "static" + path;
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);

            // Fallback: check file system if not in classpath resources
            if (is == null) {
                File localFile = new File("src/main/resources/" + resourcePath);
                if (localFile.exists() && localFile.isFile()) {
                    is = new FileInputStream(localFile);
                }
            }

            if (is == null) {
                send404(exchange, "Page not found: " + path);
                return;
            }

            byte[] content = is.readAllBytes();
            is.close();

            String contentType = getMimeType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }

        private String getMimeType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            if (path.endsWith(".svg")) return "image/svg+xml";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }

    private void send400(HttpExchange exchange, String msg) throws IOException {
        byte[] bytes = String.format("{\"success\":false,\"error\":\"%s\"}", JsonUtil.escape(msg)).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(400, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void send404(HttpExchange exchange, String msg) throws IOException {
        byte[] bytes = String.format("{\"success\":false,\"error\":\"%s\"}", JsonUtil.escape(msg)).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(404, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void send500(HttpExchange exchange, String msg) throws IOException {
        byte[] bytes = String.format("{\"success\":false,\"error\":\"%s\"}", JsonUtil.escape(msg)).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(500, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
