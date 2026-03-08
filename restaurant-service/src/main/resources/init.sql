-- Create database
CREATE DATABASE IF NOT EXISTS restaurant_db;
USE restaurant_db;

-- =========================
-- MENU ITEMS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

-- =========================
-- MENU ADDONS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS menu_addons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    menu_item_id BIGINT,
    CONSTRAINT fk_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id)
        ON DELETE CASCADE
);

-- =========================
-- RESTAURANT ORDERS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS restaurant_orders (
    order_id BIGINT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    customer_address VARCHAR(500),
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME
);



-- Sample Menu Items
INSERT INTO menu_items (restaurant_id, name, category, price, available)
VALUES
(1, 'Chicken Fried Rice', 'RICE', 8.50, TRUE),
(1, 'Beef Noodles', 'NOODLE', 9.00, TRUE),
(1, 'Spring Rolls', 'SNACK', 5.00, TRUE),
(1, 'Coca Cola', 'DRINK', 2.50, TRUE);

-- Sample Add-ons
INSERT INTO menu_addons (name, price, menu_item_id)
VALUES
('Fried Egg', 1.50, 1),
('Extra Chicken', 2.00, 1),
('Extra Beef', 2.50, 2);