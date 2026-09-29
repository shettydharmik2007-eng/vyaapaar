-- ====================================================================
-- Project: Vyaapaar - E-Commerce Order & Inventory Management System
-- Database Schema: database/schema.sql
-- Database: MySQL
-- ====================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS vyaapaar_db;
USE vyaapaar_db;

-- 2. Drop existing tables in reverse dependency order (Clean Reset)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- ====================================================================
-- 3. Table Definitions with Primary Keys, Foreign Keys, & Constraints
-- ====================================================================

-- Table 1: users (Customers & Administrators)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: categories
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table 3: products
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) 
        REFERENCES categories(category_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Table 4: inventory (1:1 relationship with products)
CREATE TABLE inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    stock_quantity INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT chk_stock_positive CHECK (stock_quantity >= 0)
);

-- Table 5: cart (1:1 relationship with users)
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

-- Table 6: cart_items (Many:1 with cart, Many:1 with products)
CREATE TABLE cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) 
        REFERENCES cart(cart_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT chk_cart_quantity CHECK (quantity > 0)
);

-- Table 7: orders
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    shipping_address TEXT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Table 8: order_items (Many:1 with orders, Many:1 with products)
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) 
        REFERENCES orders(order_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_order_quantity CHECK (quantity > 0)
);

-- ====================================================================
-- 4. Sample Seed Data for Testing
-- ====================================================================

-- 4.1 Seed Users (1 Admin, 2 Customers)
INSERT INTO users (full_name, email, password, role, phone, address) VALUES
('System Administrator', 'admin@vyaapaar.com', 'admin123', 'ADMIN', '9876543210', 'Vyaapaar Tech Hub, Bengaluru'),
('Rahul Sharma', 'rahul@gmail.com', 'rahul123', 'CUSTOMER', '9811122233', 'Flat 402, Green Glen Layout, Bellandur, Bengaluru'),
('Priya Nair', 'priya@gmail.com', 'priya123', 'CUSTOMER', '9844455566', 'House No 12, 4th Main, Indiranagar, Bengaluru');

-- 4.2 Seed Categories
INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Gadgets, accessories, smart devices, and hardware components'),
('Clothing & Fashion', 'Men and Women casual, formal, and ethnic apparel'),
('Home & Kitchen', 'Appliances, cookware, dining, and daily home essentials'),
('Books & Stationery', 'Academic textbooks, fiction novels, notebooks, and office supplies');

-- 4.3 Seed Products
INSERT INTO products (category_id, name, description, price) VALUES
(1, 'Wireless Bluetooth Headphones', 'Over-ear active noise cancelling headphones with 30h battery life', 2499.00),
(1, 'USB-C Fast Charging Hub', '7-in-1 multi-port adapter with 4K HDMI and 100W Power Delivery', 1499.00),
(1, 'Logitech Silent Wireless Mouse', 'Ergonomic 2.4GHz wireless optical mouse with silent clicks', 799.00),
(2, 'Classic Cotton Crew T-Shirt', '100% organic breathable cotton casual crew neck t-shirt (Navy Blue)', 499.00),
(2, 'Denim Slim-Fit Jeans', 'Durable stretchable blue denim jeans for everyday wear', 1299.00),
(3, 'Stainless Steel Thermal Flask', 'Double-walled insulated 1L water bottle (Keeps cold 24h, hot 12h)', 649.00),
(3, 'Electric Kettle 1.5L', 'Fast-boiling stainless steel kettle with auto cut-off protection', 999.00),
(4, 'Java: The Complete Reference', 'Comprehensive guide to Java programming by Herbert Schildt (12th Edition)', 850.00),
(4, 'Hardbound Dot-Grid Notebook', 'A5 160 GSM premium journal notebook with elastic closure', 350.00);

-- 4.4 Seed Inventory (Initial stock for products)
INSERT INTO inventory (product_id, stock_quantity) VALUES
(1, 25),
(2, 40),
(3, 50),
(4, 100),
(5, 60),
(6, 45),
(7, 30),
(8, 20),
(9, 80);
