import React, { useState, useEffect } from "react";
import ProductEdit from "./ProductEdit";

function ProductManagerPanel() {
  const [products, setProducts] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Fetch products from backend
  function fetchProducts() {
    fetch("http://localhost:8081/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }

  useEffect(() => {
    fetchProducts();
  }, []);

  return (
    <div className="container">
      <h2>Product Manager Panel</h2>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {products.map(product => (
          <li
            key={product.id}
            style={{
              marginBottom: 12,
              background: "#34495e",
              padding: 12,
              borderRadius: 6,
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <span>
              <strong>{product.name}</strong> — {product.price}₫ — Stock: {product.stock}
            </span>
            <button
              onClick={() => setSelectedProduct(product)}
              style={{
                background: "#2980b9",
                color: "#fff",
                border: "none",
                borderRadius: 4,
                padding: "6px 14px",
                cursor: "pointer",
              }}
            >
              Edit
            </button>
          </li>
        ))}
      </ul>
      <ProductEdit
        product={selectedProduct}
        onImageUploaded={fetchProducts}
      />
    </div>
  );
}

export default ProductManagerPanel;
