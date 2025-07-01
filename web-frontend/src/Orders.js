// Orders.js
import { Paper, Typography, Button, Box } from "@mui/material";

function Orders({ cart }) {
    function handleOrder() {
      alert("Order placed!");
    }
  
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <Paper elevation={3} sx={{ p: 4, width: 350 }}>
          <Typography variant="h5" fontWeight="bold" mb={2}>Place Order</Typography>
          <Typography variant="body1">Total items: {cart.length}</Typography>
          <Button onClick={handleOrder} variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Place Order
          </Button>
        </Paper>
      </Box>
    );
  }
  
  export default Orders;