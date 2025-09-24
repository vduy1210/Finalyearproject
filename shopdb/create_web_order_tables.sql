-- SQL Script to create web_order and web_order_details tables
-- These tables are used for orders coming from the web frontend

-- Create web_order table
CREATE TABLE IF NOT EXISTS web_order (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    staff_id INT,
    order_date DATETIME,
    total_amount DOUBLE,
    tax DOUBLE,
    discount DOUBLE,
    total DOUBLE,
    staff_name VARCHAR(255),
    user_id INT,
    status VARCHAR(50) DEFAULT 'Pending',
    shipping_name VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_email VARCHAR(100),
    table_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create web_order_details table
CREATE TABLE IF NOT EXISTS web_order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id BIGINT,
    quantity INT,
    price DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES web_order(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create app_order table
CREATE TABLE IF NOT EXISTS app_order (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    user_id INT,
    order_date DATETIME,
    total_amount DOUBLE,
    tax DOUBLE,
    discount DOUBLE,
    total DOUBLE,
    status VARCHAR(50) DEFAULT 'Pending',
    shipping_name VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_email VARCHAR(100),
    table_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create app_order_details table
CREATE TABLE IF NOT EXISTS app_order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id BIGINT,
    quantity INT,
    price DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES app_order(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_web_order_customer_id ON web_order(customer_id);
CREATE INDEX idx_web_order_staff_id ON web_order(staff_id);
CREATE INDEX idx_web_order_status ON web_order(status);
CREATE INDEX idx_web_order_date ON web_order(order_date);
CREATE INDEX idx_web_order_details_order_id ON web_order_details(order_id);
CREATE INDEX idx_web_order_details_product_id ON web_order_details(product_id);

CREATE INDEX idx_app_order_customer_id ON app_order(customer_id);
CREATE INDEX idx_app_order_user_id ON app_order(user_id);
CREATE INDEX idx_app_order_status ON app_order(status);
CREATE INDEX idx_app_order_date ON app_order(order_date);
CREATE INDEX idx_app_order_details_order_id ON app_order_details(order_id);
CREATE INDEX idx_app_order_details_product_id ON app_order_details(product_id);
