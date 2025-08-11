-- Add shipping snapshot fields and status to orders table
ALTER TABLE orders
ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'Pending',
ADD COLUMN IF NOT EXISTS shipping_name VARCHAR(100),
ADD COLUMN IF NOT EXISTS shipping_phone VARCHAR(20),
ADD COLUMN IF NOT EXISTS shipping_address VARCHAR(255),
ADD COLUMN IF NOT EXISTS shipping_email VARCHAR(100);


