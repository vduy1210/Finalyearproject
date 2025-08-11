// OrderHistory.js
import { Paper, Typography, List, ListItem, Box, TextField, Button } from "@mui/material";
import React from "react";

function OrderHistory() {
  const [tableNumber, setTableNumber] = React.useState("");
  const [orders, setOrders] = React.useState([
    { id: 1, date: "2025-06-30", total: 200000, tableNumber: "Bàn 1" },
    { id: 2, date: "2025-06-29", total: 150000, tableNumber: "Bàn 3" },
  ]);

  const handleTableChange = (event) => {
    setTableNumber(event.target.value);
  };

  const handleConfirmOrder = () => {
    if (!tableNumber.trim()) {
      alert("Vui lòng nhập số bàn!");
      return;
    }
    // Xử lý xác nhận đơn hàng cho bàn cụ thể
    alert(`Đã xác nhận đơn hàng cho ${tableNumber}`);
  };

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
      <Paper elevation={3} sx={{ p: 4, width: 500, borderRadius: 2 }}>
        <Typography variant="h5" fontWeight="bold" mb={3} textAlign="center" color="#1976d2">
          Order Confirmation
        </Typography>
        
        {/* Phần nhập số bàn */}
        <Box sx={{ mb: 3, p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
          <Typography variant="h6" fontWeight="600" mb={2} color="#333">
            Nhập số bàn
          </Typography>
          <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
            <TextField
              label="Số bàn"
              value={tableNumber}
              onChange={handleTableChange}
              placeholder="VD: Bàn 1, Bàn 2..."
              variant="outlined"
              size="small"
              sx={{ flex: 1 }}
            />
            <Button
              variant="contained"
              onClick={handleConfirmOrder}
              sx={{ 
                backgroundColor: "#1976d2",
                "&:hover": { backgroundColor: "#1565c0" }
              }}
            >
              Xác nhận
            </Button>
          </Box>
        </Box>

        {/* Danh sách đơn hàng */}
        <Typography variant="h6" fontWeight="600" mb={2} color="#333">
          Lịch sử đơn hàng
        </Typography>
        <List>
          {orders.map(order => (
            <ListItem 
              key={order.id} 
              divider 
              sx={{ 
                display: "flex", 
                justifyContent: "space-between",
                alignItems: "center",
                py: 1.5
              }}
            >
              <Box>
                <Typography variant="body1" fontWeight="500">
                  Đơn hàng #{order.id}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {order.date} - {order.tableNumber}
                </Typography>
              </Box>
              <Typography variant="body1" fontWeight="600" color="#1976d2">
                {order.total.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}
              </Typography>
            </ListItem>
          ))}
        </List>
      </Paper>
    </Box>
  );
}

export default OrderHistory;