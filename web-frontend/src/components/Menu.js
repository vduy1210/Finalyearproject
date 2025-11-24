// Menu.js
import React, { useEffect, useState } from "react";

function Menu({ addToCart }) {
  const [products, setProducts] = useState([]);
  const [expandedProduct, setExpandedProduct] = useState(null);

  const toggleProduct = (productId) => {
    setExpandedProduct(expandedProduct === productId ? null : productId);
  };

  useEffect(() => {
    // Use IP address instead of localhost for mobile access
    const apiUrl = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
      ? "http://localhost:8081/api/products"
      : `http://${window.location.hostname}:8081/api/products`;
    
    fetch(apiUrl)
      .then(res => res.json())
      .then(data => setProducts(data))
      .catch(error => {
        console.error('Error fetching products:', error);
        // Fallback to localhost if IP fails
        if (apiUrl !== "http://localhost:8081/api/products") {
          fetch("http://localhost:8081/api/products")
            .then(res => res.json())
            .then(data => setProducts(data))
            .catch(err => console.error('Fallback also failed:', err));
        }
      });
  }, []);

  // Responsive & modern styles
  const containerStyle = {
    maxWidth: 600,
    margin: "0 auto",
    padding: 16,
    background: "#f5f7fa",
    borderRadius: 18,
    boxShadow: "0 4px 24px rgba(44,62,80,0.08)",
    width: "95vw",
    fontFamily: 'Roboto, Arial, sans-serif'
  };
  const productListStyle = {
    display: "flex",
    flexDirection: "column",
    gap: 24
  };
  const productCardStyle = {
    display: "flex",
    flexDirection: window.innerWidth < 600 ? "column" : "row",
    alignItems: window.innerWidth < 600 ? "flex-start" : "center",
    background: "#fff",
    borderRadius: 14,
    boxShadow: "0 2px 12px rgba(44,62,80,0.08)",
    padding: 18,
    gap: 20,
    border: "1.5px solid #bbdefb",
    transition: "transform 0.18s cubic-bezier(.4,0,.2,1), box-shadow 0.18s",
    cursor: "pointer"
  };
  const productCardHover = {
    transform: "scale(1.025)",
    boxShadow: "0 6px 24px rgba(25,118,210,0.13)"
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
    width: window.innerWidth < 600 ? "100%" : 180,
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    transition: "background 0.18s"
  };
  const buttonHover = {
    background: "#64b5f6"
  };

  // Hover effect for card/button
  const [hoverIdx, setHoverIdx] = useState(-1);
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
      }}>Product Menu</h2>
      <div style={productListStyle}>
        {products.map((product, idx) => (
          <React.Fragment key={product.id}>
            <div
              style={{ ...productCardStyle, ...(hoverIdx === idx ? productCardHover : {}) }}
              onMouseEnter={() => setHoverIdx(idx)}
              onMouseLeave={() => setHoverIdx(-1)}
              onClick={() => toggleProduct(product.id)}
            >
              {/* Ảnh sản phẩm */}
              {product.imageUrl ? (
                <img
                  src={`http://${window.location.hostname}:8081${encodeURI(product.imageUrl)}`}
                  alt={product.name}
                  style={imageStyle}
                  onError={(e) => {
                    console.log('Image load error:', e.target.src);
                    e.target.style.display = 'none';
                    e.target.nextSibling.style.display = 'flex';
                  }}
                />
              ) : null}
              <div style={{...placeholderStyle, display: product.imageUrl ? 'none' : 'flex'}}>
                No Image
              </div>

              {/* Thông tin sản phẩm */}
              <div style={infoStyle}>
                <div style={nameStyle}>{product.name}</div>
                <div style={priceStyle}>Price: {product.price.toLocaleString()}₫</div>
                <div style={stockStyle}>Stock: {product.stock}</div>
              </div>
              {/* Nút thêm vào giỏ hàng */}
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  addToCart(product);
                }}
                style={{ ...buttonStyle, ...(btnHoverIdx === idx ? buttonHover : {}) }}
                onMouseEnter={() => setBtnHoverIdx(idx)}
                onMouseLeave={() => setBtnHoverIdx(-1)}
              >
                Add to Cart
              </button>
            </div>
            
            {/* Description Section - Hiển thị khi click vào card */}
            {expandedProduct === product.id && (
              <div style={{
                background: '#fff',
                borderRadius: 14,
                padding: 18,
                marginTop: -12,
                marginBottom: 12,
                border: "1.5px solid #bbdefb",
                borderTop: 'none',
                borderTopLeftRadius: 0,
                borderTopRightRadius: 0,
                boxShadow: "0 4px 12px rgba(44,62,80,0.08)",
                fontSize: 15,
                color: '#555',
                lineHeight: '1.6',
                fontFamily: 'Roboto, Arial, sans-serif'
              }}>
                <div style={{
                  fontWeight: 600,
                  color: '#1976d2',
                  marginBottom: 8,
                  fontSize: 16
                }}>Description:</div>
                {product.description ? (
                  <div>{product.description}</div>
                ) : (
                  <div style={{ fontStyle: 'italic', color: '#999' }}>
                    No description available for this product.
                  </div>
                )}
              </div>
            )}
          </React.Fragment>
        ))}
      </div>
    </div>
  );
}

export default Menu;