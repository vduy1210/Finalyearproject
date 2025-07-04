// Login.js
import React, { useState, useEffect } from "react";
import { TextField, Button, Typography, Paper, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

/**
 * Login component: Đăng nhập bằng email và password
 * - Nếu đã đăng nhập, tự động chuyển hướng sang /menu
 * - Nếu đăng nhập sai, hiển thị lỗi
 * - Dùng Material UI cho giao diện
 */
function Login({ setUserName }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  // Nếu đã đăng nhập, tự động chuyển hướng sang /menu
  useEffect(() => {
    if (localStorage.getItem("userName")) {
      navigate("/menu");
    }
  }, [navigate]);

  // Xử lý đăng nhập
  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    if (!email || !password) {
      setError("Vui lòng nhập email và mật khẩu.");
      return;
    }
    try {
      const res = await fetch("http://localhost:8081/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });
      if (!res.ok) throw new Error("Email hoặc mật khẩu không đúng!");
      const data = await res.json();
      if (data.success) {
        localStorage.setItem("userName", data.userName);
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        setUserName(data.userName);
        navigate("/menu");
      } else {
        setError("Đăng nhập thất bại!");
      }
    } catch (err) {
      setError(err.message || "Đăng nhập thất bại!");
    }
  };

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
      <Paper elevation={3} sx={{ p: 4, width: 350 }}>
        <Typography variant="h5" fontWeight="bold" mb={2}>Đăng nhập</Typography>
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
            label="Mật khẩu"
            variant="outlined"
            fullWidth
            margin="normal"
            required
          />
          {error && (
            <Typography color="error" fontSize={14} mt={1}>{error}</Typography>
          )}
          <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Đăng nhập
          </Button>
        </form>
      </Paper>
    </Box>
  );
}

export default Login;