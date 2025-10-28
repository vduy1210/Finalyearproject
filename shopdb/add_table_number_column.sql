-- Add table_number column to app_order table (if not exists)
-- Run this SQL in your MySQL database

ALTER TABLE app_order 
ADD COLUMN IF NOT EXISTS table_number VARCHAR(20) NULL 
COMMENT 'Table number for restaurant orders';

-- Add table_number column to web_order table for consistency (if not exists)
ALTER TABLE web_order 
ADD COLUMN IF NOT EXISTS table_number VARCHAR(20) NULL 
COMMENT 'Table number for restaurant orders';

-- If the above syntax doesn't work (older MySQL versions), use:
-- Check if column exists first, then add manually if needed:
-- ALTER TABLE app_order ADD COLUMN table_number VARCHAR(20) NULL;
-- ALTER TABLE web_order ADD COLUMN table_number VARCHAR(20) NULL;
