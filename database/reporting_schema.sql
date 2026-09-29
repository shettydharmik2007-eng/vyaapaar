-- ====================================================================
-- Project: Vyaapaar - E-Commerce Order & Inventory Management System
-- PostgreSQL Reporting Database Schema & Analytics Seed Data
-- Database Name: vyaapaar_reporting
-- ====================================================================

-- 1. Create Database (Execute separately in PostgreSQL if database does not exist)
-- CREATE DATABASE vyaapaar_reporting;
-- \c vyaapaar_reporting;

-- 2. Drop existing reporting tables in reverse dependency order
DROP TABLE IF EXISTS report_order_items CASCADE;
DROP TABLE IF EXISTS report_orders CASCADE;
DROP TABLE IF EXISTS report_products CASCADE;
DROP TABLE IF EXISTS report_categories CASCADE;
DROP TABLE IF EXISTS report_users CASCADE;

-- ====================================================================
-- 3. Reporting Table Definitions
-- ====================================================================

-- Table 1: report_users (Customers for demographic and purchase analysis)
CREATE TABLE report_users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    city VARCHAR(50),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: report_categories
CREATE TABLE report_categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- Table 3: report_products
CREATE TABLE report_products (
    product_id SERIAL PRIMARY KEY,
    category_id INT NOT NULL REFERENCES report_categories(category_id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

-- Table 4: report_orders
CREATE TABLE report_orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES report_users(user_id) ON DELETE CASCADE,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(10, 2) NOT NULL,
    order_status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED'
);

-- Table 5: report_order_items
CREATE TABLE report_order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES report_orders(order_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES report_products(product_id) ON DELETE RESTRICT,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL
);

-- ====================================================================
-- 4. Realistic Analytics Sample Data
-- ====================================================================

-- 4.1 Seed Users (5 customers, User #5 is inactive with no orders)
INSERT INTO report_users (full_name, email, city, registered_at) VALUES
('Aarav Mehta', 'aarav.mehta@gmail.com', 'Bengaluru', '2026-01-10 10:30:00'),
('Pooja Iyer', 'pooja.iyer@gmail.com', 'Mumbai', '2026-01-15 14:20:00'),
('Rohan Verma', 'rohan.verma@gmail.com', 'Delhi', '2026-02-01 11:00:00'),
('Ananya Sen', 'ananya.sen@gmail.com', 'Kolkata', '2026-02-20 16:45:00'),
('Vikram Malhotra', 'vikram.m@gmail.com', 'Chennai', '2026-03-05 09:15:00'); -- Inactive customer

-- 4.2 Seed Categories
INSERT INTO report_categories (category_name, description) VALUES
('Electronics', 'Gadgets, accessories, and computing peripherals'),
('Fashion & Apparel', 'Clothing, footwear, and wearable style'),
('Home & Kitchen', 'Home appliances and kitchenware'),
('Books & Media', 'Academic, technical books, and journals');

-- 4.3 Seed Products
INSERT INTO report_products (category_id, name, price) VALUES
(1, 'Wireless Noise-Cancelling Headphones', 2499.00),
(1, 'USB-C 7-in-1 Multiport Adapter', 1499.00),
(1, 'Ergonomic Optical Mouse', 799.00),
(2, 'Classic Cotton T-Shirt', 499.00),
(2, 'Slim-Fit Stretch Denim Jeans', 1299.00),
(3, 'Stainless Steel Thermal Flask 1L', 649.00),
(3, 'Electric Cordless Kettle 1.5L', 999.00),
(4, 'Java: The Complete Reference', 850.00);

-- 4.4 Seed Orders across multiple months (Jan, Feb, Mar, Apr 2026)
INSERT INTO report_orders (user_id, order_date, total_amount, order_status) VALUES
-- Order 1 (Jan 2026 - Aarav)
(1, '2026-01-18 11:30:00', 3298.00, 'DELIVERED'),
-- Order 2 (Jan 2026 - Pooja)
(2, '2026-01-25 15:45:00', 499.00, 'DELIVERED'),
-- Order 3 (Feb 2026 - Aarav)
(1, '2026-02-12 18:20:00', 2499.00, 'DELIVERED'),
-- Order 4 (Feb 2026 - Rohan)
(3, '2026-02-22 09:10:00', 2148.00, 'DELIVERED'),
-- Order 5 (Mar 2026 - Ananya)
(4, '2026-03-10 14:00:00', 1648.00, 'SHIPPED'),
-- Order 6 (Mar 2026 - Pooja)
(2, '2026-03-24 16:30:00', 2298.00, 'CONFIRMED'),
-- Order 7 (Apr 2026 - Rohan - Cancelled Order)
(3, '2026-04-02 12:15:00', 1499.00, 'CANCELLED');

-- 4.5 Seed Order Items
INSERT INTO report_order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
-- Order 1 (Aarav: 1x Headphones + 1x Mouse)
(1, 1, 1, 2499.00, 2499.00),
(1, 3, 1, 799.00, 799.00),

-- Order 2 (Pooja: 1x T-Shirt)
(2, 4, 1, 499.00, 499.00),

-- Order 3 (Aarav: 1x Headphones)
(3, 1, 1, 2499.00, 2499.00),

-- Order 4 (Rohan: 1x Java Book + 1x Jeans)
(4, 8, 1, 850.00, 850.00),
(4, 5, 1, 1298.00, 1298.00),

-- Order 5 (Ananya: 1x Kettle + 1x Flask)
(5, 7, 1, 999.00, 999.00),
(5, 6, 1, 649.00, 649.00),

-- Order 6 (Pooja: 1x Adapter + 1x Mouse)
(6, 2, 1, 1499.00, 1499.00),
(6, 3, 1, 799.00, 799.00),

-- Order 7 (Rohan - Cancelled: 1x Adapter)
(7, 2, 1, 1499.00, 1499.00);
