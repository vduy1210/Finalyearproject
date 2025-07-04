// Cart.js
import { Card, CardContent, Button, Typography, Grid, Paper, Box } from "@mui/material";
import React from "react";

function Cart({ cart, onRemoveFromCart, clearCart }) {
  // Tính tổng tiền (theo VND)
  const total = cart.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0);
  const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);

  // Style giống Menu
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
  const qtyStyle = {
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
  const totalStyle = {
    fontSize: window.innerWidth < 600 ? 22 : 18,
    fontWeight: 700,
    marginTop: 16,
    color: "#1976d2"
  };
  const orderButtonStyle = {
    background: "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: window.innerWidth < 600 ? "16px 0" : "12px 0",
    cursor: "pointer",
    fontSize: window.innerWidth < 600 ? 20 : 16,
    fontWeight: 700,
    marginTop: 24,
    width: "100%",
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    transition: "background 0.18s"
  };
  const orderButtonHover = {
    background: "#64b5f6"
  };

  // Hover effect for remove/order button
  const [hoverIdx, setHoverIdx] = React.useState(-1);
  const [orderHover, setOrderHover] = React.useState(false);

  function handleOrder() {
    alert("Order placed successfully!");
    if (clearCart) clearCart();
  }

  // Helper to get correct image url
  function getImageUrl(item) {
    if (!item.imageUrl) return null;
    if (item.imageUrl.startsWith('http')) return item.imageUrl;
    return `http://localhost:8081${item.imageUrl}`;
  }

  return (
    <Box display="flex" justifyContent="center" alignItems="flex-start" minHeight="60vh" style={{ background: "#f5f7fa" }}>
      <Paper elevation={3} sx={{ p: 4, width: window.innerWidth < 600 ? "98vw" : 500, maxWidth: 500, margin: window.innerWidth < 600 ? "8px auto" : undefined, borderRadius: 18, boxShadow: "0 4px 24px rgba(44,62,80,0.08)", fontFamily: 'Roboto, Arial, sans-serif', background: "#f5f7fa", display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="h4" gutterBottom fontSize={window.innerWidth < 600 ? 26 : 22} fontWeight={800} color="#1976d2" textAlign="center" mb={3} fontFamily="'Roboto, Arial, sans-serif'">Cart</Typography>
        <div style={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0 }}>
          {cart.map((item, idx) => (
            <div key={idx} style={{ ...productCardStyle, width: '100%', maxWidth: 400 }}>
              {item.imageUrl ? (
                <img src={getImageUrl(item)} alt={item.name} style={imageStyle} />
              ) : (
                <div style={placeholderStyle}>No Image</div>
              )}
              <div style={infoStyle}>
                <div style={nameStyle}>{item.name}</div>
                <div style={priceStyle}>Price: {item.price.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</div>
                <div style={qtyStyle}>Quantity: {item.quantity || 1}</div>
              </div>
              <button
                style={{ ...buttonStyle, ...(hoverIdx === idx ? buttonHover : {}) }}
                onMouseEnter={() => setHoverIdx(idx)}
                onMouseLeave={() => setHoverIdx(-1)}
                onClick={() => onRemoveFromCart(idx)}
              >
                Remove
              </button>
            </div>
          ))}
        </div>
        <Typography variant="h6" sx={{ mt: 4 }} style={totalStyle}>Total Quantity: {totalItems}</Typography>
        <Typography variant="h6" sx={{ mt: 1 }} style={totalStyle}>Total Price: {total.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</Typography>
        <Button
          onClick={handleOrder}
          variant="contained"
          color="primary"
          fullWidth
          sx={{ mt: 3 }}
          style={{ ...orderButtonStyle, ...(orderHover ? orderButtonHover : {}) }}
          disabled={cart.length === 0}
          onMouseEnter={() => setOrderHover(true)}
          onMouseLeave={() => setOrderHover(false)}
        >
          Place Order
        </Button>
      </Paper>
    </Box>
  );
}

export default Cart;