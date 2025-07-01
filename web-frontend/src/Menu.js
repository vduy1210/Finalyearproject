// Menu.js
import React, { useEffect, useState } from "react";
import { Card, CardContent, Button, Typography, Grid } from "@mui/material";

function Menu({ onAddToCart }) {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8081/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  return (
    <div>
      <Typography variant="h4" gutterBottom>Menu</Typography>
      <Grid container spacing={3}>
        {products.map(product => (
          <Grid item xs={12} sm={6} md={4} key={product.id}>
            <Card>
              <CardContent>
                <img src={product.imageUrl} alt={product.name} style={{ width: 100, height: 100, objectFit: "cover" }} />
                <Typography variant="h6">{product.name}</Typography>
                <Typography color="text.secondary">${product.price.toFixed(2)}</Typography>
                <Button 
                  variant="contained" 
                  color="primary" 
                  sx={{ mt: 2 }}
                  onClick={() => onAddToCart(product)}
                  fullWidth
                >
                  Add
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
}

export default Menu;