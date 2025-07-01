// Login.js
import { useState } from "react";
import { TextField, Button, Typography, Paper, Box } from "@mui/material";

function Login({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function handleLogin(e) {
    e.preventDefault();
    if (email && password) {
      onLogin({ name: "Test User", email });
    }
  }

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
      <Paper elevation={3} sx={{ p: 4, width: 350 }}>
        <Typography variant="h5" fontWeight="bold" mb={2}>Login</Typography>
        <form onSubmit={handleLogin}>
          <TextField
            value={email}
            onChange={e => setEmail(e.target.value)}
            type="email"
            label="Email"
            variant="outlined"
            fullWidth
            margin="normal"
            required
          />
          <TextField
            value={password}
            onChange={e => setPassword(e.target.value)}
            type="password"
            label="Password"
            variant="outlined"
            fullWidth
            margin="normal"
            required
          />
          <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Login
          </Button>
        </form>
      </Paper>
    </Box>
  );
}

export default Login;