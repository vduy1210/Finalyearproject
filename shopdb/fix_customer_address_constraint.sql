-- Fix customer address constraint and add table_number to orders
-- This script will remove address columns and add table_number column

-- 1. Remove address column from customers table completely
ALTER TABLE customers DROP COLUMN address;

-- 2. Make shipping_address column nullable in orders table
ALTER TABLE orders MODIFY COLUMN shipping_address VARCHAR(255) NULL;

-- 3. Add table_number column to orders table
ALTER TABLE orders ADD COLUMN table_number VARCHAR(20) DEFAULT NULL;

-- 4. Add index for better performance when searching by table number
CREATE INDEX idx_orders_table_number ON orders(table_number);

-- 5. Optional: Remove shipping_address column completely if not needed
-- ALTER TABLE orders DROP COLUMN shipping_address;

-- 6. Optional: Update existing orders to have a default table number
-- UPDATE orders SET table_number = 'N/A' WHERE table_number IS NULL;
