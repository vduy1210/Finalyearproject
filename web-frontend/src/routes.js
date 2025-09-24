import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import Menu from "./components/Menu";
import Cart from "./components/Cart";
import ProductManagerPanel from "./components/ProductManagerPanel";
import RequireAuth from "./components/RequireAuth";

const AppRoutes = ({ setUserName, addToCart, cart, removeFromCart, clearCart, updateCartQuantity, userName }) => (
  <Routes>
    <Route path="/login" element={<Login setUserName={setUserName} />} />
    <Route
      path="/menu"
      element={<Menu addToCart={addToCart} />}
    />
    <Route
      path="/cart"
      element={<Cart cart={cart} onRemoveFromCart={removeFromCart} clearCart={clearCart} updateCartQuantity={updateCartQuantity} />}
    />
    <Route
      path="/product-manager"
      element={
        <RequireAuth>
          <ProductManagerPanel />
        </RequireAuth>
      }
    />
    <Route path="/" element={<Navigate to="/menu" />} />
  </Routes>
);

export default AppRoutes; 