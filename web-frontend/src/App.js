// App.js
import { BrowserRouter as Router, Route, Routes, Navigate, Link as RouterLink } from "react-router-dom";
import { useState, createContext } from "react";
import Register from "./Register";
import Login from "./Login";
import Menu from "./Menu";
import Cart from "./Cart";
import Orders from "./Orders";
import OrderHistory from "./OrderHistory";
import { AppBar, Toolbar, Button, Typography, Container, Box, CssBaseline } from "@mui/material";
import { ThemeProvider, createTheme } from "@mui/material/styles";

export const AuthContext = createContext();

const theme = createTheme({
  palette: {
    primary: { main: '#1976d2' },
    secondary: { main: '#f50057' },
  },
});

function App() {
  const [user, setUser] = useState(null);
  const [cart, setCart] = useState([]);

  function addToCart(product) {
    setCart(prev => [...prev, product]);
  }

  function removeFromCart(index) {
    setCart(prev => prev.filter((_, i) => i !== index));
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthContext.Provider value={{ user, setUser }}>
        <Router>
          <AppBar position="static">
            <Toolbar>
              <Typography variant="h6" sx={{ flexGrow: 1 }}>
                My Modern Web
              </Typography>
              <Button color="inherit" component={RouterLink} to="/">Menu</Button>
              <Button color="inherit" component={RouterLink} to="/cart">Cart</Button>
              <Button color="inherit" component={RouterLink} to="/orders">Orders</Button>
              <Button color="inherit" component={RouterLink} to="/order-history">Order History</Button>
              {!user ? (
                <>
                  <Button color="inherit" component={RouterLink} to="/register">Register</Button>
                  <Button color="inherit" component={RouterLink} to="/login">Login</Button>
                </>
              ) : (
                <Typography variant="body1" sx={{ ml: 2 }}>Hello, {user.name}</Typography>
              )}
            </Toolbar>
          </AppBar>
          <Container maxWidth="md" sx={{ mt: 4 }}>
            <Box>
              <Routes>
                <Route path="/" element={<Menu onAddToCart={addToCart} />} />
                <Route path="/register" element={<Register />} />
                <Route path="/login" element={<Login onLogin={setUser} />} />
                <Route path="/cart" element={<Cart cart={cart} onRemoveFromCart={removeFromCart} />} />
                <Route path="/orders" element={<Orders cart={cart} />} />
                <Route path="/order-history" element={<OrderHistory />} />
                <Route path="*" element={<Navigate to="/" />} />
              </Routes>
            </Box>
          </Container>
        </Router>
      </AuthContext.Provider>
    </ThemeProvider>
  );
}

export default App;