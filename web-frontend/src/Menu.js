// Menu.js
import React, { useEffect, useState } from "react";

function Menu({ addToCart }) {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8081/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  return (
    <div style={{
      maxWidth: 600,
      margin: "0 auto",
      padding: 16,
      background: "#22313a",
      borderRadius: 12,
      boxShadow: "0 4px 24px rgba(44,62,80,0.12)"
    }}>
      <h2 style={{
        color: "#3498db",
        textAlign: "center",
        marginBottom: 24,
        letterSpacing: 1
      }}>Menu Sản Phẩm</h2>
      <div style={{
        display: "flex",
        flexDirection: "column",
        gap: 20
      }}>
        {products.map(product => (
          <div
            key={product.id}
            style={{
              display: "flex",
              alignItems: "center",
              background: "#2c3e50",
              borderRadius: 10,
              boxShadow: "0 2px 8px rgba(44,62,80,0.08)",
              padding: 16,
              gap: 20,
              transition: "box-shadow 0.2s",
              border: "1px solid #2980b9"
            }}
          >
            {/* Ảnh sản phẩm */}
            {product.imageUrl ? (
              <img
                src={`http://localhost:8081${product.imageUrl}`}
                alt={product.name}
                style={{
                  width: 90,
                  height: 90,
                  objectFit: "cover",
                  borderRadius: 8,
                  border: "2px solid #3498db",
                  background: "#fff",
                  boxShadow: "0 2px 8px rgba(52,152,219,0.08)",
                  display: "block"
                }}
              />
            ) : (
              <div
                style={{
                  width: 90,
                  height: 90,
                  borderRadius: 8,
                  background: "#bdc3c7",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  color: "#7f8c8d",
                  fontSize: 13,
                  fontStyle: "italic",
                  border: "2px dashed #95a5a6"
                }}
              >
                No Image
              </div>
            )}

            {/* Thông tin sản phẩm */}
            <div style={{ flex: 1 }}>
              <div style={{
                fontWeight: 600,
                fontSize: 18,
                color: "#ecf0f1",
                marginBottom: 6
              }}>{product.name}</div>
              <div style={{
                color: "#27ae60",
                fontWeight: 500,
                fontSize: 16,
                marginBottom: 4
              }}>Giá: {product.price.toLocaleString()}₫</div>
              <div style={{
                color: "#f1c40f",
                fontSize: 14
              }}>Tồn kho: {product.stock}</div>
            </div>
            {/* Nút thêm vào giỏ hàng */}
            <button
              onClick={() => addToCart(product)}
              style={{
                background: "#27ae60",
                color: "#fff",
                border: "none",
                borderRadius: 4,
                padding: "8px 16px",
                cursor: "pointer",
                fontWeight: 600
              }}
            >
              Thêm vào giỏ
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Menu;