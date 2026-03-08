-- ============================================
-- DELIGUY DATABASE INITIALIZATION
-- Complete init.sql with all databases, tables, and sample data
-- ============================================

-- ============================================
-- 1. CREATE DATABASES
-- ============================================
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS restaurant_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS biker_db;

-- ============================================
-- 2. AUTH DATABASE (users)
-- ============================================
USE auth_db;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- Insert users (password: 123456 - bcrypt encoded)
-- Using plain text for simplicity, services will handle encoding
INSERT INTO users (username, email, password, role, enabled) VALUES
('owner', 'owner@deliguy.com', '$2a$10$8K1p/a0dL3.HKwHkqhIW4u7ELKPLs6E7ej7W3bVRXK6N8H5WX7W2', 'OWNER', TRUE),
('biker1', 'biker1@deliguy.com', '$2a$10$8K1p/a0dL3.HKwHkqhIW4u7ELKPLs6E7ej7W3bVRXK6N8H5WX7W2', 'BIKER', TRUE),
('biker2', 'biker2@deliguy.com', '$2a$10$8K1p/a0dL3.HKwHkqhIW4u7ELKPLs6E7ej7W3bVRXK6N8H5WX7W2', 'BIKER', TRUE),
('customer', 'customer@deliguy.com', '$2a$10$8K1p/a0dL3.HKwHkqhIW4u7ELKPLs6E7ej7W3bVRXK6N8H5WX7W2', 'CUSTOMER', TRUE);

-- ============================================
-- 3. RESTAURANT DATABASE
-- ============================================
USE restaurant_db;

-- Restaurants table
DROP TABLE IF EXISTS menu_addons;
DROP TABLE IF EXISTS menu_variants;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS restaurants;

CREATE TABLE restaurants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    phone VARCHAR(50),
    email VARCHAR(255),
    open_time VARCHAR(20),
    close_time VARCHAR(20),
    delivery_fee DECIMAL(10,2),
    min_order DECIMAL(10,2),
    rating DECIMAL(3,2) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    user_id BIGINT
);

-- Categories table
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    restaurant_id BIGINT NOT NULL
);

-- Menu items table
CREATE TABLE menu_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    category_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    available BOOLEAN DEFAULT TRUE,
    sales INT DEFAULT 0,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Menu variants table (Small, Medium, Large, etc.)
CREATE TABLE menu_variants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_item_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price_modifier DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- Menu addons table
CREATE TABLE menu_addons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_item_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- Insert restaurant
INSERT INTO restaurants (name, description, address, phone, email, open_time, close_time, delivery_fee, min_order, rating, total_reviews, user_id) VALUES
('Bella Italia', 'Authentic Italian cuisine', '123 Restaurant Row, Food City', '+1 234 567 8900', 'contact@bellaitalia.com', '11:00', '22:00', 2.99, 10.00, 4.50, 234, 1);

-- Insert categories
INSERT INTO categories (name, restaurant_id) VALUES
('Pizza', 1),
('Pasta', 1),
('Salads', 1),
('Drinks', 1),
('Desserts', 1);

-- Insert menu items
INSERT INTO menu_items (restaurant_id, category_id, name, description, price, image_url, available, sales) VALUES
(1, 1, 'Margherita Pizza', 'Classic tomato sauce, mozzarella, fresh basil', 12.99, 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=400', TRUE, 45),
(1, 1, 'Pepperoni Pizza', 'Loaded with pepperoni and extra cheese', 14.99, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400', TRUE, 38),
(1, 1, 'Veggie Pizza', 'Mixed vegetables with mozzarella', 13.99, 'https://images.unsplash.com/photo-1594007654729-407eedc4be65?w=400', TRUE, 25),
(1, 2, 'Pasta Alfredo', 'Creamy alfredo sauce with fettuccine', 15.99, 'https://images.unsplash.com/photo-1645112411341-6c4fd023714a?w=400', TRUE, 22),
(1, 2, 'Pasta Carbonara', 'Classic carbonara with bacon and egg', 16.99, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=400', TRUE, 18),
(1, 2, 'Spaghetti Bolognese', 'Traditional bolognese sauce', 14.99, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400', TRUE, 15),
(1, 3, 'Caesar Salad', 'Fresh romaine, parmesan, croutons', 8.99, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400', FALSE, 15),
(1, 3, 'Greek Salad', 'Cucumber, tomato, olives, feta', 9.99, 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400', TRUE, 12),
(1, 4, 'Cola', 'Refreshing cola drink', 2.99, 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400', TRUE, 120),
(1, 4, 'Lemonade', 'Fresh squeezed lemonade', 3.49, 'https://images.unsplash.com/photo-1621263764928-df1444c5e859?w=400', TRUE, 45),
(1, 4, 'Iced Tea', 'Fresh brewed iced tea', 2.99, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', TRUE, 30),
(1, 5, 'Tiramisu', 'Classic Italian coffee dessert', 7.99, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400', TRUE, 30),
(1, 5, 'Cheesecake', 'New York style cheesecake', 6.99, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400', TRUE, 22),
(1, 5, 'Gelato', 'Italian ice cream (3 scoops)', 5.99, 'https://images.unsplash.com/photo-1557142046-c704a3adf364?w=400', TRUE, 18);

-- Insert variants for Pizza items
INSERT INTO menu_variants (menu_item_id, name, price_modifier) VALUES
(1, 'Small', -3.00),
(1, 'Medium', 0),
(1, 'Large', 4.00),
(2, 'Small', -3.00),
(2, 'Medium', 0),
(2, 'Large', 4.00),
(3, 'Small', -3.00),
(3, 'Medium', 0),
(3, 'Large', 4.00);

-- Insert variants for Drinks
INSERT INTO menu_variants (menu_item_id, name, price_modifier) VALUES
(9, 'Small', -1.00),
(9, 'Medium', 0),
(9, 'Large', 1.00),
(10, 'Small', -1.00),
(10, 'Medium', 0),
(10, 'Large', 1.00);

-- Insert add-ons for Pizza
INSERT INTO menu_addons (menu_item_id, name, price) VALUES
(1, 'Extra Cheese', 1.50),
(1, 'Pepperoni', 2.00),
(1, 'Mushrooms', 1.50),
(2, 'Extra Cheese', 1.50),
(2, 'Jalapeños', 1.00);

-- Insert add-ons for Pasta
INSERT INTO menu_addons (menu_item_id, name, price) VALUES
(4, 'Grilled Chicken', 4.00),
(4, 'Shrimp', 6.00),
(4, 'Extra Sauce', 1.00),
(5, 'Grilled Chicken', 4.00),
(5, 'Bacon', 2.50);

-- Insert add-ons for Salads
INSERT INTO menu_addons (menu_item_id, name, price) VALUES
(7, 'Grilled Chicken', 4.00),
(7, 'Extra Croutons', 0.50),
(8, 'Feta Cheese', 1.50);

-- ============================================
-- 4. ORDER DATABASE
-- ============================================
USE order_db;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_username VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    restaurant_id BIGINT NOT NULL,
    restaurant_name VARCHAR(255) NOT NULL,
    restaurant_address VARCHAR(500),
    total_amount DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2),
    total_with_delivery DECIMAL(10,2),
    biker_id BIGINT,
    biker_name VARCHAR(255),
    biker_phone VARCHAR(50),
    vehicle_number VARCHAR(100),
    customer_lat DOUBLE,
    customer_lng DOUBLE,
    restaurant_lat DOUBLE,
    restaurant_lng DOUBLE,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50),
    rejection_reason VARCHAR(500),
    created_at DATETIME NOT NULL,
    accepted_at DATETIME,
    picked_up_at DATETIME,
    delivered_at DATETIME,
    paid_at DATETIME
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Insert sample orders (various statuses)
INSERT INTO orders (customer_username, customer_phone, delivery_address, restaurant_id, restaurant_name, restaurant_address, total_amount, delivery_fee, total_with_delivery, biker_id, biker_name, biker_phone, vehicle_number, customer_lat, customer_lng, restaurant_lat, restaurant_lng, status, payment_status, created_at) VALUES
('john.doe', '+1 234 567 8900', '123 Main St, Apt 4B', 1, 'Bella Italia', '123 Restaurant Row', 18.97, 3.00, 21.97, NULL, NULL, NULL, NULL, 40.7580, -73.9855, 40.7128, -74.0060, 'PENDING', 'PENDING', NOW() - INTERVAL 5 MINUTE),
('sarah.smith', '+1 234 567 8901', '456 Oak Avenue', 1, 'Bella Italia', '123 Restaurant Row', 29.98, 3.50, 33.48, NULL, NULL, NULL, NULL, 40.7500, -73.9800, 40.7128, -74.0060, 'PENDING', 'PENDING', NOW() - INTERVAL 10 MINUTE),
('mike.johnson', '+1 234 567 8902', '888 Elm Street', 1, 'Bella Italia', '123 Restaurant Row', 20.98, 2.50, 23.48, NULL, NULL, NULL, NULL, 40.7600, -73.9750, 40.7128, -74.0060, 'PREPARING', 'COMPLETED', NOW() - INTERVAL 20 MINUTE),
('emily.brown', '+1 234 567 8903', '222 Maple Road', 1, 'Bella Italia', '123 Restaurant Row', 27.96, 3.00, 30.96, 1, 'John Doe', '+1234567890', 'V12345', 40.7550, -73.9900, 40.7128, -74.0060, 'READY', 'COMPLETED', NOW() - INTERVAL 30 MINUTE),
('david.lee', '+1 234 567 8904', '777 Pine Street', 1, 'Bella Italia', '123 Restaurant Row', 32.97, 4.00, 36.97, 2, 'Jane Smith', '+1987654321', 'V67890', 40.7520, -73.9850, 40.7128, -74.0060, 'DELIVERING', 'COMPLETED', NOW() - INTERVAL 45 MINUTE),
('james.wilson', '+1 234 567 8905', '333 Cedar Lane', 1, 'Bella Italia', '123 Restaurant Row', 15.99, 2.50, 18.49, 1, 'John Doe', '+1234567890', 'V12345', 40.7480, -73.9920, 40.7128, -74.0060, 'DELIVERED', 'COMPLETED', NOW() - INTERVAL 2 HOUR),
('lisa.anderson', '+1 234 567 8906', '444 Birch Street', 1, 'Bella Italia', '123 Restaurant Row', 22.97, 3.00, 25.97, 2, 'Jane Smith', '+1987654321', 'V67890', 40.7450, -73.9880, 40.7128, -74.0060, 'DELIVERED', 'COMPLETED', NOW() - INTERVAL 3 HOUR),
('robert.taylor', '+1 234 567 8907', '555 Walnut Ave', 1, 'Bella Italia', '123 Restaurant Row', 18.48, 2.50, 20.98, 1, 'John Doe', '+1234567890', 'V12345', 40.7420, -73.9950, 40.7128, -74.0060, 'DELIVERED', 'COMPLETED', NOW() - INTERVAL 5 HOUR),
('jennifer.martin', '+1 234 567 8908', '666 Spruce Road', 1, 'Bella Italia', '123 Restaurant Row', 25.97, 3.00, 28.97, 2, 'Jane Smith', '+1987654321', 'V67890', 40.7380, -73.9820, 40.7128, -74.0060, 'CANCELLED', 'FAILED', NOW() - INTERVAL 24 HOUR, 'Customer requested cancellation'),
('william.thomas', '+1 234 567 8909', '777 Ash Drive', 1, 'Bella Italia', '123 Restaurant Row', 31.96, 3.50, 35.46, 1, 'John Doe', '+1234567890', 'V12345', 40.7350, -73.9780, 40.7128, -74.0060, 'DELIVERED', 'COMPLETED', NOW() - INTERVAL 48 HOUR);

-- Insert order items for recent orders
INSERT INTO order_items (order_id, menu_item_id, name, quantity, price) VALUES
(1, 1, 'Margherita Pizza', 1, 12.99),
(1, 9, 'Cola', 2, 5.98),
(2, 2, 'Pepperoni Pizza', 2, 29.98),
(3, 4, 'Pasta Alfredo', 1, 15.99),
(3, 12, 'Tiramisu', 1, 7.99),
(4, 2, 'Pepperoni Pizza', 1, 14.99),
(4, 10, 'Lemonade', 2, 6.98),
(4, 13, 'Cheesecake', 1, 6.99),
(5, 1, 'Margherita Pizza', 2, 25.98),
(5, 9, 'Cola', 3, 8.97),
(6, 5, 'Pasta Carbonara', 1, 16.99),
(7, 3, 'Veggie Pizza', 1, 13.99),
(7, 9, 'Cola', 2, 5.98),
(8, 6, 'Spaghetti Bolognese', 1, 14.99),
(8, 11, 'Iced Tea', 2, 5.98),
(8, 14, 'Gelato', 1, 5.99),
(9, 2, 'Pepperoni Pizza', 2, 29.98),
(10, 1, 'Margherita Pizza', 2, 25.98),
(10, 4, 'Pasta Alfredo', 1, 15.99);

-- ============================================
-- 5. BIKER DATABASE
-- ============================================
USE biker_db;

DROP TABLE IF EXISTS biker_stats;
DROP TABLE IF EXISTS bikers;

CREATE TABLE bikers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    vehicle_number VARCHAR(100) NOT NULL,
    rating DECIMAL(3,2) DEFAULT 0,
    total_ratings INT DEFAULT 0,
    is_online BOOLEAN DEFAULT FALSE,
    current_lat DOUBLE,
    current_lng DOUBLE
);

CREATE TABLE biker_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    biker_id BIGINT NOT NULL,
    period VARCHAR(20) NOT NULL,
    earnings DECIMAL(10,2) DEFAULT 0,
    orders_count INT DEFAULT 0,
    distance DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (biker_id) REFERENCES bikers(id)
);

-- Insert bikers
INSERT INTO bikers (user_id, name, phone, vehicle_number, rating, total_ratings, is_online) VALUES
(2, 'John Doe', '+1 234 567 8900', 'V12345', 4.8, 156, FALSE),
(3, 'Jane Smith', '+1 987 654 3210', 'V67890', 4.6, 124, FALSE),
(3, 'Mike Johnson', '+1 555 555 5555', 'V11111', 4.9, 89, FALSE);

-- Insert biker stats (day, week, month)
-- John Doe stats
INSERT INTO biker_stats (biker_id, period, earnings, orders_count, distance) VALUES
(1, 'day', 45.50, 8, 24.5),
(1, 'week', 312.75, 42, 156.0),
(1, 'month', 1245.50, 168, 624.0);

-- Jane Smith stats
INSERT INTO biker_stats (biker_id, period, earnings, orders_count, distance) VALUES
(2, 'day', 38.25, 6, 18.0),
(2, 'week', 275.50, 35, 132.0),
(2, 'month', 1098.75, 142, 528.0);

-- Mike Johnson stats
INSERT INTO biker_stats (biker_id, period, earnings, orders_count, distance) VALUES
(3, 'day', 52.00, 9, 28.5),
(3, 'week', 356.25, 48, 180.0),
(3, 'month', 1425.00, 186, 720.0);

-- ============================================
-- END OF INITIALIZATION
-- ============================================
