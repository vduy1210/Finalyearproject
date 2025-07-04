-- Update customers table to remove address column and add accumulatedPoint column
USE shopdb;

-- Remove address column from customers table
ALTER TABLE customers DROP COLUMN address;

-- Add accumulatedPoint column to customers table (if not already exists)
ALTER TABLE customers ADD COLUMN accumulatedPoint INT DEFAULT 0;

-- Update existing records to have default accumulatedPoint value
UPDATE customers SET accumulatedPoint = 0 WHERE accumulatedPoint IS NULL; 