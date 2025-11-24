// Cart.js
import { Button, Typography, Paper, Box, TextField, Dialog, DialogTitle, DialogContent, DialogActions, FormControl, InputLabel, Select, MenuItem } from "@mui/material";
import React from "react";
import { useNotification } from "./NotificationProvider";

function Cart({ cart, onRemoveFromCart, clearCart, updateCartQuantity }) {
  // Calculate total (in VND)
  const total = cart.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0);
  const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);

  // Get notification functions
  const notification = useNotification();

  // State for modal
  const [openDialog, setOpenDialog] = React.useState(false);
  const [name, setName] = React.useState("");
  const [phone, setPhone] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [tableNumber, setTableNumber] = React.useState("");
  const [submitting, setSubmitting] = React.useState(false);
  const [error, setError] = React.useState("");

  // Available tables list
  const availableTables = [
    "Table 1", "Table 2", "Table 3", "Table 4", "Table 5", 
    "Table 6", "Table 7", "Table 8", "Table 9", "Table 10"
  ];

  // Style similar to Menu
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

  // Open dialog when clicking Place Order
  const handleOpenDialog = () => {
    if (cart.length === 0) {
      alert("Cart is empty!");
      return;
    }
    setOpenDialog(true);
  };

  // Close dialog
  const handleCloseDialog = () => {
    setOpenDialog(false);
    setError("");
    setName("");
    setPhone("");
    setEmail("");
    setTableNumber("");
  };

  // Handle order submission
  async function handleOrder() {
    setError("");
    
    // Validate all required fields
    if (!name || !phone || !email || !tableNumber) {
      setError("Please fill in all information and select a table!");
      return;
    }
    
    // Validate Vietnamese phone format (09/03/07/08/05 + 8 digits)
    const phoneRegex = /^(09|03|07|08|05)\d{8}$/;
    if (!phoneRegex.test(phone)) {
      setError("Invalid Vietnamese phone number! Format: 09/03/07/08/05 + 8 digits (e.g., 0901234567)");
      return;
    }
    
    // Validate email format
    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRegex.test(email)) {
      setError("Invalid email format. Expected format: user@example.com");
      return;
    }
    
    // Validate name length
    if (name.trim().length < 2) {
      setError("Name must be at least 2 characters long");
      return;
    }
    
    if (name.trim().length > 100) {
      setError("Name cannot exceed 100 characters");
      return;
    }

    const payload = {
      name,
      phone,
      email,
      tableNumber,
      items: cart.map((item) => ({
        productId: item.id,
        quantity: item.quantity || 1,
        price: item.price
      }))
    };

    try {
      setSubmitting(true);
      // Use dynamic hostname for mobile access
      const hostname = window.location.hostname;
      const apiUrl = `http://${hostname}:8081/api/orders`;
      const res = await fetch(apiUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Order failed");
      }
      const data = await res.json();
      
      // Show success notification with order details
      notification.orderPlaced({
        orderId: data.orderId,
        tableNumber: tableNumber,
        customerName: name,
        totalAmount: total,
        items: cart.map(item => ({
          name: item.name,
          quantity: item.quantity || 1,
          price: item.price
        }))
      });

      // Clear form and cart
      if (clearCart) clearCart();
      handleCloseDialog();
      
      // Show additional success message in console for debugging
      console.log("Order placed successfully:", {
        orderId: data.orderId,
        tableNumber,
        customer: name,
        total
      });
      
    } catch (e) {
      setError(e.message || "Order failed. Please try again!");
      
      // Show error notification
      notification.error(
        "Order Failed",
        e.message || "Something went wrong while placing your order. Please try again.",
        {
          tableNumber: tableNumber,
          customerName: name,
          totalAmount: total
        }
      );
    } finally {
      setSubmitting(false);
    }
  }

  // Helper to get correct image url
  function getImageUrl(item) {
    if (!item.imageUrl) return null;
    if (item.imageUrl.startsWith('http')) return item.imageUrl;
    
    // Use dynamic hostname for mobile access
    const hostname = window.location.hostname;
    const backendPort = '8081'; // Always use backend port, not frontend port
    
    // Handle URL encoding issues
    let cleanUrl = item.imageUrl;
    // Note: Removed corrupted URL check as unicode characters are valid
    
    const imageUrl = `http://${hostname}:${backendPort}${encodeURI(cleanUrl)}`;
    
    // Debug logging
    console.log('Image URL for', item.name, ':', imageUrl);
    console.log('Original imageUrl from database:', item.imageUrl);
    
    // Return image URL with error handling
    return imageUrl;
  }

  // Function to update quantity
  const updateQuantity = (idx, newQuantity) => {
    if (updateCartQuantity) {
      updateCartQuantity(idx, newQuantity);
    }
  };

  return (
    <Box display="flex" justifyContent="center" alignItems="flex-start" minHeight="60vh" style={{ background: "#f5f7fa" }}>
      <Paper elevation={3} sx={{ p: 4, width: window.innerWidth < 600 ? "98vw" : 620, maxWidth: 680, margin: window.innerWidth < 600 ? "8px auto" : undefined, borderRadius: 18, boxShadow: "0 4px 24px rgba(44,62,80,0.08)", fontFamily: 'Roboto, Arial, sans-serif', background: "#f5f7fa", display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="h4" gutterBottom fontSize={window.innerWidth < 600 ? 26 : 22} fontWeight={800} color="#1976d2" textAlign="center" mb={3} fontFamily="'Roboto, Arial, sans-serif'">Cart</Typography>
        
        {/* Product list */}
        <div style={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0 }}>
          {cart.map((item, idx) => (
            <div key={idx} style={{ ...productCardStyle, width: '100%', maxWidth: 400 }}>
              {item.imageUrl ? (
                <img 
                  src={getImageUrl(item)} 
                  alt={item.name} 
                  style={imageStyle}
                  onError={(e) => {
                    console.log('Image load error for', item.name, ':', e.target.src);
                    console.log('Error details:', e);
                    e.target.style.display = 'none';
                    e.target.nextSibling.style.display = 'flex';
                  }}
                  onLoad={(e) => {
                    console.log('Image loaded successfully for', item.name);
                    e.target.nextSibling.style.display = 'none';
                  }}
                />
              ) : null}
              <div style={{...placeholderStyle, display: item.imageUrl ? 'none' : 'flex'}}>
                <div style={{textAlign: 'center', color: '#90a4ae', fontSize: '12px'}}>
                  <div style={{fontSize: '24px', marginBottom: '4px'}}>📷</div>
                  <div>No Image</div>
                  {item.imageUrl && (
                    <div style={{fontSize: '10px', marginTop: '4px', color: '#bbb'}}>
                      URL: {item.imageUrl}
                    </div>
                  )}
                </div>
              </div>
              <div style={infoStyle}>
                <div style={nameStyle}>{item.name}</div>
                <div style={priceStyle}>Price: {item.price.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</div>
                <div style={{display: 'flex', alignItems: 'center', gap: 8, marginTop: 8}}>
                  <span style={qtyStyle}>Quantity:</span>
                  <div style={{display: 'flex', alignItems: 'center', gap: 4}}>
                    <button
                      style={{
                        background: '#f5f5f5',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        width: '28px',
                        height: '28px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '16px',
                        fontWeight: 'bold'
                      }}
                      onClick={() => updateQuantity(idx, (item.quantity || 1) - 1)}
                    >
                      -
                    </button>
                    <span style={{
                      minWidth: '30px',
                      textAlign: 'center',
                      fontWeight: 'bold',
                      fontSize: '16px'
                    }}>
                      {item.quantity || 1}
                    </span>
                    <button
                      style={{
                        background: '#f5f5f5',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        width: '28px',
                        height: '28px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '16px',
                        fontWeight: 'bold'
                      }}
                      onClick={() => updateQuantity(idx, (item.quantity || 1) + 1)}
                    >
                      +
                    </button>
                  </div>
                </div>
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

        {/* Total and order button */}
        {cart.length > 0 && (
          <div style={{ width: '100%', marginTop: 24, textAlign: 'center' }}>
            <Typography variant="body2" sx={{ mt: 1 }} style={totalStyle}>Total Items: {totalItems}</Typography>
            <Typography variant="body2" sx={{ mt: 0.5 }} style={totalStyle}>Total Amount: {total.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}</Typography>
            
            <button
              onClick={handleOpenDialog}
              style={{ ...orderButtonStyle, ...(orderHover ? orderButtonHover : {}) }}
              onMouseEnter={() => setOrderHover(true)}
              onMouseLeave={() => setOrderHover(false)}
            >
              Place Order
            </button>
          </div>
        )}

        {/* Customer information dialog */}
        <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
          <DialogTitle sx={{ color: "#1976d2", fontWeight: 700, textAlign: "center" }}>
            Order Information
          </DialogTitle>
          <DialogContent>
            {/* Table selection */}
            <FormControl fullWidth size="small" sx={{ mb: 2 }}>
              <InputLabel id="table-select-label">Select Table</InputLabel>
              <Select
                labelId="table-select-label"
                value={tableNumber}
                label="Select Table"
                onChange={(e) => setTableNumber(e.target.value)}
              >
                {availableTables.map((table) => (
                  <MenuItem key={table} value={table}>{table}</MenuItem>
                ))}
              </Select>
            </FormControl>

            {/* Customer information */}
            <TextField 
              label="Full Name" 
              value={name} 
              onChange={(e)=>setName(e.target.value)} 
              fullWidth 
              size="small" 
              margin="dense" 
              sx={{ mb: 2 }}
            />
            <TextField 
              label="Phone Number" 
              value={phone} 
              onChange={(e)=>setPhone(e.target.value)} 
              fullWidth 
              size="small" 
              margin="dense" 
              sx={{ mb: 2 }}
            />
            <TextField 
              label="Email" 
              value={email} 
              onChange={(e)=>setEmail(e.target.value)} 
              fullWidth 
              size="small" 
              margin="dense" 
              sx={{ mb: 2 }}
            />
            {error && (
              <Typography color="error" fontSize={14} mt={1}>{error}</Typography>
            )}
          </DialogContent>
          <DialogActions sx={{ p: 3, justifyContent: "center" }}>
            <Button 
              onClick={handleCloseDialog} 
              variant="outlined" 
              color="primary"
              sx={{ mr: 2 }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleOrder}
              variant="contained"
              color="primary"
              disabled={submitting}
              sx={{ 
                background: "#1976d2",
                "&:hover": { background: "#64b5f6" }
              }}
            >
              {submitting ? "Processing..." : "Confirm"}
            </Button>
          </DialogActions>
        </Dialog>
      </Paper>
    </Box>
  );
}

export default Cart;