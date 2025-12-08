-- Migration: add is_deleted and deleted_at to products
-- Run this on the shopdb database (test on staging first)

ALTER TABLE products
  ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  ADD COLUMN deleted_at DATETIME NULL;

-- Ensure existing rows have is_deleted = 0
UPDATE products SET is_deleted = 0 WHERE is_deleted IS NULL;

-- Optional: add an index to speed up queries filtering non-deleted products
CREATE INDEX idx_products_is_deleted ON products (is_deleted);
