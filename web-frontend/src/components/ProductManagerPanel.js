import React, { useState, useEffect, useRef } from "react";
import ProductEdit from "./ProductEdit";

function ProductManagerPanel() {
  const [products, setProducts] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const editFormRef = useRef(null);

  // Fetch products from backend
  function fetchProducts() {
    fetch("http://localhost:8081/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }

  useEffect(() => {
    fetchProducts();
  }, []);

  // Scroll to edit form when product is selected
  useEffect(() => {
    if (selectedProduct && editFormRef.current) {
      editFormRef.current.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'nearest' 
      });
    }
  }, [selectedProduct]);

  // Style đồng bộ với Menu/Cart
  const containerStyle = {
    maxWidth: 600,
    margin: "0 auto",
    padding: 16,
    background: "#f5f7fa",
    borderRadius: 18,
    boxShadow: "0 4px 24px rgba(44,62,80,0.08)",
    width: "95vw",
    fontFamily: 'Roboto, Arial, sans-serif',
    minHeight: '70vh'
  };
  const cardStyle = {
    display: "flex",
    flexDirection: window.innerWidth < 600 ? "column" : "row",
    alignItems: window.innerWidth < 600 ? "flex-start" : "center",
    background: "#fff",
    borderRadius: 14,
    boxShadow: "0 2px 12px rgba(44,62,80,0.08)",
    padding: 18,
    gap: 20,
    border: "1.5px solid #bbdefb",
    marginBottom: 24,
    transition: "transform 0.18s cubic-bezier(.4,0,.2,1), box-shadow 0.18s",
    cursor: "pointer"
  };
  const imageStyle = {
    width: window.innerWidth < 600 ? 120 : 90,
    height: window.innerWidth < 600 ? 120 : 90,
    objectFit: "cover",
    borderRadius: 10,
    border: "2px solid #bbdefb",
    background: "#fff",
    boxShadow: "0 2px 8px rgba(52,152,219,0.08)",
    display: "block",
    marginBottom: window.innerWidth < 600 ? 10 : 0
  };
  const placeholderStyle = {
    ...imageStyle,
    background: "#e3f2fd",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#90a4ae",
    fontSize: 15,
    fontStyle: "italic",
    border: "2px dashed #bbdefb"
  };
  const infoStyle = {
    flex: 1,
    width: "100%"
  };
  const nameStyle = {
    fontWeight: 700,
    fontSize: window.innerWidth < 600 ? 22 : 18,
    color: "#1976d2",
    marginBottom: 6,
    fontFamily: 'Roboto, Arial, sans-serif'
  };
  const priceStyle = {
    color: "#64b5f6",
    fontWeight: 700,
    fontSize: window.innerWidth < 600 ? 20 : 16,
    marginBottom: 4
  };
  const stockStyle = {
    color: "#888",
    fontSize: window.innerWidth < 600 ? 16 : 14
  };
  const buttonStyle = {
    background: "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: window.innerWidth < 600 ? "14px 0" : "10px 24px",
    cursor: "pointer",
    fontWeight: 700,
    fontSize: window.innerWidth < 600 ? 20 : 16,
    marginTop: window.innerWidth < 600 ? 10 : 0,
    width: window.innerWidth < 600 ? "100%" : 120,
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    transition: "background 0.18s"
  };
  const buttonHover = {
    background: "#64b5f6"
  };
  const [btnHoverIdx, setBtnHoverIdx] = useState(-1);

  return (
    <div style={containerStyle}>
      <h2 style={{
        color: "#1976d2",
        textAlign: "center",
        marginBottom: 28,
        letterSpacing: 1,
        fontSize: window.innerWidth < 600 ? 28 : 22,
        fontWeight: 800,
        fontFamily: 'Roboto, Arial, sans-serif'
      }}>Product Manager Panel</h2>
      <div style={{ display: "flex", flexDirection: "column", gap: 0 }}>
        {products.map((product, idx) => (
          <React.Fragment key={product.id}>
            <div style={cardStyle}>
              {product.imageUrl ? (
                <img
                  src={`http://localhost:8081${encodeURI(product.imageUrl)}`}
                  alt={product.name}
                  style={imageStyle}
                />
              ) : (
                <div style={placeholderStyle}>No Image</div>
              )}
              <div style={infoStyle}>
                <div style={nameStyle}>{product.name}</div>
                <div style={priceStyle}>Price: {product.price.toLocaleString()}₫</div>
                <div style={stockStyle}>Stock: {product.stock}</div>
              </div>
              <button
                onClick={() => setSelectedProduct(product)}
                style={{ ...buttonStyle, ...(btnHoverIdx === idx ? buttonHover : {}) }}
                onMouseEnter={() => setBtnHoverIdx(idx)}
                onMouseLeave={() => setBtnHoverIdx(-1)}
              >
                Edit
              </button>
            </div>
            {/* Show edit form right below the selected product */}
            {selectedProduct && selectedProduct.id === product.id && (
              <div ref={editFormRef} style={{ marginBottom: 24 }}>
                <ProductEdit
                  product={selectedProduct}
                  onImageUploaded={fetchProducts}
                />
              </div>
            )}
          </React.Fragment>
        ))}
      </div>
    </div>
  );
}

export default ProductManagerPanel;
