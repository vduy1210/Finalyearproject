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
    background: "#fff",
    boxShadow: open ? "-2px 0 12px rgba(44,62,80,0.18)" : "none",
    zIndex: 2000,
    transition: "right 0.3s cubic-bezier(.4,0,.2,1)",
    display: open && isMobile ? "flex" : "none",
    flexDirection: "column",
    padding: "32px 20px 20px 20px",
    borderTopLeftRadius: 18,
    borderBottomLeftRadius: 18,
    fontFamily: 'Roboto, Arial, sans-serif'
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
    color: "#1976d2",
    textDecoration: "none",
    fontWeight: 500,
    fontSize: 20,
    margin: "18px 0",
    borderRadius: 8,
    padding: "8px 12px",
    transition: "background 0.2s, color 0.2s",
    fontFamily: 'Roboto, Arial, sans-serif'
  };
  const linkHover = {
    background: "#e3f2fd",
    color: "#64b5f6"
  };
  const buttonStyle = {
    background: "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: "12px 24px",
    cursor: "pointer",
    fontSize: 20,
    margin: "18px 0",
    fontWeight: 600,
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    fontFamily: 'Roboto, Arial, sans-serif',
    transition: "background 0.2s"
  };
  const hamburger = (
    <button
      onClick={() => setOpen(true)}
      style={{
        background: "transparent",
        border: "none",
        color: "#1976d2",
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
    background: "#fff",
    padding: "10px 0",
    marginBottom: 20,
    position: "sticky",
    top: 0,
    zIndex: 1000,
    display: isMobile ? "none" : "flex",
    alignItems: "center",
    boxShadow: "0 2px 12px rgba(44,62,80,0.10)",
    borderRadius: 0,
    fontFamily: 'Roboto, Arial, sans-serif'
  };
  const ulStyle = {
    display: "flex",
    flexDirection: "row",
    listStyle: "none",
    justifyContent: "center",
    alignItems: "center",
    margin: 0,
    padding: 0,
    gap: 20,
    flex: 1
  };
  const linkStyleDesktop = {
    color: "#1976d2",
    textDecoration: "none",
    fontWeight: 500,
    fontSize: 16,
    borderRadius: 8,
    padding: "8px 12px",
    transition: "background 0.2s, color 0.2s",
    fontFamily: 'Roboto, Arial, sans-serif'
  };
  const buttonStyleDesktop = {
    background: "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: "8px 18px",
    cursor: "pointer",
    fontSize: 16,
    fontWeight: 600,
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    fontFamily: 'Roboto, Arial, sans-serif',
    transition: "background 0.2s"
  };
  // Logo/tên shop
  const logoStyle = {
    color: "#1976d2",
    fontWeight: 700,
    fontSize: 22,
    marginLeft: 24,
    marginRight: 32,
    letterSpacing: 1,
    fontFamily: 'Roboto, Arial, sans-serif',
    textShadow: "0 2px 8px rgba(44,62,80,0.10)"
  };

  // Hover effect (simple, inline)
  const [hoverIdx, setHoverIdx] = useState(-1);
  const [hoverIdxMobile, setHoverIdxMobile] = useState(-1);

  return (
    <>
      {/* Hamburger for mobile */}
      {hamburger}
      {/* Overlay for mobile drawer */}
      {open && <div style={overlayStyle} onClick={() => setOpen(false)} />}
      {/* Slide menu for mobile (chỉ hiện khi open) */}
      <nav style={drawerStyle}>
        <span style={{ ...logoStyle, color: "#1976d2", marginLeft: 0, marginBottom: 18 }}>ShopName</span>
        <button
          onClick={() => setOpen(false)}
          style={{
            background: "transparent",
            border: "none",
            color: "#1976d2",
            fontSize: 32,
            position: "absolute",
            top: 10,
            right: 18
          }}
          aria-label="Đóng menu"
        >
          
        </button>
        <Link to="/menu" style={{ ...linkStyle, ...(hoverIdxMobile === 0 ? linkHover : {}) }} onMouseEnter={() => setHoverIdxMobile(0)} onMouseLeave={() => setHoverIdxMobile(-1)} onClick={() => setOpen(false)}>Menu</Link>
        <Link to="/cart" style={{ ...linkStyle, ...(hoverIdxMobile === 1 ? linkHover : {}) }} onMouseEnter={() => setHoverIdxMobile(1)} onMouseLeave={() => setHoverIdxMobile(-1)} onClick={() => setOpen(false)}>Cart</Link>
        {loggedIn && (role === "admin" || role === "staff") && (
          <Link to="/product-manager" style={{ ...linkStyle, ...(hoverIdxMobile === 2 ? linkHover : {}) }} onMouseEnter={() => setHoverIdxMobile(2)} onMouseLeave={() => setHoverIdxMobile(-1)} onClick={() => setOpen(false)}>Product Manager</Link>
        )}
        {loggedIn ? (
          <button onClick={handleLogout} style={buttonStyle}>Logout</button>
        ) : (
          <Link to="/login" style={{ ...linkStyle, ...(hoverIdxMobile === 3 ? linkHover : {}) }} onMouseEnter={() => setHoverIdxMobile(3)} onMouseLeave={() => setHoverIdxMobile(-1)} onClick={() => setOpen(false)}>Staff Access</Link>
        )}
      </nav>
      {/* Navbar ngang cho desktop/tablet */}
      <nav style={navStyle}>
        <span style={logoStyle}>ShopName</span>
        <ul style={ulStyle}>
          <li>
            <Link to="/menu" style={{ ...linkStyleDesktop, ...(hoverIdx === 0 ? linkHover : {}) }} onMouseEnter={() => setHoverIdx(0)} onMouseLeave={() => setHoverIdx(-1)}>Menu</Link>
          </li>
          <li>
            <Link to="/cart" style={{ ...linkStyleDesktop, ...(hoverIdx === 1 ? linkHover : {}) }} onMouseEnter={() => setHoverIdx(1)} onMouseLeave={() => setHoverIdx(-1)}>Cart</Link>
          </li>
          {loggedIn && (role === "admin" || role === "staff") && (
            <li>
              <Link to="/product-manager" style={{ ...linkStyleDesktop, ...(hoverIdx === 2 ? linkHover : {}) }} onMouseEnter={() => setHoverIdx(2)} onMouseLeave={() => setHoverIdx(-1)}>Product Manager</Link>
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
              <Link to="/login" style={{ ...linkStyleDesktop, ...(hoverIdx === 3 ? linkHover : {}) }} onMouseEnter={() => setHoverIdx(3)} onMouseLeave={() => setHoverIdx(-1)}>
                Staff Access
              </Link>
            )}
          </li>
        </ul>
      </nav>
    </>
  );
}

export default Navbar;
