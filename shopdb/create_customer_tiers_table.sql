-- Create customer_tiers table to store tier configuration
-- This table works with existing customers table (no modification needed)
CREATE TABLE IF NOT EXISTS customer_tiers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tier_name VARCHAR(50) NOT NULL UNIQUE,
    min_points FLOAT NOT NULL,
    max_points FLOAT NOT NULL,
    discount_percent FLOAT NOT NULL DEFAULT 0,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default tier configuration
INSERT INTO customer_tiers (tier_name, min_points, max_points, discount_percent, description) VALUES
('Bronze', 0, 999, 0, 'New customers'),
('Silver', 1000, 4999, 5, '5% discount on all orders'),
('Gold', 5000, 9999, 10, '10% discount on all orders'),
('Platinum', 10000, 999999, 15, '15% discount on all orders')
ON DUPLICATE KEY UPDATE 
    min_points = VALUES(min_points),
    max_points = VALUES(max_points),
    discount_percent = VALUES(discount_percent);

-- Note: Tiers are calculated dynamically based on accumulatedPoint
-- No need to modify the customers table structure
