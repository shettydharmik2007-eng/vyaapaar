# Database Design & Data Dictionary

---

## 1. Primary Application Database: MySQL (`vyaapaar_db`)

The primary database implements 8 normalized tables supporting complete operational e-commerce workflows.

### 1. `users`
Stores user profile information, authentication credentials, and system roles.
* **Primary Key**: `user_id` (INT, AUTO_INCREMENT)
* **Columns**:
  * `user_id`: Unique identifier for user.
  * `full_name`: VARCHAR(100), NOT NULL - Customer or Admin name.
  * `email`: VARCHAR(100), NOT NULL, UNIQUE - Login email address.
  * `password`: VARCHAR(255), NOT NULL - User password.
  * `role`: ENUM('CUSTOMER', 'ADMIN'), NOT NULL, DEFAULT 'CUSTOMER'.
  * `phone`: VARCHAR(20) - Contact phone number.
  * `address`: TEXT - Default shipping address.
  * `created_at`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP.

### 2. `categories`
Stores product classifications.
* **Primary Key**: `category_id` (INT, AUTO_INCREMENT)
* **Columns**:
  * `category_id`: Unique identifier for category.
  * `category_name`: VARCHAR(100), NOT NULL, UNIQUE - Category name.
  * `description`: TEXT - Category description.

### 3. `products`
Stores products available in the catalog.
* **Primary Key**: `product_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**: `category_id` $\rightarrow$ `categories(category_id)` (ON DELETE RESTRICT ON UPDATE CASCADE)
* **Columns**:
  * `product_id`: Unique identifier for product.
  * `category_id`: INT, NOT NULL - Associated category.
  * `name`: VARCHAR(150), NOT NULL - Product name.
  * `description`: TEXT - Product details.
  * `price`: DECIMAL(10, 2), NOT NULL - Unit selling price.
  * `created_at`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP.

### 4. `inventory`
Tracks physical stock level per product (1:1 relationship with `products`).
* **Primary Key**: `inventory_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**: `product_id` $\rightarrow$ `products(product_id)` (UNIQUE, ON DELETE CASCADE ON UPDATE CASCADE)
* **Constraints**: `CHECK (stock_quantity >= 0)`
* **Columns**:
  * `inventory_id`: Unique inventory record ID.
  * `product_id`: INT, NOT NULL, UNIQUE - Associated product.
  * `stock_quantity`: INT, NOT NULL, DEFAULT 0 - Available stock count.
  * `last_updated`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP.

### 5. `cart`
Active shopping cart header (1:1 relationship with `users`).
* **Primary Key**: `cart_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**: `user_id` $\rightarrow$ `users(user_id)` (UNIQUE, ON DELETE CASCADE ON UPDATE CASCADE)
* **Columns**:
  * `cart_id`: Unique cart identifier.
  * `user_id`: INT, NOT NULL, UNIQUE - Owner customer.
  * `created_at`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP.

### 6. `cart_items`
Individual line items inside an active shopping cart.
* **Primary Key**: `cart_item_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**:
  * `cart_id` $\rightarrow$ `cart(cart_id)` (ON DELETE CASCADE ON UPDATE CASCADE)
  * `product_id` $\rightarrow$ `products(product_id)` (ON DELETE CASCADE ON UPDATE CASCADE)
* **Constraints**: `UNIQUE (cart_id, product_id)`, `CHECK (quantity > 0)`
* **Columns**:
  * `cart_item_id`: Unique cart item ID.
  * `cart_id`: INT, NOT NULL - Parent cart.
  * `product_id`: INT, NOT NULL - Chosen product.
  * `quantity`: INT, NOT NULL, DEFAULT 1 - Chosen quantity.
  * `added_at`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP.

### 7. `orders`
Header record for a placed customer order.
* **Primary Key**: `order_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**: `user_id` $\rightarrow$ `users(user_id)` (ON DELETE RESTRICT ON UPDATE CASCADE)
* **Columns**:
  * `order_id`: Unique order identifier.
  * `user_id`: INT, NOT NULL - Placing customer.
  * `total_amount`: DECIMAL(10, 2), NOT NULL - Grand total.
  * `order_status`: ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'), NOT NULL, DEFAULT 'CONFIRMED'.
  * `shipping_address`: TEXT, NOT NULL - Delivery address at time of order.
  * `order_date`: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP.

### 8. `order_items`
Immutable historical snapshot of items and unit prices at purchase time.
* **Primary Key**: `order_item_id` (INT, AUTO_INCREMENT)
* **Foreign Keys**:
  * `order_id` $\rightarrow$ `orders(order_id)` (ON DELETE CASCADE ON UPDATE CASCADE)
  * `product_id` $\rightarrow$ `products(product_id)` (ON DELETE RESTRICT ON UPDATE CASCADE)
* **Constraints**: `CHECK (quantity > 0)`
* **Columns**:
  * `order_item_id`: Unique record ID.
  * `order_id`: INT, NOT NULL - Parent order.
  * `product_id`: INT, NOT NULL - Purchased product.
  * `quantity`: INT, NOT NULL - Number of units purchased.
  * `unit_price`: DECIMAL(10, 2), NOT NULL - Price per unit at purchase time.
  * `subtotal`: DECIMAL(10, 2), NOT NULL - Calculated item subtotal (`quantity * unit_price`).

---

## 2. Reporting Database: PostgreSQL (`vyaapaar_reporting`)

Dedicated analytical tables optimized for query aggregations:

1. **`report_categories`** (`category_id` PK, `category_name` UNIQUE, `description`)
2. **`report_products`** (`product_id` PK, `category_id` FK, `name`, `price`)
3. **`report_users`** (`user_id` PK, `full_name`, `email` UNIQUE, `city`, `registered_at`)
4. **`report_orders`** (`order_id` PK, `user_id` FK, `order_date`, `total_amount`, `order_status`)
5. **`report_order_items`** (`order_item_id` PK, `order_id` FK, `product_id` FK, `quantity`, `unit_price`, `subtotal`)
