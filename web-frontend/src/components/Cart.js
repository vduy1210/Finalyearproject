// Cart.js
import { Card, CardContent, Button, Typography, Grid, Paper, Box } from "@mui/material";
import React from "react";

function Cart({ cart, onRemoveFromCart, clearCart }) {
  // Tính tổng tiền (theo VND)
  const total = cart.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0);
  const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);

  // Responsive styles
  const paperStyle = {
    p: 4,
    width: window.innerWidth < 600 ? "98vw" : 500,
    maxWidth: 500,
    margin: window.innerWidth < 600 ? "8px auto" : undefined
  };
  const cardContentStyle = {
    display: 'flex',
    flexDirection: window.innerWidth < 600 ? "column" : "row",
    justifyContent: 'space-between',
    alignItems: window.innerWidth < 600 ? "flex-start" : "center",
    gap: window.innerWidth < 600 ? 10 : 0
  };
  const nameStyle = {
    fontSize: window.innerWidth < 600 ? 20 : 16,
    fontWeight: 600
  };
  const priceStyle = {
    fontSize: window.innerWidth < 600 ? 18 : 14,
    color: "#27ae60"
  };
  const qtyStyle = {
    fontSize: window.innerWidth < 600 ? 16 : 14
  };
  const buttonStyle = {
    background: "#e74c3c",
    color: "#fff",
    border: "none",
    borderRadius: 4,
    padding: window.innerWidth < 600 ? "12px 24px" : "6px 14px",
    cursor: "pointer",
    fontSize: window.innerWidth < 600 ? 18 : 16,
    marginTop: window.innerWidth < 600 ? 10 : 0,
    width: window.innerWidth < 600 ? "100%" : undefined
  };
  const totalStyle = {
    fontSize: window.innerWidth < 600 ? 22 : 18,
    fontWeight: 700,
    marginTop: 16
  };

  function handleOrder() {
    // Ở đây bạn có thể gọi API đặt hàng thực tế nếu muốn
    alert("Đặt hàng thành công!");
    if (clearCart) clearCart();
  }

  return (
    <Box display="flex" justifyContent="center" alignItems="flex-start" minHeight="60vh">
      <Paper elevation={3} sx={paperStyle}>
        <Typography variant="h4" gutterBottom fontSize={window.innerWidth < 600 ? 26 : 22}>Giỏ hàng</Typography>
        <Grid container spacing={3}>
          {cart.map((item, idx) => (
            <Grid item xs={12} key={idx}>
              <Card>
                <CardContent sx={cardContentStyle}>
                  <div>
                    <Typography variant="h6" style={nameStyle}>{item.name}</Typography>
                    <Typography color="text.secondary" style={priceStyle}>Đơn giá: {item.price.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</Typography>
                    <Typography color="text.secondary" style={qtyStyle}>Số lượng: {item.quantity || 1}</Typography>
                  </div>
                  <Button style={buttonStyle} onClick={() => onRemoveFromCart(idx)}>
                    Xóa
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
        <Typography variant="h6" sx={{ mt: 4 }} style={totalStyle}>Tổng số lượng: {totalItems}</Typography>
        <Typography variant="h6" sx={{ mt: 1 }} style={totalStyle}>Tổng tiền: {total.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</Typography>
        <Button onClick={handleOrder} variant="contained" color="primary" fullWidth sx={{ mt: 3 }} style={buttonStyle} disabled={cart.length === 0}>
          Đặt hàng
        </Button>
      </Paper>
    </Box>
  );
}

export default Cart;