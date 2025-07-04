-- Debug database structure and data
USE shopdb;

-- Check table structures
DESCRIBE orders;
DESCRIBE order_details;
DESCRIBE customers;
DESCRIBE products;
DESCRIBE users;

-- Check existing data
SELECT 'Orders count:' as info, COUNT(*) as count FROM orders
UNION ALL
SELECT 'Customers count:', COUNT(*) FROM customers
UNION ALL
SELECT 'Products count:', COUNT(*) FROM products
UNION ALL
SELECT 'Users count:', COUNT(*) FROM users;

-- Check if required data exists for order creation
SELECT 'Customer with ID 1:' as info, name, phone FROM customers WHERE id = 1
UNION ALL
SELECT 'User with ID 1:', userName, role FROM users WHERE userID = 1
UNION ALL
SELECT 'Product with ID 1:', name, price FROM products WHERE id = 1;

-- Check foreign key constraints
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'shopdb' 
AND REFERENCED_TABLE_NAME IS NOT NULL; 