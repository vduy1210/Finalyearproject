// OrderHistory.js
import { Paper, Typography, List, ListItem, Box } from "@mui/material";

function OrderHistory() {
  const history = [
    { id: 1, date: "2025-06-30", total: 20 },
    { id: 2, date: "2025-06-29", total: 15 },
  ];

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
      <Paper elevation={3} sx={{ p: 4, width: 400 }}>
        <Typography variant="h5" fontWeight="bold" mb={2}>Order History</Typography>
        <List>
          {history.map(order => (
            <ListItem key={order.id} divider>
              Order #{order.id} - {order.date} - ${order.total}
            </ListItem>
          ))}
        </List>
      </Paper>
    </Box>
  );
}

export default OrderHistory;