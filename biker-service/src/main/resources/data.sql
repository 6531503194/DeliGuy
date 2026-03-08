# Biker Service Database Schema

CREATE TABLE IF NOT EXISTS bikers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    vehicle_number VARCHAR(100) NOT NULL
);

INSERT INTO bikers (name, phone, vehicle_number) VALUES 
    ('John Doe', '+1234567890', 'V12345'),
    ('Jane Smith', '+1987654321', 'V67890'),
    ('Mike Johnson', '+1555555555', 'V11111');