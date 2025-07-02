// App.js
import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";
import Navbar from "./Navbar";
import Login from "./Login";
import Menu from "./Menu";
import Cart from "./Cart";
import Orders from "./Orders";
import ProductManagerPanel from "./ProductManagerPanel";


function App() {
  const [userName, setUserName] = useState(localStorage.getItem("userName") || "");
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem("cart");
    return saved ? JSON.parse(saved) : [];
  });

  // Lưu cart vào localStorage mỗi khi thay đổi
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  // Thêm sản phẩm vào cart
  const addToCart = (product) => {
    setCart(prev => {
      // Nếu sản phẩm đã có trong cart, tăng số lượng
      const idx = prev.findIndex(item => item.id === product.id);
      if (idx !== -1) {
        const updated = [...prev];
        updated[idx].quantity = (updated[idx].quantity || 1) + 1;
        return updated;
      }
      return [...prev, { ...product, quantity: 1 }];
    });
  };

  // Xóa sản phẩm khỏi cart
  const removeFromCart = (idx) => {
    setCart(prev => prev.filter((_, i) => i !== idx));
  };

  // Xóa toàn bộ cart (sau khi đặt hàng thành công)
  const clearCart = () => setCart([]);

  return (
    <Router>
      <Navbar userName={userName} setUserName={setUserName} />
      <Routes>
        <Route path="/login" element={<Login setUserName={setUserName} />} />
        <Route path="/menu" element={<Menu addToCart={addToCart} />} />
        <Route path="/cart" element={<Cart cart={cart} onRemoveFromCart={removeFromCart} />} />
        <Route path="/orders" element={<Orders cart={cart} clearCart={clearCart} userName={userName} />} />
        <Route path="/product-manager" element={<ProductManagerPanel />} />
        <Route path="/" element={<Navigate to="/menu" />} />
      </Routes>
    </Router>
  );
}

export default App;