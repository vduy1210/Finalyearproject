// Menu.js
import React, { useEffect, useState } from "react";

function Menu({ addToCart }) {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8081/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  // Responsive styles
  const containerStyle = {
    maxWidth: 600,
    margin: "0 auto",
    padding: 16,
    background: "#22313a",
    borderRadius: 12,
    boxShadow: "0 4px 24px rgba(44,62,80,0.12)",
    width: "95vw"
  };
  const productListStyle = {
    display: "flex",
    flexDirection: "column",
    gap: 20
  };
  const productCardStyle = {
    display: "flex",
    flexDirection: window.innerWidth < 600 ? "column" : "row",
    alignItems: window.innerWidth < 600 ? "flex-start" : "center",
    background: "#2c3e50",
    borderRadius: 10,
    boxShadow: "0 2px 8px rgba(44,62,80,0.08)",
    padding: 16,
    gap: 20,
    border: "1px solid #2980b9"
  };
  const imageStyle = {
    width: window.innerWidth < 600 ? 120 : 90,
    height: window.innerWidth < 600 ? 120 : 90,
    objectFit: "cover",
    borderRadius: 8,
    border: "2px solid #3498db",
    background: "#fff",
    boxShadow: "0 2px 8px rgba(52,152,219,0.08)",
    display: "block",
    marginBottom: window.innerWidth < 600 ? 10 : 0
  };
  const infoStyle = {
    flex: 1,
    width: "100%"
  };
  const nameStyle = {
    fontWeight: 600,
    fontSize: window.innerWidth < 600 ? 20 : 18,
    color: "#ecf0f1",
    marginBottom: 6
  };
  const priceStyle = {
    color: "#27ae60",
    fontWeight: 500,
    fontSize: window.innerWidth < 600 ? 18 : 16,
    marginBottom: 4
  };
  const stockStyle = {
    color: "#f1c40f",
    fontSize: window.innerWidth < 600 ? 16 : 14
  };
  const buttonStyle = {
    background: "#27ae60",
    color: "#fff",
    border: "none",
    borderRadius: 4,
    padding: window.innerWidth < 600 ? "12px 24px" : "8px 16px",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: window.innerWidth < 600 ? 18 : 16,
    marginTop: window.innerWidth < 600 ? 10 : 0,
    width: window.innerWidth < 600 ? "100%" : undefined
  };

  return (
    <div style={containerStyle}>
      <h2 style={{
        color: "#3498db",
        textAlign: "center",
        marginBottom: 24,
        letterSpacing: 1,
        fontSize: window.innerWidth < 600 ? 26 : 22
      }}>Menu Sản Phẩm</h2>
      <div style={productListStyle}>
        {products.map(product => (
          <div
            key={product.id}
            style={productCardStyle}
          >
            {/* Ảnh sản phẩm */}
            {product.imageUrl ? (
              <img
                src={`http://localhost:8081${product.imageUrl}`}
                alt={product.name}
                style={imageStyle}
              />
            ) : (
              <div
                style={{
                  ...imageStyle,
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
            <div style={infoStyle}>
              <div style={nameStyle}>{product.name}</div>
              <div style={priceStyle}>Giá: {product.price.toLocaleString()}₫</div>
              <div style={stockStyle}>Tồn kho: {product.stock}</div>
            </div>
            {/* Nút thêm vào giỏ hàng */}
            <button
              onClick={() => addToCart(product)}
              style={buttonStyle}
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