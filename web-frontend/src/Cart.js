// Cart.js
import { Card, CardContent, Button, Typography, Grid } from "@mui/material";

function Cart({ cart, onRemoveFromCart }) {
  const total = cart.reduce((sum, item) => sum + item.price, 0);
  return (
    <div>
      <Typography variant="h4" gutterBottom>Cart</Typography>
      <Grid container spacing={3}>
        {cart.map((item, idx) => (
          <Grid item xs={12} key={idx}>
            <Card>
              <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <Typography variant="h6">{item.name}</Typography>
                  <Typography color="text.secondary">${item.price}</Typography>
                </div>
                <Button color="error" variant="outlined" onClick={() => onRemoveFromCart(idx)}>
                  Remove
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      <Typography variant="h6" sx={{ mt: 4 }}>Total: ${total}</Typography>
    </div>
  );
}

export default Cart;