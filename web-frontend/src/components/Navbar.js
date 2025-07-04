import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { isLoggedIn, logout as authLogout } from "../services/authService";

function Navbar({ setUserName }) {
  const navigate = useNavigate();
  const loggedIn = isLoggedIn();
  const role = localStorage.getItem("role");
  const [open, setOpen] = useState(false);

  function handleLogout() {
    authLogout();
    setUserName("");
    navigate("/login");
    setOpen(false);
  }

  // Detect mobile
  const isMobile = window.innerWidth < 600;

  // Slide menu styles for mobile
  const drawerStyle = {
    position: "fixed",
    top: 0,
    right: open ? 0 : "-70vw",
    width: "70vw",
    height: "100vh",
    background: "#2980b9",
    boxShadow: open ? "-2px 0 12px rgba(44,62,80,0.18)" : "none",
    zIndex: 2000,
    transition: "right 0.3s cubic-bezier(.4,0,.2,1)",
    display: open && isMobile ? "flex" : "none",
    flexDirection: "column",
    padding: "32px 20px 20px 20px"
  };
  const overlayStyle = {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100vw",
    height: "100vh",
    background: "rgba(0,0,0,0.25)",
    zIndex: 1999,
    display: open ? "block" : "none"
  };
  const linkStyle = {
    color: "#fff",
    textDecoration: "none",
    fontWeight: 500,
    fontSize: 20,
    margin: "18px 0"
  };
  const buttonStyle = {
    background: "#e74c3c",
    color: "#fff",
    border: "none",
    borderRadius: 4,
    padding: "12px 24px",
    cursor: "pointer",
    fontSize: 20,
    margin: "18px 0"
  };
  const hamburger = (
    <button
      onClick={() => setOpen(true)}
      style={{
        background: "transparent",
        border: "none",
        color: "#2980b9",
        fontSize: 32,
        position: "fixed",
        top: 14,
        right: 18,
        zIndex: 2100,
        display: isMobile ? "block" : "none"
      }}
      aria-label="Mở menu"
    >
      ☰
    </button>
  );

  // Navbar ngang cho desktop/tablet
  const navStyle = {
    background: "#3498db",
    padding: "12px 0",
    marginBottom: 20,
    position: "sticky",
    top: 0,
    zIndex: 1000,
    display: isMobile ? "none" : "block"
  };
  const ulStyle = {
    display: "flex",
    flexDirection: "row",
    listStyle: "none",
    justifyContent: "center",
    alignItems: "center",
    margin: 0,
    padding: 0,
    gap: 20
  };
  const linkStyleDesktop = {
    color: "#fff",
    textDecoration: "none",
    fontWeight: 500,
    fontSize: 16
  };
  const buttonStyleDesktop = {
    background: "#e74c3c",
    color: "#fff",
    border: "none",
    borderRadius: 4,
    padding: "6px 14px",
    cursor: "pointer",
    fontSize: 16
  };

  return (
    <>
      {/* Hamburger for mobile */}
      {hamburger}
      {/* Overlay for mobile drawer */}
      {open && <div style={overlayStyle} onClick={() => setOpen(false)} />}
      {/* Slide menu for mobile (chỉ hiện khi open) */}
      <nav style={drawerStyle}>
        <button
          onClick={() => setOpen(false)}
          style={{
            background: "transparent",
            border: "none",
            color: "#fff",
            fontSize: 32,
            position: "absolute",
            top: 10,
            right: 18
          }}
          aria-label="Đóng menu"
        >
          ×
        </button>
        <Link to="/menu" style={linkStyle} onClick={() => setOpen(false)}>Menu</Link>
        <Link to="/cart" style={linkStyle} onClick={() => setOpen(false)}>Cart</Link>
        {loggedIn && (role === "admin" || role === "staff") && (
          <Link to="/product-manager" style={linkStyle} onClick={() => setOpen(false)}>Product Manager</Link>
        )}
        {loggedIn ? (
          <button onClick={handleLogout} style={buttonStyle}>Logout</button>
        ) : (
          <Link to="/login" style={linkStyle} onClick={() => setOpen(false)}>Login</Link>
        )}
      </nav>
      {/* Navbar ngang cho desktop/tablet */}
      <nav style={navStyle}>
        <ul style={ulStyle}>
          <li>
            <Link to="/menu" style={linkStyleDesktop}>Menu</Link>
          </li>
          <li>
            <Link to="/cart" style={linkStyleDesktop}>Cart</Link>
          </li>
          {loggedIn && (role === "admin" || role === "staff") && (
            <li>
              <Link to="/product-manager" style={linkStyleDesktop}>Product Manager</Link>
            </li>
          )}
          <li style={{ marginLeft: 30 }}>
            {loggedIn ? (
              <button
                onClick={handleLogout}
                style={buttonStyleDesktop}
              >
                Logout
              </button>
            ) : (
              <Link to="/login" style={linkStyleDesktop}>
                Login
              </Link>
            )}
          </li>
        </ul>
      </nav>
    </>
  );
}

export default Navbar;
