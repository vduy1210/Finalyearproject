-- Script tạo bảng web_order_details
-- Đảm bảo bảng này có đúng cấu trúc để lưu chi tiết đơn hàng web

-- Tạo bảng web_order_details nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS web_order_details (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT,
    product_id BIGINT,
    quantity   INT,
    price      DOUBLE,
    CONSTRAINT fk_web_order_details_order
        FOREIGN KEY (order_id) REFERENCES web_order(order_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_web_order_details_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tạo index để tối ưu hiệu suất truy vấn
CREATE INDEX idx_web_order_details_order_id ON web_order_details(order_id);
CREATE INDEX idx_web_order_details_product_id ON web_order_details(product_id);

-- Kiểm tra xem bảng đã được tạo thành công
DESCRIBE web_order_details;
