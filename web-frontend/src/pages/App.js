// App.js
import React, { useState, useEffect } from "react";
import { BrowserRouter as Router } from "react-router-dom";
import Navbar from "../components/Navbar";
import { NotificationProvider, useNotification } from "../components/NotificationProvider";
import AppRoutes from "../routes";


function AppContent() {
  const notification = useNotification();
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
      // Nếu sản phẩm đã có trong cart, tăng số lượng đúng cách
      const idx = prev.findIndex(item => item.id === product.id);
      if (idx !== -1) {
        const updated = [...prev];
        updated[idx] = {
          ...updated[idx],
          quantity: (updated[idx].quantity || 1) + 1
        };
        // Thông báo khi thêm số lượng
        notification.success(
          'Added to Cart',
          `${product.name} quantity increased to ${updated[idx].quantity}`
        );
        return updated;
      }
      // Thông báo khi thêm món mới
      notification.success(
        'Added to Cart',
        `${product.name} has been added to your cart`
      );
      return [...prev, { ...product, quantity: 1 }];
    });
  };

  // Xóa sản phẩm khỏi cart
  const removeFromCart = (idx) => {
    setCart(prev => prev.filter((_, i) => i !== idx));
  };

  // Xóa toàn bộ cart (sau khi đặt hàng thành công)
  const clearCart = () => setCart([]);

  // Cập nhật số lượng sản phẩm trong cart
  const updateCartQuantity = (idx, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(idx);
      return;
    }
    setCart(prev => {
      const updated = [...prev];
      updated[idx] = { ...updated[idx], quantity: newQuantity };
      return updated;
    });
  };

  return (
    <Router>
      <Navbar userName={userName} setUserName={setUserName} />
      <AppRoutes 
        setUserName={setUserName}
        addToCart={addToCart}
        cart={cart}
        removeFromCart={removeFromCart}
        clearCart={clearCart}
        updateCartQuantity={updateCartQuantity}
        userName={userName}
      />
    </Router>
  );
}

function App() {
  return (
    <NotificationProvider>
      <AppContent />
    </NotificationProvider>
  );
}

export default App;