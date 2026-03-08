# Order Service Database Schema

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_username VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    restaurant_id VARCHAR(255) NOT NULL,
    restaurant_name VARCHAR(255) NOT NULL,
    total_amount DOUBLE NOT NULL,
    delivery_fee DOUBLE,
    total_with_delivery DOUBLE,
    biker_id BIGINT,
    biker_name VARCHAR(255),
    biker_phone VARCHAR(50),
    vehicle_number VARCHAR(100),
    customer_lat DOUBLE,
    customer_lng DOUBLE,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50),
    rejection_reason VARCHAR(500),
    created_at DATETIME NOT NULL,
    accepted_at DATETIME,
    picked_up_at DATETIME,
    delivered_at DATETIME,
    paid_at DATETIME
);

INSERT INTO orders (customer_username, customer_phone, delivery_address, restaurant_id, restaurant_name, total_amount, delivery_fee, total_with_delivery, biker_id, biker_name, biker_phone, vehicle_number, customer_lat, customer_lng, status, payment_status, created_at, rejection_reason) VALUES 
    ('john.doe', '+1234567890', '123 Main St, Anytown, USA', 'rest1', 'Pizza Palace', 25.0, 3.0, 28.0, 1, 'John Doe', '+1234567890', 'V12345', 40.712776, -74.005974, 'PENDING', 'PENDING', NOW(), NULL),
    ('jane.smith', '+1987654321', '456 Elm St, Anytown, USA', 'rest2', 'Burger Barn', 18.0, 2.5, 20.5, 2, 'Jane Smith', '+1987654321', 'V67890', 34.052235, -118.243683, 'PENDING', 'PENDING', NOW(), NULL);