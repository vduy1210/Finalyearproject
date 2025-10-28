-- Test queries for Customer Tier Management System
-- Works with existing customers table (no tier column needed)

-- 1. View all tiers with configuration
SELECT * FROM customer_tiers ORDER BY min_points;

-- 2. View all customers with their points and calculated tier
SELECT 
    c.id,
    c.name,
    c.phone,
    c.email,
    c.accumulatedPoint,
    (SELECT t.tier_name 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) as tier
FROM customers c
ORDER BY c.accumulatedPoint DESC;

-- 3. Count customers by tier (dynamically calculated)
SELECT 
    t.tier_name,
    COUNT(c.id) as customer_count,
    AVG(c.accumulatedPoint) as avg_points
FROM customer_tiers t
LEFT JOIN customers c 
    ON c.accumulatedPoint >= t.min_points 
    AND c.accumulatedPoint <= t.max_points
GROUP BY t.tier_name
ORDER BY t.min_points;

-- 4. Update a customer's accumulated points (example for testing)
-- UPDATE customers SET accumulatedPoint = 5500 WHERE id = 1;
-- UPDATE customers SET accumulatedPoint = 12000 WHERE id = 2;

-- 5. Get discount for a specific customer
SELECT 
    c.name,
    c.accumulatedPoint,
    t.tier_name,
    t.discount_percent
FROM customers c
LEFT JOIN customer_tiers t 
    ON c.accumulatedPoint >= t.min_points 
    AND c.accumulatedPoint <= t.max_points
WHERE c.id = 1;

-- 6. Calculate potential savings for all customers
SELECT 
    c.name,
    c.accumulatedPoint,
    (SELECT t.tier_name 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) as tier,
    COALESCE(SUM(o.total), 0) as total_spent,
    (SELECT t.discount_percent 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) as discount_percent,
    COALESCE(SUM(o.total), 0) * 
    COALESCE((SELECT t.discount_percent 
              FROM customer_tiers t 
              WHERE c.accumulatedPoint >= t.min_points 
                AND c.accumulatedPoint <= t.max_points 
              LIMIT 1), 0) / 100 as potential_savings
FROM customers c
LEFT JOIN web_order o ON o.customer_id = c.id
GROUP BY c.id, c.name, c.accumulatedPoint
ORDER BY potential_savings DESC;

-- 7. Add sample accumulated points to existing customers (for testing)
/*
UPDATE customers SET accumulatedPoint = 
    CASE 
        WHEN id % 4 = 0 THEN 15000  -- Platinum
        WHEN id % 4 = 1 THEN 7000   -- Gold
        WHEN id % 4 = 2 THEN 2500   -- Silver
        ELSE 500                     -- Bronze
    END;
*/

-- 8. View tier distribution statistics
SELECT 
    t.tier_name,
    t.min_points,
    t.max_points,
    t.discount_percent,
    COUNT(c.id) as customer_count,
    ROUND(COUNT(c.id) * 100.0 / (SELECT COUNT(*) FROM customers WHERE accumulatedPoint IS NOT NULL), 2) as percentage
FROM customer_tiers t
LEFT JOIN customers c 
    ON c.accumulatedPoint >= t.min_points 
    AND c.accumulatedPoint <= t.max_points
GROUP BY t.tier_name, t.min_points, t.max_points, t.discount_percent
ORDER BY t.min_points;

-- 9. Find top customers in each tier
SELECT 
    tier,
    name,
    accumulatedPoint,
    total_orders,
    total_spent
FROM (
    SELECT 
        (SELECT t.tier_name 
         FROM customer_tiers t 
         WHERE c.accumulatedPoint >= t.min_points 
           AND c.accumulatedPoint <= t.max_points 
         LIMIT 1) as tier,
        c.name,
        c.accumulatedPoint,
        COUNT(o.order_id) as total_orders,
        COALESCE(SUM(o.total), 0) as total_spent,
        ROW_NUMBER() OVER (
            PARTITION BY (SELECT t.tier_name 
                          FROM customer_tiers t 
                          WHERE c.accumulatedPoint >= t.min_points 
                            AND c.accumulatedPoint <= t.max_points 
                          LIMIT 1)
            ORDER BY c.accumulatedPoint DESC
        ) as rank_in_tier
    FROM customers c
    LEFT JOIN web_order o ON o.customer_id = c.id
    GROUP BY c.id, c.name, c.accumulatedPoint
) ranked
WHERE rank_in_tier <= 3
ORDER BY accumulatedPoint DESC;

-- 10. Simulate discount application on orders
SELECT 
    o.order_id,
    c.name as customer_name,
    c.accumulatedPoint,
    (SELECT t.tier_name 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) as tier,
    o.total as original_total,
    (SELECT t.discount_percent 
     FROM customer_tiers t 
     WHERE c.accumulatedPoint >= t.min_points 
       AND c.accumulatedPoint <= t.max_points 
     LIMIT 1) as discount_percent,
    o.total * (1 - COALESCE((SELECT t.discount_percent 
                             FROM customer_tiers t 
                             WHERE c.accumulatedPoint >= t.min_points 
                               AND c.accumulatedPoint <= t.max_points 
                             LIMIT 1), 0) / 100) as discounted_total,
    o.total - (o.total * (1 - COALESCE((SELECT t.discount_percent 
                                        FROM customer_tiers t 
                                        WHERE c.accumulatedPoint >= t.min_points 
                                          AND c.accumulatedPoint <= t.max_points 
                                        LIMIT 1), 0) / 100)) as savings
FROM web_order o
JOIN customers c ON o.customer_id = c.id
ORDER BY o.order_date DESC
LIMIT 20;
