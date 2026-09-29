package com.vyaapaar.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class to manage JDBC Database Connections to MySQL.
 * Reads database configuration from db.properties file.
 * Automatically falls back to an in-memory MySQL-compatible database if local MySQL is offline.
 */
public class DatabaseConnection {

    private static final Properties properties = new Properties();
    private static volatile boolean useFallback = false;
    private static final String H2_URL = "jdbc:h2:mem:vyaapaar_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    private static boolean schemaInitialized = false;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Warning: db.properties not found on classpath. Using fallback connection properties.");
            }
            // Register MySQL Driver
            String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            }

            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ignored) {}
        } catch (IOException e) {
            System.err.println("Error reading db.properties: " + e.getMessage());
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    /**
     * Obtains a connection to the MySQL database or active fallback database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!useFallback) {
            try {
                String envUrl = System.getenv("DB_URL");
                String envUser = System.getenv("DB_USER");
                String envPassword = System.getenv("DB_PASSWORD");

                String url = (envUrl != null && !envUrl.trim().isEmpty()) ? envUrl : properties.getProperty("db.url", "jdbc:mysql://localhost:3306/vyaapaar_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                String user = (envUser != null && !envUser.trim().isEmpty()) ? envUser : properties.getProperty("db.user", "root");
                String password = (envPassword != null) ? envPassword : properties.getProperty("db.password", "root");

                DriverManager.setLoginTimeout(2);
                Connection conn = DriverManager.getConnection(url, user, password);
                return conn;
            } catch (SQLException e) {
                System.out.println(">>> [DATABASE NOTICE] Primary database offline or unreachable (" + e.getMessage() + ").");
                System.out.println(">>> Initializing in-memory relational database fallback with schema and seed data...");
                useFallback = true;
            }
        }

        Connection h2Conn = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        initializeSchemaIfNeeded(h2Conn);
        return h2Conn;
    }

    private static synchronized void initializeSchemaIfNeeded(Connection conn) {
        if (schemaInitialized) return;
        try (Statement stmt = conn.createStatement()) {
            // Create tables compatible with MySQL & H2
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', " +
                    "phone VARCHAR(20), " +
                    "address TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "category_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "category_name VARCHAR(100) NOT NULL UNIQUE, " +
                    "description TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "product_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "category_id INT NOT NULL, " +
                    "name VARCHAR(150) NOT NULL, " +
                    "description TEXT, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (category_id) REFERENCES categories(category_id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (" +
                    "inventory_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "product_id INT NOT NULL UNIQUE, " +
                    "stock_quantity INT NOT NULL DEFAULT 0, " +
                    "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE, " +
                    "CHECK (stock_quantity >= 0))");

            stmt.execute("CREATE TABLE IF NOT EXISTS cart (" +
                    "cart_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS cart_items (" +
                    "cart_item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "cart_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL DEFAULT 1, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE, " +
                    "UNIQUE (cart_id, product_id), " +
                    "CHECK (quantity > 0))");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "total_amount DECIMAL(10, 2) NOT NULL, " +
                    "order_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', " +
                    "shipping_address TEXT NOT NULL, " +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "order_item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "unit_price DECIMAL(10, 2) NOT NULL, " +
                    "subtotal DECIMAL(10, 2) NOT NULL, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id), " +
                    "CHECK (quantity > 0))");

            // Seed Users
            stmt.execute("MERGE INTO users (user_id, full_name, email, password, role, phone, address) KEY(email) VALUES " +
                    "(1, 'System Administrator', 'admin@vyaapaar.com', 'admin123', 'ADMIN', '9876543210', 'Vyaapaar Tech Hub, Bengaluru'), " +
                    "(2, 'Rahul Sharma', 'rahul@gmail.com', 'rahul123', 'CUSTOMER', '9811122233', 'Flat 402, Green Glen Layout, Bellandur, Bengaluru'), " +
                    "(3, 'Priya Nair', 'priya@gmail.com', 'priya123', 'CUSTOMER', '9844455566', 'House No 12, 4th Main, Indiranagar, Bengaluru')");

            // Seed Categories
            stmt.execute("MERGE INTO categories (category_id, category_name, description) KEY(category_name) VALUES " +
                    "(1, 'Electronics', 'Gadgets, accessories, smart devices, and hardware components'), " +
                    "(2, 'Clothing & Fashion', 'Men and Women casual, formal, and ethnic apparel'), " +
                    "(3, 'Home & Kitchen', 'Appliances, cookware, dining, and daily home essentials'), " +
                    "(4, 'Books & Stationery', 'Academic textbooks, fiction novels, notebooks, and office supplies')");

            // Seed Products
            stmt.execute("MERGE INTO products (product_id, category_id, name, description, price) KEY(product_id) VALUES " +
                    "(1, 1, 'Wireless Bluetooth Headphones', 'Over-ear active noise cancelling headphones with 30h battery life', 2499.00), " +
                    "(2, 1, 'USB-C Fast Charging Hub', '7-in-1 multi-port adapter with 4K HDMI and 100W Power Delivery', 1499.00), " +
                    "(3, 1, 'Logitech Silent Wireless Mouse', 'Ergonomic 2.4GHz wireless optical mouse with silent clicks', 799.00), " +
                    "(4, 2, 'Classic Cotton Crew T-Shirt', '100% organic breathable cotton casual crew neck t-shirt (Navy Blue)', 499.00), " +
                    "(5, 2, 'Denim Slim-Fit Jeans', 'Durable stretchable blue denim jeans for everyday wear', 1299.00), " +
                    "(6, 3, 'Stainless Steel Thermal Flask', 'Double-walled insulated 1L water bottle (Keeps cold 24h, hot 12h)', 649.00), " +
                    "(7, 3, 'Electric Kettle 1.5L', 'Fast-boiling fast heating kettle with auto cut-off protection', 999.00), " +
                    "(8, 4, 'Java: The Complete Reference', 'Comprehensive guide to Java programming by Herbert Schildt (12th Edition)', 850.00), " +
                    "(9, 4, 'Hardbound Dot-Grid Notebook', 'A5 160 GSM premium journal notebook with elastic closure', 350.00)");

            // Seed Inventory
            stmt.execute("MERGE INTO inventory (inventory_id, product_id, stock_quantity) KEY(product_id) VALUES " +
                    "(1, 1, 25), (2, 2, 40), (3, 3, 50), (4, 4, 100), (5, 5, 60), " +
                    "(6, 6, 45), (7, 7, 30), (8, 8, 20), (9, 9, 80)");

            schemaInitialized = true;
            System.out.println(">>> [DATABASE] Relational schema and seed data initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing schema: " + e.getMessage());
        }
    }

    /**
     * Safely closes an open database connection.
     *
     * @param conn the Connection object to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
